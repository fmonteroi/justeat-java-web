package es.unex.cume.tw.resources;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import es.unex.cume.tw.dao.CategoryDAO;
import es.unex.cume.tw.dao.JDBCCategoryDAOImpl;
import es.unex.cume.tw.dao.JDBCRestaurantCategoriesDAOImpl;
import es.unex.cume.tw.dao.JDBCRestaurantDAOImpl;
import es.unex.cume.tw.dao.JDBCUserFavoritesDAOImpl;
import es.unex.cume.tw.dao.RestaurantCategoriesDAO;
import es.unex.cume.tw.dao.RestaurantDAO;
import es.unex.cume.tw.dao.UserFavoritesDAO;
import es.unex.cume.tw.dto.RestaurantResponse;
import es.unex.cume.tw.model.Category;
import es.unex.cume.tw.model.Restaurant;
import es.unex.cume.tw.model.RestaurantCategories;
import es.unex.cume.tw.model.User;
import es.unex.cume.tw.model.UserFavorite;
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

@Path("/usuarios/{id: [0-9]+}/favoritos")
@Produces(MediaType.APPLICATION_JSON)
public class UserFavoritesResource {

	@Context
	private ServletContext sc;

	/**
	 * GET /api/usuarios/{id}/favoritos
	 */
	@GET
	public List<RestaurantResponse> getUserFavorites(@PathParam("id") long userId,
			@Context HttpServletRequest request) {

		User user = Utils.getLoggedUser(request);

		if (user.getId() != userId) {
			throw new WebApplicationException("No autorizado", Response.Status.FORBIDDEN);
		}

		Connection conn = (Connection) sc.getAttribute("dbConn");

		UserFavoritesDAO userFavoritesDao = new JDBCUserFavoritesDAOImpl();
		userFavoritesDao.setConnection(conn);

		RestaurantDAO restaurantDao = new JDBCRestaurantDAOImpl();
		restaurantDao.setConnection(conn);

		RestaurantCategoriesDAO restaurantCategoriesDao = new JDBCRestaurantCategoriesDAOImpl();
		restaurantCategoriesDao.setConnection(conn);

		CategoryDAO categoryDao = new JDBCCategoryDAOImpl();
		categoryDao.setConnection(conn);

		List<RestaurantResponse> responses = new ArrayList<RestaurantResponse>();
		List<UserFavorite> favorites = userFavoritesDao.getAllByUser(userId);

		if (favorites == null) {
			return responses;
		}

		for (UserFavorite favorite : favorites) {
			Restaurant restaurant = restaurantDao.get(favorite.getIdr());

			if (restaurant != null) {
				responses.add(buildRestaurantResponse(restaurant, restaurantCategoriesDao, categoryDao));
			}
		}

		return responses;
	}

	private RestaurantResponse buildRestaurantResponse(Restaurant restaurant,
			RestaurantCategoriesDAO restaurantCategoriesDao, CategoryDAO categoryDao) {

		RestaurantResponse response = new RestaurantResponse();
		response.setRestaurant(restaurant);

		List<Category> categories = new ArrayList<Category>();
		List<RestaurantCategories> relations = restaurantCategoriesDao.getAllByRestaurant(restaurant.getId());

		if (relations != null) {
			for (RestaurantCategories relation : relations) {
				Category category = categoryDao.get(relation.getIdct());

				if (category != null) {
					categories.add(category);
				}
			}
		}

		response.setCategories(categories);

		return response;
	}
}
