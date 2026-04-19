package es.unex.cume.tw.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Connection;

import es.unex.cume.tw.dao.JDBCUserDAOImpl;
import es.unex.cume.tw.dao.UserDAO;
import es.unex.cume.tw.model.User;
import es.unex.cume.tw.util.ValidationUtils;

/**
 * Servlet implementation class RegisterServlet
 */
@WebServlet("/RegisterServlet.do")
public class RegisterServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public RegisterServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// Render register form
		RequestDispatcher view = request.getRequestDispatcher("/WEB-INF/register.jsp");
		view.forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		// Prepare database access
		Connection conn = (Connection) getServletContext().getAttribute("dbConn");
        UserDAO userDao = new JDBCUserDAOImpl();
        userDao.setConnection(conn);
        
		// Read form fields
		String name = request.getParameter("name");
		String surname = request.getParameter("surname");
		String email = request.getParameter("email");
		String password = request.getParameter("password");
		String passwordConfirm = request.getParameter("passwordConfirm");

		String messages = null;

		// Validate required fields
		if (name == null || name.trim().isEmpty() || surname == null || surname.trim().isEmpty() || email == null
				|| email.trim().isEmpty() || password == null || password.trim().isEmpty() || passwordConfirm == null
				|| passwordConfirm.trim().isEmpty()) {
			messages = "Todos los campos son obligatorios.";
		}
		
		// Validate password confirmation
		if (messages == null && !password.equals(passwordConfirm)) {
		    messages = "Las contraseñas no coinciden.";
		}
		
		// Validate password format
		if (messages == null && !ValidationUtils.isPasswordValid(password)) {
		    messages = "La contraseña debe tener al menos 8 caracteres, una mayúscula, una minúscula, un número y un caracter especial.";
		}
		
		// Validate email uniqueness
		if (messages == null && userDao.get(email) != null) {
		    messages = "Ya existe un usuario con ese correo electronico.";
		}
		
		// Render form again on validation errors
		if (messages != null) {
			request.setAttribute("messages", messages);
		    request.setAttribute("name", name);
		    request.setAttribute("surname", surname);
		    request.setAttribute("email", email);

		    RequestDispatcher view = request.getRequestDispatcher("/WEB-INF/register.jsp");
		    
		    view.forward(request, response);
		    return;
		}
		
		// Build the user object
		User user = new User();
		user.setName(name.trim());
		user.setSurname(surname.trim());
		user.setEmail(email.trim());
		user.setPassword(password);

		long id = userDao.add(user);
		
		// Handle persistence errors
		if (id < 0) {
		    request.setAttribute("messages", "No se pudo registrar el usuario.");
		    request.setAttribute("name", name);
		    request.setAttribute("surname", surname);
		    request.setAttribute("email", email);

		    RequestDispatcher view = request.getRequestDispatcher("/WEB-INF/register.jsp");
		    view.forward(request, response);
		    return;
		}
		
		user.setId(id);

		// Start session after registration
		HttpSession session = request.getSession();
		session.setAttribute("user", user);

		response.sendRedirect("SearchServlet.do");

	}
}
