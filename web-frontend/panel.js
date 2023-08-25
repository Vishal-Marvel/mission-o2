document.addEventListener('DOMContentLoaded', function() {
    const goToAddPlant = document.getElementById('addPlant');
    const goToApprOrder = document.getElementById('apprOrder');
    const goToViewOrder = document.getElementById('viewOrder');
    const logoutButton = document.getElementById('logoutButton');
    
    goToAddPlant.addEventListener('click', function() {
          window.location.href = 'addplant.html';
    });
    
    goToApprOrder.addEventListener('click', function() {
        window.location.href = 'apprOrders.html';
    });
    
    goToViewOrder.addEventListener('click', function() {
        window.location.href = 'viewOrders.html';
    });
    
    logoutButton.addEventListener('click', function() {
        window.location.href = 'index.html';
    });
});
  