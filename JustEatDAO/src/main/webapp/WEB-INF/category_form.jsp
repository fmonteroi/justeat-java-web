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
  <main class="container" role="main">
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
            <textarea
              id="category-description"
              name="category-description"
              rows="3"><c:choose><c:when test='${not empty category}'>${category.description}</c:when><c:otherwise>${categoryDescription}</c:otherwise></c:choose></textarea>
          </div>

          <footer class="edit-actions">
            <a class="btn btn-ghost" href="ListCategoryServlet.do">Cancelar</a>
            <button class="btn btn-primary" type="submit">Guardar</button>
          </footer>
        </form>
      </article>
    </section>
  </main>
</body>
</html>
