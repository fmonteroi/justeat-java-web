package es.unex.cume.tw.resources;

import java.sql.Connection;

import es.unex.cume.tw.dao.JDBCRestaurantDAOImpl;
import es.unex.cume.tw.dao.JDBCUserFavoritesDAOImpl;
import es.unex.cume.tw.dao.RestaurantDAO;
import es.unex.cume.tw.dao.UserFavoritesDAO;
import es.unex.cume.tw.model.Restaurant;
import es.unex.cume.tw.model.User;
import es.unex.cume.tw.model.UserFavorite;
import es.unex.cume.tw.util.Utils;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/restaurantes/{id: [0-9]+}/favoritos")
@Produces(MediaType.APPLICATION_JSON)
public class RestaurantFavoritesResource {

	@Context
	private ServletContext sc;

	/**
	 * POST /api/restaurantes/{id}/favoritos
	 */
	@POST
	public Response addRestaurantToFavorites(@PathParam("id") long restaurantId,
			@Context HttpServletRequest request) {

		User user = Utils.getLoggedUser(request);

		Connection conn = (Connection) sc.getAttribute("dbConn");

		RestaurantDAO restaurantDao = new JDBCRestaurantDAOImpl();
		restaurantDao.setConnection(conn);

		UserFavoritesDAO userFavoritesDao = new JDBCUserFavoritesDAOImpl();
		userFavoritesDao.setConnection(conn);

		Restaurant restaurant = restaurantDao.get(restaurantId);

		if (restaurant == null) {
			throw new CustomNotFoundException("Restaurante no encontrado");
		}

		if (userFavoritesDao.get(user.getId(), restaurantId) != null) {
			throw new WebApplicationException("El restaurante ya está en favoritos", Response.Status.CONFLICT);
		}

		UserFavorite favorite = new UserFavorite();
		favorite.setIdu(user.getId());
		favorite.setIdr(restaurantId);

		if (!userFavoritesDao.add(favorite)) {
			throw new WebApplicationException("No se pudo añadir el restaurante a favoritos",
					Response.Status.INTERNAL_SERVER_ERROR);
		}

		return Response.status(Response.Status.CREATED).build();
	}

	/**
	 * DELETE /api/restaurantes/{id}/favoritos
	 */
	@DELETE
	public Response removeRestaurantFromFavorites(@PathParam("id") long restaurantId,
			@Context HttpServletRequest request) {

		User user = Utils.getLoggedUser(request);

		Connection conn = (Connection) sc.getAttribute("dbConn");

		RestaurantDAO restaurantDao = new JDBCRestaurantDAOImpl();
		restaurantDao.setConnection(conn);

		UserFavoritesDAO userFavoritesDao = new JDBCUserFavoritesDAOImpl();
		userFavoritesDao.setConnection(conn);

		Restaurant restaurant = restaurantDao.get(restaurantId);

		if (restaurant == null) {
			throw new CustomNotFoundException("Restaurante no encontrado");
		}

		if (userFavoritesDao.get(user.getId(), restaurantId) == null) {
			throw new CustomNotFoundException("El restaurante no está en favoritos");
		}

		if (!userFavoritesDao.delete(user.getId(), restaurantId)) {
			throw new WebApplicationException("No se pudo eliminar el restaurante de favoritos",
					Response.Status.INTERNAL_SERVER_ERROR);
		}

		return Response.noContent().build();
	}
}
