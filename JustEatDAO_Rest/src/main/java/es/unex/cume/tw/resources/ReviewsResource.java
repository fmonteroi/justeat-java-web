package es.unex.cume.tw.resources;

import java.sql.Connection;
import java.util.List;

import es.unex.cume.tw.dao.JDBCRestaurantDAOImpl;
import es.unex.cume.tw.dao.JDBCReviewsDAOImpl;
import es.unex.cume.tw.dao.RestaurantDAO;
import es.unex.cume.tw.dao.ReviewsDAO;
import es.unex.cume.tw.model.Restaurant;
import es.unex.cume.tw.model.Review;
import es.unex.cume.tw.model.User;
import es.unex.cume.tw.util.Utils;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/restaurantes/{id: [0-9]+}/valoraciones")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ReviewsResource {

    @Context
    private ServletContext sc;

    /**
     * GET /api/restaurantes/{id}/valoraciones
     * 
     * @param restaurantId
     * @param request
     * @return
     */
    @GET
    public List<Review> getRestaurantReviews(@PathParam("id") long restaurantId,
            @Context HttpServletRequest request) {

        Utils.getLoggedUser(request);

        Connection conn = (Connection) sc.getAttribute("dbConn");

        RestaurantDAO restaurantDao = new JDBCRestaurantDAOImpl();
        restaurantDao.setConnection(conn);

        ReviewsDAO reviewsDao = new JDBCReviewsDAOImpl();
        reviewsDao.setConnection(conn);

        Restaurant restaurant = restaurantDao.get(restaurantId);

        if (restaurant == null) {
            throw new WebApplicationException("Restaurante no encontrado", Response.Status.NOT_FOUND);
        }

        return reviewsDao.getAllByRestaurant(restaurantId);
    }

    /**
     * POST /api/restaurantes/{id}/valoraciones
     * 
     * @param restaurantId
     * @param review
     * @param request
     * @return
     */
    @POST
    public Review createOrUpdateReview(@PathParam("id") long restaurantId, Review review,
            @Context HttpServletRequest request) {

        User user = Utils.getLoggedUser(request);

        if (review == null) {
            throw new WebApplicationException("El cuerpo es obligatorio", Response.Status.BAD_REQUEST);
        }

        // Check grade range
        if (review.getGrade() < 1 || review.getGrade() > 5) {
            throw new WebApplicationException("La puntuación debe estar entre 1 y 5", Response.Status.BAD_REQUEST);
        }

        Connection conn = (Connection) sc.getAttribute("dbConn");

        RestaurantDAO restaurantDao = new JDBCRestaurantDAOImpl();
        restaurantDao.setConnection(conn);

        ReviewsDAO reviewsDao = new JDBCReviewsDAOImpl();
        reviewsDao.setConnection(conn);

        Restaurant restaurant = restaurantDao.get(restaurantId);

        if (restaurant == null) {
            throw new WebApplicationException("Restaurante no encontrado", Response.Status.NOT_FOUND);
        }

        Review existingReview = reviewsDao.get(restaurantId, user.getId());

        // Create a new review or update the existing one from this user
        if (existingReview == null) {
            // Force server-controlled relation fields
            review.setIdr(restaurantId);
            review.setIdu(user.getId());

            if (review.getReview() == null) {
                review.setReview("");
            } else {
                review.setReview(review.getReview().trim());
            }

            if (!reviewsDao.add(review)) {
                throw new WebApplicationException("No se pudo crear la valoración", Response.Status.INTERNAL_SERVER_ERROR);
            }
        } else {
            // Update previous review from same user
            existingReview.setGrade(review.getGrade());

            if (review.getReview() == null) {
                existingReview.setReview("");
            } else {
                existingReview.setReview(review.getReview().trim());
            }

            if (!reviewsDao.update(existingReview)) {
                throw new WebApplicationException("No se pudo actualizar la valoración", Response.Status.INTERNAL_SERVER_ERROR);
            }
        }

        // Update restaurant average grade
        updateRestaurantAverage(restaurantId, reviewsDao, restaurantDao);

        return reviewsDao.get(restaurantId, user.getId());
    }

    private void updateRestaurantAverage(long restaurantId, ReviewsDAO reviewsDao, RestaurantDAO restaurantDao) {
        List<Review> reviews = reviewsDao.getAllByRestaurant(restaurantId);
        Restaurant restaurant = restaurantDao.get(restaurantId);

        if (restaurant == null) {
            return;
        }

        if (reviews == null || reviews.isEmpty()) {
            restaurant.setGradesAverage(0);
        } else {
            int total = 0;

            // Sum all review grades
            for (Review review : reviews) {
                total += review.getGrade();
            }

            // Round average because database stores integer value
            int average = Math.round((float) total / reviews.size());
            restaurant.setGradesAverage(average);
        }

        restaurantDao.update(restaurant);
    }
}
