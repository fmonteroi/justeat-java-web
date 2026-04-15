<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Iniciar sesión</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/IniSesion.css">
    <script src="${pageContext.request.contextPath}/js/IniSesion2.js" defer></script>
</head>

<body>
    <header class="top">
        <a class="back" href="LoginServlet.do" aria-label="Volver">←</a>

        <a class="brand" href="LoginServlet.do" aria-label="Inicio">
            <span class="brand__icon" aria-hidden="true">🍴</span>
            <strong class="brand__text">JUST EAT</strong>
        </a>
    </header>

    <main class="wrap">
        <section class="card" aria-label="Iniciar sesión">
            <h1 class="title">Iniciar sesión</h1>

            <!-- Contenedor de mensajes -->
            <div id="formMessages" class="messages" aria-live="polite">
                <c:if test="${not empty messages}">
                    <p>${messages}</p>
                </c:if>
            </div>

            <form id="loginForm" action="LoginServlet.do" method="post" novalidate>
                <div class="field">
                    <label for="email">Correo electrónico</label>
                    <input
                        id="email"
                        name="email"
                        type="email"
                        autocomplete="email"
                        inputmode="email"
                        placeholder="nombre@dominio.com"
                        required
                        maxlength="120"
                    />
                </div>

                <div class="field">
                    <label for="pass">Contraseña</label>

                    <div class="pw">
                        <input
                            id="pass"
                            name="password"
                            type="password"
                            autocomplete="current-password"
                            placeholder="Mínimo 8 caracteres"
                            required
                            minlength="8"
                            maxlength="64"
                        />

                        <button
                            id="togglePassword"
                            type="button"
                            class="eye"
                            aria-label="Mostrar u ocultar contraseña"
                            aria-pressed="false">
                            👁
                        </button>
                    </div>

                    <p class="hint" id="passHint">Usa al menos 8 caracteres. Evita espacios.</p>
                </div>

                <button class="btn btn--primary" type="submit">Iniciar sesión</button>

                <p class="center">
                    <a class="link" href="#">¿Olvidaste tu contraseña?</a>
                </p>

                <hr class="sep" />

                <p class="center muted">
                    ¿No tienes cuenta?
                    <a class="link link--accent" href="RegisterServlet.do">Regístrate</a>
                </p>

                <p class="center muted">
                    Al iniciar sesión o registrarte, aceptas nuestros términos y condiciones.
                    Consulta también nuestra política de privacidad y nuestra política de cookies.
                </p>
            </form>
        </section>
    </main>
</body>
</html>