package es.unex.cume.tw.resources;

import java.sql.Connection;
import java.util.List;

import es.unex.cume.tw.dao.JDBCRestaurantDAOImpl;
import es.unex.cume.tw.dao.JDBCRestaurantSocialLinksDAOImpl;
import es.unex.cume.tw.dao.RestaurantDAO;
import es.unex.cume.tw.dao.RestaurantSocialLinksDAO;
import es.unex.cume.tw.model.Restaurant;
import es.unex.cume.tw.model.RestaurantSocialLink;
import es.unex.cume.tw.model.User;
import es.unex.cume.tw.util.Utils;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
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

@Path("/restaurantes/{id: [0-9]+}/redes")
@Produces(MediaType.APPLICATION_JSON)
public class RestaurantSocialLinksResource {

	@Context
	private ServletContext sc;

	@Context
	private UriInfo uriInfo;

	/**
	 * GET /api/restaurantes/{id}/redes
	 */
	@GET
	public List<RestaurantSocialLink> getRestaurantSocialLinks(@PathParam("id") long restaurantId,
			@Context HttpServletRequest request) {

		Utils.getLoggedUser(request);

		Connection conn = (Connection) sc.getAttribute("dbConn");

		RestaurantDAO restaurantDao = new JDBCRestaurantDAOImpl();
		restaurantDao.setConnection(conn);

		RestaurantSocialLinksDAO socialLinksDao = new JDBCRestaurantSocialLinksDAOImpl();
		socialLinksDao.setConnection(conn);

		Restaurant restaurant = restaurantDao.get(restaurantId);

		if (restaurant == null) {
			throw new CustomNotFoundException("Restaurante no encontrado");
		}

		return socialLinksDao.getAllByRestaurant(restaurantId);
	}

	/**
	 * POST /api/restaurantes/{id}/redes
	 */
	@POST
	public Response addRestaurantSocialLink(@PathParam("id") long restaurantId, RestaurantSocialLink socialLink,
			@Context HttpServletRequest request) {

		User user = Utils.getLoggedUser(request);

		Connection conn = (Connection) sc.getAttribute("dbConn");

		RestaurantDAO restaurantDao = new JDBCRestaurantDAOImpl();
		restaurantDao.setConnection(conn);

		RestaurantSocialLinksDAO socialLinksDao = new JDBCRestaurantSocialLinksDAOImpl();
		socialLinksDao.setConnection(conn);

		Restaurant restaurant = restaurantDao.get(restaurantId);

		if (restaurant == null) {
			throw new CustomNotFoundException("Restaurante no encontrado");
		}

		if (restaurant.getIdu() != user.getId()) {
			throw new WebApplicationException("No autorizado", Response.Status.FORBIDDEN);
		}

		validateSocialLink(socialLink);

		socialLink.setId(0);
		socialLink.setIdr(restaurantId);
		socialLink.setPlatform(socialLink.getPlatform().trim().toLowerCase());
		socialLink.setUrl(socialLink.getUrl().trim());

		// avoid repeated platform
		if (socialLinksDao.get(restaurantId, socialLink.getPlatform()) != null) {
			throw new WebApplicationException("El restaurante ya tiene esa red social", Response.Status.CONFLICT);
		}

		long id = socialLinksDao.add(socialLink);

		if (id < 0) {
			throw new WebApplicationException("No se pudo crear la red social",
					Response.Status.INTERNAL_SERVER_ERROR);
		}

		return Response.created(uriInfo.getAbsolutePathBuilder().path(Long.toString(id)).build())
				.entity(socialLinksDao.get(id)).build();
	}

