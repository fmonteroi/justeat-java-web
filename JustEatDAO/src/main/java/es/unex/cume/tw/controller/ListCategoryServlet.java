package es.unex.cume.tw.controller;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;

import es.unex.cume.tw.dao.CategoryDAO;
import es.unex.cume.tw.dao.JDBCCategoryDAOImpl;
import es.unex.cume.tw.model.Category;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/ListCategoryServlet.do")
public class ListCategoryServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
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

        // Load categories
        List<Category> categories = categoryDao.getAll();
        request.setAttribute("categories", categories);

        // Render category list
        RequestDispatcher view = request.getRequestDispatcher("/WEB-INF/category_list.jsp");
        view.forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Reuse GET behavior
        doGet(request, response);
    }
}
