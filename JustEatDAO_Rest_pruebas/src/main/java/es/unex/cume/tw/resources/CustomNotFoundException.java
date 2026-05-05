package es.unex.cume.tw.resources;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

public class CustomNotFoundException extends WebApplicationException {
    private static final long serialVersionUID = 1L;

    public CustomNotFoundException() {
        super(Response.status(Response.Status.NOT_FOUND).build());
    }

    public CustomNotFoundException(String message) {
        super(Response.status(Response.Status.NOT_FOUND)
                .entity(message)
                .type("text/plain")
                .build());
    }
}
