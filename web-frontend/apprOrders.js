document.addEventListener('DOMContentLoaded', function() {
    const productsContainer = document.getElementById('productsContainer');
    
    const products = [
      { name: 'Product 1', price: 10.99 },
      { name: 'Product 2', price: 19.99 },
      { name: 'Product 3', price: 15.49 },
      // Add more products as needed
    ];
  
    products.forEach(product => {
      const productElement = document.createElement('div');
      productElement.classList.add('product-panel');
      productElement.innerHTML = `
      <div class="product">
        <img src="resources\icon.jpeg" alt="Product 1">
        <div class = "product-description">
          <h3>${product.name}</h3>
          <p class="price">${product.price}</p>
          <button class="add-to-cart-button">View Order</button>
          <button class="add-to-cart-button">Approve Order</button>
        </div>
      </div>
      `;
      productsContainer.appendChild(productElement);
    });
  });
  