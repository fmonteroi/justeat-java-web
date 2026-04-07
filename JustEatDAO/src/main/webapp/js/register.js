(() => {
  "use strict";

  const form = document.getElementById("userForm");

  if (!form) {
    return;
  }

  const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[^A-Za-z0-9]).{8,}$/;

  function clearChildren(node) {
    while (node.firstChild) node.removeChild(node.firstChild);
  }

  function ensureErrorEl(input) {
    const next = input.nextElementSibling;
    if (next && next.classList.contains("field-error")) return next;

    const p = document.createElement("p");
    p.className = "field-error";
    p.setAttribute("role", "alert");
    input.insertAdjacentElement("afterend", p);
    return p;
  }

  function showError(input, message) {
    const error = ensureErrorEl(input);
    clearChildren(error);
    error.appendChild(document.createTextNode(message));
    input.setAttribute("aria-invalid", "true");
  }

  function clearError(input) {
    const next = input.nextElementSibling;
    if (next && next.classList.contains("field-error")) {
      clearChildren(next);
      next.remove();
    }
    input.removeAttribute("aria-invalid");
  }

  function applyCustomRules() {
    const email = document.getElementById("email");
    const pass = document.getElementById("password");
    const pass2 = document.getElementById("passwordConfirm");
    const terms = document.getElementById("terms");

    if (email && email.value.trim() && !/^[^\s@]+@[^\s@]+\.[^\s@]{2,}$/.test(email.value.trim())) {
      email.setCustomValidity("Formato inválido.");
    } else if (email) {
      email.setCustomValidity("");
    }

    if (pass && pass.value && !passwordRegex.test(pass.value)) {
      pass.setCustomValidity("Contraseña insegura.");
    } else if (pass) {
      pass.setCustomValidity("");
    }

    if (pass2 && pass && pass2.value !== pass.value) {
      pass2.setCustomValidity("Las contraseñas no coinciden.");
    } else if (pass2) {
      pass2.setCustomValidity("");
    }

    if (terms && !terms.checked) {
      terms.setCustomValidity("Debes aceptar los términos.");
    } else if (terms) {
      terms.setCustomValidity("");
    }
  }

  function validateControl(control) {
    clearError(control);

    if (!control.checkValidity()) {
      if (control.validity.valueMissing) showError(control, "Este campo es obligatorio.");
      else if (control.validity.typeMismatch) showError(control, "Formato inválido.");
      else if (control.validity.customError) showError(control, control.validationMessage);
      else showError(control, "Valor inválido.");
      return false;
    }

    return true;
  }

  const controls = form.querySelectorAll("input");

  form.addEventListener("submit", (event) => {
    applyCustomRules();

    let valid = true;
    controls.forEach((control) => {
      valid = validateControl(control) && valid;
    });

    if (!valid) {
      event.preventDefault();
    }
  });

  form.addEventListener(
    "blur",
    (event) => {
      const target = event.target;
      if (!target || typeof target.checkValidity !== "function") return;
      applyCustomRules();
      validateControl(target);
    },
    true
  );
})();
