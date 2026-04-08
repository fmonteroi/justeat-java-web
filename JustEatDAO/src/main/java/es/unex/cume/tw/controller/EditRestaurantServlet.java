package es.unex.cume.tw.controller;

import es.unex.cume.tw.dao.*;
import es.unex.cume.tw.model.Category;
import es.unex.cume.tw.model.Dish;
import es.unex.cume.tw.model.Restaurant;
import es.unex.cume.tw.model.RestaurantCategories;
import es.unex.cume.tw.model.User;
import es.unex.cume.tw.util.Triplet;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

/**
 * Servlet implementation class EditRestaurantServlet
 */
@WebServlet(urlPatterns = { "/EditRestaurantServlet.do" })
public class EditRestaurantServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(HttpServlet.class.getName());

    /**
     * @see HttpServlet#HttpServlet()
     */
    public EditRestaurantServlet() {
        super();
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("LoginServlet.do");
            return;
        }
        
        User user = (User) session.getAttribute("user");
        try {
            Connection conn = (Connection) getServletContext().getAttribute("dbConn");
            RestaurantDAO restaurantDao = new JDBCRestaurantDAOImpl();
            restaurantDao.setConnection(conn);

            CategoryDAO categoryDao = new JDBCCategoryDAOImpl();
            categoryDao.setConnection(conn);

            RestaurantCategoriesDAO restaurantsCategoriesDAO = new JDBCRestaurantCategoriesDAOImpl();
            restaurantsCategoriesDAO.setConnection(conn);
            
            DishDAO dishDao = new JDBCDishDAOImpl();
            dishDao.setConnection(conn);

            logger.info(request.getParameter("id"));

            long rid = Long.parseLong(request.getParameter("id"));
            Restaurant restaurant = restaurantDao.get(rid);
            
            List<Dish> dishes = dishDao.getAllByRestaurant(rid);

            List<RestaurantCategories> restaurantCategories = restaurantsCategoriesDAO.getAll();

            List<Triplet<Category, Boolean, User>> categoriesChecked = new ArrayList<Triplet<Category, Boolean, User>>();

            List<Category> categories = categoryDao.getAll();
            Iterator<Category> itCategories = categories.iterator();

            while (itCategories.hasNext()) {
                Category category = itCategories.next();

                int len = restaurantCategories.size();
                Boolean exist = false;
                for (int i = 0; i < len; i++) {
                    RestaurantCategories restaurantCategory = restaurantCategories.get(i);
                    if (restaurant.getId() == restaurantCategory.getIdr()
                            && category.getId() == restaurantCategory.getIdct()) {
                        exist = true;
                        break;
                    }
                }
                categoriesChecked.add(new Triplet<Category, Boolean, User>(category, exist, user));
            }

            if (restaurant != null && restaurant.getIdu() == user.getId()) {
                session.setAttribute("restaurant", restaurant);
                session.setAttribute("categories", categoriesChecked);
                request.setAttribute("dishes", dishes);
                
                RequestDispatcher view = request.getRequestDispatcher("/WEB-INF/restaurant_edit.jsp");
                view.forward(request, response);
            } else {
                response.sendRedirect("ListRestaurantServlet.do");
            }

        } catch (NumberFormatException e) {
            logger.info("parameter id is not a number");
        }
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        
        // PREGUNTAR POR ESTO
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("LoginServlet.do");
            return;
        }
        
        User user = (User) session.getAttribute("user");
        Restaurant restaurant = (Restaurant) session.getAttribute("restaurant");
        
        // PREGUNTAR POR ESTO
        if (restaurant == null) {
            response.sendRedirect("ListRestaurantServlet.do");
            return;
        }

        restaurant.setName(request.getParameter("restaurant-name"));
        restaurant.setAddress(request.getParameter("restaurant-address"));
        restaurant.setTelephone(request.getParameter("restaurant-tel"));
        restaurant.setCity(request.getParameter("restaurant-city"));
        restaurant.setMinPrice(Integer.parseInt(request.getParameter("min-price")));
        restaurant.setMaxPrice(Integer.parseInt(request.getParameter("max-price")));
        restaurant.setGradesAverage(0);
        restaurant.setContactEmail(request.getParameter("restaurant-email"));
        restaurant.setBikeFriendly(Integer.parseInt(request.getParameter("bikefriendly")));
        restaurant.setAvailable(Integer.parseInt(request.getParameter("available")));

        Connection conn = (Connection) getServletContext().getAttribute("dbConn");
        RestaurantDAO restaurantDao = new JDBCRestaurantDAOImpl();
        restaurantDao.setConnection(conn);

        RestaurantCategoriesDAO restaurantsCategoriesDAO = new JDBCRestaurantCategoriesDAOImpl();
        restaurantsCategoriesDAO.setConnection(conn);

        List<RestaurantCategories> restaurantCategories = restaurantsCategoriesDAO
                .getAllByRestaurant(restaurant.getId());

        int len = restaurantCategories.size();
        for (int i = 0; i < len; i++) {
            RestaurantCategories rc = restaurantCategories.get(i);

            restaurantsCategoriesDAO.delete(rc.getIdr(), rc.getIdct());
        }

        String[] categories = request.getParameterValues("restaurant-category");
        for (int i = 0; i < categories.length; i++) {
            try {
                RestaurantCategories rc = new RestaurantCategories();
                rc.setIdct(Long.parseLong(categories[i]));
                rc.setIdr(restaurant.getId());

                restaurantsCategoriesDAO.add(rc);
            } catch (Exception e) {
                logger.info("id not a number");
            }
        }

        if (restaurant.getIdu() == user.getId() && restaurantDao.update(restaurant)) {
            response.sendRedirect("ListRestaurantServlet.do");
        } else {
            request.setAttribute("messages", "Hubo un fallo al guardar los datos del restaurante");
            RequestDispatcher view = request.getRequestDispatcher("/WEB-INF/restaurant_edit.jsp");
            view.forward(request, response);
        }
    }

}
