document.addEventListener('DOMContentLoaded', function() {
  const productsContainer = document.getElementById('productsContainer');
  
  const products = [
    { name: 'Product 1', price: 10.99, imageUrl: 'resources/product.png' },
    { name: 'Product 2', price: 19.99, imageUrl: 'resources/product.png' },
    { name: 'Product 3', price: 15.49, imageUrl: 'resources/product.png' },
    { name: 'Product 4', price: 15.49, imageUrl: 'resources/product.png' },
    { name: 'Product 5', price: 15.49, imageUrl: 'resources/product.png' },
    { name: 'Product 6', price: 15.49, imageUrl: 'resources/product.png' },
    { name: 'Product 7', price: 15.49, imageUrl: 'resources/product.png' },
    // Add more products as needed
  ];

  products.forEach(product => {
    const productElement = document.createElement('div');
    productElement.classList.add('product-panel');
    productElement.innerHTML = `
      <div class="product">
        <img src="${product.imageUrl}" alt="${product.name}">
        <div class="product-description">
          <h3>${product.name}</h3>
          <p class="price">$${product.price.toFixed(2)}</p>
        </div>
        <div class="product-actions">
          <button class="add-to-cart-button view-button">View Order</button>
          <button class="add-to-cart-button">Approve Order</button>
        </div>
      </div>
    `;
    productsContainer.appendChild(productElement);
  });
  const viewButtons = document.querySelectorAll('.view-button');

  viewButtons.forEach((viewButton, index) => {
    const product = products[index];

    viewButton.addEventListener('click', function() {
      window.location.href = `orderDetails.html?product=${encodeURIComponent(product.name)}`;
    });
  });
});