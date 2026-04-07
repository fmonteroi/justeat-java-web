package es.unex.cume.tw.util;

public class ValidationUtils {
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
}
