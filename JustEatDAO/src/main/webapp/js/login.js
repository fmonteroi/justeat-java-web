(() => {
  "use strict";

  const form = document.getElementById("loginForm");
  const emailInput = document.getElementById("email");
  const passInput = document.getElementById("pass");
  const toggleBtn = document.getElementById("togglePassword");
  const formMessages = document.getElementById("formMessages");

  if (!form || !emailInput || !passInput || !toggleBtn || !formMessages) {
    return;
  }

  const EMAIL_MAX = 120;
  const PASS_MIN = 8;
  const PASS_MAX = 64;

  function clearChildren(node) {
    while (node.firstChild) node.removeChild(node.firstChild);
  }

  function getErrorIdFor(input) {
    return `err-${input.id}`;
  }

  function removeFieldError(input) {
    const errId = getErrorIdFor(input);
    const existing = document.getElementById(errId);
    if (existing) existing.remove();

    input.classList.remove("is-invalid");
    input.removeAttribute("aria-invalid");
  }

  function addFieldError(input, message) {
    const errId = getErrorIdFor(input);
    const existing = document.getElementById(errId);

    if (existing) {
      clearChildren(existing);
      existing.appendChild(document.createTextNode(message));
      input.classList.add("is-invalid");
      input.setAttribute("aria-invalid", "true");
      return;
    }

    const p = document.createElement("p");
    p.className = "field-error";
    p.id = errId;
    p.setAttribute("role", "alert");
    p.appendChild(document.createTextNode(message));

    const field = input.closest(".field");
    if (field) field.appendChild(p);

    input.classList.add("is-invalid");
    input.setAttribute("aria-invalid", "true");
  }

  function setFormMessage(text) {
    if (!text) return;
    clearChildren(formMessages);

    const msg = document.createElement("p");
    msg.className = "msg msg--error";
    msg.setAttribute("role", "status");
    msg.appendChild(document.createTextNode(text));

    formMessages.appendChild(msg);
  }

  function validateEmail() {
    const value = String(emailInput.value || "").trim();
    removeFieldError(emailInput);

    if (!value) {
      addFieldError(emailInput, "El correo electrónico es obligatorio.");
      return false;
    }

    if (value.length > EMAIL_MAX) {
      addFieldError(emailInput, `El correo no puede superar ${EMAIL_MAX} caracteres.`);
      return false;
    }

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]{2,}$/;
    if (!emailRegex.test(value)) {
      addFieldError(emailInput, "Introduce un correo válido.");
      return false;
    }

    return true;
  }

  function validatePassword() {
    const value = String(passInput.value || "");
    removeFieldError(passInput);

    if (!value) {
      addFieldError(passInput, "La contraseña es obligatoria.");
      return false;
    }

    if (value.includes(" ")) {
      addFieldError(passInput, "La contraseña no puede contener espacios.");
      return false;
    }

    if (value.length < PASS_MIN) {
      addFieldError(passInput, `La contraseña debe tener al menos ${PASS_MIN} caracteres.`);
      return false;
    }

    if (value.length > PASS_MAX) {
      addFieldError(passInput, `La contraseña no puede superar ${PASS_MAX} caracteres.`);
      return false;
    }

    return true;
  }

  toggleBtn.addEventListener("click", () => {
    const isHidden = passInput.type === "password";
    passInput.type = isHidden ? "text" : "password";
    toggleBtn.setAttribute("aria-pressed", String(isHidden));
    toggleBtn.textContent = isHidden ? "🙈" : "👁";
  });

  form.addEventListener("submit", (event) => {
    clearChildren(formMessages);

    const emailValid = validateEmail();
    const passwordValid = validatePassword();
    if (!(emailValid && passwordValid)) {
      event.preventDefault();
      setFormMessage("Revisa los campos marcados en rojo.");
    }
  });

  emailInput.addEventListener("blur", validateEmail);
  passInput.addEventListener("blur", validatePassword);
})();
