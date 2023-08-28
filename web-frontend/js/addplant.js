
document.addEventListener('DOMContentLoaded', function() {
    const plantForm = document.getElementById('addPlantForm');
    const loadingOverlay = document.getElementById('loadingOverlay');

    function startLoading() {
      loadingOverlay.style.display = 'flex';
    }

    function stopLoading() {
      loadingOverlay.style.display = 'none';
    }
    plantForm.addEventListener('submit', async function(event) {
      event.preventDefault();
  
      const plantName = document.getElementById('plantName').value;
      const plantPrice = document.getElementById('plantPrice').value;
      const seedPrice = document.getElementById('seedPrice').value;
      const imageFiles = document.getElementById('image').files;
  
      // Store data in a FormData object
      const formData = new FormData();
      formData.append('name', plantName);
      formData.append('plantPrice', parseFloat(plantPrice));
      formData.append('seedPrice', parseFloat(seedPrice));
  
      // Append multiple image files
      for (const file of imageFiles) {
        formData.append('images', file);
      }
  
      // Send FormData to server
      try {
        startLoading();
        const response = await axios.post(`${APIURL}/plant/create/add`, formData, {
          headers :{
            'Content-Type': 'multipart/form-data',
            'Authorization': `Bearer ${token}`
          }
        });
        stopLoading()
        if (response.status==200) {

          alert('Plant data and images uploaded successfully!');
          plantForm.reset();
        } else {
          throw new Error('Failed to upload plant data and images');
        }
      } catch (error) {
        console.error(error);
        alert(error.response.data.message);
      } finally {
        // window.location.href = 'panel.html';
      }
    });
  });
  
  