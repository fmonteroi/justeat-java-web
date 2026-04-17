<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!doctype html>
<html lang="es">
<head>
<meta charset="utf-8" />
<meta name="viewport" content="width=device-width, initial-scale=1" />
<title>Añadir restaurante</title>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/styles.css" />
</head>

<body class="page-editar">
	<header class="top">
		<a class="back" href="ListUserRestaurantServlet.do"
			aria-label="Volver">&lt;</a> <a class="brand" href="LoginServlet.do"
			aria-label="Inicio"> <img
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
			<a class="edit-back" href="ListUserRestaurantServlet.do"
				aria-label="Volver">&larr; Volver</a>
		</div>

		<div class="edit-layout">
			<aside class="edit-side" aria-label="Menu lateral">
				<nav class="edit-sidecard" aria-label="Secciones">
					<ul class="edit-sidemenu">
						<li><a class="edit-sidelink" href="EditUserServlet.do"><span
								class="edit-sidelink-label">Mi perfil</span></a></li>
								
						<li><a class="edit-sidelink" href="ListUserOrderServlet.do">
								<span class="edit-sidelink-label">Mis pedidos</span>
						</a></li>
						
						<li><a class="edit-sidelink" href="ListCategoryServlet.do">
								<span class="edit-sidelink-label">Mis categorías</span>
						</a></li>
						
						<li><a class="edit-sidelink"
							href="ListUserRestaurantServlet.do"><span
								class="edit-sidelink-label">Mis restaurantes</span></a></li>
								
						<li><a class="edit-sidelink is-active"
							href="CreateRestaurantServlet.do"><span
								class="edit-sidelink-label">&rarr; Añadir restaurante</span></a></li>


						<li><a class="edit-sidelink" href="LogoutServlet.do"><span
								class="edit-sidelink-label">Salir</span></a></li>
					</ul>
				</nav>
			</aside>

			<section class="edit-main" aria-label="Alta del restaurante">
				<article class="card edit-card" aria-label="Formulario de alta">
					<form id="createRestaurantForm" class="edit-form"
						action="CreateRestaurantServlet.do" method="post" novalidate>
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
										required maxlength="80" value="${restaurantName}" />
								</div>

								<div class="field">
									<label for="restaurant-address">Dirección</label>
									<textarea id="restaurant-address" name="restaurant-address"
										rows="2" required>${restaurantAddress}</textarea>
								</div>

								<div class="field">
									<label for="restaurant-description">Descripción</label>
									<textarea id="restaurant-description"
										name="restaurant-description" rows="4" required>${restaurantDescription}</textarea>
								</div>

								<div class="field">
									<label for="restaurant-tel">Teléfono</label> <input
										id="restaurant-tel" name="restaurant-tel" type="tel" required
										placeholder="924311151" value="${restaurantTel}" />
								</div>

								<div class="field">
									<label for="restaurant-city">Ciudad</label> <input
										id="restaurant-city" name="restaurant-city" type="text"
										required value="${restaurantCity}" />
								</div>

								<fieldset class="edit-fieldset">
									<legend>Rango de precio</legend>

									<div class="edit-price">
										<div class="field">
											<label for="min-price">Mínimo (€)</label> <input
												id="min-price" name="min-price" type="number" required
												min="0" step="1" value="${minPrice}" />
										</div>

										<div class="field">
											<label for="max-price">Máximo (€)</label> <input
												id="max-price" name="max-price" type="number" required
												min="0" step="1" value="${maxPrice}" />
										</div>
									</div>

									<p class="help">No se permiten precios negativos y el
										máximo debe ser mayor o igual que el mínimo.</p>
								</fieldset>

								<div class="edit-rating">
									<span class="edit-rating-label">Media de valoraciones</span> <span
										class="help">Se inicializará automaticamente al crear
										el restaurante.</span>
								</div>
							</section>

							<section class="edit-panel" aria-label="Contacto y categorias">
								<h2 class="edit-h2">Contacto y categorías</h2>

								<figure class="edit-photo">
									<img
										src="${pageContext.request.contextPath}/images/restaurantes/placeholder.webp"
										alt="Imagen por defecto del restaurante" />
									<figcaption class="sr-only">Imagen provisional del
										restaurante</figcaption>
								</figure>

								<div class="field">
									<label for="restaurant-email">Correo de contacto</label> <input
										id="restaurant-email" name="restaurant-email" type="email"
										required placeholder="contacto@restaurante.com"
										value="${restaurantEmail}" />
								</div>

								<fieldset class="edit-fieldset">
									<legend>Categorías</legend>
									<div class="edit-checks">
										<c:forEach var="category" items="${categories}">
											<label class="check" for="cat_${category.id}"> <input
												id="cat_${category.id}" name="restaurant-category"
												type="checkbox" value="${category.id}" /> ${category.name}
											</label>
										</c:forEach>
									</div>
								</fieldset>

								<fieldset class="edit-fieldset">
									<legend>Bike Friendly</legend>
									<div class="edit-radios">
										<label class="radio" for="bike_yes"> <input
											id="bike_yes" type="radio" name="bikefriendly" value="1"
											checked /> Si
										</label> <label class="radio" for="bike_no"> <input
											id="bike_no" type="radio" name="bikefriendly" value="0" />
											No
										</label>
									</div>
								</fieldset>

								<fieldset class="edit-fieldset">
									<legend>Estado del restaurante</legend>
									<div class="edit-radios">
										<label class="radio" for="available_yes"> <input
											id="available_yes" type="radio" name="available" value="1"
											checked /> Acepta pedidos
										</label> <label class="radio" for="available_no"> <input
											id="available_no" type="radio" name="available" value="0" />
											No acepta pedidos
										</label>
									</div>
								</fieldset>

								<div class="field">
									<label for="instagramUrl">Instagram</label> <input
										id="instagramUrl" name="instagramUrl" type="url"
										value="${instagramUrl}"
										placeholder="https://instagram.com/tu_restaurante" />
								</div>

								<div class="field">
									<label for="facebookUrl">Facebook</label> <input
										id="facebookUrl" name="facebookUrl" type="url"
										value="${facebookUrl}"
										placeholder="https://facebook.com/tu_restaurante" />
								</div>

								<div class="field">
									<label for="xUrl">X / Twitter</label> <input id="xUrl"
										name="xUrl" type="url" value="${xUrl}"
										placeholder="https://x.com/tu_restaurante" />
								</div>
							</section>
						</div>

						<footer class="edit-actions" aria-label="Acciones del formulario">
							<a class="btn btn-ghost" href="ListUserRestaurantServlet.do">Cancelar</a>
							<button class="btn btn-primary" type="submit">Crear
								restaurante</button>
						</footer>
					</form>
				</article>

			</section>
		</div>
	</main>
</body>
</html>
