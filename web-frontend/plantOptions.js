document.addEventListener('DOMContentLoaded', function() {
    const productsContainer = document.getElementById('plantContainer');
    
    const products = [
      { name: 'Plant 1', price: 10.99, imageUrl: 'resources/product.png' },
      { name: 'Plant 2', price: 19.99, imageUrl: 'resources/product.png' },
      { name: 'Plant 3', price: 15.49, imageUrl: 'resources/product.png' },
      { name: 'Plant 4', price: 15.49, imageUrl: 'resources/product.png' },
      { name: 'Plant 5', price: 15.49, imageUrl: 'resources/product.png' },
      { name: 'Plant 6', price: 15.49, imageUrl: 'resources/product.png' },
      { name: 'Plant 7', price: 15.49, imageUrl: 'resources/product.png' },
      // Add more products as needed
    ];
  
    products.forEach(product => {
      const plantElement = document.createElement('div');
      plantElement.classList.add('plant-panel');
      plantElement.innerHTML = `
        <div class="plant">
          <img src="${product.imageUrl}" alt="${product.name}">
          <div class="plant-description">
            <h3>${product.name}</h3>
            <p class="price">${product.price.toFixed(2)}</p>
          </div>
          <div class="plant-actions">
            <button class="plant-operation-button view-button">View Plant</button>
            <button class="plant-operation-button update-button">Update Plant</button>
            <button class="plant-operation-button delete-button">Delete Plant</button>
          </div>
        </div>
      `;
      plantContainer.appendChild(plantElement);
    });
    const operationButtons = document.querySelectorAll('.plant-operation-button');
  
    operationButtons.forEach((operationButton, index) => {
      const product = products[index];
      operationButton.addEventListener('click', function(event) {
        const clickedButton = event.target;
      
        if (clickedButton.classList.contains('view-button')) {
          operationButton.addEventListener('click', function() {
            window.location.href = `plantDetails.html?plant=${encodeURIComponent(product.name)}`;
          });
        }
        else if (clickedButton.classList.contains('update-button')) {
          operationButton.addEventListener('click', function() {
            window.location.href = `plantUpdate.html?plant=${encodeURIComponent(product.name)}`;
          });
        } else if (clickedButton.classList.contains('delete-button')) {
          //detele operations here
          console.log('Delete button clicked');
          alert("Delete button clicked");
        }
    });
    });
  });