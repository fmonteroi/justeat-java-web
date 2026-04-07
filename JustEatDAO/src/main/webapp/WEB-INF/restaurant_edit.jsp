<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="restaurant" value="${sessionScope.restaurant}" />
<!doctype html>
<html lang="es">
<head>
<meta charset="utf-8" />
<meta name="viewport" content="width=device-width, initial-scale=1" />
<title>Editar restaurante</title>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/styles.css" />
<script src="${pageContext.request.contextPath}/js/restaurant_edit.js"
	defer></script>
</head>

<body class="page-editar">
	<header class="top">
		<a class="back" href="RestaurantDetailServlet.do?id=${restaurant.id}"
			aria-label="Volver">&lt;</a> <a class="brand" href="LoginServlet.do"
			aria-label="Inicio"> <img
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
				aria-hidden="true">U</span> <span>${sessionScope.user.name}
					${sessionScope.user.surname}</span>
			</a> <span class="badge" aria-hidden="true">2</span> <span class="flag"
				aria-hidden="true">ES</span>
			<button class="menu" type="button" aria-label="Menu">=</button>
		</div>
	</header>

	<main class="container" role="main">
		<div class="edit-header">
			<a class="edit-back"
				href="RestaurantDetailServlet.do?id=${restaurant.id}"
				aria-label="Volver a detalles">Volver</a>
		</div>

		<div class="edit-layout">
			<aside class="edit-side" aria-label="Menu lateral">
				<nav class="edit-sidecard" aria-label="Secciones">
					<ul class="edit-sidemenu">
						<li><a class="edit-sidelink" href="ListRestaurantServlet.do">Mis
								restaurantes</a></li>
						<li><a class="edit-sidelink"
							href="CreateRestaurantServlet.do">Añadir restaurante</a></li>
						<li><a class="edit-sidelink is-active" href="#">Establecimiento</a></li>
						<li><a class="edit-sidelink" href="#restaurant-menu">Carta</a></li>
						<li><a class="edit-sidelink" href="LogoutServlet.do">Salir</a></li>
					</ul>
				</nav>

				<form action="DeleteRestaurantServlet.do" method="post">
					<input type="hidden" name="id" value="${restaurant.id}" />
					<button class="btn btn-danger" type="submit">Eliminar
						restaurante</button>
				</form>
			</aside>

			<section class="edit-main" aria-label="Gestion del restaurante">
				<article class="card edit-card" aria-label="Formulario de edicion">
					<form id="editForm" class="edit-form"
						action="EditRestaurantServlet.do?id=${restaurant.id}"
						method="post" novalidate>
						<div class="messages" aria-live="polite">
							<c:if test="${not empty messages}">
								<p class="msg msg--error">${messages}</p>
							</c:if>
						</div>

						<div class="edit-panels">
							<section class="edit-panel" aria-label="Datos del restaurante">
								<h2 class="edit-h2">Datos del restaurante</h2>

								<div class="field">
									<label for="restaurant-name">Nombre</label> <input
										id="restaurant-name" name="restaurant-name" type="text"
										required maxlength="80" value="${restaurant.name}" />
								</div>

								<div class="field">
									<label for="restaurant-address">Direccion</label>
									<textarea id="restaurant-address" name="restaurant-address"
										rows="2" required>${restaurant.address}</textarea>
								</div>

								<div class="field">
									<label for="restaurant-tel">Telefono</label> <input
										id="restaurant-tel" name="restaurant-tel" type="tel" required
										placeholder="924311151" value="${restaurant.telephone}" />
								</div>

								<div class="field">
									<label for="restaurant-city">Ciudad</label> <input
										id="restaurant-city" name="restaurant-city" type="text"
										required value="${restaurant.city}" />
								</div>

								<fieldset class="edit-fieldset">
									<legend>Rango de precio</legend>

									<div class="edit-price">
										<div class="field">
											<label for="min-price">Minimo (EUR)</label> <input
												id="min-price" name="min-price" type="number" required
												min="0" step="1" value="${restaurant.minPrice}" />
										</div>

										<div class="field">
											<label for="max-price">Maximo (EUR)</label> <input
												id="max-price" name="max-price" type="number" required
												min="0" step="1" value="${restaurant.maxPrice}" />
										</div>
									</div>

									<p class="help">No se permiten precios negativos y el
										maximo debe ser mayor o igual que el minimo.</p>
								</fieldset>

								<div class="edit-rating">
									<span class="edit-rating-label">Media de valoraciones</span> <span
										class="edit-rating-stars" aria-hidden="true">*****</span> <span
										class="edit-rating-value">${restaurant.gradesAverage}</span>
								</div>
							</section>

							<section class="edit-panel" aria-label="Imagen y contacto">
								<h2 class="edit-h2">Imagen y contacto</h2>

								<figure class="edit-photo">
									<img id="photoPreview"
										src="${pageContext.request.contextPath}/images/restaurantes/cume.jpeg"
										alt="Foto del restaurante" />
									<figcaption class="sr-only">Vista previa de la
										imagen</figcaption>
								</figure>

								<div class="field">
									<label for="restaurant-email">Correo de contacto</label> <input
										id="restaurant-email" name="restaurant-email" type="email"
										required placeholder="contacto@restaurante.com"
										value="${restaurant.contactEmail}" />
								</div>

								<fieldset class="edit-fieldset">
									<legend>Categorias</legend>
									<div class="edit-checks">
										<c:forEach var="category" items="${sessionScope.categories}">
											<label class="check" for="cat_${category.first.id}">
												<input id="cat_${category.first.id}"
												name="restaurant-category" type="checkbox"
												value="${category.first.id}"
												<c:if test="${category.second}">checked</c:if> />
												${category.first.name}
											</label>
										</c:forEach>
									</div>
								</fieldset>

								<fieldset class="edit-fieldset">
									<legend>Bike Friendly</legend>
									<div class="edit-radios">
										<label class="radio" for="bike_yes"> <input
											id="bike_yes" type="radio" name="bikefriendly" value="1"
											<c:if test="${restaurant.bikeFriendly eq 1}">checked</c:if> />
											Si
										</label> <label class="radio" for="bike_no"> <input
											id="bike_no" type="radio" name="bikefriendly" value="0"
											<c:if test="${restaurant.bikeFriendly eq 0}">checked</c:if> />
											No
										</label>
									</div>
								</fieldset>

								<fieldset class="edit-fieldset">
									<legend>Estado del restaurante</legend>
									<div class="edit-radios">
										<label class="radio" for="available_yes"> <input
											id="available_yes" type="radio" name="available" value="1"
											<c:if test="${restaurant.available eq 1}">checked</c:if> />
											Acepta pedidos
										</label> <label class="radio" for="available_no"> <input
											id="available_no" type="radio" name="available" value="0"
											<c:if test="${restaurant.available eq 0}">checked</c:if> />
											No acepta pedidos
										</label>
									</div>
								</fieldset>
							</section>
						</div>

						<footer class="edit-actions" aria-label="Acciones del formulario">
							<button class="btn btn-ghost" type="reset">Cancelar</button>
							<button class="btn btn-primary" type="submit">Guardar
								cambios</button>
						</footer>
					</form>
				</article>

				<article id="restaurant-menu" class="card edit-card menu-card"
					aria-label="Carta del restaurante">
					<header class="menu-head">
						<h2 class="menu-title">Carta del restaurante</h2>

						<nav class="menu-tabs" aria-label="Categorias de la carta">
							<a class="menu-tab is-active" href="#">Destacados</a> <a
								class="menu-tab" href="#">Menu del dia</a> <a class="menu-tab"
								href="#">Desayunos</a> <a class="menu-tab" href="#">Platos
								combinados</a> <a class="menu-tab" href="#">Complementos</a> <a
								class="menu-tab" href="#">Bebidas</a>
						</nav>
					</header>

					<section class="menu-row" aria-label="Platos destacados">
						<article class="dish">
							<img class="dish-img"
								src="${pageContext.request.contextPath}/images/platos_cume/menu_del_dia.jpeg"
								alt="Menu del dia" />
							<div class="dish-info">
								<p class="dish-name">Menu del dia</p>
								<div class="dish-bottom">
									<span class="dish-price">9,00 EUR</span>
									<button class="dish-edit" type="button">Editar</button>
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
									<button class="dish-edit" type="button">Editar</button>
								</div>
							</div>
						</article>
					</section>
				</article>
			</section>

			<aside class="edit-cart" aria-label="Cesta">
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
