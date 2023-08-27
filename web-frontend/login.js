
document.addEventListener('DOMContentLoaded', function() {
    const loginForm = document.getElementById('loginForm');
  
    loginForm.addEventListener('submit', function(event) {
      event.preventDefault();
      
      const username = document.getElementById('username').value;
      const password = document.getElementById('password').value;
  
      if (username && password) {
        axios.post("http://localhost:8080/api/v1/user/login-portal", {
          email: username,
          password: password,
        })
        .then(response=>{
          sessionStorage.setItem('token', response.data.token);
          if (response.data.role != "ROLE_ADMIN"){
            alert('You are not allowed');
          }
          else{
            alert('Login successful.');
            window.location.href = 'panel.html';
          }


        })
        .catch(error=>{
          if(error.response.data.message == "Bad Credentials"){
            alert("Incorrect User name or Password")
          }
          else{
            alert(error.response.data.message)
          }
        });
        // localStorage.setItem('password', password);
        
      } else {
        alert('Please enter both username and password.');
      }
    });
  });
  