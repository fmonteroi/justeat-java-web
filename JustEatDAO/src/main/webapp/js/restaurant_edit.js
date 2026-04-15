(() => {
  "use strict";

  /* -------------------- Helpers -------------------- */
  function $(sel, root) {
    return (root || document).querySelector(sel);
  }

  function clearChildren(node) {
    while (node && node.firstChild) node.removeChild(node.firstChild);
  }

  function ensureErrorEl(input) {
    var next = input.nextElementSibling;
    if (next && next.classList.contains("field-error")) return next;

    // crear <p class="field-error"> tras el control
    var p = document.createElement("p");
    p.className = "field-error";
    p.setAttribute("role", "alert");
    p.hidden = true;

    input.insertAdjacentElement("afterend", p);

    return p;
  }

  function showError(input, message) {
    var err = ensureErrorEl(input);
    clearChildren(err);
    err.appendChild(document.createTextNode(message));
    err.hidden = false;
    input.setAttribute("aria-invalid", "true");
  }

  function clearError(input) {
    var next = input.nextElementSibling;
    if (next && next.classList.contains("field-error")) {
      next.hidden = true;
      clearChildren(next);
    }
    input.removeAttribute("aria-invalid");
  }

  function validateControl(control) {
    clearError(control);

    if (!control.checkValidity()) {
      if (control.validity.valueMissing) showError(control, "Este campo es obligatorio.");
      else if (control.validity.typeMismatch) showError(control, "Formato inválido.");
      else if (control.validity.tooShort) showError(control, "Debe tener al menos " + control.minLength + " caracteres.");
      else if (control.validity.rangeUnderflow) showError(control, "El valor mínimo es " + control.min + ".");
      else if (control.validity.customError) showError(control, control.validationMessage); // para que se muestre el error personalizado
      else showError(control, "Valor inválido.");
      return false;
    }

    return true;
  }

  function attachLiveValidation(form, extraRules) {
    var controls = form.querySelectorAll("input, select, textarea");

    form.addEventListener("submit", function (e) {
      var ok = true;
      var i = 0;

      if (typeof extraRules === "function") extraRules(form);

      while (i < controls.length) {
        if (!controls[i].disabled) ok = validateControl(controls[i]) && ok;
        i++;
      }

      if (!ok) e.preventDefault();
    });

    form.addEventListener(
      "blur",
      function (e) {
        var t = e.target;
        if (!t || !t.checkValidity) return;

        if (typeof extraRules === "function") extraRules(form);

        // validar solo el campo actual
        clearError(t);
        validateControl(t);
      },
      true
    );
  }

  /* -------------------- Editar restaurante -------------------- */
  var editForm = $("#editForm");

  function editExtraRules(form) {
    var phone = $("#restaurant-tel", form);
    var email = $("#restaurant-email", form);
    var min = $("#min-price", form);
    var max = $("#max-price", form);
    var instagram = $("#instagramUrl", form);
    var facebook = $("#facebookUrl", form);
    var x = $("#xUrl", form);

    // teléfono 9 dígitos
    if (phone.value.trim() && !/^\d{9}$/.test(phone.value.trim())) {
      phone.setCustomValidity("El teléfono debe tener 9 dígitos numéricos.");
    } else phone.setCustomValidity("");

    // email obligatorio (type=email ya valida formato)
    if (!String(email.value || "").trim()) email.setCustomValidity("Email obligatorio.");
    else email.setCustomValidity("");

    // precios no negativos y max >= min
    var nMin = Number(min.value);
    var nMax = Number(max.value);

    if (isFinite(nMin) && nMin < 0) min.setCustomValidity("No se permiten precios negativos.");
    else min.setCustomValidity("");

    if (isFinite(nMax) && nMax < 0) max.setCustomValidity("No se permiten precios negativos.");
    else max.setCustomValidity("");

    if (isFinite(nMin) && isFinite(nMax) && nMax < nMin) {
      max.setCustomValidity("El máximo debe ser mayor o igual que el mínimo.");
    }

    // urls opcionales, pero válidas si se informan
    if (instagram.value.trim() && !instagram.checkValidity()) {
      instagram.setCustomValidity("Introduce una URL válida.");
    } else instagram.setCustomValidity("");

    if (facebook.value.trim() && !facebook.checkValidity()) {
      facebook.setCustomValidity("Introduce una URL válida.");
    } else facebook.setCustomValidity("");

    if (x.value.trim() && !x.checkValidity()) {
      x.setCustomValidity("Introduce una URL válida.");
    } else x.setCustomValidity("");
  }

  attachLiveValidation(editForm, editExtraRules);

  /* -------------------- Confirmación de borrado -------------------- */
  var deleteForm = document.querySelector("form[action='DeleteRestaurantServlet.do']");
  if (deleteForm) {
    deleteForm.addEventListener("submit", function (e) {
      if (!window.confirm("¿Seguro que quieres eliminar el restaurante?")) {
        e.preventDefault();
      }
    });
  }
})();
