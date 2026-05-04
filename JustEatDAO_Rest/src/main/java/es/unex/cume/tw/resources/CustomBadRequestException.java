package es.unex.cume.tw.resources;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

public class CustomBadRequestException extends WebApplicationException {
    private static final long serialVersionUID = 1L;

    public CustomBadRequestException() {
        super(Response.status(Response.Status.BAD_REQUEST).build());
    }

    public CustomBadRequestException(String message) {
        super(Response.status(Response.Status.BAD_REQUEST)
                .entity(message)
                .type("text/plain")
                .build());
    }
}
