(function () {
  function applyChuckStyle() {
    var marker = document.querySelector(".chuck-norris-marker[data-chuck-style]");
    var panel = document.getElementById("main-panel");
    if (!marker || !panel) {
      return;
    }
    panel.classList.add("chuck-style", marker.getAttribute("data-chuck-style"));
  }

  if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", applyChuckStyle);
  } else {
    applyChuckStyle();
  }
})();
