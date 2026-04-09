<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!doctype html>
<html lang="es">
<head>
<meta charset="utf-8" />
<meta name="viewport" content="width=device-width, initial-scale=1" />
<title>Iniciar sesion</title>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/styles.css" />
<!-- <script src="${pageContext.request.contextPath}/js/login.js" defer></script> -->
</head>

<body class="page-login">
	<header class="top">
		<a class="back" href="search.jsp" aria-label="Volver">&lt;</a> <a
			class="brand" href="LoginServlet.do" aria-label="Inicio"> <img
			src="${pageContext.request.contextPath}/images/logo_justeat.svg"
			alt="Logo Just Eat" width="30" height="30" /> <strong>JUST
				EAT</strong>
		</a>
	</header>

	<main class="wrap">
		<section class="card auth-card" aria-label="Iniciar sesion">
			<h1>Iniciar sesion</h1>

			<div id="formMessages" class="messages" aria-live="polite">
				<c:if test="${not empty messages}">
					<p class="msg msg--error">${messages}</p>
				</c:if>
			</div>

			<form id="loginForm" action="LoginServlet.do" method="post"
				novalidate>
				
				<c:if test="${not empty redirect}">
					<input type="hidden" name="redirect" value="${redirect}" />
				</c:if>
				
				<div class="field">
					<label for="email">Correo electronico</label> <input id="email"
						name="email" type="email" autocomplete="email" inputmode="email"
						placeholder="nombre@dominio.com" required maxlength="120" />
				</div>

				<div class="field">
					<label for="pass">Contrasena</label>
					<div class="pw">
						<input id="pass" name="password" type="password"
							autocomplete="current-password" placeholder="Minimo 8 caracteres"
							required minlength="8" maxlength="64" />

						<button id="togglePassword" type="button" class="eye"
							aria-label="Mostrar u ocultar contrasena" aria-pressed="false">
							O</button>
					</div>

					<p class="hint" id="passHint">Usa al menos 8 caracteres. Evita
						espacios.</p>
				</div>

				<button class="boton" type="submit">Iniciar sesion</button>

				<p class="center">
					<a href="#">Has olvidado tu contrasena?</a>
				</p>
				<hr />
				<p class="center muted">
					No tienes cuenta? <a class="accent" href="RegisterServlet.do">Registrate</a>
				</p>
			</form>
		</section>
	</main>
</body>
</html>
