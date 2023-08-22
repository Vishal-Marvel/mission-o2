
document.addEventListener('DOMContentLoaded', function() {
    const loginForm = document.getElementById('loginForm');
  
    loginForm.addEventListener('submit', function(event) {
      event.preventDefault();
      
      const username = document.getElementById('username').value;
      const password = document.getElementById('password').value;
  
      if (username && password) {
        localStorage.setItem('username', username);
        localStorage.setItem('password', password);
        
        alert('Login successful. User data stored in local storage.');
        window.location.href = 'https://www.google.com';
      } else {
        alert('Please enter both username and password.');
      }
    });
  });
  