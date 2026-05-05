package es.unex.cume.tw.resources;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import es.unex.cume.tw.dao.CategoryDAO;
import es.unex.cume.tw.dao.DishDAO;
import es.unex.cume.tw.dao.JDBCCategoryDAOImpl;
import es.unex.cume.tw.dao.JDBCDishDAOImpl;
import es.unex.cume.tw.dao.JDBCRestaurantDAOImpl;
import es.unex.cume.tw.dao.JDBCRestaurantCategoriesDAOImpl;
import es.unex.cume.tw.dao.JDBCReviewsDAOImpl;
import es.unex.cume.tw.dao.JDBCUserDAOImpl;
import es.unex.cume.tw.dao.RestaurantDAO;
import es.unex.cume.tw.dao.UserDAO;
import es.unex.cume.tw.dto.RestaurantGradeResponse;
import es.unex.cume.tw.dto.RestaurantRequest;
import es.unex.cume.tw.dto.RestaurantResponse;
import es.unex.cume.tw.dto.RestaurantSpendingResponse;
import es.unex.cume.tw.dto.UserRestaurantCountResponse;
import es.unex.cume.tw.dao.RestaurantCategoriesDAO;
import es.unex.cume.tw.dao.ReviewsDAO;
import es.unex.cume.tw.model.Category;
import es.unex.cume.tw.model.Dish;
import es.unex.cume.tw.model.Restaurant;
import es.unex.cume.tw.model.RestaurantCategories;
import es.unex.cume.tw.model.Review;
import es.unex.cume.tw.model.User;
import es.unex.cume.tw.util.Triplet;
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
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

