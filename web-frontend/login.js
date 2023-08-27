document.addEventListener('DOMContentLoaded', function() {
<<<<<<< HEAD
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

          alert('Login successful. User data stored in local storage.');
          window.location.href = 'panel.html';
        } 
        else {
          alert('Invalid username or password.');
        }
      } catch (error) {
        console.error('An error occurred:', error);
        alert('An error occurred while attempting to login.');
=======
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
>>>>>>> c88df3aeb2a18cc8ef6d35bc52aabf9329185417
      }
    } else {
      alert('Please enter both username and password.');
    }
  });
});
