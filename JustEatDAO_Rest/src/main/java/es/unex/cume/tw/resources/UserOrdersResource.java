package es.unex.cume.tw.resources;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import es.unex.cume.tw.dao.JDBCOrderDAOImpl;
import es.unex.cume.tw.dao.OrderDAO;
import es.unex.cume.tw.model.Order;
import es.unex.cume.tw.model.User;
import es.unex.cume.tw.util.Utils;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/usuarios/{id: [0-9]+}/pedidos")
@Produces(MediaType.APPLICATION_JSON)
public class UserOrdersResource {

	@Context
	private ServletContext sc;

	/**
	 * GET /api/usuarios/{id}/pedidos
	 * 
	 * @param userId
	 * @param request
	 * @return
	 */
	@GET
	public List<Order> getUserOrders(@PathParam("id") long userId, @Context HttpServletRequest request) {
		User loggedUser = Utils.getLoggedUser(request);

		// Check user reads only his own orders
		if (loggedUser.getId() != userId) {
			throw new WebApplicationException("No autorizado", Response.Status.FORBIDDEN);
		}

		Connection conn = (Connection) sc.getAttribute("dbConn");
		OrderDAO orderDao = new JDBCOrderDAOImpl();
		orderDao.setConnection(conn);

		List<Order> result = new ArrayList<Order>();

		// Filter orders by user
		for (Order order : orderDao.getAll()) {
			if (order.getIdu() == userId) {
				result.add(order);
			}
		}

		return result;
	}
}
