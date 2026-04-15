<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!doctype html>
<html lang="es">
<head>
<meta charset="utf-8" />
<meta name="viewport" content="width=device-width, initial-scale=1" />
<title><c:choose><c:when test="${not empty category}">Editar categoría</c:when><c:otherwise>Añadir categoría</c:otherwise></c:choose></title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css" />
</head>

<body class="page-editar">
  <header class="top">
    <a class="back" href="ListCategoryServlet.do" aria-label="Volver">&lt;</a>

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
    <div class="edit-header">
      <a class="edit-back" href="ListCategoryServlet.do" aria-label="Volver a categorías">&larr; Volver</a>
    </div>

    <div class="edit-layout">
      <aside class="edit-side" aria-label="Menu lateral">
        <nav class="edit-sidecard" aria-label="Secciones privadas">
          <ul class="edit-sidemenu">
            <li><a class="edit-sidelink" href="EditUserServlet.do"><span class="edit-sidelink-label">Mi perfil</span></a></li>
            <li><a class="edit-sidelink" href="ListUserOrderServlet.do"><span class="edit-sidelink-label">Mis pedidos</span></a></li>
            <li><a class="edit-sidelink" href="ListCategoryServlet.do"><span class="edit-sidelink-label">Mis categorías</span></a></li>
            <li>
              <a class="edit-sidelink is-active" href="<c:choose><c:when test='${not empty category}'>EditCategoryServlet.do?id=${category.id}</c:when><c:otherwise>CreateCategoryServlet.do</c:otherwise></c:choose>">
                <span class="edit-sidelink-label">
                  <c:choose>
                    <c:when test="${not empty category}">&rarr; Editar categoría</c:when>
                    <c:otherwise>&rarr; Añadir categoría</c:otherwise>
                  </c:choose>
                </span>
              </a>
            </li>
            <li><a class="edit-sidelink" href="ListUserRestaurantServlet.do"><span class="edit-sidelink-label">Mis restaurantes</span></a></li>
            <li><a class="edit-sidelink" href="LogoutServlet.do"><span class="edit-sidelink-label">Salir</span></a></li>
          </ul>
        </nav>
      </aside>

      <section class="edit-main" aria-label="Formulario de categoría">
        <article class="card edit-card">
          <header class="menu-head">
            <h2 class="menu-title">
              <c:choose>
                <c:when test="${not empty category}">Editar categoría</c:when>
                <c:otherwise>Añadir categoría</c:otherwise>
              </c:choose>
            </h2>
          </header>

          <form
            class="edit-form"
            action="<c:choose><c:when test='${not empty category}'>EditCategoryServlet.do?id=${category.id}</c:when><c:otherwise>CreateCategoryServlet.do</c:otherwise></c:choose>"
            method="post">

            <div class="messages" aria-live="polite">
              <c:if test="${not empty messages}">
                <p class="msg msg--error">${messages}</p>
              </c:if>
            </div>

            <div class="edit-panels">
              <section class="edit-panel" aria-label="Datos de la categoría">
                <h2 class="edit-h2">Datos de la categoría</h2>

                <div class="field">
                  <label for="category-name">Nombre</label>
                  <input
                    id="category-name"
                    name="category-name"
                    type="text"
                    required
                    value="<c:choose><c:when test='${not empty category}'>${category.name}</c:when><c:otherwise>${categoryName}</c:otherwise></c:choose>" />
                </div>

                <div class="field">
                  <label for="category-description">Descripción</label>
                  <textarea id="category-description" name="category-description" rows="5"><c:choose><c:when test='${not empty category}'>${category.description}</c:when><c:otherwise>${categoryDescription}</c:otherwise></c:choose></textarea>
                </div>
              </section>
            </div>

            <footer class="edit-actions">
              <a class="btn btn-ghost" href="ListCategoryServlet.do">Cancelar</a>
              <button class="btn btn-primary" type="submit">Guardar</button>
            </footer>
          </form>
        </article>
      </section>
    </div>
  </main>
</body>
</html>
