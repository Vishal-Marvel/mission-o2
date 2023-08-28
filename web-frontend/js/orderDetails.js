

document.addEventListener('DOMContentLoaded', async function() {

  const urlParams = new URLSearchParams(window.location.search);
  const orderId = urlParams.get('order');
  let orderData;
  const loadingOverlay = document.getElementById('loadingOverlay');

  function startLoading() {
    loadingOverlay.style.display = 'flex';
  }

  function stopLoading() {
    loadingOverlay.style.display = 'none';
  }
  startLoading();
  await axios.get(`${APIURL}/orders/view-order/${orderId}`, {
    headers :{
      'Authorization': `Bearer ${token}`
    }
  }).then(response=>{
    orderData = response.data;
  }).catch(error=>{
    console.error(error);
  })
  stopLoading();
  console.log(orderData)


  document.getElementById('userName').textContent = orderData.user;
  // document.getElementById('userAddress').textContent = orderData.user.address;
  document.getElementById('userDistrict').textContent = orderData.district;
  document.getElementById('userTaluk').textContent = orderData.taluk;
  document.getElementById('userState').textContent = orderData.state;
  document.getElementById('dateOfOrder').textContent = orderData.orderDate;

  let n = 1;
  const plantDetailsContainer = document.getElementById('plantDetailsContainer');
  orderData.products.forEach(async plant => {
    let plantDetails;
    await axios.get(`${APIURL}/plant/${plant.plantId}`)
    .then(response=>{
      plantDetails=response.data;})
    .catch(error=>console.error(error.response.data));

    console.log(plantDetails)
    const plantElement = document.createElement('div');
    plantElement.classList.add('plant-details');
    plantElement.innerHTML = `
      <h2>Plant-${n++}</h2>
      <p><strong>Plant Name:</strong> ${plantDetails.name}</p>
      <p><strong>Quantity:</strong> ${plant.quantity}</p>
      <p><strong>Details:</strong> ${plant.type}</p>
    `;
    plantDetailsContainer.appendChild(plantElement);
  });

  document.getElementById('googleMapsLink').href = orderData.locationURL;
  const locationImagesContainer = document.getElementById('locationImages');
  orderData.images.forEach(imageUrl => {
    const imgElement = document.createElement('img');
    imgElement.src = "data:image/jpg;base64," + imageUrl;
    imgElement.alt = 'Location Image';
    locationImagesContainer.appendChild(imgElement);
  });

  const approveOrderButton = document.getElementById('approveOrderButton');
  approveOrderButton.addEventListener('click',async function() {
    await axios.post(`${APIURL}/orders/status/${orderId}/APPROVED`, {}, {
      headers:{
        Authorization: `Bearer ${token}`
      }
    })
    .then(response=>{
      plantDetails=response.data;})
    .catch(error=>console.error(error.response.data));
    alert('Order Approved!');
    window.location.href="panel.html"
  });
});
