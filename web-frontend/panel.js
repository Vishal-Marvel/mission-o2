const goToAddPlant = document.getElementById('addPlant');
const goToApprOrder = document.getElementById('apprOrder');
const goToViewOrder = document.getElementById('viewOrder');
goToAddPlant.addEventListener('click', function() {
      window.location.href = 'addplant.html';
});
goToApprOrder.addEventListener('click', function() {
    window.location.href = 'apprOrders.html';
});
goToViewOrder.addEventListener('click',function() {
    window.location.href = 'viewOrders.html'
});