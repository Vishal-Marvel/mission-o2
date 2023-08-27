document.addEventListener('DOMContentLoaded', function() {
  if (window.location.pathname.endsWith('orderDetails.html')) {
    const urlParams = new URLSearchParams(window.location.search);
    const productName = urlParams.get('product');
    console.log(productName);
  }

  const orderData = {
    user: {
      name: 'John Doe',
      address: '123 Main St',
      district: 'Sample District',
      taluk: 'Sample Taluk',
      state: 'Sample State',
      dateOfOrder: '2023-08-21',
    },
    plants: [
      {
        name: 'Sample Plant 1',
        quantity: 5,
        details: 'Lorem ipsum dolor sit amet...',
      },
      {
        name: 'Sample Plant 2',
        quantity: 3,
        details: 'Lorem ipsum dolor sit amet...',
      },
    ],
    location: {
      googleMapsLink: 'https://maps.google.com',
      locationImages: [
        'https://via.placeholder.com/300',
        'https://via.placeholder.com/300',
        'https://via.placeholder.com/300',
      ],
    },
  };

  document.getElementById('userName').textContent = orderData.user.name;
  document.getElementById('userAddress').textContent = orderData.user.address;
  document.getElementById('userDistrict').textContent = orderData.user.district;
  document.getElementById('userTaluk').textContent = orderData.user.taluk;
  document.getElementById('userState').textContent = orderData.user.state;
  document.getElementById('dateOfOrder').textContent = orderData.user.dateOfOrder;

  let n = 1;
  const plantDetailsContainer = document.getElementById('plantDetailsContainer');
  orderData.plants.forEach(plant => {
    const plantElement = document.createElement('div');
    plantElement.classList.add('plant-details');
    plantElement.innerHTML = `
      <h2>Plant-${n++}</h2>
      <p><strong>Plant Name:</strong> ${plant.name}</p>
      <p><strong>Quantity:</strong> ${plant.quantity}</p>
      <p><strong>Details:</strong> ${plant.details}</p>
    `;
    plantDetailsContainer.appendChild(plantElement);
  });

  document.getElementById('googleMapsLink').href = orderData.location.googleMapsLink;
  const locationImagesContainer = document.getElementById('locationImages');
  orderData.location.locationImages.forEach(imageUrl => {
    const imgElement = document.createElement('img');
    imgElement.src = imageUrl;
    imgElement.alt = 'Location Image';
    locationImagesContainer.appendChild(imgElement);
  });

  const approveOrderButton = document.getElementById('approveOrderButton');
  approveOrderButton.addEventListener('click', function() {
    alert('Order Approved!');
  });
});
