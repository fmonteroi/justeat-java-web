<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!doctype html>
<html lang="es">
<head>
<meta charset="utf-8" />
<meta name="viewport" content="width=device-width, initial-scale=1" />
<title>Busqueda</title>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/styles.css" />
</head>
<body class="page-busqueda">
	<header class="top top-detalle">
		<a class="back" href="SearchServlet.do" aria-label="Volver">&lt;</a>

		<a class="brand" href="SearchServlet.do" aria-label="Inicio"> <img
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

	<main>
		<section class="hero" aria-label="Busqueda por direccion">
			<div class="hero-grid">
				<div class="hero-left">
					<div class="hero-box">
						<h1>Pide comida y mucho mas</h1>
						<p class="muted">Restaurantes y tiendas de alimentacion que
							realizan entregas cerca de ti</p>

						<form class="search" action="ListRestaurantServlet.do"
							method="get">
							<input name="search" type="text"
								placeholder="Busca por ciudad, nombre o descripción del restaurante"
								autocomplete="address-line1" required />
							<button class="boton" type="submit">Buscar</button>
						</form>

						<div class="hero-cats">
							<h2>Categorias populares</h2>
							<p class="muted">Explora los tipos de cocina mas populares y
								encuentra tus tiendas y restaurantes favoritos.</p>
						</div>
					</div>
				</div>

				<div class="hero-right" aria-hidden="true">
					<img class="hero-right-img"
						src="${pageContext.request.contextPath}/images/logo_busqueda.png"
						alt="" />
				</div>
			</div>
		</section>

		<section class="container" aria-label="Categorias populares">
			<h2>Categorias populares</h2>
			<div class="cats">
				<a href="#"><img
					src="${pageContext.request.contextPath}/images/categorias/destacados.webp"
					alt="" /><span>Destacados</span></a> <a href="#"><img
					src="${pageContext.request.contextPath}/images/categorias/tradicional.webp"
					alt="" /><span>Tradicional</span></a> <a href="#"><img
					src="${pageContext.request.contextPath}/images/categorias/tapas.webp"
					alt="" /><span>Tapas</span></a> <a href="#"><img
					src="${pageContext.request.contextPath}/images/categorias/carnes.png"
					alt="" /><span>Carnes</span></a> <a href="#"><img
					src="${pageContext.request.contextPath}/images/categorias/italiana.webp"
					alt="" /><span>Italiana</span></a> <a href="#"><img
					src="${pageContext.request.contextPath}/images/categorias/mexicana.webp"
					alt="" /><span>Mexicana</span></a> <a href="#"><img
					src="${pageContext.request.contextPath}/images/categorias/china.png"
					alt="" /><span>China</span></a>
			</div>
		</section>
	</main>
</body>
</html>
