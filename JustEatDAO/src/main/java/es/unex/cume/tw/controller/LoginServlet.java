package es.unex.cume.tw.controller;

import es.unex.cume.tw.dao.JDBCUserDAOImpl;
import es.unex.cume.tw.dao.UserDAO;
import es.unex.cume.tw.model.User;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Connection;
import java.util.logging.Logger;

/**
 * Servlet implementation class LoginServlet
 */
@WebServlet(urlPatterns = { "/LoginServlet.do" })
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(HttpServlet.class.getName());

    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoginServlet() {
        super();
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     * response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        if (session.getAttribute("user") != null)
            response.sendRedirect("ListRestaurantServlet.do");
        else {
            RequestDispatcher view = request.getRequestDispatcher("/WEB-INF/login.jsp");
            view.forward(request, response);
        }
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     * response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Connection conn = (Connection) getServletContext().getAttribute("dbConn");
        UserDAO userDao = new JDBCUserDAOImpl();
        userDao.setConnection(conn);

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        logger.info("credentials: " + email + " - " + password);

        User user = userDao.get(email);

        if ((user != null) && (user.getPassword().equals(password))) {
            HttpSession session = request.getSession();
            session.setAttribute("user", user);
            response.sendRedirect("ListRestaurantServlet.do");
        } else {
            request.setAttribute("messages", "Email o contraseña incorrecta");
            RequestDispatcher view = request.getRequestDispatcher("/WEB-INF/login.jsp");
            view.forward(request, response);
        }
    }

}
