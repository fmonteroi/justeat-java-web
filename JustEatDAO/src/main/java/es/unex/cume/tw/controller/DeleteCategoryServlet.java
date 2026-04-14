package es.unex.cume.tw.controller;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;

import es.unex.cume.tw.dao.CategoryDAO;
import es.unex.cume.tw.dao.JDBCCategoryDAOImpl;
import es.unex.cume.tw.dao.JDBCRestaurantCategoriesDAOImpl;
import es.unex.cume.tw.dao.RestaurantCategoriesDAO;
import es.unex.cume.tw.model.RestaurantCategories;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/DeleteCategoryServlet.do")
public class DeleteCategoryServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Get session
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("LoginServlet.do");
            return;
        }

        // Prepare database access
        Connection conn = (Connection) getServletContext().getAttribute("dbConn");

        CategoryDAO categoryDao = new JDBCCategoryDAOImpl();
        categoryDao.setConnection(conn);

        RestaurantCategoriesDAO rcDao = new JDBCRestaurantCategoriesDAOImpl();
        rcDao.setConnection(conn);

        // Read and validate category id
        long id = 0;
        try {
            id = Long.parseLong(request.getParameter("id"));
        } catch (NumberFormatException e) {
            response.sendRedirect("ListCategoryServlet.do");
            return;
        }

        // Remove related category links
        List<RestaurantCategories> relations = rcDao.getAllByCategory(id);
        for (RestaurantCategories rc : relations) {
            rcDao.delete(rc.getIdr(), rc.getIdct());
        }

        // Delete the category
        categoryDao.delete(id);

        // Return to the category list
        response.sendRedirect("ListCategoryServlet.do");
    }
}
