const logoutButton = document.getElementById("redirectButton");
const panel = document.getElementById("panel");
const token = sessionStorage.getItem("Token");
const role = sessionStorage.getItem("Role");
const logo = document.querySelector(".logo-container");
const APIURL = "https://api.missiono2.com:8080/api/v1";
let valid = true;

document.addEventListener("DOMContentLoaded", async function () {
  if (logo) {
    logo.addEventListener("click", function () {
      window.location.href = "index.html";
    })
  }
  if (!token) {
    if (
      !window.location.href.includes("login.html") &&
      !window.location.href.includes("index.html") &&
      !window.location.href.includes("/")
    ) {
      alert("Log in to continue");
      window.location.href = "login.html";
    }
  } else {
    await axios.get(`${APIURL}/user/isActive/${token}`).catch((error) => {
      if (error.message === "Network Error") {
        alert("Cant connect To Server");
        sessionStorage.clear();
        if (
          !window.location.href.includes("index.html") &&
          !window.location.href.includes("/")
        ) {
          window.location.href = "index.html";
        }
      }
      if (error.response.status === 400) {
        
        sessionStorage.clear();
        if (
          !window.location.href.includes("login.html") &&
          !window.location.href.includes("index.html") &&
          !window.location.href.includes("/")
        ) {
          valid = false;
          alert("Session Expired");
          window.location.href = "login.html";
        }
      }
    });
  }
  if (logoutButton) {
    logoutButton.addEventListener("click", function () {
      sessionStorage.clear();
      window.location.href = "index.html";
    });
  }
  if (panel) {
    panel.addEventListener("click", function () {
      window.location.href = "panel.html";
    });
  }
});
