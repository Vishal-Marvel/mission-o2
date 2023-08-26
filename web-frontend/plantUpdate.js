document.addEventListener('DOMContentLoaded', function() {
    const updateForm = document.getElementById('updateForm');
    const plantNameInput = document.getElementById('plantName');
    const plantPriceInput = document.getElementById('plantPrice');
    const seedPriceInput = document.getElementById('seedPrice');
    const imagesInput = document.getElementById('images');
    const currentImagesContainer = document.getElementById('currentImagesContainer');
  
    // Simulated prefilled data
    const prefilledData = {
      plantName: 'Sample Plant',
      plantPrice: 10.99,
      seedPrice: 5.99,
      images: [
        'https://via.placeholder.com/300',
        'https://via.placeholder.com/300',
        'https://via.placeholder.com/300',
      ],
    };
  
    // Prefill form with data
    plantNameInput.value = prefilledData.plantName;
    plantPriceInput.value = prefilledData.plantPrice;
    seedPriceInput.value = prefilledData.seedPrice;
  
    // Display current images
    prefilledData.images.forEach(imageUrl => {
      const imageContainer = document.createElement('div');
      imageContainer.classList.add('image-container');
      
      const image = document.createElement('img');
      image.src = imageUrl;
      image.alt = 'Image';
      
      const deleteButton = document.createElement('button');
      deleteButton.textContent = 'Delete';
      deleteButton.addEventListener('click', function() {
        currentImagesContainer.removeChild(imageContainer);
      });
  
      imageContainer.appendChild(image);
      imageContainer.appendChild(deleteButton);
      
      currentImagesContainer.appendChild(imageContainer);
    });
  
    updateForm.addEventListener('submit', function(event) {
      event.preventDefault();
  
      // Retrieve updated data
      const updatedPlantName = plantNameInput.value;
      const updatedPlantPrice = parseFloat(plantPriceInput.value);
      const updatedSeedPrice = parseFloat(seedPriceInput.value);
      // ... handle image uploads if needed ...
  
      // Perform update operation (e.g., send to backend)
      console.log('Updated Plant Name:', updatedPlantName);
      console.log('Updated Plant Price:', updatedPlantPrice);
      console.log('Updated Seed Price:', updatedSeedPrice);
  
      alert('Plant details updated successfully!');
    });
  });
  