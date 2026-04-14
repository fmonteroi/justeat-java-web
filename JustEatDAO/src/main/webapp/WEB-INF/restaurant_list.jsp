<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!doctype html>
<html lang="es">
<head>
<meta charset="utf-8" />
<meta name="viewport" content="width=device-width, initial-scale=1" />
<title>Listado</title>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/styles.css" />
</head>

<body class="page-listado">
	<header class="top top-listado">
		<a class="back" href="SearchServlet.do" aria-label="Volver">&lt;</a> <a
			class="brand" href="LoginServlet.do"> <img
			src="${pageContext.request.contextPath}/images/logo_justeat.svg"
			alt="Logo Just Eat" width="30" height="30" /> <strong>JUST
				EAT</strong>
		</a>

		<div class="top-mid">
			<div class="pill addr" id="addrPill">
				<span aria-hidden="true">&#128205;</span> <span id="addrText">${searchValue}</span>
			</div>

			<nav class="pill tabs" aria-label="Disponibilidad">
				<a class="<c:if test='${empty availabilityValue}'>is-active</c:if>"
					href="ListRestaurantServlet.do?search=${searchValue}<c:if test='${not empty categoryValue}'>&category=${categoryValue}</c:if><c:if test='${not empty cityValue}'>&city=${cityValue}</c:if><c:if test='${not empty commentsValue}'>&comments=${commentsValue}</c:if><c:if test='${not empty dishValue}'>&dish=${dishValue}</c:if><c:if test='${not empty minPriceValue}'>&minPrice=${minPriceValue}</c:if><c:if test='${not empty maxPriceValue}'>&maxPrice=${maxPriceValue}</c:if><c:if test='${not empty sortValue}'>&sort=${sortValue}</c:if>">
					Todos </a> <a
					class="<c:if test='${availabilityValue == \"open\"}'>is-active</c:if>"
					href="ListRestaurantServlet.do?search=${searchValue}&availability=open<c:if test='${not empty categoryValue}'>&category=${categoryValue}</c:if><c:if test='${not empty cityValue}'>&city=${cityValue}</c:if><c:if test='${not empty commentsValue}'>&comments=${commentsValue}</c:if><c:if test='${not empty dishValue}'>&dish=${dishValue}</c:if><c:if test='${not empty minPriceValue}'>&minPrice=${minPriceValue}</c:if><c:if test='${not empty maxPriceValue}'>&maxPrice=${maxPriceValue}</c:if><c:if test='${not empty sortValue}'>&sort=${sortValue}</c:if>">
					Aceptan pedidos </a> <a
					class="<c:if test='${availabilityValue == \"closed\"}'>is-active</c:if>"
					href="ListRestaurantServlet.do?search=${searchValue}&availability=closed<c:if test='${not empty categoryValue}'>&category=${categoryValue}</c:if><c:if test='${not empty cityValue}'>&city=${cityValue}</c:if><c:if test='${not empty commentsValue}'>&comments=${commentsValue}</c:if><c:if test='${not empty dishValue}'>&dish=${dishValue}</c:if><c:if test='${not empty minPriceValue}'>&minPrice=${minPriceValue}</c:if><c:if test='${not empty maxPriceValue}'>&maxPrice=${maxPriceValue}</c:if><c:if test='${not empty sortValue}'>&sort=${sortValue}</c:if>">
					No aceptan pedidos </a>
			</nav>
		</div>

		<div class="top-right">
			<span class="flag" aria-hidden="true">ES</span>
			<button class="menu" type="button" aria-label="Menu">=</button>
		</div>
	</header>

	<main class="container">
		<section class="layout">
			<aside class="filters">
				<form class="stack" action="ListRestaurantServlet.do" method="get">
					<input type="hidden" name="search" value="${searchValue}" /> <input
						type="hidden" name="sort" value="${sortValue}" /> <input
						type="hidden" name="availability" value="${availabilityValue}" />

					<div class="field">
						<label for="category">Categoría</label> <select id="category"
							name="category">
							<option value="">Todas</option>
							<c:forEach var="cat" items="${allCategories}">
								<option value="${cat.id}"
									<c:if test="${not empty categoryValue and categoryValue == cat.id}">selected</c:if>>
									${cat.name}</option>
							</c:forEach>
						</select>
					</div>

					<div class="field">
						<label for="city">Localidad</label> <input id="city" name="city"
							type="text" value="${cityValue}" />
					</div>

					<div class="field">
						<label for="comments">Comentarios</label> <input id="comments"
							name="comments" type="text" value="${commentsValue}" />
					</div>

					<div class="field">
						<label for="dish">Platos</label> <input id="dish" name="dish"
							type="text" value="${dishValue}" />
					</div>

					<div class="field">
						<label for="minPrice">Precio mínimo</label> <input id="minPrice"
							name="minPrice" type="number" min="0" value="${minPriceValue}" />
					</div>

					<div class="field">
						<label for="maxPrice">Precio máximo</label> <input id="maxPrice"
							name="maxPrice" type="number" min="0" value="${maxPriceValue}" />
					</div>

					<button class="btn btn-primary" type="submit">Aplicar
						filtros</button>
				</form>
			</aside>

			<section class="results">
				<header class="results-top">
					<form class="searchbar" action="ListRestaurantServlet.do"
						method="get">
						<input type="hidden" name="sort" value="${sortValue}" /> <input
							type="hidden" name="availability" value="${availabilityValue}" />
						<input type="hidden" name="category" value="${categoryValue}" />
						<input type="hidden" name="city" value="${cityValue}" /> <input
							type="hidden" name="comments" value="${commentsValue}" /> <input
							type="hidden" name="dish" value="${dishValue}" /> <input
							type="hidden" name="minPrice" value="${minPriceValue}" /> <input
							type="hidden" name="maxPrice" value="${maxPriceValue}" /> <span
							aria-hidden="true"></span> <input type="search" name="search"
							placeholder="¿Buscas restaurantes?" value="${searchValue}" />
					</form>

					<form action="ListRestaurantServlet.do" method="get"
						class="orderby">
						<input type="hidden" name="search" value="${searchValue}" /> <input
							type="hidden" name="availability" value="${availabilityValue}" />
						<input type="hidden" name="category" value="${categoryValue}" />
						<input type="hidden" name="city" value="${cityValue}" /> <input
							type="hidden" name="comments" value="${commentsValue}" /> <input
							type="hidden" name="dish" value="${dishValue}" /> <input
							type="hidden" name="minPrice" value="${minPriceValue}" /> <input
							type="hidden" name="maxPrice" value="${maxPriceValue}" /> <span>Ordenar
							por</span> <select name="sort" onchange="this.form.submit()">
							<option value="">Por defecto</option>
							<option value="rating"
								<c:if test="${sortValue == 'rating'}">selected</c:if>>
								Valoracion media</option>
						</select>
					</form>
				</header>

				<div class="match">
					<p>
						<strong><span class="countText">${fn:length(restaurantsList)}</span>
							establecimientos</strong> encontrados con los filtros aplicados
					</p>
				</div>

				<c:choose>
					<c:when test="${empty restaurantsList}">
						<section class="cards" id="cards">
							<article class="r-card">
								<div class="r-body">
									<h3>No se han encontrado restaurantes</h3>
									<p class="r-meta">Prueba con otros filtros o cambia el
										texto de búsqueda.</p>
								</div>
							</article>
						</section>
					</c:when>

					<c:otherwise>
						<section class="cards" id="cards">
							<c:forEach var="restaurant" items="${restaurantsList}">
								<article class="r-card">
									<img class="r-img"
										src="${pageContext.request.contextPath}/images/restaurantes/placeholder.webp"
										alt="Restaurante" />

									<div class="r-body">
										<h3>
											<a class="r-card-link"
												href="RestaurantDetailServlet.do?id=${restaurant.first.id}"
												aria-label="Ver detalles de ${restaurant.first.name}">
												${restaurant.first.name} </a>
										</h3>

										<p class="r-meta">Ciudad: ${restaurant.first.city}
											&middot; Precio:
											${restaurant.first.minPrice}-${restaurant.first.maxPrice}
											EUR</p>

										<p class="r-meta">
											Propietario: ${restaurant.second.name} &middot; Bike
											Friendly:
											<c:choose>
												<c:when test="${restaurant.first.bikeFriendly eq 1}">Si</c:when>
												<c:otherwise>No</c:otherwise>
											</c:choose>
											&middot;
											<c:choose>
												<c:when test="${restaurant.first.available eq 1}">Acepta pedidos</c:when>
												<c:otherwise>No acepta pedidos</c:otherwise>
											</c:choose>
										</p>

										<p class="r-meta">Categorias:</p>
										<div class="detail-tags">
											<c:forEach var="category" items="${restaurant.third}">
												<span class="tag">${category.name}</span>
											</c:forEach>
										</div>

										<p class="r-meta" style="margin-top: 10px;">
											<a class="btn btn-ghost btn-mini"
												href="RestaurantDetailServlet.do?id=${restaurant.first.id}">
												Ver restaurante </a>
										</p>
									</div>
								</article>
							</c:forEach>
						</section>
					</c:otherwise>
				</c:choose>
			</section>
		</section>
	</main>
</body>
</html>
