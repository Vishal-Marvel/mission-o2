const logoutButton = document.getElementById('redirectButton')
const token = localStorage.getItem('Token'); 
const APIURL = "http://localhost:8080/api/v1"
document.addEventListener('DOMContentLoaded', async function(){

if (!token) {
  if (!(window.location.href).endsWith('login.html')){
  window.location.href = 'login.html';
  }
} 
await axios.get(`${APIURL}/user/isActive/${token}`)

.catch(error=>{
  if (error.message == "Network Error"){
    alert("Cant connect To Server")
    // console.log(window.location.search)
    if (!(window.location.href).endsWith('index.html')){
      window.location.href = 'index.html';
      }
  }
  if (error.response.status==400){
    // console.log('error1')
    localStorage.clear();
    if (!(window.location.href).endsWith('login.html')){
      window.location.href = 'login.html';
      }
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