document.addEventListener("DOMContentLoaded", function () {
  const tabs = document.querySelectorAll(".detail-switch-tab");
  const panels = document.querySelectorAll(".detail-switch-panel");

  tabs.forEach(function (tab) {
    tab.addEventListener("click", function () {
      const target = tab.getAttribute("data-target");

      tabs.forEach(function (item) {
        item.classList.remove("is-active");
        item.setAttribute("aria-selected", "false");
      });

      panels.forEach(function (panel) {
        panel.classList.remove("is-active");
      });

      tab.classList.add("is-active");
      tab.setAttribute("aria-selected", "true");

      const activePanel = document.getElementById(target);
      if (activePanel) {
        activePanel.classList.add("is-active");
      }
    });
  });
});
