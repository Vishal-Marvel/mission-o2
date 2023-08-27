  const token = sessionStorage.getItem('Token'); 
  if (!token) {
    window.location.href = 'login.html';
  } 

document.addEventListener('DOMContentLoaded', function() {
    const plantForm = document.getElementById('addPlantForm');
  
    plantForm.addEventListener('submit', async function(event) {
      event.preventDefault();
  
      const plantName = document.getElementById('plantName').value;
      const plantPrice = document.getElementById('plantPrice').value;
      const seedPrice = document.getElementById('seedPrice').value;
      const imageFiles = document.getElementById('image').files;
  
      // Store data in a FormData object
      const formData = new FormData();
      formData.append('name', plantName);
      formData.append('price', parseFloat(plantPrice));
      formData.append('seedPrice', parseFloat(seedPrice));
  
      // Append multiple image files
      for (const file of imageFiles) {
        formData.append('images', file);
      }
  
      // Send FormData to server
      try {
        const response = await fetch('server-upload-endpoint', {
          method: 'POST',
          body: formData
        });
  
        if (response.ok) {
          const responseData = await response.json();
          // Handle response data if needed
          console.log(responseData);
          alert('Plant data and images uploaded successfully!');
          plantForm.reset();
        } else {
          throw new Error('Failed to upload plant data and images');
        }
      } catch (error) {
        console.error(error);
        alert('Error uploading plant data and images');
      } finally {
        window.location.href = 'panel.html';
      }
    });
  });
  
  