	/**
	 * PUT /api/restaurantes/{id}/redes/{socialId}
	 */
	@PUT
	@Path("/{socialId: [0-9]+}")
	public RestaurantSocialLink updateRestaurantSocialLink(@PathParam("id") long restaurantId,
			@PathParam("socialId") long socialId, RestaurantSocialLink socialLink,
			@Context HttpServletRequest request) {

		User user = Utils.getLoggedUser(request);

		Connection conn = (Connection) sc.getAttribute("dbConn");

		RestaurantDAO restaurantDao = new JDBCRestaurantDAOImpl();
		restaurantDao.setConnection(conn);

		RestaurantSocialLinksDAO socialLinksDao = new JDBCRestaurantSocialLinksDAOImpl();
		socialLinksDao.setConnection(conn);

		Restaurant restaurant = restaurantDao.get(restaurantId);

		if (restaurant == null) {
			throw new CustomNotFoundException("Restaurante no encontrado");
		}

		if (restaurant.getIdu() != user.getId()) {
			throw new WebApplicationException("No autorizado", Response.Status.FORBIDDEN);
		}

		RestaurantSocialLink currentSocialLink = socialLinksDao.get(socialId);

		if (currentSocialLink == null || currentSocialLink.getIdr() != restaurantId) {
			throw new CustomNotFoundException("Red social no encontrada");
		}

		validateSocialLink(socialLink);

		String platform = socialLink.getPlatform().trim().toLowerCase();
		RestaurantSocialLink existingPlatform = socialLinksDao.get(restaurantId, platform);

		// ignore current link
		if (existingPlatform != null && existingPlatform.getId() != socialId) {
			throw new WebApplicationException("El restaurante ya tiene esa red social", Response.Status.CONFLICT);
		}

		currentSocialLink.setPlatform(platform);
		currentSocialLink.setUrl(socialLink.getUrl().trim());

		if (!socialLinksDao.update(currentSocialLink)) {
			throw new WebApplicationException("No se pudo actualizar la red social",
					Response.Status.INTERNAL_SERVER_ERROR);
		}

		return socialLinksDao.get(socialId);
	}

	/**
	 * DELETE /api/restaurantes/{id}/redes/{socialId}
	 */
	@DELETE
	@Path("/{socialId: [0-9]+}")
	public Response deleteRestaurantSocialLink(@PathParam("id") long restaurantId,
			@PathParam("socialId") long socialId, @Context HttpServletRequest request) {

		User user = Utils.getLoggedUser(request);

		Connection conn = (Connection) sc.getAttribute("dbConn");

		RestaurantDAO restaurantDao = new JDBCRestaurantDAOImpl();
		restaurantDao.setConnection(conn);

		RestaurantSocialLinksDAO socialLinksDao = new JDBCRestaurantSocialLinksDAOImpl();
		socialLinksDao.setConnection(conn);

		Restaurant restaurant = restaurantDao.get(restaurantId);

		if (restaurant == null) {
			throw new CustomNotFoundException("Restaurante no encontrado");
		}

		if (restaurant.getIdu() != user.getId()) {
			throw new WebApplicationException("No autorizado", Response.Status.FORBIDDEN);
		}

		RestaurantSocialLink socialLink = socialLinksDao.get(socialId);

		if (socialLink == null || socialLink.getIdr() != restaurantId) {
			throw new CustomNotFoundException("Red social no encontrada");
		}

		if (!socialLinksDao.delete(socialId)) {
			throw new WebApplicationException("No se pudo eliminar la red social",
					Response.Status.INTERNAL_SERVER_ERROR);
		}

		return Response.noContent().build();
	}

	private void validateSocialLink(RestaurantSocialLink socialLink) {
		if (socialLink == null) {
			throw new CustomBadRequestException("El cuerpo es obligatorio");
		}

		if (socialLink.getPlatform() == null || socialLink.getPlatform().trim().isEmpty()) {
			throw new CustomBadRequestException("La red social es obligatoria");
		}

		String platform = socialLink.getPlatform().trim().toLowerCase();

		if (!"instagram".equals(platform) && !"x".equals(platform) && !"facebook".equals(platform)) {
			throw new CustomBadRequestException("La red social debe ser instagram, x o facebook");
		}

		if (socialLink.getUrl() == null || socialLink.getUrl().trim().isEmpty()) {
			throw new CustomBadRequestException("La URL es obligatoria");
		}

		String url = socialLink.getUrl().trim().toLowerCase();

		if (!url.startsWith("http://") && !url.startsWith("https://")) {
			throw new CustomBadRequestException("La URL debe empezar por http:// o https://");
		}
	}
}
