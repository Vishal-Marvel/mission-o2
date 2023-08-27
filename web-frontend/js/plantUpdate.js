function base64ToBlob(base64Data, contentType) {
  const sliceSize = 1024;
  const byteCharacters = atob(base64Data);
  const byteArrays = [];

  for (let offset = 0; offset < byteCharacters.length; offset += sliceSize) {
    const slice = byteCharacters.slice(offset, offset + sliceSize);
    const byteNumbers = new Array(slice.length);

    for (let i = 0; i < slice.length; i++) {
      byteNumbers[i] = slice.charCodeAt(i);
    }

    const byteArray = new Uint8Array(byteNumbers);
    byteArrays.push(byteArray);
  }

  return new Blob(byteArrays, { type: contentType });
}


document.addEventListener('DOMContentLoaded',async function() {
    const updateForm = document.getElementById('updateForm');
    const plantNameInput = document.getElementById('plantName');
    const plantPriceInput = document.getElementById('plantPrice');
    const seedPriceInput = document.getElementById('seedPrice');
    const imagesInput = document.getElementById('images');
    const currentImagesContainer = document.getElementById('currentImagesContainer');
    const urlParams = new URLSearchParams(window.location.search);
    const plantId = urlParams.get('plant');
    // Simulated prefilled data
    // const prefilledData = {
    //   plantName: 'Sample Plant',
    //   plantPrice: 10.99,
    //   seedPrice: 5.99,
    //   images: [
    //     'https://via.placeholder.com/300',
    //     'https://via.placeholder.com/300',
    //     'https://via.placeholder.com/300',
    //   ],
    // };
    let plant;
    await axios.get('http://localhost:8080/api/v1/plant/'+plantId).then(response=>{
      plant = response.data;
    })
    console.log(plant)
    // Prefill form with data
    plantNameInput.value = plant.name;
    plantPriceInput.value = plant.plantPrice;
    seedPriceInput.value = plant.seedPrice;
    
  
    // Display current images
    plant.images.forEach(imageUrl => {
      const imageContainer = document.createElement('div');
      imageContainer.classList.add('image-container');
      
      const image = document.createElement('img');
      image.src = "data:image/jpg;base64,"+imageUrl;
      image.alt = plant.name;
      
      const deleteButton = document.createElement('button');
      deleteButton.textContent = 'Delete';
      deleteButton.addEventListener('click', function() {
        plant.images.pop(imageUrl);
        currentImagesContainer.removeChild(imageContainer);
      });
  
      imageContainer.appendChild(image);
      imageContainer.appendChild(deleteButton);
      
      currentImagesContainer.appendChild(imageContainer);
    });
  
    updateForm.addEventListener('submit',async function(event) {
      event.preventDefault();
  
      // Retrieve updated data
      const updatedPlantName = plantNameInput.value;
      const updatedPlantPrice = parseFloat(plantPriceInput.value);
      const updatedSeedPrice = parseFloat(seedPriceInput.value);
      const newImages = imagesInput.files;

      const formData = new FormData();
      formData.append('name', updatedPlantName);
      formData.append('plantPrice', parseFloat(updatedPlantPrice));
      formData.append('seedPrice', parseFloat(updatedSeedPrice));
  
      // Append multiple image files
      for (const file of newImages) {
        formData.append('images', file);
      }
      plant.images.forEach(image=>{
        formData.append('images', base64ToBlob(image, 'image/jpg'));
      })
      await axios.put('http://localhost:8080/api/v1/plant/'+plantId, formData, {
          headers :{
            'Content-Type': 'multipart/form-data',
            'Authorization': `Bearer ${token}`
          }
        }).then(response=>{
          if (response.status==200){
            alert('Plant details updated successfully!');
            window.location.reload();
          }
        }).catch(error=>{
          alert (error.response.data.message)
          console.error(error);
        });
  
      
    });
  });
  