const logoutButton = document.getElementById('redirectButton')
const token = localStorage.getItem('Token'); 
const APIURL = "https://c689-2405-201-e030-ae44-10b1-d00a-2ead-dd7b.ngrok-free.app/api/v1"
document.addEventListener('DOMContentLoaded', async function(){

if (!token) {
  window.location.href = 'login.html';
} 
await axios.get(`${APIURL}/user/isActive/${token}`)

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