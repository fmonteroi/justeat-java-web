package es.unex.cume.tw.resources;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import es.unex.cume.tw.dao.JDBCRestaurantDAOImpl;
import es.unex.cume.tw.dao.RestaurantDAO;
import es.unex.cume.tw.model.Restaurant;
import es.unex.cume.tw.model.User;
import es.unex.cume.tw.resources.CustomBadRequestException;
import es.unex.cume.tw.resources.CustomNotFoundException;
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
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.core.MultivaluedMap;

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
	 * GET /api/restaurantes/all?q=...
	 * GET /api/restaurantes/all?localidad=...
	 * GET /api/restaurantes/all?estado=acepta
	 * GET /api/restaurantes/all?estado=no-acepta
	 * GET /api/restaurantes/all?sort=valoracionMedia
	 * 
	 * @param request
	 * @return
	 */
	@GET
	@Path("/all")
	public List<Restaurant> getAllRestaurants(@Context HttpServletRequest request, @QueryParam("q") String q,
			@QueryParam("localidad") String localidad, @QueryParam("estado") String estado,
			@QueryParam("sort") String sort) {

		Utils.getLoggedUser(request);

		Connection conn = (Connection) sc.getAttribute("dbConn");
		RestaurantDAO restaurantDao = new JDBCRestaurantDAOImpl();
		restaurantDao.setConnection(conn);

		List<Restaurant> restaurants = restaurantDao.getAll();
		List<Restaurant> filtered = new ArrayList<Restaurant>();

		for (Restaurant restaurant : restaurants) {
			// Filter by name or city
			if (q != null && !q.trim().isEmpty()) {
				String search = q.trim().toLowerCase();
				boolean matches = false;

				if (restaurant.getName() != null && restaurant.getName().toLowerCase().contains(search)) {
					matches = true;
				}

				if (restaurant.getCity() != null && restaurant.getCity().toLowerCase().contains(search)) {
					matches = true;
				}

				if (!matches) {
					continue;
				}
			}

			// Filter by city
			if (localidad != null && !localidad.trim().isEmpty()) {
				if (restaurant.getCity() == null
						|| !restaurant.getCity().toLowerCase().contains(localidad.trim().toLowerCase())) {
					continue;
				}
			}

			// Filter available restaurants
			if ("acepta".equalsIgnoreCase(estado) && restaurant.getAvailable() != 1) {
				continue;
			}

			// Filter unavailable restaurants
			if ("no-acepta".equalsIgnoreCase(estado) && restaurant.getAvailable() != 0) {
				continue;
			}

			filtered.add(restaurant);
		}

		// Sort by average grade descending
		if ("valoracionMedia".equalsIgnoreCase(sort)) {
			filtered.sort((r1, r2) -> Integer.compare(r2.getGradesAverage(), r1.getGradesAverage()));
		}

		return filtered;
	}

	/**
	 * GET /api/restaurantes
	 * 
	 * @param request
	 * @return
	 */
	@GET
	public List<Restaurant> getOwnRestaurants(@Context HttpServletRequest request) {
		User user = Utils.getLoggedUser(request);

		Connection conn = (Connection) sc.getAttribute("dbConn");
		RestaurantDAO restaurantDao = new JDBCRestaurantDAOImpl();
		restaurantDao.setConnection(conn);

		// Return restaurants owned by logged user
		return restaurantDao.getAllByUser(user.getId());
	}

	/**
	 * GET /api/restaurantes/{id}
	 * 
	 * @param id
	 * @param request
	 * @return
	 */
	@GET
	@Path("/{id: [0-9]+}")
	public Restaurant getRestaurant(@PathParam("id") long id, @Context HttpServletRequest request) {
		Utils.getLoggedUser(request);

		Connection conn = (Connection) sc.getAttribute("dbConn");
		RestaurantDAO restaurantDao = new JDBCRestaurantDAOImpl();
		restaurantDao.setConnection(conn);

		Restaurant restaurant = restaurantDao.get(id);

		if (restaurant == null) {
			throw new WebApplicationException("Restaurante no encontrado", Response.Status.NOT_FOUND);
		}

		return restaurant;
	}

	/**
	 * POST /api/restaurantes
	 * 
	 * @param restaurant
	 * @param request
	 * @return
	 */
	@POST
	public Response createRestaurant(Restaurant restaurant, @Context HttpServletRequest request) {
		User user = Utils.getLoggedUser(request);
		
		if (restaurant == null) {
		    throw new WebApplicationException("El cuerpo es obligatorio", Response.Status.BAD_REQUEST);
		}

		// Force server-controlled fields
		restaurant.setId(0);
		restaurant.setIdu((int) user.getId());
		restaurant.setGradesAverage(0);

		validateRestaurant(restaurant);

		Connection conn = (Connection) sc.getAttribute("dbConn");
		RestaurantDAO restaurantDao = new JDBCRestaurantDAOImpl();
		restaurantDao.setConnection(conn);

		long id = restaurantDao.add(restaurant);

		if (id < 0) {
			throw new WebApplicationException("No se pudo crear el restaurante", Response.Status.INTERNAL_SERVER_ERROR);
		}

		return Response.created(uriInfo.getAbsolutePathBuilder().path(Long.toString(id)).build())
				.entity(restaurantDao.get(id)).build();
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
	 * 
	 * @param id
	 * @param restaurant
	 * @param request
	 * @return
	 */
	// PUT /api/restaurantes/{id}
	@PUT
	@Path("/{restaurantId: [0-9]+}")
	public Response updateRestaurant(@PathParam("restaurantId") long restaurantId, Restaurant restaurant,
			@Context HttpServletRequest request) {

		User user = Utils.getLoggedUser(request);

		Connection conn = (Connection) sc.getAttribute("dbConn");
		RestaurantDAO restaurantDao = new JDBCRestaurantDAOImpl();
		restaurantDao.setConnection(conn);

		Restaurant currentRestaurant = restaurantDao.get(restaurantId);

		if (currentRestaurant == null) {
			throw new WebApplicationException("Restaurante no encontrado", Response.Status.NOT_FOUND);
		}

		// Check restaurant ownership
		if (currentRestaurant.getIdu() != user.getId()) {
			throw new WebApplicationException("No autorizado", Response.Status.FORBIDDEN);
		}
		
		if (restaurant == null) {
		    throw new WebApplicationException("El cuerpo es obligatorio", Response.Status.BAD_REQUEST);
		}

		// Check URL id and body id match
		if (restaurant.getId() != 0 && restaurant.getId() != restaurantId) {
			throw new WebApplicationException("El id del cuerpo no coincide con el de la URL",
					Response.Status.BAD_REQUEST);
		}

		// Force server-controlled fields
		restaurant.setId(restaurantId);
		restaurant.setIdu(currentRestaurant.getIdu());
		restaurant.setGradesAverage(currentRestaurant.getGradesAverage());

		// Validate restaurant input
		validateRestaurant(restaurant);

		boolean updated = restaurantDao.update(restaurant);

		if (!updated) {
			throw new WebApplicationException("No se pudo actualizar el restaurante",
					Response.Status.INTERNAL_SERVER_ERROR);
		}

		return Response.ok(restaurantDao.get(restaurantId)).build();
	}
	
	/**
	 * PUT /api/restaurantes/{id}/estado
	 * 
	 * @param id
	 * @param restaurant
	 * @param request
	 * @return
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
	        throw new WebApplicationException("Restaurante no encontrado", Response.Status.NOT_FOUND);
	    }

	    // Check restaurant ownership
	    if (currentRestaurant.getIdu() != user.getId()) {
	        throw new WebApplicationException("No autorizado", Response.Status.FORBIDDEN);
	    }

	    // Check valid availability value
	    if (restaurant == null || (restaurant.getAvailable() != 0 && restaurant.getAvailable() != 1)) {
	        throw new WebApplicationException("Estado no válido", Response.Status.BAD_REQUEST);
	    }

	    // Update only availability
	    currentRestaurant.setAvailable(restaurant.getAvailable());

	    if (!restaurantDao.update(currentRestaurant)) {
	        throw new WebApplicationException("No se pudo actualizar el estado", Response.Status.INTERNAL_SERVER_ERROR);
	    }

	    return restaurantDao.get(id);
	}

	/**
	 * DELETE /api/restaurantes/{id}
	 * 
	 * @param id
	 * @param request
	 * @return
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
			throw new WebApplicationException("Restaurante no encontrado", Response.Status.NOT_FOUND);
		}

		// Check restaurant ownership
		if (restaurant.getIdu() != user.getId()) {
			throw new WebApplicationException("No autorizado", Response.Status.FORBIDDEN);
		}

		if (!restaurantDao.delete(id)) {
			throw new WebApplicationException("No se pudo eliminar el restaurante",
					Response.Status.INTERNAL_SERVER_ERROR);
		}

		return Response.noContent().build();
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