@Path("/restaurantes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RestaurantsResource {

	@Context
	private ServletContext sc;

	@Context
	private UriInfo uriInfo;

	/**
	 * GET /api/restaurantes/all
	 */
	@GET
	@Path("/all")
	public List<RestaurantResponse> getAllRestaurants(@Context HttpServletRequest request, @QueryParam("q") String q,
			@QueryParam("localidad") String localidad, @QueryParam("estado") String estado,
			@QueryParam("sort") String sort, @QueryParam("categoria") Long categoria,
			@QueryParam("comentarios") String comentarios, @QueryParam("plato") String plato,
			@QueryParam("minPrecio") Integer minPrecio, @QueryParam("maxPrecio") Integer maxPrecio) {

		Utils.getLoggedUser(request);

		validatePriceSearchRange(minPrecio, maxPrecio);

		Connection conn = (Connection) sc.getAttribute("dbConn");
		RestaurantDAO restaurantDao = new JDBCRestaurantDAOImpl();
		restaurantDao.setConnection(conn);

		RestaurantCategoriesDAO restaurantCategoriesDao = new JDBCRestaurantCategoriesDAOImpl();
		restaurantCategoriesDao.setConnection(conn);

		CategoryDAO categoryDao = new JDBCCategoryDAOImpl();
		categoryDao.setConnection(conn);

		DishDAO dishDao = new JDBCDishDAOImpl();
		dishDao.setConnection(conn);

		ReviewsDAO reviewsDao = new JDBCReviewsDAOImpl();
		reviewsDao.setConnection(conn);

		List<Restaurant> restaurants = restaurantDao.getAll();
		List<Restaurant> filtered = new ArrayList<Restaurant>();

		// optional filters
		for (Restaurant restaurant : restaurants) {
			if (q != null && !q.trim().isEmpty()) {
				String search = q.trim().toLowerCase();
				boolean matches = false;

				if (restaurant.getName() != null && restaurant.getName().toLowerCase().contains(search)) {
					matches = true;
				}

				if (!matches) {
					continue;
				}
			}

			if (localidad != null && !localidad.trim().isEmpty()) {
				if (restaurant.getCity() == null
						|| !restaurant.getCity().toLowerCase().contains(localidad.trim().toLowerCase())) {
					continue;
				}
			}

			if ("acepta".equalsIgnoreCase(estado) && restaurant.getAvailable() != 1) {
				continue;
			}

			if ("no-acepta".equalsIgnoreCase(estado) && restaurant.getAvailable() != 0) {
				continue;
			}

			if (categoria != null
					&& !restaurantHasCategory(restaurant.getId(), categoria.longValue(), restaurantCategoriesDao)) {
				continue;
			}

			if (comentarios != null && !comentarios.trim().isEmpty()
					&& !restaurantHasReviewComment(restaurant.getId(), comentarios.trim(), reviewsDao)) {
				continue;
			}

			if (plato != null && !plato.trim().isEmpty()
					&& !restaurantOffersDish(restaurant.getId(), plato.trim(), dishDao)) {
				continue;
			}

			if (minPrecio != null && restaurant.getMinPrice() < minPrecio.intValue()) {
				continue;
			}

			if (maxPrecio != null && restaurant.getMaxPrice() > maxPrecio.intValue()) {
				continue;
			}

			filtered.add(restaurant);
		}

		if ("valoracionMedia".equalsIgnoreCase(sort)) {
			// best rated first
			filtered.sort((r1, r2) -> Integer.compare(r2.getGradesAverage(), r1.getGradesAverage()));
		}

		return buildRestaurantResponseList(filtered, restaurantCategoriesDao, categoryDao);
	}

	/**
	 * GET /api/restaurantes
	 */
	@GET
	public List<RestaurantResponse> getOwnRestaurants(@Context HttpServletRequest request) {
		User user = Utils.getLoggedUser(request);

		Connection conn = (Connection) sc.getAttribute("dbConn");
		RestaurantDAO restaurantDao = new JDBCRestaurantDAOImpl();
		restaurantDao.setConnection(conn);

		RestaurantCategoriesDAO restaurantCategoriesDao = new JDBCRestaurantCategoriesDAOImpl();
		restaurantCategoriesDao.setConnection(conn);

		CategoryDAO categoryDao = new JDBCCategoryDAOImpl();
		categoryDao.setConnection(conn);

		return buildRestaurantResponseList(restaurantDao.getAllByUser(user.getId()), restaurantCategoriesDao,
				categoryDao);
	}

	/**
	 * GET /api/restaurantes/count
	 */
	@GET
	@Path("/count")
	public List<UserRestaurantCountResponse> getRestaurantCountByUser(@Context HttpServletRequest request) {
		// Defensa Actividad 3 - Enunciado 1
		Utils.getLoggedUser(request);

		Connection conn = (Connection) sc.getAttribute("dbConn");

		UserDAO userDao = new JDBCUserDAOImpl();
		userDao.setConnection(conn);

		RestaurantDAO restaurantDao = new JDBCRestaurantDAOImpl();
		restaurantDao.setConnection(conn);

		List<UserRestaurantCountResponse> result = new ArrayList<UserRestaurantCountResponse>();
		List<User> users = userDao.getAll();

		for (User user : users) {
			UserRestaurantCountResponse response = new UserRestaurantCountResponse();

			response.setUserId(user.getId());
			response.setName(user.getName());
			response.setSurname(user.getSurname());
			response.setEmail(user.getEmail());
			response.setNumberOfRestaurants(restaurantDao.getAllByUser(user.getId()).size());

			result.add(response);
		}

		return result;
	}

	/**
	 * GET /api/restaurantes/bestlist
	 */
	@GET
	@Path("/bestlist")
	public List<RestaurantGradeResponse> getBestRatedRestaurants(@Context HttpServletRequest request) {
		// Defensa Actividad 3 - Enunciado 2
		Utils.getLoggedUser(request);

		Connection conn = (Connection) sc.getAttribute("dbConn");

		RestaurantDAO restaurantDao = new JDBCRestaurantDAOImpl();
		restaurantDao.setConnection(conn);

		List<Restaurant> restaurants = restaurantDao.getAll();

		Collections.sort(restaurants, new Comparator<Restaurant>() {
			@Override
			public int compare(Restaurant r1, Restaurant r2) {
				return Integer.compare(r2.getGradesAverage(), r1.getGradesAverage());
			}
		});

		List<RestaurantGradeResponse> result = new ArrayList<RestaurantGradeResponse>();

		for (Restaurant restaurant : restaurants) {
			RestaurantGradeResponse response = new RestaurantGradeResponse();

			response.setName(restaurant.getName());
			response.setGradesAverage(restaurant.getGradesAverage());

			result.add(response);
		}

		return result;
	}

	/**
	 * GET /api/restaurantes/spending
	 */
	@GET
	@Path("/spending")
	public List<RestaurantSpendingResponse> getRestaurantsByAverageSpending(@Context HttpServletRequest request) {
		// Defensa Actividad 3 - Enunciado 3
		Utils.getLoggedUser(request);

		Connection conn = (Connection) sc.getAttribute("dbConn");

		RestaurantDAO restaurantDao = new JDBCRestaurantDAOImpl();
		restaurantDao.setConnection(conn);

		List<Restaurant> restaurants = restaurantDao.getAll();

		Collections.sort(restaurants, new Comparator<Restaurant>() {
			@Override
			public int compare(Restaurant r1, Restaurant r2) {
				int spending1 = (r1.getMinPrice() + r1.getMaxPrice()) / 2;
				int spending2 = (r2.getMinPrice() + r2.getMaxPrice()) / 2;

				return Integer.compare(spending2, spending1);
			}
		});

		List<RestaurantSpendingResponse> result = new ArrayList<RestaurantSpendingResponse>();

		for (Restaurant restaurant : restaurants) {
			RestaurantSpendingResponse response = new RestaurantSpendingResponse();
			int averageSpending = (restaurant.getMinPrice() + restaurant.getMaxPrice()) / 2;

			response.setName(restaurant.getName());
			response.setAverageSpending(averageSpending);

			result.add(response);
		}

		return result;
	}

	/**
	 * GET /api/restaurantes/{id}
	 */
	@GET
	@Path("/{id: [0-9]+}")
	public RestaurantResponse getRestaurant(@PathParam("id") long id, @Context HttpServletRequest request) {
		Utils.getLoggedUser(request);

		Connection conn = (Connection) sc.getAttribute("dbConn");
		RestaurantDAO restaurantDao = new JDBCRestaurantDAOImpl();
		restaurantDao.setConnection(conn);

		RestaurantCategoriesDAO restaurantCategoriesDao = new JDBCRestaurantCategoriesDAOImpl();
		restaurantCategoriesDao.setConnection(conn);

		CategoryDAO categoryDao = new JDBCCategoryDAOImpl();
		categoryDao.setConnection(conn);

		Restaurant restaurant = restaurantDao.get(id);

		if (restaurant == null) {
			throw new CustomNotFoundException("Restaurante no encontrado");
		}

		return buildRestaurantResponse(restaurant, restaurantCategoriesDao, categoryDao);
	}

	/**
	 * GET /api/restaurantes/{id}/relacionados
	 */
	@GET
	@Path("/{id: [0-9]+}/relacionados")
	public List<RestaurantResponse> getRelatedRestaurants(@PathParam("id") long id,
			@Context HttpServletRequest request) {

		Utils.getLoggedUser(request);

		Connection conn = (Connection) sc.getAttribute("dbConn");
		RestaurantDAO restaurantDao = new JDBCRestaurantDAOImpl();
		restaurantDao.setConnection(conn);

		RestaurantCategoriesDAO restaurantCategoriesDao = new JDBCRestaurantCategoriesDAOImpl();
		restaurantCategoriesDao.setConnection(conn);

		CategoryDAO categoryDao = new JDBCCategoryDAOImpl();
		categoryDao.setConnection(conn);

		Restaurant restaurant = restaurantDao.get(id);

		if (restaurant == null) {
			throw new CustomNotFoundException("Restaurante no encontrado");
		}

		List<Category> categories = getRestaurantCategories(restaurant.getId(), restaurantCategoriesDao, categoryDao);
		List<Triplet<Restaurant, Integer, List<Category>>> relatedRestaurants = getRelatedRestaurants(restaurant,
				categories, restaurantDao, restaurantCategoriesDao, categoryDao);

		List<RestaurantResponse> responses = new ArrayList<RestaurantResponse>();

		for (Triplet<Restaurant, Integer, List<Category>> relatedRestaurant : relatedRestaurants) {
			RestaurantResponse response = new RestaurantResponse();
			response.setRestaurant(relatedRestaurant.getFirst());
			response.setCategories(relatedRestaurant.getThird());
			responses.add(response);
		}

		return responses;
	}

	/**
	 * POST /api/restaurantes
	 */
	@POST
	public Response createRestaurant(RestaurantRequest restaurantRequest, @Context HttpServletRequest request) {
		User user = Utils.getLoggedUser(request);

		if (restaurantRequest == null) {
			throw new CustomBadRequestException("El cuerpo es obligatorio");
		}

		Restaurant restaurant = restaurantRequest.getRestaurant();

		if (restaurant == null) {
			throw new CustomBadRequestException("El cuerpo es obligatorio");
		}

		restaurant.setId(0);
		restaurant.setIdu((int) user.getId());
		restaurant.setGradesAverage(0);

		validateRestaurant(restaurant);

		Connection conn = (Connection) sc.getAttribute("dbConn");
		RestaurantDAO restaurantDao = new JDBCRestaurantDAOImpl();
		restaurantDao.setConnection(conn);

		RestaurantCategoriesDAO restaurantCategoriesDao = new JDBCRestaurantCategoriesDAOImpl();
		restaurantCategoriesDao.setConnection(conn);

		CategoryDAO categoryDao = new JDBCCategoryDAOImpl();
		categoryDao.setConnection(conn);

		// validate category ids
		validateCategoryIds(restaurantRequest.getCategoryIds(), categoryDao);

		long id = restaurantDao.add(restaurant);

		if (id < 0) {
			throw new WebApplicationException("No se pudo crear el restaurante", Response.Status.INTERNAL_SERVER_ERROR);
		}

		saveRestaurantCategories(id, restaurantRequest.getCategoryIds(), restaurantCategoriesDao);

		Restaurant created = restaurantDao.get(id);

		return Response.created(uriInfo.getAbsolutePathBuilder().path(Long.toString(id)).build())
				.entity(buildRestaurantResponse(created, restaurantCategoriesDao, categoryDao)).build();
	}

	/*
	 * Preguntar por esto
	 *
	 * @POST
	 *
	 * @Consumes(MediaType.APPLICATION_FORM_URLENCODED) public Response
	 * createRestaurantFromForm(MultivaluedMap<String, String> formParams,
	 *
	 * @Context HttpServletRequest request) {
	 *
	 * User user = getLoggedUser(request);
	 *
	 * Restaurant restaurant = new Restaurant();
	 * restaurant.setName(formParams.getFirst("name"));
	 * restaurant.setAddress(formParams.getFirst("address"));
	 * restaurant.setTelephone(formParams.getFirst("telephone"));
	 * restaurant.setCity(formParams.getFirst("city"));
	 * restaurant.setContactEmail(formParams.getFirst("contactEmail"));
	 *
	 * try { String minPriceStr = formParams.getFirst("minPrice"); String
	 * maxPriceStr = formParams.getFirst("maxPrice"); String bikeFriendlyStr =
	 * formParams.getFirst("bikeFriendly"); String availableStr =
	 * formParams.getFirst("available"); String gradesAverageStr =
	 * formParams.getFirst("gradesAverage");
	 *
	 * restaurant.setMinPrice( minPriceStr != null && !minPriceStr.trim().isEmpty()
	 * ? Integer.parseInt(minPriceStr) : 0);
	 *
	 * restaurant.setMaxPrice( maxPriceStr != null && !maxPriceStr.trim().isEmpty()
	 * ? Integer.parseInt(maxPriceStr) : 0);
	 *
	 * restaurant.setBikeFriendly( bikeFriendlyStr != null &&
	 * !bikeFriendlyStr.trim().isEmpty() ? Integer.parseInt(bikeFriendlyStr) : 0);
	 *
	 * restaurant.setAvailable( availableStr != null &&
	 * !availableStr.trim().isEmpty() ? Integer.parseInt(availableStr) : 0);
	 *
	 * restaurant.setGradesAverage( gradesAverageStr != null &&
	 * !gradesAverageStr.trim().isEmpty() ? Integer.parseInt(gradesAverageStr) : 0);
	 *
	 * } catch (NumberFormatException e) { throw new
	 * CustomBadRequestException("Error en los parámetros numéricos del formulario"
	 * ); }
	 *
	 * restaurant.setId(0); restaurant.setIdu((int) user.getId());
	 *
	 * validateRestaurant(restaurant);
	 *
	 * Connection conn = (Connection) sc.getAttribute("dbConn"); RestaurantDAO
	 * restaurantDao = new JDBCRestaurantDAOImpl();
	 * restaurantDao.setConnection(conn);
	 *
	 * long newId = restaurantDao.add(restaurant);
	 *
	 * if (newId < 0) { throw new
	 * WebApplicationException("No se pudo crear el restaurante",
	 * Response.Status.INTERNAL_SERVER_ERROR); }
	 *
	 * Restaurant created = restaurantDao.get(newId);
	 *
	 * return
	 * Response.created(uriInfo.getAbsolutePathBuilder().path(Long.toString(newId)).
	 * build()).entity(created) .build(); }
	 */

	/**
	 * PUT /api/restaurantes/{id}
	 */
	@PUT
	@Path("/{restaurantId: [0-9]+}")
	public Response updateRestaurant(@PathParam("restaurantId") long restaurantId, RestaurantRequest restaurantRequest,
			@Context HttpServletRequest request) {

		User user = Utils.getLoggedUser(request);

		Connection conn = (Connection) sc.getAttribute("dbConn");
		RestaurantDAO restaurantDao = new JDBCRestaurantDAOImpl();
		restaurantDao.setConnection(conn);

		RestaurantCategoriesDAO restaurantCategoriesDao = new JDBCRestaurantCategoriesDAOImpl();
		restaurantCategoriesDao.setConnection(conn);

		CategoryDAO categoryDao = new JDBCCategoryDAOImpl();
		categoryDao.setConnection(conn);

		Restaurant currentRestaurant = restaurantDao.get(restaurantId);

		if (currentRestaurant == null) {
			throw new CustomNotFoundException("Restaurante no encontrado");
		}

		if (currentRestaurant.getIdu() != user.getId()) {
			throw new WebApplicationException("No autorizado", Response.Status.FORBIDDEN);
		}

		if (restaurantRequest == null) {
			throw new CustomBadRequestException("El cuerpo es obligatorio");
		}

		Restaurant restaurant = restaurantRequest.getRestaurant();

		if (restaurant == null) {
			throw new CustomBadRequestException("El cuerpo es obligatorio");
		}

		if (restaurant.getId() != 0 && restaurant.getId() != restaurantId) {
			throw new CustomBadRequestException("El id del cuerpo no coincide con el de la URL");
		}

		restaurant.setId(restaurantId);
		restaurant.setIdu(currentRestaurant.getIdu());
		// keep review average
		restaurant.setGradesAverage(currentRestaurant.getGradesAverage());

		validateRestaurant(restaurant);
		validateCategoryIds(restaurantRequest.getCategoryIds(), categoryDao);

		boolean updated = restaurantDao.update(restaurant);

		if (!updated) {
			throw new WebApplicationException("No se pudo actualizar el restaurante",
					Response.Status.INTERNAL_SERVER_ERROR);
		}

		// update categories if sent
		replaceRestaurantCategories(restaurantId, restaurantRequest.getCategoryIds(), restaurantCategoriesDao,
				categoryDao);

		Restaurant updatedRestaurant = restaurantDao.get(restaurantId);

		return Response.ok(buildRestaurantResponse(updatedRestaurant, restaurantCategoriesDao, categoryDao)).build();
	}

	/**
	 * PUT /api/restaurantes/{id}/estado
	 */
	@PUT
	@Path("/{id: [0-9]+}/estado")
	public Restaurant updateRestaurantStatus(@PathParam("id") long id, Restaurant restaurant,
			@Context HttpServletRequest request) {

		User user = Utils.getLoggedUser(request);

		Connection conn = (Connection) sc.getAttribute("dbConn");
		RestaurantDAO restaurantDao = new JDBCRestaurantDAOImpl();
		restaurantDao.setConnection(conn);

		Restaurant currentRestaurant = restaurantDao.get(id);

		if (currentRestaurant == null) {
			throw new CustomNotFoundException("Restaurante no encontrado");
		}

		if (currentRestaurant.getIdu() != user.getId()) {
			throw new WebApplicationException("No autorizado", Response.Status.FORBIDDEN);
		}

		if (restaurant == null || (restaurant.getAvailable() != 0 && restaurant.getAvailable() != 1)) {
			throw new CustomBadRequestException("Estado no válido");
		}

		currentRestaurant.setAvailable(restaurant.getAvailable());

		if (!restaurantDao.update(currentRestaurant)) {
			throw new WebApplicationException("No se pudo actualizar el estado", Response.Status.INTERNAL_SERVER_ERROR);
		}

		return restaurantDao.get(id);
	}

	/**
	 * DELETE /api/restaurantes/{id}
	 */
	@DELETE
	@Path("/{id: [0-9]+}")
	public Response deleteRestaurant(@PathParam("id") long id, @Context HttpServletRequest request) {
		User user = Utils.getLoggedUser(request);

		Connection conn = (Connection) sc.getAttribute("dbConn");
		RestaurantDAO restaurantDao = new JDBCRestaurantDAOImpl();
		restaurantDao.setConnection(conn);

		Restaurant restaurant = restaurantDao.get(id);

		if (restaurant == null) {
			throw new CustomNotFoundException("Restaurante no encontrado");
		}

		if (restaurant.getIdu() != user.getId()) {
			throw new WebApplicationException("No autorizado", Response.Status.FORBIDDEN);
		}

		if (!restaurantDao.delete(id)) {
			throw new WebApplicationException("No se pudo eliminar el restaurante",
					Response.Status.INTERNAL_SERVER_ERROR);
		}

		return Response.noContent().build();
	}

	private void replaceRestaurantCategories(long restaurantId, List<Long> categoryIds,
			RestaurantCategoriesDAO restaurantCategoriesDao, CategoryDAO categoryDao) {

		if (categoryIds == null) {
			return;
		}

		List<RestaurantCategories> currentCategories = restaurantCategoriesDao.getAllByRestaurant(restaurantId);

		if (currentCategories != null) {
			for (RestaurantCategories rc : currentCategories) {
				restaurantCategoriesDao.delete(rc.getIdr(), rc.getIdct());
			}
		}

		for (Long categoryId : categoryIds) {
			if (categoryId == null) {
				continue;
			}

			// skip nulls and duplicates
			if (restaurantCategoriesDao.get(restaurantId, categoryId.longValue()) != null) {
				continue;
			}

			RestaurantCategories rc = new RestaurantCategories();
			rc.setIdr(restaurantId);
			rc.setIdct(categoryId.longValue());

			if (!restaurantCategoriesDao.add(rc)) {
				throw new WebApplicationException("No se pudieron actualizar las categorías",
						Response.Status.INTERNAL_SERVER_ERROR);
			}
		}
	}

	private void saveRestaurantCategories(long restaurantId, List<Long> categoryIds,
			RestaurantCategoriesDAO restaurantCategoriesDao) {

		if (categoryIds == null) {
			return;
		}

		for (Long categoryId : categoryIds) {
			if (categoryId == null) {
				continue;
			}

			if (restaurantCategoriesDao.get(restaurantId, categoryId.longValue()) != null) {
				continue;
			}

			RestaurantCategories rc = new RestaurantCategories();
			rc.setIdr(restaurantId);
			rc.setIdct(categoryId.longValue());

			if (!restaurantCategoriesDao.add(rc)) {
				throw new WebApplicationException("No se pudieron actualizar las categorías",
						Response.Status.INTERNAL_SERVER_ERROR);
			}
		}
	}

	private void validateCategoryIds(List<Long> categoryIds, CategoryDAO categoryDao) {
		if (categoryIds == null) {
			return;
		}

		for (Long categoryId : categoryIds) {
			if (categoryId == null) {
				continue;
			}

			if (categoryDao.get(categoryId.longValue()) == null) {
				throw new CustomNotFoundException("Categoría no encontrada: " + categoryId);
			}
		}
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

	private List<RestaurantResponse> buildRestaurantResponseList(List<Restaurant> restaurants,
			RestaurantCategoriesDAO restaurantCategoriesDao, CategoryDAO categoryDao) {

		List<RestaurantResponse> responses = new ArrayList<RestaurantResponse>();

		if (restaurants == null) {
			return responses;
		}

		for (Restaurant restaurant : restaurants) {
			responses.add(buildRestaurantResponse(restaurant, restaurantCategoriesDao, categoryDao));
		}

		return responses;
	}

	private List<Category> getRestaurantCategories(long restaurantId, RestaurantCategoriesDAO restaurantCategoriesDao,
			CategoryDAO categoryDao) {

		List<Category> categories = new ArrayList<Category>();
		List<RestaurantCategories> relations = restaurantCategoriesDao.getAllByRestaurant(restaurantId);

		if (relations == null) {
			return categories;
		}

		for (RestaurantCategories rc : relations) {
			Category category = categoryDao.get(rc.getIdct());
			if (category != null) {
				categories.add(category);
			}
		}

		return categories;
	}

	private List<Triplet<Restaurant, Integer, List<Category>>> getRelatedRestaurants(Restaurant restaurant,
			List<Category> categories, RestaurantDAO restaurantDao, RestaurantCategoriesDAO restaurantCategoriesDao,
			CategoryDAO categoryDao) {

		List<Restaurant> allRestaurants = restaurantDao.getAll();

		List<Triplet<Restaurant, Integer, List<Category>>> relatedScored = new ArrayList<Triplet<Restaurant, Integer, List<Category>>>();

		for (Restaurant candidate : allRestaurants) {
			if (candidate.getId() == restaurant.getId()) {
				continue;
			}

			int score = 0;

			// related score
			if (candidate.getCity() != null && restaurant.getCity() != null
					&& candidate.getCity().equalsIgnoreCase(restaurant.getCity())) {
				score = score + 5;
			}

			List<Category> candidateCategories = getRestaurantCategories(candidate.getId(), restaurantCategoriesDao,
					categoryDao);

			for (Category currentCategory : categories) {
				for (Category candidateCategory : candidateCategories) {
					if (currentCategory.getId() == candidateCategory.getId()) {
						score = score + 2;
					}
				}
			}

			int minDiff = Math.abs(candidate.getMinPrice() - restaurant.getMinPrice());
			int maxDiff = Math.abs(candidate.getMaxPrice() - restaurant.getMaxPrice());
			int diff = minDiff + maxDiff;

			if (diff < 5) {
				if (diff == 0) {
					score = score + 5;
				} else {
					score = score + 3;
				}
			}

			if (score > 0) {
				relatedScored
						.add(new Triplet<Restaurant, Integer, List<Category>>(candidate, score, candidateCategories));
			}
		}

		Collections.sort(relatedScored, new Comparator<Triplet<Restaurant, Integer, List<Category>>>() {
			@Override
			public int compare(Triplet<Restaurant, Integer, List<Category>> o1,
					Triplet<Restaurant, Integer, List<Category>> o2) {
				return Integer.compare(o2.getSecond(), o1.getSecond());
			}
		});

		List<Triplet<Restaurant, Integer, List<Category>>> relatedRestaurants = new ArrayList<Triplet<Restaurant, Integer, List<Category>>>();

		int maxRelatedRestaurants = relatedScored.size();
		if (maxRelatedRestaurants > 4) {
			maxRelatedRestaurants = 4;
		}

		for (int i = 0; i < maxRelatedRestaurants; i++) {
			relatedRestaurants.add(relatedScored.get(i));
		}

		return relatedRestaurants;
	}

	private boolean restaurantHasCategory(long restaurantId, long categoryId,
			RestaurantCategoriesDAO restaurantCategoriesDao) {

		List<RestaurantCategories> relations = restaurantCategoriesDao.getAllByRestaurant(restaurantId);

		if (relations == null) {
			return false;
		}

		for (RestaurantCategories relation : relations) {
			if (relation.getIdct() == categoryId) {
				return true;
			}
		}

		return false;
	}

	private boolean restaurantHasReviewComment(long restaurantId, String comments, ReviewsDAO reviewsDao) {
		String search = comments.toLowerCase();
		List<Review> reviews = reviewsDao.getAllByRestaurant(restaurantId);

		if (reviews == null) {
			return false;
		}

		for (Review review : reviews) {
			if (review.getReview() != null && review.getReview().toLowerCase().contains(search)) {
				return true;
			}
		}

		return false;
	}

	private boolean restaurantOffersDish(long restaurantId, String dishName, DishDAO dishDao) {
		String search = dishName.toLowerCase();
		List<Dish> dishes = dishDao.getAll();

		if (dishes == null) {
			return false;
		}

		for (Dish dish : dishes) {
			if (dish.getIdr() == restaurantId && dish.getName() != null
					&& dish.getName().toLowerCase().contains(search)) {
				return true;
			}
		}

		return false;
	}

	private void validatePriceSearchRange(Integer minPrecio, Integer maxPrecio) {
		if (minPrecio != null && minPrecio.intValue() < 0) {
			throw new CustomBadRequestException("minPrecio debe ser mayor o igual que 0");
		}

		if (maxPrecio != null && maxPrecio.intValue() < 0) {
			throw new CustomBadRequestException("maxPrecio debe ser mayor o igual que 0");
		}

		if (minPrecio != null && maxPrecio != null && maxPrecio.intValue() < minPrecio.intValue()) {
			throw new CustomBadRequestException("maxPrecio debe ser mayor o igual que minPrecio");
		}
	}

	private void validateRestaurant(Restaurant restaurant) {
		if (restaurant == null) {
			throw new CustomBadRequestException("El restaurante no puede ser nulo");
		}

		if (restaurant.getName() == null || restaurant.getName().trim().isEmpty()) {
			throw new CustomBadRequestException("El nombre es obligatorio");
		}

		if (restaurant.getAddress() == null || restaurant.getAddress().trim().isEmpty()) {
			throw new CustomBadRequestException("La dirección es obligatoria");
		}

		if (restaurant.getTelephone() == null || restaurant.getTelephone().trim().isEmpty()) {
			throw new CustomBadRequestException("El teléfono es obligatorio");
		}

		if (restaurant.getCity() == null || restaurant.getCity().trim().isEmpty()) {
			throw new CustomBadRequestException("La ciudad es obligatoria");
		}

		if (restaurant.getContactEmail() == null || restaurant.getContactEmail().trim().isEmpty()) {
			throw new CustomBadRequestException("El email de contacto es obligatorio");
		}

		if (!restaurant.getContactEmail().contains("@")) {
			throw new CustomBadRequestException("El email de contacto no es válido");
		}

		if (restaurant.getMinPrice() < 0) {
			throw new CustomBadRequestException("El precio mínimo debe ser mayor o igual que 0");
		}

		if (restaurant.getMaxPrice() < 0) {
			throw new CustomBadRequestException("El precio máximo debe ser mayor o igual que 0");
		}

		if (restaurant.getMaxPrice() < restaurant.getMinPrice()) {
			throw new CustomBadRequestException("El precio máximo debe ser mayor o igual que el mínimo");
		}

		if (restaurant.getBikeFriendly() != 0 && restaurant.getBikeFriendly() != 1) {
			throw new CustomBadRequestException("bikeFriendly debe valer 0 o 1");
		}

		if (restaurant.getAvailable() != 0 && restaurant.getAvailable() != 1) {
			throw new CustomBadRequestException("available debe valer 0 o 1");
		}

		if (restaurant.getGradesAverage() < 0) {
			restaurant.setGradesAverage(0);
		}
	}
}
