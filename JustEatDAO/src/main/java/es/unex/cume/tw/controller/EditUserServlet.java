package es.unex.cume.tw.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import es.unex.cume.tw.dao.JDBCUserDAOImpl;
import es.unex.cume.tw.dao.UserDAO;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Connection;

import es.unex.cume.tw.model.User;
import es.unex.cume.tw.util.ValidationUtils;

/**
 * Servlet implementation class EditUserServlet
 */
@WebServlet("/EditUserServlet.do")
public class EditUserServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public EditUserServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
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
		UserDAO userDao = new JDBCUserDAOImpl();
		userDao.setConnection(conn);

		User sessionUser = (User) session.getAttribute("user");

		// Load fresh user data
		User user = userDao.get(sessionUser.getId());
		
		if (user == null) {
		    session.invalidate();
		    response.sendRedirect("LoginServlet.do");
		    return;
		}
		
		// Render profile form
		request.setAttribute("profileUser", user);

		RequestDispatcher view = request.getRequestDispatcher("/WEB-INF/user_edit.jsp");
		view.forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
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
		UserDAO userDao = new JDBCUserDAOImpl();
		userDao.setConnection(conn);

		User sessionUser = (User) session.getAttribute("user");

		// Read requested action
		String action = request.getParameter("action");

		if ("update".equals(action)) {
			// Read form fields
			String name = request.getParameter("name");
			String surname = request.getParameter("surname");
			String email = request.getParameter("email");
			String password = request.getParameter("password");
			String passwordConfirm = request.getParameter("passwordConfirm");

			String messages = null;

			// Validate required fields
			if (name == null || name.trim().isEmpty() || surname == null || surname.trim().isEmpty() || email == null
					|| email.trim().isEmpty()) {
				messages = "Nombre, apellidos y correo son obligatorios.";
			}

			// Validate email uniqueness
			if (messages == null) {
			    User otherUser = userDao.get(email);
			    if (otherUser != null && otherUser.getId() != sessionUser.getId()) {
			        messages = "Ya existe otro usuario con ese correo electronico.";
			    }
			}

			// Validate password only if it changes
			if (messages == null && password != null && !password.trim().isEmpty()) {
				if (!password.equals(passwordConfirm)) {
					messages = "Las contraseñas no coinciden.";
				} else if (!ValidationUtils.isPasswordValid(password)) {
					messages = "La contraseña debe tener al menos 8 caracteres, una mayuscula, una minúscula, un número y un caracter especial.";
				}
			}

			// Reload the current user from the database
			User freshUser = userDao.get(sessionUser.getId());
			if (freshUser == null) {
				session.invalidate();
				response.sendRedirect("LoginServlet.do");
				return;
			}

			// Render form again on validation errors
			if (messages != null) {
				freshUser.setName(name);
				freshUser.setSurname(surname);
				freshUser.setEmail(email);

				request.setAttribute("profileUser", freshUser);
				request.setAttribute("messages", messages);

				RequestDispatcher view = request.getRequestDispatcher("/WEB-INF/user_edit.jsp");
				view.forward(request, response);
				return;
			}

			// Apply profile changes
			freshUser.setName(name.trim());
			freshUser.setSurname(surname.trim());
			freshUser.setEmail(email.trim());

			if (password != null && !password.trim().isEmpty()) {
				freshUser.setPassword(password);
			}

			// Persist profile changes
			boolean updated = userDao.update(freshUser);

			// Render form again on persistence errors
			if (!updated) {
				request.setAttribute("profileUser", freshUser);
				request.setAttribute("messages", "No se pudo actualizar el perfil.");

				RequestDispatcher view = request.getRequestDispatcher("/WEB-INF/user_edit.jsp");
				view.forward(request, response);
				return;
			}

			// Refresh session data
			session.setAttribute("user", freshUser);
			request.setAttribute("profileUser", freshUser);
			request.setAttribute("messagesOk", "Perfil actualizado correctamente.");

			RequestDispatcher view = request.getRequestDispatcher("/WEB-INF/user_edit.jsp");
			view.forward(request, response);
		} else {
			// Fallback for unsupported actions
			response.sendRedirect("EditUserServlet.do");
		}
	}

}
