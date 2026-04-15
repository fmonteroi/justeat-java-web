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

@Path("/restaurants")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RestaurantsResource {

    @Context
    private ServletContext sc;

    @Context
    private UriInfo uriInfo;

    @GET
    public List<Restaurant> getRestaurants(@Context HttpServletRequest request,
                                           @QueryParam("search") String search) {

        User user = getLoggedUser(request);

        Connection conn = (Connection) sc.getAttribute("dbConn");
        RestaurantDAO restaurantDao = new JDBCRestaurantDAOImpl();
        restaurantDao.setConnection(conn);

        if (search != null && !search.trim().isEmpty()) {
            List<Restaurant> restaurants = restaurantDao.getAllBySearchName(search.trim());
            return filterByOwner(restaurants, user.getId());
        }

        return restaurantDao.getAllByUser(user.getId());
    }

    @GET
    @Path("/{restaurantId: [0-9]+}")
    public Restaurant getRestaurant(@PathParam("restaurantId") long restaurantId,
                                    @Context HttpServletRequest request) {

        User user = getLoggedUser(request);

        Connection conn = (Connection) sc.getAttribute("dbConn");
        RestaurantDAO restaurantDao = new JDBCRestaurantDAOImpl();
        restaurantDao.setConnection(conn);

        Restaurant restaurant = restaurantDao.get(restaurantId);

        if (restaurant == null) {
            throw new CustomNotFoundException("Restaurante no encontrado");
        }

        if (!isOwner(user, restaurant)) {
            throw new WebApplicationException("No autorizado", Response.Status.FORBIDDEN);
        }

        return restaurant;
    }

    @POST
    public Response createRestaurant(Restaurant restaurant,
                                     @Context HttpServletRequest request) {

        User user = getLoggedUser(request);
        validateRestaurant(restaurant);

        restaurant.setId(0);
        restaurant.setIdu((int) user.getId());

        Connection conn = (Connection) sc.getAttribute("dbConn");
        RestaurantDAO restaurantDao = new JDBCRestaurantDAOImpl();
        restaurantDao.setConnection(conn);

        long newId = restaurantDao.add(restaurant);

        if (newId < 0) {
            throw new WebApplicationException("No se pudo crear el restaurante",
                    Response.Status.INTERNAL_SERVER_ERROR);
        }

        Restaurant created = restaurantDao.get(newId);

        return Response.created(
                uriInfo.getAbsolutePathBuilder()
                       .path(Long.toString(newId))
                       .build())
                .entity(created)
                .build();
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response createRestaurantFromForm(MultivaluedMap<String, String> formParams,
                                             @Context HttpServletRequest request) {

        User user = getLoggedUser(request);

        Restaurant restaurant = new Restaurant();
        restaurant.setName(formParams.getFirst("name"));
        restaurant.setAddress(formParams.getFirst("address"));
        restaurant.setTelephone(formParams.getFirst("telephone"));
        restaurant.setCity(formParams.getFirst("city"));
        restaurant.setContactEmail(formParams.getFirst("contactEmail"));

        try {
            String minPriceStr = formParams.getFirst("minPrice");
            String maxPriceStr = formParams.getFirst("maxPrice");
            String bikeFriendlyStr = formParams.getFirst("bikeFriendly");
            String availableStr = formParams.getFirst("available");
            String gradesAverageStr = formParams.getFirst("gradesAverage");

            restaurant.setMinPrice(
                minPriceStr != null && !minPriceStr.trim().isEmpty()
                    ? Integer.parseInt(minPriceStr)
                    : 0
            );

            restaurant.setMaxPrice(
                maxPriceStr != null && !maxPriceStr.trim().isEmpty()
                    ? Integer.parseInt(maxPriceStr)
                    : 0
            );

            restaurant.setBikeFriendly(
                bikeFriendlyStr != null && !bikeFriendlyStr.trim().isEmpty()
                    ? Integer.parseInt(bikeFriendlyStr)
                    : 0
            );

            restaurant.setAvailable(
                availableStr != null && !availableStr.trim().isEmpty()
                    ? Integer.parseInt(availableStr)
                    : 0
            );

            restaurant.setGradesAverage(
                gradesAverageStr != null && !gradesAverageStr.trim().isEmpty()
                    ? Integer.parseInt(gradesAverageStr)
                    : 0
            );

        } catch (NumberFormatException e) {
            throw new CustomBadRequestException("Error en los parámetros numéricos del formulario");
        }

        restaurant.setId(0);
        restaurant.setIdu((int) user.getId());

        validateRestaurant(restaurant);

        Connection conn = (Connection) sc.getAttribute("dbConn");
        RestaurantDAO restaurantDao = new JDBCRestaurantDAOImpl();
        restaurantDao.setConnection(conn);

        long newId = restaurantDao.add(restaurant);

        if (newId < 0) {
            throw new WebApplicationException(
                "No se pudo crear el restaurante",
                Response.Status.INTERNAL_SERVER_ERROR
            );
        }

        Restaurant created = restaurantDao.get(newId);

        return Response.created(
                uriInfo.getAbsolutePathBuilder()
                       .path(Long.toString(newId))
                       .build())
                .entity(created)
                .build();
    }

    @PUT
    @Path("/{restaurantId: [0-9]+}")
    public Response updateRestaurant(@PathParam("restaurantId") long restaurantId,
                                     Restaurant restaurant,
                                     @Context HttpServletRequest request) {

        User user = getLoggedUser(request);

        Connection conn = (Connection) sc.getAttribute("dbConn");
        RestaurantDAO restaurantDao = new JDBCRestaurantDAOImpl();
        restaurantDao.setConnection(conn);

        Restaurant currentRestaurant = restaurantDao.get(restaurantId);

        if (currentRestaurant == null) {
            throw new CustomNotFoundException("Restaurante no encontrado");
        }

        if (!isOwner(user, currentRestaurant)) {
            throw new WebApplicationException("No autorizado", Response.Status.FORBIDDEN);
        }

        if (restaurant.getId() != 0 && restaurant.getId() != restaurantId) {
            throw new CustomBadRequestException("El id del cuerpo no coincide con el de la URL");
        }

        restaurant.setId(restaurantId);
        restaurant.setIdu(currentRestaurant.getIdu());

        validateRestaurant(restaurant);

        boolean updated = restaurantDao.update(restaurant);

        if (!updated) {
            throw new WebApplicationException("No se pudo actualizar el restaurante",
                    Response.Status.INTERNAL_SERVER_ERROR);
        }

        return Response.ok(restaurantDao.get(restaurantId)).build();
    }

    @DELETE
    @Path("/{restaurantId: [0-9]+}")
    public Response deleteRestaurant(@PathParam("restaurantId") long restaurantId,
                                     @Context HttpServletRequest request) {

        User user = getLoggedUser(request);

        Connection conn = (Connection) sc.getAttribute("dbConn");
        RestaurantDAO restaurantDao = new JDBCRestaurantDAOImpl();
        restaurantDao.setConnection(conn);

        Restaurant restaurant = restaurantDao.get(restaurantId);

        if (restaurant == null) {
            throw new CustomNotFoundException("Restaurante no encontrado");
        }

        if (!isOwner(user, restaurant)) {
            throw new WebApplicationException("No autorizado", Response.Status.FORBIDDEN);
        }

        boolean deleted = restaurantDao.delete(restaurantId);

        if (!deleted) {
            throw new WebApplicationException("No se pudo eliminar el restaurante",
                    Response.Status.INTERNAL_SERVER_ERROR);
        }

        return Response.noContent().build();
    }

    private User getLoggedUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("user") == null) {
            throw new WebApplicationException("Usuario no autenticado", Response.Status.UNAUTHORIZED);
        }

        return (User) session.getAttribute("user");
    }

    private boolean isOwner(User user, Restaurant restaurant) {
        return restaurant.getIdu() == user.getId();
    }

    private List<Restaurant> filterByOwner(List<Restaurant> restaurants, long userId) {
        List<Restaurant> result = new ArrayList<>();

        for (Restaurant r : restaurants) {
            if (r.getIdu() == userId) {
                result.add(r);
            }
        }

        return result;
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