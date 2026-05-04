package es.unex.cume.tw.resources;

import java.sql.Connection;

import es.unex.cume.tw.model.User;
import es.unex.cume.tw.dao.JDBCUserDAOImpl;
import es.unex.cume.tw.dao.UserDAO;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/sesiones")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SessionsResource {

	@Context
	private ServletContext sc;


	/**
	 * POST /api/sesiones
	 */
	@POST
	public Response login(User loginUser, @Context HttpServletRequest request) {
	    if (loginUser == null || loginUser.getEmail() == null || loginUser.getPassword() == null) {
	        throw new CustomBadRequestException("Email y contraseña obligatorios");
	    }

	    Connection conn = (Connection) sc.getAttribute("dbConn");

	    UserDAO userDao = new JDBCUserDAOImpl();
	    userDao.setConnection(conn);

	    User user = userDao.get(loginUser.getEmail());

	    if (user == null || !user.getPassword().equals(loginUser.getPassword())) {
	        throw new WebApplicationException("Credenciales incorrectas", Response.Status.UNAUTHORIZED);
	    }

	    HttpSession session = request.getSession(true);
	    session.setAttribute("user", user);

	    User responseUser = userDao.get(user.getId());
	    responseUser.setPassword(null);

	    return Response.ok(responseUser).build();
	}

}
