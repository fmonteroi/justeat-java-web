package es.unex.cume.tw.controller;

import java.io.IOException;
import java.sql.Connection;

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

@WebServlet("/EditCategoryServlet.do")
public class EditCategoryServlet extends HttpServlet {
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

        // Read and validate category id
        long id = 0;
        try {
            id = Long.parseLong(request.getParameter("id"));
        } catch (NumberFormatException e) {
            response.sendRedirect("ListCategoryServlet.do");
            return;
        }

        // Load current category
        Category category = categoryDao.get(id);
        if (category == null) {
            response.sendRedirect("ListCategoryServlet.do");
            return;
        }

        // Render form
        request.setAttribute("category", category);

        RequestDispatcher view = request.getRequestDispatcher("/WEB-INF/category_form.jsp");
        view.forward(request, response);
    }

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

        // Read and validate category id
        long id = 0;
        try {
            id = Long.parseLong(request.getParameter("id"));
        } catch (NumberFormatException e) {
            response.sendRedirect("ListCategoryServlet.do");
            return;
        }

        // Load current category
        Category category = categoryDao.get(id);
        if (category == null) {
            response.sendRedirect("ListCategoryServlet.do");
            return;
        }

        // Read form fields
        String name = request.getParameter("category-name");
        String description = request.getParameter("category-description");
        String messages = null;

        // Normalize optional fields
        if (description == null) {
            description = "";
        }
        description = description.trim();

        // Validate category data
        if (name == null || name.trim().isEmpty()) {
            messages = "El nombre de la categoría es obligatorio.";
        } else {
            name = name.trim();
            Category existing = categoryDao.get(name);
            if (existing != null && existing.getId() != category.getId()) {
                messages = "Ya existe una categoría con ese nombre.";
            }
        }

        // Render form again on validation errors
        if (messages != null) {
            category.setName(name);
            category.setDescription(description);
            request.setAttribute("messages", messages);
            request.setAttribute("category", category);
            RequestDispatcher view = request.getRequestDispatcher("/WEB-INF/category_form.jsp");
            view.forward(request, response);
            return;
        }

        // Save category changes
        category.setName(name);
        category.setDescription(description);
        categoryDao.update(category);

        // Return to the category list
        response.sendRedirect("ListCategoryServlet.do");
    }
}
