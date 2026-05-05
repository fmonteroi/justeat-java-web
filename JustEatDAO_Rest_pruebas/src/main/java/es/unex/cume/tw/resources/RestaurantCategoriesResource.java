package es.unex.cume.tw.resources;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import es.unex.cume.tw.dao.CategoryDAO;
import es.unex.cume.tw.dao.JDBCCategoryDAOImpl;
import es.unex.cume.tw.dao.JDBCRestaurantCategoriesDAOImpl;
import es.unex.cume.tw.dao.JDBCRestaurantDAOImpl;
import es.unex.cume.tw.dao.RestaurantCategoriesDAO;
import es.unex.cume.tw.dao.RestaurantDAO;
import es.unex.cume.tw.model.Category;
import es.unex.cume.tw.model.Restaurant;
import es.unex.cume.tw.model.RestaurantCategories;
import es.unex.cume.tw.model.User;
import es.unex.cume.tw.util.Utils;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/restaurantes/{id: [0-9]+}/categorias")
@Produces(MediaType.APPLICATION_JSON)
public class RestaurantCategoriesResource {
	@Context
	private ServletContext sc;

	/**
	 * GET /api/restaurantes/{id}/categorias
	 */
	@GET
	public List<Category> getRestaurantCategories(@PathParam("id") long restaurantId,
			@Context HttpServletRequest request) {

		Utils.getLoggedUser(request);

		Connection conn = (Connection) sc.getAttribute("dbConn");

		RestaurantDAO restaurantDao = new JDBCRestaurantDAOImpl();
		restaurantDao.setConnection(conn);

		RestaurantCategoriesDAO restaurantCategoriesDao = new JDBCRestaurantCategoriesDAOImpl();
		restaurantCategoriesDao.setConnection(conn);

		CategoryDAO categoryDao = new JDBCCategoryDAOImpl();
		categoryDao.setConnection(conn);

		Restaurant restaurant = restaurantDao.get(restaurantId);

		if (restaurant == null) {
			throw new CustomNotFoundException("Restaurante no encontrado");
		}

		List<Category> categories = new ArrayList<Category>();
		List<RestaurantCategories> relations = restaurantCategoriesDao.getAllByRestaurant(restaurantId);

		if (relations != null) {
			for (RestaurantCategories relation : relations) {
				Category category = categoryDao.get(relation.getIdct());

				if (category != null) {
					categories.add(category);
				}
			}
		}

		return categories;
	}

	/**
	 * POST /api/restaurantes/{id}/categorias/{categoryId}
	 */
	@POST
	@Path("/{categoryId: [0-9]+}")
	public Response addCategoryToRestaurant(@PathParam("id") long restaurantId,
			@PathParam("categoryId") long categoryId, @Context HttpServletRequest request) {

		User user = Utils.getLoggedUser(request);

		Connection conn = (Connection) sc.getAttribute("dbConn");

		RestaurantDAO restaurantDao = new JDBCRestaurantDAOImpl();
		restaurantDao.setConnection(conn);

		CategoryDAO categoryDao = new JDBCCategoryDAOImpl();
		categoryDao.setConnection(conn);

		RestaurantCategoriesDAO restaurantCategoriesDao = new JDBCRestaurantCategoriesDAOImpl();
		restaurantCategoriesDao.setConnection(conn);

		Restaurant restaurant = restaurantDao.get(restaurantId);

		if (restaurant == null) {
			throw new CustomNotFoundException("Restaurante no encontrado");
		}

		if (restaurant.getIdu() != user.getId()) {
			throw new WebApplicationException("No autorizado", Response.Status.FORBIDDEN);
		}

		Category category = categoryDao.get(categoryId);

		if (category == null) {
			throw new CustomNotFoundException("Categoria no encontrada");
		}

		if (restaurantCategoriesDao.get(restaurantId, categoryId) != null) {
			throw new WebApplicationException("La categoria ya esta asociada", Response.Status.CONFLICT);
		}

		RestaurantCategories relation = new RestaurantCategories();
		relation.setIdr(restaurantId);
		relation.setIdct(categoryId);

		if (!restaurantCategoriesDao.add(relation)) {
			throw new WebApplicationException("No se pudo asociar la categoria", Response.Status.INTERNAL_SERVER_ERROR);
		}

		return Response.status(Response.Status.CREATED).build();
	}

	/**
	 * DELETE /api/restaurantes/{id}/categorias/{categoryId}
	 */
	@DELETE
	@Path("/{categoryId: [0-9]+}")
	public Response removeCategoryFromRestaurant(@PathParam("id") long restaurantId,
			@PathParam("categoryId") long categoryId, @Context HttpServletRequest request) {

		User user = Utils.getLoggedUser(request);

		Connection conn = (Connection) sc.getAttribute("dbConn");

		RestaurantDAO restaurantDao = new JDBCRestaurantDAOImpl();
		restaurantDao.setConnection(conn);

		RestaurantCategoriesDAO restaurantCategoriesDao = new JDBCRestaurantCategoriesDAOImpl();
		restaurantCategoriesDao.setConnection(conn);

		Restaurant restaurant = restaurantDao.get(restaurantId);

		if (restaurant == null) {
			throw new CustomNotFoundException("Restaurante no encontrado");
		}

		if (restaurant.getIdu() != user.getId()) {
			throw new WebApplicationException("No autorizado", Response.Status.FORBIDDEN);
		}

		if (restaurantCategoriesDao.get(restaurantId, categoryId) == null) {
			throw new CustomNotFoundException("La categoria no esta asociada");
		}

		if (!restaurantCategoriesDao.delete(restaurantId, categoryId)) {
			throw new WebApplicationException("No se pudo eliminar la categoria",
					Response.Status.INTERNAL_SERVER_ERROR);
		}

		return Response.noContent().build();
	}

}
