<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<!doctype html>
<html lang="es">
<head>
<meta charset="utf-8" />
<meta name="viewport" content="width=device-width, initial-scale=1" />
<title>${restaurant.name}</title>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/styles.css" />
<script src="${pageContext.request.contextPath}/js/restaurant_detail.js"
	defer></script>
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
									class="detail-score"><fmt:formatNumber
											value="${restaurant.gradesAverage}" minFractionDigits="1"
											maxFractionDigits="1" /></strong> <span
									class="detail-count">(${reviewsCount} valoraciones)</span> <span
									class="detail-dot" aria-hidden="true">•</span> <span
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
							<span class="detail-ico" aria-hidden="true">&#128221;</span>
							<div>
								<strong>Descripcion</strong>
								<p>${restaurant.description}</p>
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
										<a class="tag"
											href="ListRestaurantServlet.do?search=${category.name}&category=${category.id}">
											${category.name} </a>
									</c:forEach>
								</div>
							</div>
						</div>
					</div>
				</article>

				<article class="card detail-menu-card detail-switch-card"
					aria-label="Carta y valoraciones">
					<div class="detail-switch-tabs" role="tablist"
						aria-label="Contenido del restaurante">
						<button class="detail-switch-tab is-active" type="button"
							data-target="menu-panel" aria-selected="true">Carta</button>
						<button class="detail-switch-tab" type="button"
							data-target="reviews-panel" aria-selected="false">
							Valoraciones</button>
					</div>

					<section id="menu-panel" class="detail-switch-panel is-active"
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
											<span class="menu-item-price"><fmt:formatNumber
													value="${dish.price}" minFractionDigits="2"
													maxFractionDigits="2" /> €</span>

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
					</section>

					<section id="reviews-panel" class="detail-switch-panel"
						aria-label="Valoraciones del restaurante">
						<header class="menu-head">
							<h2 class="menu-title">Valoraciones</h2>
							<p class="help">
								<strong>${reviewsCount}</strong> valoraciones registradas
							</p>
						</header>

						<c:choose>
							<c:when test="${sessionScope.user != null}">
								<section class="menu-section" aria-label="Tu valoracion">
									<h3 class="section-title">
										<c:choose>
											<c:when test="${not empty myReview}">Tu valoracion</c:when>
											<c:otherwise>Añadir valoracion</c:otherwise>
										</c:choose>
									</h3>

									<form action="ReviewRestaurantServlet.do" method="post">
										<input type="hidden" name="restaurantId"
											value="${restaurant.id}" />

										<div class="field">
											<label for="grade">Puntuacion</label> <select id="grade"
												name="grade">
												<option value="1"
													<c:if test="${not empty myReview && myReview.grade == 1}">selected</c:if>>1</option>
												<option value="2"
													<c:if test="${not empty myReview && myReview.grade == 2}">selected</c:if>>2</option>
												<option value="3"
													<c:if test="${empty myReview || myReview.grade == 3}">selected</c:if>>3</option>
												<option value="4"
													<c:if test="${not empty myReview && myReview.grade == 4}">selected</c:if>>4</option>
												<option value="5"
													<c:if test="${not empty myReview && myReview.grade == 5}">selected</c:if>>5</option>
											</select>
										</div>

										<div class="field">
											<label for="review">Comentario</label>
											<textarea id="review" name="review" rows="4">${not empty myReview ? myReview.review : ''}</textarea>
										</div>

										<button class="btn btn-primary" type="submit">
											<c:choose>
												<c:when test="${not empty myReview}">Guardar cambios</c:when>
												<c:otherwise>Publicar valoracion</c:otherwise>
											</c:choose>
										</button>
									</form>
								</section>
							</c:when>

							<c:otherwise>
								<section class="menu-section" aria-label="Acceso a valoraciones">
									<p class="help">Inicia sesion para valorar este
										restaurante.</p>
									<a class="btn btn-primary"
										href="LoginServlet.do?redirect=RestaurantDetailServlet.do?id=${restaurant.id}">
										Inicia sesion </a>
								</section>
							</c:otherwise>
						</c:choose>

						<section class="menu-section" aria-label="Lista de valoraciones">
							<h3 class="section-title">Opiniones de usuarios</h3>

							<c:choose>
								<c:when test="${empty reviewsList}">
									<p class="help">Todavia no hay valoraciones para este
										restaurante.</p>
								</c:when>

								<c:otherwise>
									<div class="menu-list" role="list">
										<c:forEach var="reviewItem" items="${reviewsList}">
											<article class="menu-item" role="listitem">
												<div class="menu-item-main">
													<h4 class="menu-item-title">${reviewItem.second.name}
														${reviewItem.second.surname}</h4>

													<p class="menu-item-desc">
														<strong>Puntuacion:</strong> ${reviewItem.first.grade}/5
													</p>

													<c:if test="${not empty reviewItem.first.review}">
														<p class="menu-item-desc">${reviewItem.first.review}</p>
													</c:if>
												</div>

												<div class="menu-item-side">
													<c:if test="${reviewItem.third}">
														<span class="tag">Tu valoracion</span>
													</c:if>
												</div>
											</article>
										</c:forEach>
									</div>
								</c:otherwise>
							</c:choose>
						</section>
					</section>
				</article>
				<article class="card detail-menu-card"
					aria-label="Restaurantes relacionados">
					<header class="menu-head">
						<h2 class="menu-title">Restaurantes relacionados</h2>
						<p class="help">Opciones similares por categoria, ciudad o
							precio</p>
					</header>

					<c:choose>
						<c:when test="${empty relatedRestaurants}">
							<div class="edit-empty">
								<div class="edit-empty-icon" aria-hidden="true">&#127869;</div>
								<p class="edit-empty-title">No hay restaurantes relacionados</p>
								<p class="help">Todavia no hemos encontrado opciones
									parecidas</p>
							</div>
						</c:when>

						<c:otherwise>
							<div class="menu-list" role="list">
								<c:forEach var="relatedItem" items="${relatedRestaurants}">
									<article class="menu-item" role="listitem">
										<div class="menu-item-main">
											<h4 class="menu-item-title">${relatedItem.first.name}</h4>

											<p class="menu-item-desc">${relatedItem.first.city}·
												${relatedItem.first.minPrice}-${relatedItem.first.maxPrice}
												€</p>

											<div class="detail-tags">
												<c:forEach var="cat" items="${relatedItem.third}">
													<span class="tag">${cat.name}</span>
												</c:forEach>
											</div>
										</div>

										<div class="menu-item-side">
											<span class="menu-item-price"><fmt:formatNumber
													value="${relatedItem.first.gradesAverage}"
													minFractionDigits="1" maxFractionDigits="1" />/5</span>
											<a class="btn btn-ghost btn-mini"
												href="RestaurantDetailServlet.do?id=${relatedItem.first.id}">
												Ver </a>
										</div>
									</article>
								</c:forEach>
							</div>
						</c:otherwise>
					</c:choose>
				</article>

				<article class="card detail-menu-card"
					aria-label="Redes sociales del restaurante">
					<header class="menu-head">
						<h2 class="menu-title">Redes sociales</h2>
						<p class="help">Sigue al restaurante en sus perfiles</p>
					</header>

					<c:choose>
						<c:when
							test="${empty restaurant.instagramUrl and empty restaurant.facebookUrl and empty restaurant.xUrl}">
							<div class="edit-empty">
								<div class="edit-empty-icon" aria-hidden="true">&#128241;</div>
								<p class="edit-empty-title">Sin redes sociales disponibles</p>
								<p class="help">Este restaurante todavia no ha añadido
									perfiles</p>
							</div>
						</c:when>

						<c:otherwise>
							<div class="detail-tags">
								<c:if test="${not empty restaurant.instagramUrl}">
									<a class="tag" href="${restaurant.instagramUrl}"
										target="_blank" rel="noopener noreferrer"> Instagram </a>
								</c:if>

								<c:if test="${not empty restaurant.facebookUrl}">
									<a class="tag" href="${restaurant.facebookUrl}" target="_blank"
										rel="noopener noreferrer"> Facebook </a>
								</c:if>

								<c:if test="${not empty restaurant.xUrl}">
									<a class="tag" href="${restaurant.xUrl}" target="_blank"
										rel="noopener noreferrer"> X / Twitter </a>
								</c:if>
							</div>
						</c:otherwise>
					</c:choose>
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
							<div class="menu-list" role="list">
								<c:forEach var="cartDish" items="${cartDishes}">
									<article class="menu-item" role="listitem">
										<div class="menu-item-main">
											<h4 class="menu-item-title">${cartDish.name}</h4>
											<p class="menu-item-desc"><fmt:formatNumber
													value="${cartDish.price}" minFractionDigits="2"
													maxFractionDigits="2" />€</p>
										</div>

										<div class="menu-item-side">
											<form action="RemoveDishFromCartServlet.do" method="post">
												<input type="hidden" name="dishId" value="${cartDish.id}" />
												<button class="btn btn-ghost btn-mini" type="submit">Quitar</button>
											</form>
										</div>
									</article>
								</c:forEach>
							</div>

							<p class="edit-empty-title"
								style="text-align: center; margin-bottom: 12px;">Total:
								<fmt:formatNumber value="${cartTotal}"
									minFractionDigits="2" maxFractionDigits="2" /> €</p>

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
