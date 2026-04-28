package es.unex.cume.tw.util;

import es.unex.cume.tw.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

public class Utils {
	public static boolean isPasswordValid(String password) {
		if (password.length() < 8) {
	        return false;
	    }

	    boolean hasUpper = false;
	    boolean hasLower = false;
	    boolean hasDigit = false;
	    boolean hasSpecial = false;
	    char c;

	    for (int i = 0; i < password.length(); i++) {
	        c = password.charAt(i);

	        if (Character.isUpperCase(c)) {
	            hasUpper = true;
	        } else if (Character.isLowerCase(c)) {
	            hasLower = true;
	        } else if (Character.isDigit(c)) {
	            hasDigit = true;
	        } else {
	            hasSpecial = true;
	        }
	    }

	    return hasUpper && hasLower && hasDigit && hasSpecial;
	}
	
	public static User getLoggedUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("user") == null) {
            throw new WebApplicationException("No autorizado", Response.Status.UNAUTHORIZED);
        }

        return (User) session.getAttribute("user");
    }
}
