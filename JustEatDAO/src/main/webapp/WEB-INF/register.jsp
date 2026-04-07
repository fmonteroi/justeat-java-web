<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!doctype html>
<html lang="es">
<head>
<meta charset="utf-8" />
<meta name="viewport" content="width=device-width, initial-scale=1" />
<title>Registro</title>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/styles.css" />
<script src="${pageContext.request.contextPath}/js/register.js" defer></script>
</head>

<body class="page-registro">
	<header class="top">
		<a class="back" href="LoginServlet.do" aria-label="Volver">&lt;</a> <a
			class="brand" href="LoginServlet.do" aria-label="Inicio"> <img
			src="${pageContext.request.contextPath}/images/logo_justeat.svg"
			alt="Logo Just Eat" width="30" height="30" /> <strong>JUST
				EAT</strong>
		</a>
	</header>

	<main class="wrap">
		<section class="card auth-card" aria-label="Registro de usuario">
			<h1>Completar cuenta</h1>

			<div id="formMessages" class="messages" aria-live="polite">
				<c:if test="${not empty messages}">
					<p class="msg msg--error">${messages}</p>
				</c:if>
			</div>


			<form id="userForm" action="RegisterServlet.do" method="post"
				novalidate>
				<div class="field">
					<label for="name">Nombre</label> <input id="name" name="name"
						type="text" autocomplete="given-name" placeholder="Nombre"
						value="${name}" required />
				</div>

				<div class="field">
					<label for="surname">Apellidos</label> <input id="surname"
						name="surname" type="text" autocomplete="family-name"
						placeholder="Apellidos" value="${surname}" required />
				</div>

				<div class="field">
					<label for="email">Correo electronico</label> <input id="email"
						name="email" type="email" autocomplete="email"
						placeholder="Correo electronico" value="${email}" required />
				</div>

				<div class="field">
					<label for="password">Contrasena</label>
					<div class="pw">
						<input id="password" name="password" type="password"
							autocomplete="new-password" placeholder="Contrasena" required />
					</div>
				</div>

				<div class="field">
					<label for="passwordConfirm">Confirmar contrasena</label>
					<div class="pw">
						<input id="passwordConfirm" name="passwordConfirm" type="password"
							placeholder="Repite la contrasena" autocomplete="new-password"
							required />
					</div>
				</div>

				<div class="field">
					<div class="terms">
						<input id="terms" name="terms" type="checkbox" required /> <label
							for="terms">Acepto los terminos</label>
					</div>
				</div>

				<button class="boton" type="submit">Completar</button>
			</form>
		</section>
	</main>
</body>
</html>
