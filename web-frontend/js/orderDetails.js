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

  document.getElementById('userName').textContent = orderData.user;
  document.getElementById('orderID').textContent = orderData.orderNum;
  document.getElementById('userAddress').textContent =
      `${orderData.address.addressLine1}, ${orderData.address.addressLine2}, ${orderData.address.taluk}, ${orderData.address.district}, ${orderData.address.state}, ${orderData.address.pincode}` ;
  document.getElementById('userDistrict').textContent = orderData.district;
  document.getElementById('userTaluk').textContent = orderData.taluk;
  document.getElementById('userState').textContent = orderData.state;
  document.getElementById('status').textContent = orderData.orderStatus;
  document.getElementById('approvedBy').textContent = orderData.approvedBy;
  document.getElementById('dateOfOrder').textContent = orderData.orderDate.slice(0, 10);

  let n = 1;
  const userDetailsContainer = document.getElementById("userDetailsContainer");
  const plantDetailsContainer = document.getElementById('plantDetailsContainer');
  for(const plant of orderData.products){
    let plantname;
    await axios.get(`${APIURL}/plant/${plant.plantId}`)
    .then(response=>{
      plantname=response.data.name;})
    .catch(error=>console.error(error.response.data)
    );
    if (!plantname) {
      plantname = plant.name;
    }
    const plantElement = document.createElement('div');
    plantElement.classList.add('plant-details');
    plantElement.innerHTML = `
      <h2>Plant-${n++}</h2>
      <ul style="list-style-type: none">
      <li><p><strong>Plant Name:</strong> ${plantname}</p></li>
      <li><p><strong>Quantity:</strong> ${plant.quantity}</p></li>
      <li><p><strong>Details:</strong> ${plant.type}</p></li>
      </ul>
    `;
    plantDetailsContainer.appendChild(plantElement);
  }

  function setMaxHeight() {
    const userDetailsHeight = userDetailsContainer.offsetHeight;
    console.log(userDetailsHeight)
    plantDetailsContainer.style.maxHeight = userDetailsHeight-40 + "px";
  }
  setMaxHeight();
  window.addEventListener("resize", setMaxHeight);



  document.getElementById('googleMapsLink').href = orderData.locationURL;
  const locationImagesContainer = document.getElementById('prelocationImages');
  for(const imageUrl of orderData.images){
    const imgElement = document.createElement('img');
    let image;
    await axios.get(`${APIURL}/plant/image/${imageUrl}`).
    then(response=>{
      image = response.data.image;
    })
    .catch(error=>{
      console.error(error.message);
    })
    imgElement.src = "data:image/jpg;base64," + image;
    imgElement.alt = 'Location Image';
    // imgElement.classList.add("location-image")
    locationImagesContainer.appendChild(imgElement);
  }
  const postlocationImagesContainer = document.getElementById('postlocationImages');
  for(const imageUrl of orderData.postImages) {
    const imgElement = document.createElement('img');
    let image;
    await axios.get(`${APIURL}/plant/image/${imageUrl}`).
    then(response=>{
      image = response.data.image;
    })
        .catch(error=>{
          console.error(error.message);
        })
    imgElement.src = "data:image/jpg;base64," + image;
    imgElement.alt = 'Location Image';
    // imgElement.classList.add("location-image")
    postlocationImagesContainer.appendChild(imgElement);
  }
  const approveOrderButton = document.createElement('button')
  approveOrderButton.innerText = "Approve"
  approveOrderButton.classList.add("approve");
  if (orderData.orderStatus == "PENDING"){
    const container = document.getElementById('approveOrderButton');

    container.appendChild(approveOrderButton);
    
  }
  approveOrderButton.addEventListener('click',async function() {
    await axios.post(`${APIURL}/orders/status/${orderId}/APPROVED`, {}, {
      headers:{
        Authorization: `Bearer ${token}`
      }
    })
    .then(response=>{
      let plantDetails = response.data;})
    .catch(error=>console.error(error.response.data));
    console.log(document.referrer)
    alert('Order Approved!');
    window.location.href="viewOrders.html"
  });
});
