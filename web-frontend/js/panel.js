
document.addEventListener('DOMContentLoaded', async function() {
    let orders, count;

    const goToAddPlant = document.getElementById('addPlant');
    const counter = document.getElementById('count');
    const goToApprOrder = document.getElementById('apprOrder');
    const goToViewOrder = document.getElementById('viewOrder');
    const goToPlantOptions = document.getElementById('plantOptions');
    const goTostateWise = document.getElementById('stateWise');

    function startLoading() {
      loadingOverlay.style.display = 'flex';
    }

    function stopLoading() {
      loadingOverlay.style.display = 'none';
    }

    startLoading();
    await axios.get(`${APIURL}/orders/total-plants`, {
        headers :{
          'Authorization': `Bearer ${token}`
        }
    }).then(response=>{
        count = response.data.response;
    }).catch(error=>{
        console.log(error);
    })
    counter.innerText = (10000008-count).toLocaleString();
    await axios.get(`${APIURL}/orders/view-pending-orders`, {
        headers :{
          'Authorization': `Bearer ${token}`
        }
    }).then(response=>{
        orders = response.data.length;
    }).catch(error=>{
        console.log(error);
    })
    
    goToApprOrder.innerText += ` (${orders})`;
    stopLoading();
    goToAddPlant.addEventListener('click', function() {
          window.location.href = 'addplant.html';
    });
    
    goToApprOrder.addEventListener('click', function() {
        window.location.href = 'apprOrders.html';
    });
    
    goToViewOrder.addEventListener('click', function() {
        window.location.href = 'viewOrders.html';
    });
    
    goToPlantOptions.addEventListener('click', function(){
        window.location.href = 'plantOptions.html';
    });
    goTostateWise.addEventListener('click', function(){
        window.location.href = 'analysis.html';
    });
    
});
  