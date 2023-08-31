const logoutButton = document.getElementById('redirectButton')
const token = sessionStorage.getItem('Token'); 
const APIURL = "https://mission-o2-web-demo.onrender.com/api/v1"
document.addEventListener('DOMContentLoaded', async function(){

if (!token) {
  if (!(window.location.href).endsWith('login.html') && !(window.location.href).endsWith('index.html') && !(window.location.href).endsWith('/')){
  // console.log('err');
    window.location.href = 'login.html';
  }
} 
await axios.get(`${APIURL}/user/isActive/${token}`)
.catch(error=>{
  if (error.message == "Network Error"){
    alert("Cant connect To Server")
    // console.log(window.location.search)
    if (!(window.location.href).endsWith('index.html') && !(window.location.href).endsWith('/')){
      window.location.href = 'index.html';
      }
  }
  if (error.response.status==400){
    // console.log('error1')
    localStorage.clear();
    if (!(window.location.href).endsWith('login.html')&& !(window.location.href).endsWith('index.html') && !(window.location.href).endsWith('/')){
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