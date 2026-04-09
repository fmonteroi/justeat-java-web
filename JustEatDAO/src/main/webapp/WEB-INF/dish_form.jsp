<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!doctype html>
<html lang="es">
<head>
<meta charset="utf-8" />
<meta name="viewport" content="width=device-width, initial-scale=1" />
<title>${formTitle}</title>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/styles.css" />
</head>

<body class="page-editar">
	<header class="top">
		<a class="back" href="EditRestaurantServlet.do?id=${restaurant.id}"
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
						repartidor</span></a> <a href="#"><span class="top-nav-ico"
					aria-hidden="true">&#127970;</span><span>Registra tu negocio</span></a>
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
			<a class="edit-back"
				href="EditRestaurantServlet.do?id=${restaurant.id}"
				aria-label="Volver a editar restaurante">&larr; Volver</a>
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
						<li><a class="edit-sidelink"
							href="ListUserRestaurantServlet.do"><span
								class="edit-sidelink-label">Mis restaurantes</span></a></li>
						<li><a class="edit-sidelink"
							href="EditRestaurantServlet.do?id=${restaurant.id}"><span
								class="edit-sidelink-label">Establecimiento</span></a></li>
						<li><a class="edit-sidelink is-active" href="#"><span
								class="edit-sidelink-label">Carta</span></a></li>
						<li><a class="edit-sidelink" href="LogoutServlet.do"><span
								class="edit-sidelink-label">Salir</span></a></li>
					</ul>
				</nav>
			</aside>

			<section class="edit-main" aria-label="Formulario del plato">
				<article class="card edit-card" aria-label="Formulario de plato">
					<form class="edit-form" action="${formAction}" method="post"
						novalidate>
						<div class="messages" aria-live="polite">
							<c:if test="${not empty messages}">
								<p class="msg msg--error">${messages}</p>
							</c:if>
						</div>

						<input type="hidden" name="restaurantId" value="${restaurant.id}" />

						<c:if test="${not empty dish}">
							<input type="hidden" name="id" value="${dish.id}" />
						</c:if>

						<div class="edit-panels">
							<section class="edit-panel" aria-label="Datos del plato">
								<h2 class="edit-h2">${formTitle}</h2>

								<div class="field">
									<label for="dish-name">Nombre del plato</label> <input
										id="dish-name" name="dish-name" type="text" required
										value="${not empty dish ? dish.name : dishName}" />
								</div>

								<div class="field">
									<label for="dish-description">Descripcion</label>
									<textarea id="dish-description" name="dish-description"
										rows="4" required>${not empty dish ? dish.description : dishDescription}</textarea>
								</div>

								<div class="field">
									<label for="dish-price">Precio</label> <input id="dish-price"
										name="dish-price" type="number" min="0" step="1" required
										value="${not empty dish ? dish.price : dishPrice}" />
								</div>
							</section>
						</div>

						<footer class="edit-actions" aria-label="Acciones del formulario">
							<a class="btn btn-ghost"
								href="EditRestaurantServlet.do?id=${restaurant.id}">Cancelar</a>
							<button class="btn btn-primary" type="submit">Guardar
								plato</button>
						</footer>
					</form>
				</article>
			</section>
		</div>
	</main>
</body>
</html>
