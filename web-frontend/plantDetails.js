document.addEventListener('DOMContentLoaded', function() {
    if (window.location.pathname.endsWith('plantDetails.html')) {
      const urlParams = new URLSearchParams(window.location.search);
      const productName = urlParams.get('product');
      console.log(productName);
    }
  
    const plantData = {
          name: 'Sample Plant-1',
          quantity: 3,
          details: 'Lorem ipsum dolor sit amet...',
      };

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