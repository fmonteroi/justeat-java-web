<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!doctype html>
<html lang="es">
  <head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>Listado</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css" />
  </head>

  <body class="page-listado">
    <header class="top top-listado">
      <a class="back" href="search.jsp" aria-label="Volver">&lt;</a>

      <a class="brand" href="LoginServlet.do">
        <img src="${pageContext.request.contextPath}/images/logo_justeat.svg" alt="Logo Just Eat" width="30" height="30" />
        <strong>JUST EAT</strong>
      </a>

      <div class="top-mid">
        <div class="pill addr" id="addrPill">
          <span aria-hidden="true">o</span>
          <span id="addrText">Calle Santa Eulalia 2, 06800 Merida</span>
        </div>

        <nav class="pill tabs" aria-label="Tipo de pedido">
          <a class="is-active" href="#">Entrega</a>
          <a href="#">Recogida</a>
        </nav>
      </div>

      <div class="top-right">
        <span class="flag" aria-hidden="true">ES</span>
        <button class="menu" type="button" aria-label="Menu">=</button>
      </div>
    </header>

    <main class="container">
      <section class="layout">
        <aside class="filters">
          <p class="f-title">
            <strong><span class="countText">${fn:length(restaurantsList)}</span> establecimientos</strong>
          </p>

          <form class="stack" action="#" method="get">
            <label class="toggle">
              <span>Abiertos ahora</span>
              <input type="checkbox" name="abiertos" />
              <i aria-hidden="true"></i>
            </label>

            <label class="toggle">
              <span>Nuevo</span>
              <input type="checkbox" name="nuevo" />
              <i aria-hidden="true"></i>
            </label>

            <label class="toggle">
              <span>Gastos de entrega gratis</span>
              <input type="checkbox" name="envio_gratis" />
              <i aria-hidden="true"></i>
            </label>

            <fieldset>
              <legend>Importe minimo del pedido</legend>
              <label><input type="radio" name="min" value="all" checked /> Mostrar todo</label>
              <label><input type="radio" name="min" value="10" /> 10,00 EUR o menos</label>
              <label><input type="radio" name="min" value="15" /> 15,00 EUR o menos</label>
            </fieldset>

            <fieldset>
              <legend>Puntuacion</legend>
              <div class="stars">*****</div>
            </fieldset>
          </form>
        </aside>

        <section class="results">
          <header class="results-top">
            <form class="searchbar" action="#" method="get">
              <span aria-hidden="true">?</span>
              <input type="search" name="q" placeholder="Buscas restaurantes?" />
            </form>

            <label class="orderby">
              <span>Ordenar por</span>
              <select name="orden">
                <option selected>Resenas</option>
                <option>Tiempo de entrega</option>
                <option>Precio</option>
              </select>
            </label>

            <button class="sliders" type="button" aria-label="Filtros rapidos">:</button>
          </header>

          <nav class="catbar" aria-label="Categorias">
            <a class="cat is-selected" href="#">
              <img src="${pageContext.request.contextPath}/images/categorias/destacados.webp" alt="" />
              <span>Destacados</span>
            </a>
            <a class="cat" href="#"><img src="${pageContext.request.contextPath}/images/categorias/tradicional.webp" alt="" /><span>Tradicional</span></a>
            <a class="cat" href="#"><img src="${pageContext.request.contextPath}/images/categorias/tapas.webp" alt="" /><span>Tapas</span></a>
            <a class="cat" href="#"><img src="${pageContext.request.contextPath}/images/categorias/carnes.png" alt="" /><span>Carnes</span></a>
            <a class="cat" href="#"><img src="${pageContext.request.contextPath}/images/categorias/italiana.webp" alt="" /><span>Italiana</span></a>
            <a class="cat" href="#"><img src="${pageContext.request.contextPath}/images/categorias/mexicana.webp" alt="" /><span>Mexicana</span></a>
            <a class="cat" href="#"><img src="${pageContext.request.contextPath}/images/categorias/china.png" alt="" /><span>China</span></a>
          </nav>

          <div class="match">
            <p><strong><span class="countText">${fn:length(restaurantsList)}</span> establecimientos</strong> cargados desde el servlet</p>
          </div>

          <section class="cards" id="cards">
            <c:forEach var="restaurant" items="${restaurantsList}">
              <article class="r-card">
                <a class="r-card-link" href="RestaurantDetailServlet.do?id=${restaurant.first.id}" aria-label="Ver detalles de ${restaurant.first.name}">
                  <img class="r-img" src="${pageContext.request.contextPath}/images/restaurantes/placeholder.webp" alt="Restaurante" />
                  <div class="r-body">
                    <h3>${restaurant.first.name}</h3>
                    <p class="r-meta">
                      Ciudad: ${restaurant.first.city}
                      &middot; Precio: ${restaurant.first.minPrice}-${restaurant.first.maxPrice} EUR
                    </p>
                    <p class="r-meta">
                      Propietario: ${restaurant.second.name}
                      &middot; Bike Friendly:
                      <c:choose>
                        <c:when test="${restaurant.first.bikeFriendly eq 1}">Si</c:when>
                        <c:otherwise>No</c:otherwise>
                      </c:choose>
                      &middot;
                      <c:choose>
                        <c:when test="${restaurant.first.available eq 1}">Acepta pedidos</c:when>
                        <c:otherwise>No acepta pedidos</c:otherwise>
                      </c:choose>
                    </p>
                    <p class="r-meta">
                      Categorias:
                      <c:forEach var="category" items="${restaurant.third}" varStatus="status">
                        ${category.idct}<c:if test="${!status.last}">, </c:if>
                      </c:forEach>
                    </p>
                  </div>
                </a>
              </article>
            </c:forEach>
          </section>
        </section>
      </section>
    </main>
  </body>
</html>
