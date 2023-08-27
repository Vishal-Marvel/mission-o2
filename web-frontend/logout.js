const logoutButton = document.getElementById('redirectButton')
logoutButton.addEventListener('click', function() {
    localStorage.clear();
    window.location.href = 'index.html';
});