(() => {
  "use strict";

  function setActiveTab(tabs, panels, currentTab) {
    var target = currentTab.getAttribute("data-target");
    var i = 0;

    while (i < tabs.length) {
      tabs[i].classList.remove("is-active");
      tabs[i].setAttribute("aria-selected", "false");
      i++;
    }

    i = 0;
    while (i < panels.length) {
      panels[i].classList.remove("is-active");
      i++;
    }

    currentTab.classList.add("is-active");
    currentTab.setAttribute("aria-selected", "true");

    var activePanel = document.getElementById(target);
    if (activePanel) activePanel.classList.add("is-active");
  }

  var tabs = document.querySelectorAll(".detail-switch-tab");
  var panels = document.querySelectorAll(".detail-switch-panel");

  var i = 0;
  while (i < tabs.length) {
    (function (tab) {
      tab.addEventListener("click", function () {
        setActiveTab(tabs, panels, tab);
      });
    })(tabs[i]);
    i++;
  }
})();
