
document.addEventListener('DOMContentLoaded', async function(){

const loginButton = document.getElementById('login-button');
const adminPanelButton = document.getElementById('admin-panel-button');
loginButton.addEventListener('click', function() {
      window.location.href = 'login.html';
});
adminPanelButton.addEventListener('click', function() {
    window.location.href = 'panel.html';
});
let count;
const counter = document.getElementById('count');

await axios.get('http://localhost:8080/api/v1/orders/total-plants')
    .then(response=>{
        count = response.data.response;
    }).catch(error=>{
        console.log(error);
    })
counter.innerText = (10000008-count).toLocaleString();
});