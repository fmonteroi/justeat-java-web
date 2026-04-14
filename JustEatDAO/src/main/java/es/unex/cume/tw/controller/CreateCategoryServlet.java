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

@WebServlet("/CreateCategoryServlet.do")
public class CreateCategoryServlet extends HttpServlet {
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

        // Render form
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
            if (existing != null) {
                messages = "Ya existe una categoría con ese nombre.";
            }
        }

        // Render form again on validation errors
        if (messages != null) {
            request.setAttribute("messages", messages);
            request.setAttribute("categoryName", name);
            request.setAttribute("categoryDescription", description);
            RequestDispatcher view = request.getRequestDispatcher("/WEB-INF/category_form.jsp");
            view.forward(request, response);
            return;
        }

        // Build the category object
        Category category = new Category();
        category.setName(name);
        category.setDescription(description);

        // Persist the category
        categoryDao.add(category);

        // Return to the category list
        response.sendRedirect("ListCategoryServlet.do");
    }
}
