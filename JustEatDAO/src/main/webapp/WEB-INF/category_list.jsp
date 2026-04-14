<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!doctype html>
<html lang="es">
<head>
<meta charset="utf-8" />
<meta name="viewport" content="width=device-width, initial-scale=1" />
<title>Categorías</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css" />
</head>

<body class="page-editar">
  <header class="top">
    <a class="back" href="EditUserServlet.do" aria-label="Volver">&lt;</a>

    <a class="brand" href="LoginServlet.do" aria-label="Inicio">
      <img src="${pageContext.request.contextPath}/images/logo_justeat.svg" alt="Logo Just Eat" width="30" height="30" />
      <strong>JUST EAT</strong>
    </a>

    <div class="top-right top-right-all">
      <nav class="top-nav" aria-label="Navegacion principal">
        <a href="#"><span class="top-nav-ico" aria-hidden="true">&#128188;</span><span>Pedidos de empresa</span></a>
        <a href="#"><span class="top-nav-ico" aria-hidden="true">&#128692;</span><span>Trabaja como repartidor</span></a>
        <a href="#"><span class="top-nav-ico" aria-hidden="true">&#127970;</span><span>Registra tu negocio</span></a>
      </nav>

      <a class="top-userlink" href="EditUserServlet.do">
        <span aria-hidden="true">&#128100;</span>
        <span>${sessionScope.user.name} ${sessionScope.user.surname}</span>
      </a>

      <span class="flag" aria-hidden="true">ES</span>
      <button class="menu" type="button" aria-label="Menu">=</button>
    </div>
  </header>

  <main class="container" role="main">
    <div class="edit-layout">
      <aside class="edit-side" aria-label="Menu lateral">
        <nav class="edit-sidecard" aria-label="Secciones privadas">
          <ul class="edit-sidemenu">
            <li><a class="edit-sidelink" href="EditUserServlet.do"><span class="edit-sidelink-label">Mi perfil</span></a></li>
            <li><a class="edit-sidelink" href="ListUserOrderServlet.do"><span class="edit-sidelink-label">Mis pedidos</span></a></li>
            <li><a class="edit-sidelink" href="ListUserRestaurantServlet.do"><span class="edit-sidelink-label">Mis restaurantes</span></a></li>
            <li><a class="edit-sidelink" href="CreateRestaurantServlet.do"><span class="edit-sidelink-label">Añadir restaurante</span></a></li>
            <li><a class="edit-sidelink is-active" href="ListCategoryServlet.do"><span class="edit-sidelink-label">Categorías</span></a></li>
            <li><a class="edit-sidelink" href="LogoutServlet.do"><span class="edit-sidelink-label">Salir</span></a></li>
          </ul>
        </nav>
      </aside>

      <section class="edit-main" aria-label="Categorías">
        <article class="card edit-card">
          <header class="menu-head">
            <h2 class="menu-title">Categorías</h2>
          </header>

          <div class="edit-actions" style="margin-bottom: 16px;">
            <a class="btn btn-primary" href="CreateCategoryServlet.do">Añadir categoría</a>
          </div>

          <c:choose>
            <c:when test="${empty categories}">
              <div class="edit-empty">
                <div class="edit-empty-icon" aria-hidden="true">&#127991;</div>
                <p class="edit-empty-title">No hay categorías</p>
              </div>
            </c:when>

            <c:otherwise>
              <div class="menu-list" role="list">
                <c:forEach var="category" items="${categories}">
                  <article class="menu-item" role="listitem">
                    <div class="menu-item-main">
                      <h4 class="menu-item-title">${category.name}</h4>
                    </div>

                    <div class="menu-item-side">
                      <a class="btn btn-ghost btn-mini" href="EditCategoryServlet.do?id=${category.id}">Editar</a>

                      <form action="DeleteCategoryServlet.do" method="post">
                        <input type="hidden" name="id" value="${category.id}" />
                        <button class="btn btn-danger btn-mini" type="submit">Borrar</button>
                      </form>
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
