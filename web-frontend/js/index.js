const loginButton = document.getElementById('login-button');
const adminPanelButton = document.getElementById('admin-panel-button');

loginButton.addEventListener('click', function() {
      window.location.href = 'login.html';
});
adminPanelButton.addEventListener('click', function() {
    window.location.href = 'panel.html';
});