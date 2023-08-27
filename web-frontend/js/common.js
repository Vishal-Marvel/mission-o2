
document.addEventListener('DOMContentLoaded', async function(){
  const logoutButton = document.getElementById('redirectButton')
  const token = localStorage.getItem('Token'); 
  if (!token) {
    window.location.href = 'login.html';
  } 
await axios.get(`http://localhost:8080/api/v1/user/isActive/${token}`)

.catch(error=>{
  if (error.response.status==400){
    consol
    localStorage.clear();
    window.location.href = 'login.html';
  }
});

logoutButton.addEventListener('click', function() {
    localStorage.clear();
    window.location.href = 'index.html';
});
})