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
<title>Mis pedidos</title>
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
			</a> <span class="flag" aria-hidden="true">ES</span>
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

						<li><a class="edit-sidelink is-active"
							href="ListUserOrderServlet.do"> <span
								class="edit-sidelink-label">Mis pedidos</span>
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
			</aside>

			<section class="edit-main" aria-label="Mis pedidos">
				<article class="card edit-card" aria-label="Historico de pedidos">
					<header class="menu-head">
						<h2 class="menu-title">Mis pedidos</h2>
						<p class="help">
							<strong>${fn:length(ordersList)} pedidos</strong> realizados por
							tu cuenta
						</p>
					</header>

					<c:choose>
						<c:when test="${empty ordersList}">
							<div class="edit-empty">
								<div class="edit-empty-icon" aria-hidden="true">&#128722;</div>
								<p class="edit-empty-title">Todavía no has hecho pedidos</p>
								<p class="help">Cuando confirmes uno, aparecera aquí</p>
							</div>
						</c:when>

						<c:otherwise>
							<div class="menu-list" role="list">
								<c:forEach var="orderItem" items="${ordersList}">
									<article class="menu-item" role="listitem">
										<div class="menu-item-main">
											<h4 class="menu-item-title">Pedido
												#${orderItem.first.id}</h4>

											<p class="menu-item-desc">
												Restaurante:
												<c:choose>
													<c:when test="${orderItem.second != null}">
                              ${orderItem.second.name}
                            </c:when>
													<c:otherwise>
                              Restaurante no disponible
                            </c:otherwise>
												</c:choose>
											</p>

											<div class="detail-tags">
												<c:forEach var="dish" items="${orderItem.third}">
													<span class="tag">${dish.first.name} x${dish.second} - <fmt:formatNumber
															value="${dish.third}" minFractionDigits="2"
															maxFractionDigits="2" /> €
													</span>
												</c:forEach>
											</div>
										</div>

										<div class="menu-item-side">
											<span class="menu-item-price"><fmt:formatNumber
													value="${orderItem.first.totalPrice}" minFractionDigits="2"
													maxFractionDigits="2" /> €</span>

											<c:if test="${orderItem.second != null}">
												<a class="btn btn-ghost btn-mini"
													href="RestaurantDetailServlet.do?id=${orderItem.second.id}">
													Ver restaurante </a>
											</c:if>
										</div>
									</article>
								</c:forEach>
							</div>
						</c:otherwise>
					</c:choose>
				</article>
			</section>
		</div>
	</main>
</body>
</html>
