document.addEventListener('DOMContentLoaded',async function() {
    const productsContainer = document.getElementById('plantContainer');
    let products;
    const loadingOverlay = document.getElementById('loadingOverlay');

    function startLoading() {
      loadingOverlay.style.display = 'flex';
    }

    function stopLoading() {
      loadingOverlay.style.display = 'none';
    }
    startLoading();
    await axios.get('http://localhost:8080/api/v1/plant/view/all').then(response=>{
      products = response.data;
    })
    stopLoading();
    products.forEach(product => {
      const plantElement = document.createElement('div');
      plantElement.classList.add('plant-panel');
      plantElement.innerHTML = `
        <div class="plant">
          <img src="data:image/jpg;base64,${product.images[0]}" alt="${product.name}">
          <div class="plant-description">
            <h3>${product.name}</h3>
            <p class="price">Plant: ${product.plantPrice.toFixed(2)}</p>
            <p class="price">Seed: ${product.seedPrice.toFixed(2)}</p>
          </div>
          <div class="plant-actions">
            <button class="plant-operation-button update-button">Update Plant</button>
            <button class="plant-operation-button delete-button">Delete Plant</button>
          </div>
        </div>
      `;
      productsContainer.appendChild(plantElement);
    });
    const operationButtons = document.querySelectorAll('.plant-operation-button');
  
    operationButtons.forEach((operationButton, index) => {
      index = Math.floor(index/2);
      let product = products[index];
      operationButton.addEventListener('click', async function(event) {
        const clickedButton = event.target;
        console.log(product);
        if (clickedButton.classList.contains('view-button')) {
            window.location.href = `plantDetails.html?plant=${encodeURIComponent(product.id)}`;  
        }
        else if (clickedButton.classList.contains('update-button')) {
            window.location.href = `plantUpdate.html?plant=${encodeURIComponent(product.id)}`;
          
        } else if (clickedButton.classList.contains('delete-button')) {
          console.log(product)
          await axios.delete(`http://localhost:8080/api/v1/plant/${product.id}`, {
            headers :{
              'Authorization': `Bearer ${token}`
            }
          }).then(response=>{
            if (response.status==200){
              alert('Plant deleted successfully!');
              window.location.reload();
            }
          }).catch(error=>{
            alert (error.response.data.message)
            console.error(error);
          });
        }
      });
          

    });
  });