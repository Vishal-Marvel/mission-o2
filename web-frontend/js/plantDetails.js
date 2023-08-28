document.addEventListener('DOMContentLoaded', function() {
    if (window.location.pathname.endsWith('plantDetails.html')) {
      const urlParams = new URLSearchParams(window.location.search);
      const plantId = urlParams.get('plant');
      console.log(plantId);
    }
    const loadingOverlay = document.getElementById('loadingOverlay');

    function startLoading() {
      loadingOverlay.style.display = 'flex';
    }

    function stopLoading() {
      loadingOverlay.style.display = 'none';
    }
  
    const plantDetailsContainer = document.getElementById('plantDetailsContainer');
    const plantElement = document.createElement('div');
    plantElement.classList.add('plant-details');
    plantElement.innerHTML = `
      <p><strong>Plant Name:</strong> ${plantData.name}</p>
      <p><strong>Quantity:</strong> ${plantData.quantity}</p>
      <p><strong>Details:</strong> ${plantData.details}</p>
    `;
    plantDetailsContainer.appendChild(plantElement);
    
  });