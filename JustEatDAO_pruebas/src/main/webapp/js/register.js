(() => {
  "use strict";

  const form = document.getElementById("userForm");
  const formMessages = document.getElementById("formMessages");

  // modificado
  const PASS_RE = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[^A-Za-z0-9]).{8,}$/;

  function clearChildren(node) {
    while (node.firstChild) node.removeChild(node.firstChild);
  }

  function setTextNode(node, text) {
    if (!node) return;
    clearChildren(node);
    node.appendChild(document.createTextNode(text));
  }

  function setMsg(el, text, ok) {
    if (!el) return;
    el.classList.remove("msg--ok", "msg--err", "msg--error");
    el.classList.add(ok ? "msg--ok" : "msg--error");
    setTextNode(el, text);
  }

  function ensureErrorEl(input) {
    const next = input.nextElementSibling;
    if (next && next.classList.contains("field-error")) return next;

    // Crear nodo <p class="field-error"> con TextNode (como en apuntes)
    const p = document.createElement("p");
    p.className = "field-error";
    p.setAttribute("role", "alert");
    p.hidden = true;

    input.insertAdjacentElement("afterend", p);

    if (input.id) {
      const id = "err_" + input.id;
      p.id = id;

      const prev = (input.getAttribute("aria-describedby") || "").split(/\s+/).filter(Boolean);

      if (prev.indexOf(id) === -1) {
        prev.push(id);
        input.setAttribute("aria-describedby", prev.join(" "));
      }
    }

    return p;
  }

  function showError(input, message) {
    const err = ensureErrorEl(input);
    clearChildren(err);
    err.appendChild(document.createTextNode(message));
    err.hidden = false;
    input.setAttribute("aria-invalid", "true");
  }

  function clearError(input) {
    const next = input.nextElementSibling;
    if (next && next.classList.contains("field-error")) {
      next.hidden = true;
      clearChildren(next);
    }
    input.removeAttribute("aria-invalid");
  }

  function validateForm(extraRules) {
    const controls = form.querySelectorAll("input");
    let ok = true;

    // limpiar errores
    let i = 0;
    while (i < controls.length) {
      if (!controls[i].disabled) clearError(controls[i]);
      i++;
    }

    if (typeof extraRules === "function") extraRules();

    // validar y crear nodos error
    i = 0;
    while (i < controls.length) {
      const c = controls[i];
      if (c.disabled) {
        i++;
        continue;
      }

      if (!c.checkValidity()) {
        ok = false;

        if (c.validity.valueMissing) showError(c, "Este campo es obligatorio.");
        else if (c.validity.typeMismatch) showError(c, "Formato inválido.");
        else if (c.validity.tooShort) showError(c, "Debe tener al menos " + c.minLength + " caracteres.");
        else if (c.validity.rangeUnderflow) showError(c, "El valor mínimo es " + c.min + ".");
        else if (c.validity.customError) showError(c, c.validationMessage);
        else showError(c, "Valor inválido.");
      }

      i++;
    }

    return ok;
  }

  function attachLiveValidation(extraRules) {
    form.addEventListener("submit", function (e) {
      clearChildren(formMessages);

      const ok = validateForm(extraRules);
      if (!ok) {
        e.preventDefault();
        setMsg(formMessages, "Revisa los campos marcados en rojo.", false);
      }
    });

    // Modificado para comprobar solo cada campo no todos de golpe
    form.addEventListener(
      "blur",
      function (e) {
        const t = e.target;
        if (!t || !t.checkValidity) return;

        // Actualiza reglas extra (setCustomValidity), pero no valida todo el form
        if (typeof extraRules === "function") extraRules();

        // Limpia/valida solo este campo
        clearError(t);

        if (!t.checkValidity()) {
          if (t.validity.valueMissing) showError(t, "Este campo es obligatorio.");
          else if (t.validity.typeMismatch) showError(t, "Formato inválido.");
          else if (t.validity.tooShort) showError(t, "Debe tener al menos " + t.minLength + " caracteres.");
          else if (t.validity.rangeUnderflow) showError(t, "El valor mínimo es " + t.min + ".");
          else if (t.validity.customError) showError(t, t.validationMessage);
          else showError(t, "Valor inválido.");
        }
      },
      true
    );
  }

  function userExtraRules() {
    const p1 = document.getElementById("password");
    const p2 = document.getElementById("passwordConfirm");
    const terms = document.getElementById("terms");

    if (p1.value && !PASS_RE.test(p1.value)) {
      p1.setCustomValidity("Contraseña: mínimo 8 caracteres, con mayúscula, minúscula, número y símbolo.");
    } else {
      p1.setCustomValidity("");
    }

    if (p2.value !== p1.value) p2.setCustomValidity("Las contraseñas no coinciden.");
    else p2.setCustomValidity("");

    if (!terms.checked) terms.setCustomValidity("Debes aceptar los términos.");
    else terms.setCustomValidity("");
  }

  attachLiveValidation(userExtraRules);
})();
