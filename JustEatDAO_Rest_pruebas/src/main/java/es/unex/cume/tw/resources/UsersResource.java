package es.unex.cume.tw.resources;

import java.sql.Connection;

import es.unex.cume.tw.dao.JDBCUserDAOImpl;
import es.unex.cume.tw.model.User;
import es.unex.cume.tw.dao.UserDAO;
import es.unex.cume.tw.util.Utils;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

@Path("/usuarios")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UsersResource {

	@Context
	private ServletContext sc;

	@Context
	private UriInfo uriInfo;

	/**
	 * POST /api/usuarios
	 */
	@POST
	public Response createUser(User user, @Context HttpServletRequest request) {
		Connection conn = (Connection) sc.getAttribute("dbConn");

		UserDAO userDao = new JDBCUserDAOImpl();
		userDao.setConnection(conn);

		validateUser(user);

		if (userDao.get(user.getEmail().trim()) != null) {
			throw new WebApplicationException("Ya existe un usuario con ese email", Response.Status.CONFLICT);
		}

		user.setName(user.getName().trim());
		user.setSurname(user.getSurname().trim());
		user.setEmail(user.getEmail().trim());

		long id = userDao.add(user);

		if (id < 0) {
			throw new WebApplicationException("No se pudo crear el usuario", Response.Status.INTERNAL_SERVER_ERROR);
		}

		user.setId(id);

		HttpSession session = request.getSession(true);
		session.setAttribute("user", user);

		User responseUser = userDao.get(id);
		responseUser.setPassword(null);

		return Response.created(uriInfo.getAbsolutePathBuilder().path(Long.toString(id)).build()).entity(responseUser)
				.build();
	}

	/**
	 * GET /api/usuarios/{id}
	 */
	@GET
	@Path("/{id: [0-9]+}")
	public User getUser(@PathParam("id") long id, @Context HttpServletRequest request) {
		User loggedUser = Utils.getLoggedUser(request);

		if (loggedUser.getId() != id) {
			throw new WebApplicationException("No autorizado", Response.Status.FORBIDDEN);
		}

		Connection conn = (Connection) sc.getAttribute("dbConn");
		UserDAO userDao = new JDBCUserDAOImpl();
		userDao.setConnection(conn);

		User user = userDao.get(id);

		if (user == null) {
			throw new CustomNotFoundException("Usuario no encontrado");
		}

		user.setPassword(null);
		return user;
	}

	/**
	 * PUT /api/usuarios/{id}
	 */
	@PUT
	@Path("/{id: [0-9]+}")
	public User updateUser(@PathParam("id") long id, User user, @Context HttpServletRequest request) {
		User loggedUser = Utils.getLoggedUser(request);

		if (loggedUser.getId() != id) {
			throw new WebApplicationException("No autorizado", Response.Status.FORBIDDEN);
		}

		Connection conn = (Connection) sc.getAttribute("dbConn");
		UserDAO userDao = new JDBCUserDAOImpl();
		userDao.setConnection(conn);

		User currentUser = userDao.get(id);

		if (currentUser == null) {
			throw new CustomNotFoundException("Usuario no encontrado");
		}

		if (user == null) {
			throw new CustomBadRequestException("El cuerpo de la petición es obligatorio");
		}

		if (user.getName() == null || user.getName().trim().isEmpty() || user.getSurname() == null
				|| user.getSurname().trim().isEmpty() || user.getEmail() == null || user.getEmail().trim().isEmpty()) {
			throw new CustomBadRequestException("Nombre, apellidos y email son obligatorios");
		}

		if (!user.getEmail().contains("@")) {
			throw new CustomBadRequestException("Email no válido");
		}

		User existingByEmail = userDao.get(user.getEmail().trim());
		if (existingByEmail != null && existingByEmail.getId() != id) {
			throw new WebApplicationException("Ya existe otro usuario con ese email", Response.Status.CONFLICT);
		}

		currentUser.setName(user.getName().trim());
		currentUser.setSurname(user.getSurname().trim());
		currentUser.setEmail(user.getEmail().trim());

		if (user.getPassword() != null && !user.getPassword().trim().isEmpty()) {
			if (!Utils.isPasswordValid(user.getPassword())) {
				throw new CustomBadRequestException(
						"La contraseña debe tener al menos 8 caracteres, mayúscula, minúscula, número y carácter especial");
			}
			currentUser.setPassword(user.getPassword());
		}

		if (!userDao.update(currentUser)) {
			throw new WebApplicationException("No se pudo actualizar el usuario",
					Response.Status.INTERNAL_SERVER_ERROR);
		}

		HttpSession session = request.getSession(false);
		if (session != null) {
			session.setAttribute("user", currentUser);
		}

		User responseUser = userDao.get(id);
		responseUser.setPassword(null);
		return responseUser;
	}

	/**
	 * DELETE /api/usuarios/{id}
	 */
	@DELETE
	@Path("/{id: [0-9]+}")
	public Response deleteUser(@PathParam("id") long id, @Context HttpServletRequest request) {
		User loggedUser = Utils.getLoggedUser(request);

		if (loggedUser.getId() != id) {
			throw new WebApplicationException("No autorizado", Response.Status.FORBIDDEN);
		}

		Connection conn = (Connection) sc.getAttribute("dbConn");
		UserDAO userDao = new JDBCUserDAOImpl();
		userDao.setConnection(conn);

		User user = userDao.get(id);

		if (user == null) {
			throw new CustomNotFoundException("Usuario no encontrado");
		}

		if (!userDao.delete(id)) {
			throw new WebApplicationException("No se pudo eliminar el usuario", Response.Status.INTERNAL_SERVER_ERROR);
		}

		HttpSession session = request.getSession(false);
		if (session != null) {
			session.invalidate();
		}

		return Response.noContent().build();
	}

	private void validateUser(User user) {
		if (user == null) {
			throw new CustomBadRequestException("El cuerpo de la petición es obligatorio");
		}

		if (user.getName() == null || user.getName().trim().isEmpty()) {
			throw new CustomBadRequestException("El nombre es obligatorio");
		}

		if (user.getSurname() == null || user.getSurname().trim().isEmpty()) {
			throw new CustomBadRequestException("Los apellidos son obligatorios");
		}

		if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
			throw new CustomBadRequestException("El email es obligatorio");
		}

		if (!user.getEmail().contains("@")) {
			throw new CustomBadRequestException("El email no es válido");
		}

		if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
			throw new CustomBadRequestException("La contraseña es obligatoria");
		}

		if (!Utils.isPasswordValid(user.getPassword())) {
			throw new CustomBadRequestException(
					"La contraseña debe tener al menos 8 caracteres, mayúscula, minúscula, número y carácter especial");
		}
	}
}
