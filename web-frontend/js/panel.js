
document.addEventListener('DOMContentLoaded', async function() {
    let orders;
    const goToAddPlant = document.getElementById('addPlant');
    const goToApprOrder = document.getElementById('apprOrder');
    const goToViewOrder = document.getElementById('viewOrder');
    const goToPlantOptions = document.getElementById('plantOptions');
    const goTostateWise = document.getElementById('stateWise');

    const loadingOverlay = document.getElementById('loadingOverlay');

    function startLoading() {
      loadingOverlay.style.display = 'flex';
    }

    function stopLoading() {
      loadingOverlay.style.display = 'none';
    }

    startLoading();
    await axios.get('http://localhost:8080/api/v1/orders/view-pending-orders', {
        headers :{
          'Authorization': `Bearer ${token}`
        }
    }).then(response=>{
        orders = response.data.length;
    }).catch(error=>{
        console.log(error);
    })
    stopLoading();
    goToApprOrder.innerText += ` (${orders})`;
    
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
  