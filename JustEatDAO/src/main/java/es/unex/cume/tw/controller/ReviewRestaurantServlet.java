package es.unex.cume.tw.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.util.List;
import es.unex.cume.tw.dao.JDBCRestaurantDAOImpl;
import es.unex.cume.tw.dao.JDBCReviewsDAOImpl;
import es.unex.cume.tw.dao.RestaurantDAO;
import es.unex.cume.tw.dao.ReviewsDAO;
import es.unex.cume.tw.model.Restaurant;
import es.unex.cume.tw.model.Review;
import es.unex.cume.tw.model.User;

/**
 * Servlet implementation class ReviewRestaurantServlet
 */
@WebServlet("/ReviewRestaurantServlet.do")
public class ReviewRestaurantServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ReviewRestaurantServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.sendRedirect("ListRestaurantServlet.do");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Get session
		HttpSession session = request.getSession(false);
	    if (session == null || session.getAttribute("user") == null) {
	        response.sendRedirect("LoginServlet.do");
	        return;
	    }

	    User user = (User) session.getAttribute("user");

	    // Prepare database access
	    Connection conn = (Connection) getServletContext().getAttribute("dbConn");

	    ReviewsDAO reviewsDao = new JDBCReviewsDAOImpl();
	    reviewsDao.setConnection(conn);

	    RestaurantDAO restaurantDao = new JDBCRestaurantDAOImpl();
	    restaurantDao.setConnection(conn);
	    
	    // Read form fields
	    String restaurantIdParam = request.getParameter("restaurantId");
	    String gradeParam = request.getParameter("grade");
	    String reviewText = request.getParameter("review");
	    
	    long restaurantId = 0;
	    int grade = 0;

	    // Validate numeric fields
	    try {
	        restaurantId = Long.parseLong(restaurantIdParam);
	        grade = Integer.parseInt(gradeParam);
	    } catch (NumberFormatException e) {
	        response.sendRedirect("ListRestaurantServlet.do");
	        return;
	    }

	    Restaurant restaurant = restaurantDao.get(restaurantId);
	    if (restaurant == null) {
	        response.sendRedirect("ListRestaurantServlet.do");
	        return;
	    }

	    // Validate grade range
	    if (grade < 1 || grade > 5) {
	        response.sendRedirect("RestaurantDetailServlet.do?id=" + restaurantId);
	        return;
	    }

	    // Normalize optional review text
	    if (reviewText == null) {
	        reviewText = "";
	    }

	    reviewText = reviewText.trim();
	    
	    // Load current review if it exists
	    Review existingReview = reviewsDao.get(restaurantId, user.getId());

	    // Create or update the review
	    if (existingReview == null) {
	        Review review = new Review();
	        review.setIdr(restaurantId);
	        review.setIdu(user.getId());
	        review.setGrade(grade);
	        review.setReview(reviewText);

	        reviewsDao.add(review);
	    } else {
	        existingReview.setGrade(grade);
	        existingReview.setReview(reviewText);

	        reviewsDao.update(existingReview);
	    }
	    
	    // Refresh restaurant average
	    updateRestaurantAverage(reviewsDao, restaurantDao, restaurantId);

	    // Return to the restaurant detail
	    response.sendRedirect("RestaurantDetailServlet.do?id=" + restaurantId);
	}
	
	private void updateRestaurantAverage(ReviewsDAO reviewsDao, RestaurantDAO restaurantDao, long restaurantId) {
	    // Load current reviews
	    List<Review> reviews = reviewsDao.getAllByRestaurant(restaurantId);
	    Restaurant restaurant = restaurantDao.get(restaurantId);

	    if (restaurant == null) return;

	    // Reset average when there are no reviews
	    if (reviews == null || reviews.isEmpty()) {
	        restaurant.setGradesAverage(0);
	        restaurantDao.update(restaurant);
	        return;
	    }

	    // Compute the average grade
	    int total = 0;
	    for (Review review : reviews) {
	    	total = total + review.getGrade();
	    }

	    int average = Math.round((float) total / reviews.size());
	    // Persist the new average
	    restaurant.setGradesAverage(average);
	    restaurantDao.update(restaurant);
	}


}
