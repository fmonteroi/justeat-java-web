(() => {
  "use strict";

  const form = document.getElementById("editForm");
  const deleteForm = document.getElementById("deleteRestaurantForm");

  if (!form) {
    return;
  }

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
    const phone = document.getElementById("restaurant-tel");
    const email = document.getElementById("restaurant-email");
    const min = document.getElementById("min-price");
    const max = document.getElementById("max-price");

    if (phone && phone.value.trim() && !/^\d{9}$/.test(phone.value.trim())) {
      phone.setCustomValidity("El teléfono debe tener 9 dígitos numéricos.");
    } else if (phone) {
      phone.setCustomValidity("");
    }

    if (email && email.value.trim() && !/^[^\s@]+@[^\s@]+\.[^\s@]{2,}$/.test(email.value.trim())) {
      email.setCustomValidity("Introduce un correo válido.");
    } else if (email) {
      email.setCustomValidity("");
    }

    if (min && Number(min.value) < 0) {
      min.setCustomValidity("No se permiten precios negativos.");
    } else if (min) {
      min.setCustomValidity("");
    }

    if (max && Number(max.value) < 0) {
      max.setCustomValidity("No se permiten precios negativos.");
    } else if (max) {
      max.setCustomValidity("");
    }

    if (min && max && Number(max.value) < Number(min.value)) {
      max.setCustomValidity("El máximo debe ser mayor o igual que el mínimo.");
    }
  }

  function validateControl(control) {
    clearError(control);

    if (!control.checkValidity()) {
      if (control.validity.valueMissing) showError(control, "Este campo es obligatorio.");
      else if (control.validity.typeMismatch) showError(control, "Formato inválido.");
      else if (control.validity.customError) showError(control, control.validationMessage);
      else if (control.validity.rangeUnderflow) showError(control, `El valor mínimo es ${control.min}.`);
      else showError(control, "Valor inválido.");
      return false;
    }

    return true;
  }

  const controls = form.querySelectorAll("input, textarea");

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

  if (deleteForm) {
    deleteForm.addEventListener("submit", (event) => {
      if (!window.confirm("¿Seguro que quieres eliminar el restaurante?")) {
        event.preventDefault();
      }
    });
  }
})();
