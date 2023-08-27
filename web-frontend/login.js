document.addEventListener('DOMContentLoaded', function() {
  const loginForm = document.getElementById('loginForm');

  loginForm.addEventListener('submit', async function(event) {
    event.preventDefault();

    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;

    if (username && password) {
      try {
        const response = await axios.post('http://localhost:8080/api/v1/user/login-portal', {
          email: username,
          password: password
        });

        const responseData = response.data;

        if (responseData.role=='ROLE_ADMIN') {
          localStorage.setItem('Role', response.data.role);
          localStorage.setItem('Token',response.data.token);

          alert('Login successful');
          window.location.href = 'panel.html';
        } 
        else {
          alert('Invalid username or password.');
        }
      } catch (error) {
        if (error.response.data.message == "Bad Credentials"){
        alert('Invalid Username and Password.');
        }else{
          console.log(error);
        }
      }
    } else {
      alert('Please enter both username and password.');
    }
  });
});
