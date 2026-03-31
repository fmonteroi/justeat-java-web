<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Listado de restaurantes</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/restaurants-list.css">
</head>
<body>
    <header class="site-header">
        <button class="icon-button" type="button" aria-label="Volver">&#8249;</button>

        <a class="brand" href="${pageContext.request.contextPath}/" aria-label="Ir al inicio">
            <span class="brand__icon" aria-hidden="true">🍴</span>
            <span class="brand__text">JUST EAT</span>
        </a>

        <form class="top-search" action="#" method="get">
            <label class="visually-hidden" for="restaurantSearch">Buscar restaurante</label>
            <input
                type="search"
                id="restaurantSearch"
                name="restaurantSearch"
                placeholder="¿Buscas restaurantes?"
            >
        </form>
    </header>

    <main class="page">
        <aside class="filters" aria-label="Filtros de búsqueda">
            <h1 class="filters__title">${fn:length(restaurantsList)} establecimientos</h1>

            <form class="filters-form" action="#" method="get">
                <fieldset class="filter-group">
                    <legend>Filtros rápidos</legend>

                    <label class="switch-row" for="availableNow">
                        <span>Disponibles ahora</span>
                        <input type="checkbox" id="availableNow" name="availableNow">
                    </label>

                    <label class="switch-row" for="bikeFriendlyOnly">
                        <span>Bike Friendly</span>
                        <input type="checkbox" id="bikeFriendlyOnly" name="bikeFriendlyOnly">
                    </label>
                </fieldset>

                <fieldset class="filter-group">
                    <legend>Importe mínimo</legend>

                    <label class="option-row" for="priceAll">
                        <input type="radio" id="priceAll" name="priceFilter" value="all" checked>
                        <span>Mostrar todo</span>
                    </label>

                    <label class="option-row" for="price10">
                        <input type="radio" id="price10" name="priceFilter" value="10">
                        <span>10,00 € o menos</span>
                    </label>

                    <label class="option-row" for="price20">
                        <input type="radio" id="price20" name="priceFilter" value="20">
                        <span>20,00 € o menos</span>
                    </label>
                </fieldset>

                <fieldset class="filter-group">
                    <legend>Puntuación</legend>

                    <label class="option-row" for="rating4">
                        <input type="checkbox" id="rating4" name="rating4" value="4">
                        <span>4.0 o superior</span>
                    </label>

                    <label class="option-row" for="rating45">
                        <input type="checkbox" id="rating45" name="rating45" value="4.5">
                        <span>4.5 o superior</span>
                    </label>
                </fieldset>
            </form>
        </aside>

        <section class="results" aria-labelledby="resultsTitle">
            <header class="results-header">
                <h2 id="resultsTitle">
                    ${fn:length(restaurantsList)} establecimientos disponibles
                </h2>
                <p class="results-header__text">Listado generado a partir de la colección <strong>restaurantsList</strong>.</p>
            </header>

            <section class="restaurant-list" aria-label="Lista de restaurantes">
                <c:forEach var="restaurant" items="${restaurantsList}">
                    <article class="restaurant-card">
                        <header class="restaurant-card__top">
                            <c:choose>
                                <c:when test="${restaurant.first.available eq '1'}">
                                    <p class="restaurant-card__status restaurant-card__status--open">Disponible</p>
                                </c:when>
                                <c:otherwise>
                                    <p class="restaurant-card__status restaurant-card__status--closed">No disponible</p>
                                </c:otherwise>
                            </c:choose>

                            <p class="restaurant-card__rating">
                                Puntuación: ${restaurant.first.gradesAverage}
                            </p>
                        </header>

                        <div class="restaurant-card__content">
                            <figure class="restaurant-card__visual">
                                <span class="restaurant-card__icon" aria-hidden="true">🍽</span>
                                <figcaption class="restaurant-card__city">${restaurant.first.city}</figcaption>
                            </figure>

                            <section class="restaurant-card__body">
                                <header class="restaurant-card__header">
                                    <h3>${restaurant.first.name}</h3>
                                    <p class="restaurant-card__owner">Usuario: ${restaurant.second.name}</p>
                                </header>

                                <ul class="restaurant-card__info">
                                    <li><strong>Dirección:</strong> ${restaurant.first.address}</li>
                                    <li><strong>Teléfono:</strong> ${restaurant.first.telephone}</li>
                                    <li><strong>Ciudad:</strong> ${restaurant.first.city}</li>
                                    <li><strong>Email de contacto:</strong> ${restaurant.first.contactEmail}</li>
                                </ul>

                                <section class="restaurant-card__prices" aria-label="Rango de precios">
                                    <p><strong>Precio mínimo:</strong> ${restaurant.first.minPrice} €</p>
                                    <p><strong>Precio máximo:</strong> ${restaurant.first.maxPrice} €</p>
                                </section>

                                <section class="restaurant-card__tags" aria-label="Características del restaurante">
                                    <c:choose>
                                        <c:when test="${restaurant.first.bikeFriendly eq '1'}">
                                            <span class="tag tag--ok">Bike Friendly</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="tag tag--neutral">No Bike Friendly</span>
                                        </c:otherwise>
                                    </c:choose>

                                    <c:choose>
                                        <c:when test="${restaurant.first.available eq '1'}">
                                            <span class="tag tag--ok">Disponible</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="tag tag--neutral">No disponible</span>
                                        </c:otherwise>
                                    </c:choose>
                                </section>

                                <section class="restaurant-card__categories" aria-label="Categorías del restaurante">
                                    <h4>Categorías</h4>
                                    <ul class="category-list">
                                        <c:forEach var="category" items="${restaurant.third}">
                                            <li class="category-list__item">${category.idct}</li>
                                        </c:forEach>
                                    </ul>
                                </section>
                            </section>
                        </div>
                    </article>
                </c:forEach>
            </section>

            <section class="users-section" aria-labelledby="usersSectionTitle">
                <header class="users-section__header">
                    <h2 id="usersSectionTitle">Restaurantes por usuario</h2>
                </header>

                <c:forEach var="restaurantsByUser" items="${usersMap}">
                    <article class="user-card">
                        <header class="user-card__header">
                            <h3>${restaurantsByUser.key.name}</h3>
                            <p>${restaurantsByUser.key.email}</p>
                        </header>

                        <section class="user-card__restaurants" aria-label="Restaurantes del usuario">
                            <c:forEach var="restaurant" items="${restaurantsByUser.value}">
                                <article class="user-restaurant">
                                    <h4>${restaurant.name}</h4>

                                    <ul class="user-restaurant__info">
                                        <li><strong>Dirección:</strong> ${restaurant.address}</li>
                                        <li><strong>Teléfono:</strong> ${restaurant.telephone}</li>
                                        <li><strong>Ciudad:</strong> ${restaurant.city}</li>
                                        <li><strong>Precio mínimo:</strong> ${restaurant.minPrice} €</li>
                                        <li><strong>Precio máximo:</strong> ${restaurant.maxPrice} €</li>
                                        <li><strong>Puntuación:</strong> ${restaurant.gradesAverage}</li>
                                        <li><strong>Email:</strong> ${restaurant.contactEmail}</li>
                                    </ul>

                                    <div class="user-restaurant__tags">
                                        <c:choose>
                                            <c:when test="${restaurant.bikeFriendly eq '1'}">
                                                <span class="tag tag--ok">Bike Friendly</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="tag tag--neutral">No Bike Friendly</span>
                                            </c:otherwise>
                                        </c:choose>

                                        <c:choose>
                                            <c:when test="${restaurant.available eq '1'}">
                                                <span class="tag tag--ok">Disponible</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="tag tag--neutral">No disponible</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                </article>
                            </c:forEach>
                        </section>
                    </article>
                </c:forEach>
            </section>
        </section>
    </main>
</body>
</html>
