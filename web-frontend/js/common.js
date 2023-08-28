const logoutButton = document.getElementById('redirectButton')
const token = localStorage.getItem('Token'); 
document.addEventListener('DOMContentLoaded', async function(){

if (!token) {
  window.location.href = 'login.html';
} 
await axios.get(`http://localhost:8080/api/v1/user/isActive/${token}`)

.catch(error=>{
  if (error.message == "Network Error"){
    alert("Cant connect To Server")
    window.location.href = 'index.html';
  }
  if (error.response.status==400){
    // console.log('error1')
    localStorage.clear();
    window.location.href = 'login.html';
  }
});
if (logoutButton){
    logoutButton.addEventListener('click', function() {
    // console.log('error2')
    localStorage.clear();
    window.location.href = 'index.html';
});
}

})