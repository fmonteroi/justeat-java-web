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
				<a href="#">Pedidos de empresa</a> <a href="#">Trabaja como
					repartidor</a> <a href="#">Registra tu negocio</a>
			</nav>

			<a class="top-userlink" href="LoginServlet.do"> <span
				aria-hidden="true">U</span> <span>Inicia sesion</span>
			</a> <span class="flag" aria-hidden="true">ES</span>
			<button class="menu" type="button" aria-label="Menu">=</button>
		</div>
	</header>

	<main class="container" role="main">
		<div class="detail-page-grid">
			<section class="detail-left" aria-label="Detalle del restaurante">
				<div class="card detail-cover-card">
					<img class="detail-cover"
						src="${pageContext.request.contextPath}/images/restaurantes/cume.jpeg"
						alt="Cafeteria del CUMe" width="1400" height="600" />
				</div>

				<article class="card detail-info-card"
					aria-label="Informacion del restaurante">
					<header class="detail-head">
						<div class="detail-head-left">
							<h1 class="detail-name">${restaurant.name}</h1>

							<p class="detail-sub">
								<span class="detail-stars" aria-hidden="true">*****</span> <strong
									class="detail-score">5</strong> <span class="detail-count">(5.000+)</span>

								<span class="detail-dot" aria-hidden="true">.</span> <span
									class="detail-meta">${restaurant.minPrice}-${restaurant.maxPrice}
									EUR</span> <span class="detail-dot" aria-hidden="true">.</span> <span
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
										href="EditRestaurantServlet.do?id=${restaurant.id}">Editar restaurante</a>
								</p>
							</c:if>

						</div>
					</header>

					<div class="detail-data">
						<div class="detail-line">
							<span class="detail-ico" aria-hidden="true">o</span>
							<div>
								<strong>Direccion</strong>
								<p>${restaurant.address}</p>
							</div>
						</div>

						<div class="detail-line">
							<span class="detail-ico" aria-hidden="true">T</span>
							<div>
								<strong>Telefono</strong>
								<p>
									<a href="tel:${restaurant.telephone}">${restaurant.telephone}</a>
								</p>
							</div>
						</div>

						<div class="detail-line">
							<span class="detail-ico" aria-hidden="true">@</span>
							<div>
								<strong>Correo de contacto</strong>
								<p>
									<a href="mailto:${restaurant.contactEmail}">${restaurant.contactEmail}</a>
								</p>
							</div>
						</div>

						<div class="detail-line">
							<span class="detail-ico" aria-hidden="true">#</span>
							<div>
								<strong>Categorías</strong>
								<div class="detail-tags">
									<c:forEach var="category" items="${categories}">
										<span class="tag">${category.name}</span>
									</c:forEach>
								</div>
							</div>
						</div>

						<div class="detail-line">
							<span class="detail-ico" aria-hidden="true">U</span>
							<div>
								<strong>Propietario</strong>
								<p>${owner.name}${owner.surname}</p>
							</div>
						</div>

					</div>
				</article>

				<article class="card detail-menu-card"
					aria-label="Carta del restaurante">
					<header class="menu-head">
						<h2 class="menu-title">Carta de la Cafeteria del CUMe</h2>
					</header>

					<section class="menu-row" aria-label="Destacados">
						<article class="dish">
							<img class="dish-img"
								src="${pageContext.request.contextPath}/images/platos_cume/menu_del_dia.jpeg"
								alt="Menu del dia" />
							<div class="dish-info">
								<p class="dish-name">Menu del dia</p>
								<div class="dish-bottom">
									<span class="dish-price">9,00 EUR</span>
									<button class="dish-plus" type="button">+</button>
								</div>
							</div>
						</article>

						<article class="dish">
							<img class="dish-img"
								src="${pageContext.request.contextPath}/images/platos_cume/cartucho_patatas.jpg"
								alt="Cartucho de patatas" />
							<div class="dish-info">
								<p class="dish-name">Cartucho de Patatas</p>
								<div class="dish-bottom">
									<span class="dish-price">2,00 EUR</span>
									<button class="dish-plus" type="button">+</button>
								</div>
							</div>
						</article>
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

					<div class="edit-empty">
						<div class="edit-empty-icon" aria-hidden="true">[]</div>
						<p class="edit-empty-title">Llena tu cesta</p>
						<p class="help">Tu cesta esta vacia</p>
					</div>
				</section>
			</aside>
		</div>
	</main>
</body>
</html>
