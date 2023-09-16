document.addEventListener('DOMContentLoaded', async function() {
    var userData;
    const loadingOverlay = document.getElementById('loadingOverlay');
    // Sample user data with multiple addresses (replace with actual data)
    const urlParams = new URLSearchParams(window.location.search);
    const userId = urlParams.get('user');
    function startLoading() {
        loadingOverlay.style.display = 'flex';
      }
  
      function stopLoading() {
        loadingOverlay.style.display = 'none';
      }
      function animateCount(elementID, start, end) {
        let current = start;
        const counter = document.getElementById(elementID)
        // Calculate the decrement value based on the difference
        const difference = Math.abs(end - start); // Absolute difference
        const step = Math.ceil(difference / 100); // Divide into 100 steps
        
        // Calculate the timeout duration based on the difference
        const duration = 1000; // Total animation duration in milliseconds
        const timeout = Math.floor(duration / difference * step);
        const timer = setInterval(() => {
          current += step; // Use the calculated step value
          counter.innerText = Math.round(current).toLocaleString();
          
          // Check if we've reached the end
          if ((start < end && current >= end) || (start > end && current <= end)) {
            clearInterval(timer);
            counter.innerText = end.toLocaleString();
          }
        }, timeout); // Use the calculated timeout value
      }
      startLoading();
      await axios.get(`${APIURL}/user/view-profile?user=${userId}`, {
        headers :{
          'Authorization': `Bearer ${token}`
        }
      }).then(response=>{
        userData = response.data;

      }).catch(error=>{
        console.error(error);
      })

  
    // Populate user details
    document.getElementById('userName').textContent = userData.name;
    document.getElementById('userEmail').textContent = userData.email;

    document.getElementById('userMobile').textContent = userData.mobile;
    if (userData.address) {
        document.getElementById('userAddress').textContent = `${userData.address.addressLine1}, ${userData.address.addressLine2}, ${userData.address.taluk}, ${userData.address.district}, ${userData.address.state}, ${userData.address.pinCode}`
    }
    animateCount('totalPlants',0,userData.totalPlants);
    animateCount('totalOrders',0,userData.totalOrders);
    // document.getElementById('totalPlants').textContent = userData.totalPlants;
    // document.getElementById('totalOrders').textContent = userData.totalOrders;

    // await axios.get(`${APIURL}/user/view-profile-file/${userData.proof}`, {
    //   headers :{
    //     'Authorization': `Bearer ${token}`
    //   }
    // })
    // .then(response=>{
    //   console.log(response.data)
    //   // Convert Base64 string to ArrayBuffer
    //     const binaryData = atob(response.data.image);
    //     const arrayBuffer = new Uint8Array(binaryData.length);
    //     for (let i = 0; i < binaryData.length; i++) {
    //     arrayBuffer[i] = binaryData.charCodeAt(i);
    //     }
    //
    //     // Create Blob URL
    //     const blob = new Blob([arrayBuffer], { type: 'application/octet-stream' });
    //     const blobUrl = URL.createObjectURL(blob);
    //
    //     // Update anchor link attributes
    //     const proofLink = document.getElementById('proof');
    //     proofLink.href = blobUrl;
    //     proofLink.download = response.data.fileName;
    // }).catch(error=>{
    //   console.error(error);
    // });
    stopLoading();
    const tableBody = document.getElementById("tableBody");
    const prevBtn = document.getElementById("prevBtn");
    const nextBtn = document.getElementById("nextBtn");
    const dataPerPageInput = document.getElementById("dataPerPage");
    const currentPageInput = document.getElementById("currentPage");
    const totalPagesDoc = document.getElementById("totalPage");
    const noRecordsMessage = document.getElementById('noRecordsMessage');

    let currentPage = 1;
    let dataPerPage = dataPerPageInput.value; 
    let orderData, totalPages, url="";
  
    function startLoading() {
      loadingOverlay.style.display = 'flex';
    }
  
    function stopLoading() {
      loadingOverlay.style.display = 'none';
    }
  
    //ADDING NEW SECTION
    async function displayData() {
      startLoading();
      await axios.get(`${APIURL}/orders/view-user-orders/${currentPage-1}/${dataPerPage}?user=${userId}&${url}`, {
        headers :{
          'Authorization': `Bearer ${token}`
        }
      }).then(response=>{
        orderData = response.data.data;
        totalPages = response.data.totalPages;
        
      }).catch(error=>{
        console.error(error);
      })
      stopLoading();
      const len = Object.keys(orderData).length;
      if(len === 0){
        noRecordsMessage.style.display = 'table-row';
      }
      const startIndex = (currentPage - 1) * dataPerPage;
  
      totalPagesDoc.innerText = totalPages
      currentPageInput.value = currentPage;
      tableBody.innerHTML = "";
  
      //plantData.slice(startIndex, endIndex);
    
      orderData.forEach((order, index) => {
          let colourStyle;
          if(order.orderStatus === "APPROVED"){
            colourStyle = "green";
          }
          else if(order.orderStatus === "COMPLETED"){
            colourStyle = "blue";
          }
          else{
            colourStyle = "red";
          }
          const row = document.createElement("tr");
          row.innerHTML = `
              <td>${order.orderNum}</td>
              <td>${order.orderDate.slice(0,10)}</td>
              <td>${order.totalPlant}</td>
              <td>${order.totalPrice}</td>
              <td>${order.state}</td>
              <td>${order.district}</td>
              <td>${order.taluk}</td>
              <td style="color:${colourStyle}">${order.orderStatus}</td>
              <td>${order.approvedBy}</td>
              <td><button class="details-button approve-button" data-index="${index}">View</button></td>
          `;
          tableBody.appendChild(row);
      });
      prevBtn.disabled = currentPage === 1;
      nextBtn.disabled = currentPage === totalPages || totalPages===0;
    }
    function updateDataPerPage() {
      const value = parseInt(dataPerPageInput.value);
      if (!isNaN(value) && value > 0) {
          dataPerPage = value;
          currentPage = 1; // Reset to the first page
          displayData();
      }
    }
    function updatePageNumber() {
      const value = parseInt(currentPageInput.value);
      if(!isNaN(value) && value>0 && value <= totalPages){
        currentPage = value;
        displayData();
      }
      else{
        alert("Page Number exceeded");
      }
    }
    
    prevBtn.addEventListener("click", () => {
      if (currentPage > 1) {
          currentPage--;
          currentPageInput.value = currentPage;
          displayData();
      }
    });
    
    nextBtn.addEventListener("click", () => {
      //if (currentPage < Math.ceil(orderData.length / dataPerPage)) 
      if (currentPage< totalPages)
      {
          currentPage++;
          currentPageInput.value = currentPage;
          displayData();
      }
    });
    
    dataPerPageInput.addEventListener("change", updateDataPerPage);
    currentPageInput.addEventListener("change", updatePageNumber);
    
    tableBody.addEventListener("click", async (event) => {
      if (event.target.classList.contains("details-button") && event.target.classList.contains("approve-button")) {
          const index = parseInt(event.target.getAttribute("data-index"));
          if (!isNaN(index) && index >= 0 ) {
              // const selectedPlant = plantData[index];
              // alert(`Plant Name: ${selectedPlant.name}\nSeed Price: ${selectedPlant.seedPrice}\nPlant Price: ${selectedPlant.plantPrice}`);
              window.location.href = `orderDetails.html?order=${encodeURIComponent(orderData[index].id)}`;
          }
      }
    });
    
    await displayData();
  
    //Filters
    const stateInput = document.getElementById("state");
    const districtInput = document.getElementById("district");
    const talukInput = document.getElementById("taluk");
    const fromDateInput = document.getElementById("from");
    const toDateInput = document.getElementById("to");
    const downloadAsExcel = document.getElementById("downloadAsExcel");
    const downloadAsPDF = document.getElementById("downloadAsPDF");
    const toggle = document.getElementById("toggleFilter");
    const filterSection = document.getElementById("filterSection");
    const applyFilter = document.getElementById('applyFilter');
    const reset = document.getElementById('resetFilter');
  
    function doFilter(){
      const selectedStatus = document.querySelector('input[name="status"]:checked').value;
  
      url=""
      if (stateInput.value){
        url += `state=${stateInput.value}&`;
      }
      if (districtInput.value){
        url += `district=${districtInput.value}&`;
      }
      if (talukInput.value){
        url += `taluk=${talukInput.value}&`;
      }
      if (selectedStatus !== "ALL"){
        url+= `status=${selectedStatus}&`
      }
      if (fromDateInput.value && toDateInput.value){
        const from = fromDateInput.value.replaceAll('-', '/');
        const to = toDateInput.value.replaceAll('-', '/');
  
        url += `from=${from}&to=${to}`;
  
      }
      else if(fromDateInput.value || toDateInput.value){
        alert('Both Dates needed')
      }
      
      displayData();
  
      
    }
    async function downloadAsExcelFunc(){
      await axios({
        method: 'get',
        url: `${APIURL}/orders/download?type=XLS&?user=${userId}&${url}`, // Replace with your server endpoint
        responseType: 'blob', // This specifies the response type as a binary blob
        headers: {
          Authorization: `Bearer ${token}`, // Include the authorization token here
        },
    
      })
          .then(response=>{
            const url = window.URL.createObjectURL(new Blob([response.data]));
  
            const a = document.createElement('a');
            a.style.display = 'none';
            a.href = url;
            a.download = 'Orders.xlsx'; // Specify the desired file name and extension
            document.body.appendChild(a);
  
            // Trigger a click event on the anchor element to initiate the download
            a.click();
  
            // Clean up by revoking the blob URL
            window.URL.revokeObjectURL(url);
          })
          .catch(error=>{
            console.error(error)
          })
        
    }
    async function downloadAsPDFFunc(){
      await axios({
        method: 'get',
        url: `${APIURL}/orders/download?type=PDF&?user=${userId}&${url}`, // Replace with your server endpoint
        responseType: 'blob', // This specifies the response type as a binary blob
        headers: {
          Authorization: `Bearer ${token}`, // Include the authorization token here
        },
    
      })
          .then(response=>{
            const url = window.URL.createObjectURL(new Blob([response.data]));
  
            const a = document.createElement('a');
            a.style.display = 'none';
            a.href = url;
            a.download = 'Orders.pdf'; // Specify the desired file name and extension
            document.body.appendChild(a);
  
            // Trigger a click event on the anchor element to initiate the download
            a.click();
  
            // Clean up by revoking the blob URL
            window.URL.revokeObjectURL(url);
          })
          .catch(error=>{
            console.error(error)
          })
        
    }
    downloadAsExcel.addEventListener("click", downloadAsExcelFunc);
    downloadAsPDF.addEventListener("click", downloadAsPDFFunc);
    applyFilter.addEventListener("click", doFilter);
    reset.addEventListener("click", function(){
      window.location.reload();
    })
    toggle.addEventListener("click", function () {
      this.classList.toggle("active");
      // Toggle the visibility of the filter section by changing its display style
      if (filterSection.style.maxHeight){
        filterSection.style.maxHeight = null;
        filterSection.style.padding = 0;
        filterSection.style.maxWidth = null;
  
        
      } else {
        filterSection.style.maxHeight = filterSection.scrollHeight + "px";
        filterSection.style.padding = "20px";
      } 
    });
   
  });
  