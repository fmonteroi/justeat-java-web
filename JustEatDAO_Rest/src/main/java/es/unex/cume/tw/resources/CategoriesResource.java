package es.unex.cume.tw.resources;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import es.unex.cume.tw.dao.CategoryDAO;
import es.unex.cume.tw.dao.RestaurantDAO;
import es.unex.cume.tw.dao.RestaurantCategoriesDAO;
import es.unex.cume.tw.dao.JDBCCategoryDAOImpl;
import es.unex.cume.tw.dao.JDBCRestaurantCategoriesDAOImpl;
import es.unex.cume.tw.dao.JDBCRestaurantDAOImpl;
import es.unex.cume.tw.model.Category;
import es.unex.cume.tw.model.Restaurant;
import es.unex.cume.tw.model.RestaurantCategories;
import es.unex.cume.tw.util.Utils;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
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

@Path("/categorias")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CategoriesResource {
	@Context
	private ServletContext sc;

	@Context
	private UriInfo uriInfo;

	/**
	 * GET /api/categorias
	 */
	@GET
	public List<Category> getCategories(@Context HttpServletRequest request) {
	    Utils.getLoggedUser(request);

	    Connection conn = (Connection) sc.getAttribute("dbConn");

	    CategoryDAO categoryDao = new JDBCCategoryDAOImpl();
	    categoryDao.setConnection(conn);

	    return categoryDao.getAll();
	}

	/**
	 * GET /api/categorias/{id}
	 */
	@GET
	@Path("/{id: [0-9]+}")
	public Category getCategory(@PathParam("id") long id, @Context HttpServletRequest request) {
	    Utils.getLoggedUser(request);

	    Connection conn = (Connection) sc.getAttribute("dbConn");

	    CategoryDAO categoryDao = new JDBCCategoryDAOImpl();
	    categoryDao.setConnection(conn);

	    Category category = categoryDao.get(id);

	    if (category == null) {
	        throw new CustomNotFoundException("Categoría no encontrada");
	    }

	    return category;
	}

	/**
	 * GET /api/categorias/{id}/restaurantes
	 */
	@GET
	@Path("/{id: [0-9]+}/restaurantes")
	public List<Restaurant> getRestaurantsByCategory(@PathParam("id") long categoryId,
	        @Context HttpServletRequest request) {

	    Utils.getLoggedUser(request);

	    Connection conn = (Connection) sc.getAttribute("dbConn");

	    CategoryDAO categoryDao = new JDBCCategoryDAOImpl();
	    categoryDao.setConnection(conn);

	    RestaurantDAO restaurantDao = new JDBCRestaurantDAOImpl();
	    restaurantDao.setConnection(conn);

	    RestaurantCategoriesDAO restaurantCategoriesDao = new JDBCRestaurantCategoriesDAOImpl();
	    restaurantCategoriesDao.setConnection(conn);

	    Category category = categoryDao.get(categoryId);

	    if (category == null) {
	        throw new CustomNotFoundException("Categoria no encontrada");
	    }

	    List<Restaurant> restaurants = new ArrayList<Restaurant>();
	    List<RestaurantCategories> relations = restaurantCategoriesDao.getAllByCategory(categoryId);

	    if (relations != null) {
	        for (RestaurantCategories relation : relations) {
	            Restaurant restaurant = restaurantDao.get(relation.getIdr());

	            if (restaurant != null) {
	                restaurants.add(restaurant);
	            }
	        }
	    }

	    return restaurants;
	}


	/**
	 * POST /api/categorias
	 */
	@POST
	public Response createCategory(Category category, @Context HttpServletRequest request) {
	    Utils.getLoggedUser(request);

	    validateCategory(category);

	    Connection conn = (Connection) sc.getAttribute("dbConn");

	    CategoryDAO categoryDao = new JDBCCategoryDAOImpl();
	    categoryDao.setConnection(conn);

	    long id = categoryDao.add(category);

	    if (id < 0) {
	        throw new WebApplicationException("No se pudo crear la categoría", Response.Status.INTERNAL_SERVER_ERROR);
	    }

	    return Response.status(Response.Status.CREATED).entity(categoryDao.get(id)).build();
	}

	/**
	 * PUT /api/categorias/{id}
	 */
	@PUT
	@Path("/{id: [0-9]+}")
	public Category updateCategory(@PathParam("id") long id, Category category,
	        @Context HttpServletRequest request) {
	    Utils.getLoggedUser(request);

	    validateCategory(category);

	    Connection conn = (Connection) sc.getAttribute("dbConn");

	    CategoryDAO categoryDao = new JDBCCategoryDAOImpl();
	    categoryDao.setConnection(conn);

	    Category current = categoryDao.get(id);

	    if (current == null) {
	        throw new CustomNotFoundException("Categoría no encontrada");
	    }

	    category.setId(id);

	    if (!categoryDao.update(category)) {
	        throw new WebApplicationException("No se pudo actualizar la categoría", Response.Status.INTERNAL_SERVER_ERROR);
	    }

	    return categoryDao.get(id);
	}

	/**
	 * DELETE /api/categorias/{id}
	 */
	@DELETE
	@Path("/{id: [0-9]+}")
	public Response deleteCategory(@PathParam("id") long id, @Context HttpServletRequest request) {
	    Utils.getLoggedUser(request);

	    Connection conn = (Connection) sc.getAttribute("dbConn");

	    CategoryDAO categoryDao = new JDBCCategoryDAOImpl();
	    categoryDao.setConnection(conn);

	    Category current = categoryDao.get(id);

	    if (current == null) {
	        throw new CustomNotFoundException("Categoría no encontrada");
	    }

	    if (!categoryDao.delete(id)) {
	        throw new WebApplicationException("No se pudo eliminar la categoría", Response.Status.INTERNAL_SERVER_ERROR);
	    }

	    return Response.noContent().build();
	}

	private void validateCategory(Category category) {
	    if (category == null) {
	        throw new CustomBadRequestException("El cuerpo es obligatorio");
	    }

	    if (category.getName() == null || category.getName().trim().isEmpty()) {
	        throw new CustomBadRequestException("El nombre es obligatorio");
	    }

	    if (category.getDescription() == null) {
	        category.setDescription("");
	    }

	    category.setName(category.getName().trim());
	    category.setDescription(category.getDescription().trim());
	}



}
