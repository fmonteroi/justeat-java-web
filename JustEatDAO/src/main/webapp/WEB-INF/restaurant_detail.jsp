<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!doctype html>
<html lang="es">
<head>
<meta charset="utf-8" />
<meta name="viewport" content="width=device-width, initial-scale=1" />
<title>${restaurant.name}</title>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/styles.css" />
</head>

<body class="page-detalle">
	<header class="top top-detalle">
		<a class="back" href="ListRestaurantServlet.do" aria-label="Volver">&lt;</a>

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
						repartidor</span></a> <a href="#"><span class="top-nav-ico"
					aria-hidden="true">&#127970;</span><span>Registra tu negocio</span></a>
			</nav>

			<c:choose>
				<c:when test="${sessionScope.user != null}">
					<a class="top-userlink" href="EditUserServlet.do"> <span
						aria-hidden="true">&#128100;</span> <span>${sessionScope.user.name}
							${sessionScope.user.surname}</span>
					</a>
				</c:when>
				<c:otherwise>
					<a class="top-userlink" href="LoginServlet.do"> <span
						aria-hidden="true">&#128100;</span> <span>Inicia sesion</span>
					</a>
				</c:otherwise>
			</c:choose>

			<span class="flag" aria-hidden="true">ES</span>
			<button class="menu" type="button" aria-label="Menu">=</button>
		</div>
	</header>

	<main class="container" role="main">
		<div class="detail-page-grid">
			<section class="detail-left" aria-label="Detalle del restaurante">
				<div class="card detail-cover-card">
					<img class="detail-cover"
						src="${pageContext.request.contextPath}/images/restaurantes/cume.jpeg"
						alt="${restaurant.name}" width="1400" height="600" />
				</div>

				<article class="card detail-info-card"
					aria-label="Informacion del restaurante">
					<header class="detail-head">
						<div class="detail-head-left">
							<h1 class="detail-name">${restaurant.name}</h1>

							<p class="detail-sub">
								<span class="detail-stars" aria-hidden="true">★★★★★</span> <strong
									class="detail-score">5</strong> <span class="detail-count">(5.000+)</span>

								<span class="detail-dot" aria-hidden="true">•</span> <span
									class="detail-meta">${restaurant.minPrice}-${restaurant.maxPrice}
									€</span> <span class="detail-dot" aria-hidden="true">•</span> <span
									class="detail-meta"> Bike Friendly: <strong> <c:choose>
											<c:when test="${restaurant.bikeFriendly eq 1}">Si</c:when>
											<c:otherwise>No</c:otherwise>
										</c:choose>
								</strong>
								</span>
							</p>

							<c:if
								test="${sessionScope.user != null && sessionScope.user.id == restaurant.idu}">
								<p>
									<a class="accent"
										href="EditRestaurantServlet.do?id=${restaurant.id}">Editar
										restaurante</a>
								</p>
							</c:if>
						</div>
					</header>

					<div class="detail-data">
						<div class="detail-line">
							<span class="detail-ico" aria-hidden="true">&#128205;</span>
							<div>
								<strong>Direccion</strong>
								<p>${restaurant.address}</p>
							</div>
						</div>

						<div class="detail-line">
							<span class="detail-ico" aria-hidden="true">&#128222;</span>
							<div>
								<strong>Telefono</strong>
								<p>
									<a href="tel:${restaurant.telephone}">${restaurant.telephone}</a>
								</p>
							</div>
						</div>

						<div class="detail-line">
							<span class="detail-ico" aria-hidden="true">✉️</span>
							<div>
								<strong>Correo de contacto</strong>
								<p>
									<a href="mailto:${restaurant.contactEmail}">${restaurant.contactEmail}</a>
								</p>
							</div>
						</div>

						<div class="detail-line">
							<span class="detail-ico" aria-hidden="true">🏷️</span>
							<div>
								<strong>Categorias</strong>
								<div class="detail-tags">
									<c:forEach var="category" items="${categories}">
										<span class="tag">${category.name}</span>
									</c:forEach>
								</div>
							</div>
						</div>
					</div>
				</article>

				<article class="card detail-menu-card"
					aria-label="Carta del restaurante">
					<header class="menu-head">
						<h2 class="menu-title">Carta de ${restaurant.name}</h2>
					</header>

					<section class="menu-section" id="platos"
						aria-label="Platos del restaurante">
						<h3 class="section-title">Platos del restaurante</h3>

						<div class="menu-list" role="list">
							<c:forEach var="dish" items="${dishes}">
								<article class="menu-item" role="listitem">
									<div class="menu-item-main">
										<h4 class="menu-item-title">${dish.name}</h4>
										<p class="menu-item-desc">${dish.description}</p>
									</div>
									<div class="menu-item-side">
										<span class="menu-item-price">${dish.price},00 €</span>
										<c:choose>
											<c:when test="${sessionScope.user != null}">
												<form action="AddDishToCartServlet.do" method="post">
													<input type="hidden" name="dishId" value="${dish.id}" />
													<button class="btn btn-primary btn-mini" type="submit">Añadir</button>
												</form>
											</c:when>
											<c:otherwise>
												<a class="btn btn-primary btn-mini"
													href="LoginServlet.do?redirect=RestaurantDetailServlet.do?id=${restaurant.id}">
													Inicia sesion </a>
											</c:otherwise>
										</c:choose>
									</div>
								</article>
							</c:forEach>
						</div>
					</section>
				</article>
			</section>

			<aside class="detail-right" aria-label="Cesta">
				<section class="card edit-card">
					<h2 class="edit-h2">Cesta</h2>

					<div class="edit-pillrow">
						<button class="edit-pill is-active" type="button">Entrega</button>
						<button class="edit-pill" type="button">Recogida</button>
					</div>

					<c:choose>
						<c:when test="${sessionScope.user == null}">
							<div class="edit-empty">
								<div class="edit-empty-icon" aria-hidden="true">&#128722;</div>
								<p class="edit-empty-title">Inicia sesion para pedir</p>
								<p class="help">Necesitas una cuenta para guardar pedidos</p>
								<a class="btn btn-primary"
									href="LoginServlet.do?redirect=RestaurantDetailServlet.do?id=${restaurant.id}">
									Ir a login </a>
							</div>
						</c:when>

						<c:when test="${empty cartDishes}">
							<div class="edit-empty">
								<div class="edit-empty-icon" aria-hidden="true">&#128722;</div>
								<p class="edit-empty-title">Llena tu cesta</p>
								<p class="help">Tu cesta esta vacía</p>
							</div>
						</c:when>

						<c:otherwise>
							<div class="detail-tags" style="margin-bottom: 12px;">
								<c:forEach var="cartDish" items="${cartDishes}">
									<span class="tag">${cartDish.name} -
										${cartDish.price},00 €</span>
								</c:forEach>
							</div>

							<p class="edit-empty-title"
								style="text-align: center; margin-bottom: 12px;">Total:
								${cartTotal},00 €</p>

							<form action="CreateOrderServlet.do" method="post">
								<button class="btn btn-primary" type="submit"
									style="width: 100%;">Confirmar pedido</button>
							</form>
						</c:otherwise>
					</c:choose>
				</section>
			</aside>
		</div>
	</main>
</body>
</html>
