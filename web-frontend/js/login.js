document.addEventListener("DOMContentLoaded", function () {
  const loginForm = document.getElementById("loginForm");
  if (token) {
    window.location.href = "panel.html";
  }

  loginForm.addEventListener("submit", async function (event) {
    event.preventDefault();
    const loadingOverlay = document.getElementById("loadingOverlay");

    function startLoading() {
      loadingOverlay.style.display = "flex";
    }

    function stopLoading() {
      loadingOverlay.style.display = "none";
    }

    const username = document.getElementById("username").value;
    const password = document.getElementById("password").value;

    if (username && password) {
      try {
        startLoading();
        const response = await axios.post(`${APIURL}/user/login-portal`, {
          email: username,
          password: password,
        });
        const responseData = response.data;

        if (responseData.role === "ROLE_ADMIN" || responseData.role === "ROLE_ADMIN_ASSIST") {
          sessionStorage.setItem("Role", response.data.role);
          sessionStorage.setItem("Token", response.data.token);

          window.location.href = "panel.html";
        } else {
          alert("Invalid username or password.");
        }
      } catch (error) {
          alert(error.response.data.message)

      } finally {
        stopLoading();
      }
    } else {
      alert("Please enter both username and password.");
    }
  });
});
