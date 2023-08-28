
document.addEventListener('DOMContentLoaded', async function() {
  const productsContainer = document.getElementById('productsContainer');
  let orders;
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
    orders = response.data;
  }).catch(error=>{
    console.error(error);
  })
  stopLoading();
  

  orders.forEach(order => {
    const productElement = document.createElement('div');
    productElement.classList.add('product-panel');
    productElement.innerHTML = `
      <div class="product">
          <div class="product-description">
          <h3>Order Number: ${order.orderNum}</h3>
          <h4>Total Plant Ordered: ${order.totalPlant}</h4>
        </div>
        <div class="product-description">
        <h3>State: ${order.state}</h3>
         <h4>Order Date: ${order.orderDate}</h4>
      </div>
        <div class="product-actions">
          <button class="add-to-cart-button view-button">View Order</button>
        </div>
      </div>
    `;
    productsContainer.appendChild(productElement);
  });
  const viewButtons = document.querySelectorAll('.view-button');

  viewButtons.forEach((viewButton, index) => {
    const order = orders[index];

    viewButton.addEventListener('click', function() {
      window.location.href = `orderDetails.html?order=${encodeURIComponent(order.id)}`;
    });
  });
});