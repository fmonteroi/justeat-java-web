<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!doctype html>
<html lang="es">
  <head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>Mi perfil</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css" />
  </head>

  <body class="page-registro">
    <header class="top">
      <a class="back" href="ListRestaurantServlet.do" aria-label="Volver">&lt;</a>

      <a class="brand" href="LoginServlet.do" aria-label="Inicio">
        <img
          src="${pageContext.request.contextPath}/images/logo_justeat.svg"
          alt="Logo Just Eat"
          width="30"
          height="30" />
        <strong>JUST EAT</strong>
      </a>
    </header>

    <main class="wrap">
      <section class="card auth-card" aria-label="Editar perfil">
        <h1>Mi perfil</h1>

        <div class="messages" aria-live="polite">
          <c:if test="${not empty messages}">
            <p class="msg msg--error">${messages}</p>
          </c:if>

          <c:if test="${not empty messagesOk}">
            <p class="msg msg--ok">${messagesOk}</p>
          </c:if>
        </div>

        <form id="userEditForm" action="EditUserServlet.do" method="post" novalidate>
          <input type="hidden" name="action" value="update" />

          <div class="field">
            <label for="name">Nombre</label>
            <input
              id="name"
              name="name"
              type="text"
              autocomplete="given-name"
              required
              value="${profileUser.name}" />
          </div>

          <div class="field">
            <label for="surname">Apellidos</label>
            <input
              id="surname"
              name="surname"
              type="text"
              autocomplete="family-name"
              required
              value="${profileUser.surname}" />
          </div>

          <div class="field">
            <label for="email">Correo electrónico</label>
            <input
              id="email"
              name="email"
              type="email"
              autocomplete="email"
              required
              value="${profileUser.email}" />
          </div>

          <div class="field">
            <label for="password">Nueva contraseña</label>
            <div class="pw">
              <input
                id="password"
                name="password"
                type="password"
                autocomplete="new-password"
                placeholder="Nueva contraseña" />
            </div>
            <p class="hint">Rellena estos campos solo si quieres cambiar tu contraseña.</p>
          </div>

          <div class="field">
            <label for="passwordConfirm">Confirmar nueva contraseña</label>
            <div class="pw">
              <input
                id="passwordConfirm"
                name="passwordConfirm"
                type="password"
                autocomplete="new-password"
                placeholder="Repite la nueva contrasena" />
            </div>
          </div>

          <button class="boton" type="submit">Guardar cambios</button>
        </form>

        <hr />

        <form action="EditUserServlet.do" method="post" novalidate>
          <input type="hidden" name="action" value="delete" />
          <button class="boton" type="submit">Eliminar cuenta</button>
        </form>

        <p class="center muted">
          <a class="accent" href="LogoutServlet.do">Cerrar sesion</a>
        </p>
      </section>
    </main>
  </body>
</html>
