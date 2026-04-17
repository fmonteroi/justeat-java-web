<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!doctype html>
<html lang="es">
<head>
<meta charset="utf-8" />
<meta name="viewport" content="width=device-width, initial-scale=1" />
<title>Mis restaurantes</title>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/styles.css" />
</head>

<body class="page-editar">
	<header class="top">
		<a class="back" href="EditUserServlet.do" aria-label="Volver">&lt;</a>

		<a class="brand" href="LoginServlet.do" aria-label="Inicio"> <img
			src="${pageContext.request.contextPath}/images/logo_justeat.svg"
			alt="Logo Just Eat" width="30" height="30" /> <strong>JUST
				EAT</strong>
		</a>

		<div class="top-right top-right-all">
			<nav class="top-nav" aria-label="Navegacion principal">
				<a href="#"><span class="top-nav-ico" aria-hidden="true">&#128188;</span><span>Pedidos
						de empresa</span></a> <a href="#"><span class="top-nav-ico"
					aria-hidden="true">&#128692;</span><span>Trabaja como
						repartidor</span></a>
				<c:choose>
					<c:when test="${sessionScope.user != null}">
						<a href="ListUserRestaurantServlet.do"><span class="top-nav-ico"
							aria-hidden="true">&#127970;</span><span>Registra tu negocio</span></a>
					</c:when>
					<c:otherwise>
						<a href="LoginServlet.do"><span class="top-nav-ico"
							aria-hidden="true">&#127970;</span><span>Registra tu negocio</span></a>
					</c:otherwise>
				</c:choose>
			</nav>

			<a class="top-userlink" href="EditUserServlet.do"> <span
				aria-hidden="true">&#128100;</span> <span>${sessionScope.user.name}
					${sessionScope.user.surname}</span>
			</a><span class="flag" aria-hidden="true">ES</span>
			<button class="menu" type="button" aria-label="Menu">=</button>
		</div>
	</header>

	<main class="container" role="main">
		<div class="edit-header">
			<a class="edit-back" href="EditUserServlet.do" aria-label="Volver">&larr;
				Volver</a>
		</div>

		<div class="edit-layout">
			<aside class="edit-side" aria-label="Menu lateral">
				<nav class="edit-sidecard" aria-label="Secciones privadas">
					<ul class="edit-sidemenu">
						<li><a class="edit-sidelink" href="EditUserServlet.do"> <span
								class="edit-sidelink-label">Mi perfil</span>
						</a></li>

						<li><a class="edit-sidelink" href="ListUserOrderServlet.do">
								<span class="edit-sidelink-label">Mis pedidos</span>
						</a></li>

						<li><a class="edit-sidelink" href="ListCategoryServlet.do">
								<span class="edit-sidelink-label">Mis categorías</span>
						</a></li>

						<li><a class="edit-sidelink is-active"
							href="ListUserRestaurantServlet.do"> <span
								class="edit-sidelink-label">Mis restaurantes</span>
						</a></li>

						<li><a class="edit-sidelink" href="LogoutServlet.do"> <span
								class="edit-sidelink-label">Salir</span>
						</a></li>
					</ul>
				</nav>
			</aside>

			<section class="edit-main" aria-label="Mis restaurantes">
				<article class="card edit-card" aria-label="Listado de restaurantes">
					<header class="menu-head">
						<div class="results-top">
							<div>
								<h2 class="menu-title">Mis restaurantes</h2>
								<p class="help">
									<strong><span class="countText">${fn:length(restaurantsList)}</span>
										restaurantes</strong> asociados a tu cuenta
								</p>
							</div>

							<a class="btn btn-primary" href="CreateRestaurantServlet.do">
								Añadir restaurante </a>
						</div>
					</header>

					<section class="cards" id="cards">
						<c:forEach var="restaurant" items="${restaurantsList}">
							<article class="r-card">
								<div class="r-card-link">
									<a href="EditRestaurantServlet.do?id=${restaurant.first.id}"
										aria-label="Editar ${restaurant.first.name}"> <img class="r-img"
										src="${pageContext.request.contextPath}/images/restaurantes/placeholder.webp"
										alt="Restaurante" />
									</a>

									<div class="r-body">
										<div class="r-card-head">
											<h3>
												<a class="r-title-link"
													href="EditRestaurantServlet.do?id=${restaurant.first.id}">${restaurant.first.name}</a>
											</h3>
											<a class="btn btn-ghost btn-mini"
												href="RestaurantDetailServlet.do?id=${restaurant.first.id}">Ver
												detalles</a>
										</div>

										<p class="r-rating">
											<span class="r-rating-star">★</span> <span
												class="r-rating-value"><fmt:formatNumber
													value="${restaurant.first.gradesAverage}"
													minFractionDigits="1" maxFractionDigits="1" /></span>
										</p>

										<div class="r-spacer" aria-hidden="true"></div>

										<c:if test="${not empty restaurant.first.description}">
											<p class="r-meta">
												<strong>Descripción:</strong> ${restaurant.first.description}
											</p>

											<div class="r-spacer" aria-hidden="true"></div>
										</c:if>

										<p class="r-meta">Ciudad: ${restaurant.first.city}</p>

										<p class="r-meta">Rango de precio:
											${restaurant.first.minPrice}-${restaurant.first.maxPrice} €
										</p>

										<p class="r-meta">
											Disponibilidad:
											<c:choose>
												<c:when test="${restaurant.first.available eq 1}">Acepta pedidos</c:when>
												<c:otherwise>No acepta pedidos</c:otherwise>
											</c:choose>
										</p>

										<div class="detail-tags detail-tags--compact">
											<c:forEach var="category" items="${restaurant.third}">
												<span class="tag">${category.name}</span>
											</c:forEach>
										</div>
									</div>
								</div>
							</article>
						</c:forEach>
					</section>

				</article>
			</section>
		</div>
	</main>
</body>
</html>
