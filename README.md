# JustEat — Web Technologies Project

Academic clone of Just Eat, developed for the **Web Technologies (Tecnologías Web)** course at the **University of Extremadura**.

The project explores restaurant browsing and food ordering through two implementations: a server-rendered application and a REST API version.

## Features

- User registration, login and profile management.
- Restaurant browsing and search.
- Restaurant, category and dish management.
- Shopping cart and order creation in the server-rendered version.
- Restaurant reviews.
- Additional REST endpoints for favorites and restaurant social links.

## Technologies

- **Java 21** — backend development.
- **Jakarta Servlets, JSP and JSTL** — request handling and server-rendered pages.
- **Jakarta REST (JAX-RS) and Jersey** — REST API implementation.
- **SQLite and JDBC** — database storage and access.
- **HTML, CSS and JavaScript** — user interface.
- **Apache Tomcat** — application server.
- **Eclipse IDE** — development environment.

## Repository Structure

| Directory | Description |
| --- | --- |
| `JustEatDAO/` | Server-rendered application using Servlets, JSP and the DAO pattern. |
| `JustEatDAO_Rest/` | REST API implementation using JAX-RS and Jersey. |
| `bdp2/` | SQLite database for the server-rendered application. |
| `bdp3/` | SQLite database for the REST version. |

Both implementations separate database access into DAO interfaces and JDBC implementations.

## Running Locally

1. Clone the repository.
2. Import the application projects into Eclipse as existing projects.
3. Configure JDK 21 and an Apache Tomcat runtime. The REST project is configured for Tomcat 11.
4. Update the SQLite database path in each project's `ServletContextListener.java` to match your local checkout:
   - `JustEatDAO` → `bdp2/FMS.db`
   - `JustEatDAO_Rest` → `bdp3/FMS.db`
5. Deploy the desired project to Tomcat.

The REST project's deployment descriptor requires HTTPS, so its Tomcat runtime must have HTTPS configured.

Dependencies are included in each project's `WEB-INF/lib` directory.

## Academic Context

This repository contains coursework developed to practice Java web development, relational database access, session handling and REST API design.

It is an educational project with no affiliation with Just Eat.
