<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!doctype html>
<html lang="es">
<head>
<meta charset="utf-8" />
<meta name="viewport" content="width=device-width, initial-scale=1" />
<title>Mi perfil</title>
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
			</a> <span class="flag" aria-hidden="true">ES</span>
			<button class="menu" type="button" aria-label="Menu">=</button>
		</div>
	</header>

	<main class="container" role="main">
		<div class="edit-header">
			<a class="edit-back" href="ListRestaurantServlet.do"
				aria-label="Volver">&larr; Volver</a>
		</div>

		<div class="edit-layout">
			<aside class="edit-side" aria-label="Menu lateral">
				<nav class="edit-sidecard" aria-label="Secciones privadas">
					<ul class="edit-sidemenu">
					
						<li><a class="edit-sidelink is-active"
							href="EditUserServlet.do"> <span class="edit-sidelink-label">Mi
									perfil</span>
						</a></li>
						
						<li><a class="edit-sidelink" href="ListUserOrderServlet.do">
								<span class="edit-sidelink-label">Mis pedidos</span>
						</a></li>
						
						<li><a class="edit-sidelink" href="ListCategoryServlet.do">
								<span class="edit-sidelink-label">Mis categorías</span>
						</a></li>
						
						<li><a class="edit-sidelink"
							href="ListUserRestaurantServlet.do"> <span
								class="edit-sidelink-label">Mis restaurantes</span>
						</a></li>

						<li><a class="edit-sidelink" href="LogoutServlet.do"> <span
								class="edit-sidelink-label">Salir</span>
						</a></li>
					</ul>
				</nav>

				<form action="EditUserServlet.do" method="post" novalidate>
					<input type="hidden" name="action" value="delete" />
					<button class="btn btn-danger" type="submit">Eliminar
						cuenta</button>
				</form>
			</aside>

			<section class="edit-main" aria-label="Mi perfil">
				<article class="card edit-card" aria-label="Formulario de perfil">
					<form id="userEditForm" class="edit-form"
						action="EditUserServlet.do" method="post" novalidate>
						<div class="messages" aria-live="polite">
							<c:if test="${not empty messages}">
								<p class="msg msg--error">${messages}</p>
							</c:if>

							<c:if test="${not empty messagesOk}">
								<p class="msg msg--ok">${messagesOk}</p>
							</c:if>
						</div>

						<input type="hidden" name="action" value="update" />

						<div class="edit-panels">
							<section class="edit-panel" aria-label="Datos personales">
								<h2 class="edit-h2">Mi perfil</h2>

								<div class="field">
									<label for="name">Nombre</label> <input id="name" name="name"
										type="text" autocomplete="given-name" required
										value="${profileUser.name}" />
								</div>

								<div class="field">
									<label for="surname">Apellidos</label> <input id="surname"
										name="surname" type="text" autocomplete="family-name" required
										value="${profileUser.surname}" />
								</div>

								<div class="field">
									<label for="email">Correo electrónico</label> <input id="email"
										name="email" type="email" autocomplete="email" required
										value="${profileUser.email}" />
								</div>
							</section>

							<section class="edit-panel" aria-label="Seguridad">
								<h2 class="edit-h2">Seguridad</h2>

								<div class="field">
									<label for="password">Nueva contraseña</label>
									<div class="pw">
										<input id="password" name="password" type="password"
											autocomplete="new-password" placeholder="Nueva contraseña" />
									</div>
									<p class="hint">Rellena estos campos solo si quieres
										cambiar tu contraseña.</p>
								</div>

								<div class="field">
									<label for="passwordConfirm">Confirmar nueva contraseña</label>
									<div class="pw">
										<input id="passwordConfirm" name="passwordConfirm"
											type="password" autocomplete="new-password"
											placeholder="Repite la nueva contraseña" />
									</div>
								</div>
							</section>
						</div>

						<footer class="edit-actions" aria-label="Acciones del perfil">
							<a class="btn btn-ghost" href="ListRestaurantServlet.do">Cancelar</a>
							<button class="btn btn-primary" type="submit">Guardar
								cambios</button>
						</footer>
					</form>
				</article>
			</section>
		</div>
	</main>
</body>
</html>
