<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="es">

<head>
    <meta charset="UTF-8">
    <title>Iniciar sesión</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/login.css">
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/header_footer.css">
</head>

<body>
<header>
    <div class="logo-header">
        <a href="LoginServlet.do"> <img
                src="${pageContext.request.contextPath}/img/logo_header.png"
                alt="Logo de Just Eat para la cabecera">
        </a>
    </div>
</header>
<main>
    <form action="LoginServlet.do" method="post">
        <fieldset>
            <p>${messages}</p>
            <h2>Inicia sesión</h2>
            <div class="form-group">
                <label for="email">Email:</label> <input type="email" name="email"
                                                         id="email" placeholder="Introduce tu email" required>
            </div>
            <div class="form-group">
                <label for="password">Contraseña:</label> <input type="password"
                                                                 name="password" id="password"
                                                                 placeholder="Introduce contraseña"
                                                                 required>
            </div>
            <button type="submit" id="submit_button">Inicia sesión</button>
            <p>
                ¿Nuevo en Just Eat? <a href="RegisterServlet.do">Crear cuenta</a>
            </p>
            <p>
                Al crear la cuenta, aceptas nuestros términos y condiciones.<br>
                Por favor, lee nuestra política de privacidad y nuestra política de
                cookies
            </p>
        </fieldset>
    </form>
</main>
<footer>
    <p>&copy; Todos los derechos reservados</p>
</footer>
</body>

</html>