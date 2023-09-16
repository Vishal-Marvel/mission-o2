document.addEventListener('DOMContentLoaded',async function() {
  const tableBody = document.getElementById("tableBody");
  const prevBtn = document.getElementById("prevBtn");
  const nextBtn = document.getElementById("nextBtn");
  const dataPerPageInput = document.getElementById("dataPerPage");
  const currentPageInput = document.getElementById("currentPage");
  const totalPagesDoc = document.getElementById("totalPage");
  const noRecordsMessage = document.getElementById('noRecordsMessage');

  let currentPage = 1;
  let dataPerPage = dataPerPageInput.value; 
  let orderData, totalPages, url;
  const loadingOverlay = document.getElementById('loadingOverlay');

  function startLoading() {
    loadingOverlay.style.display = 'flex';
  }

  function stopLoading() {
    loadingOverlay.style.display = 'none';
  }
  //ADDING NEW SECTION
  async function displayData() {
    noRecordsMessage.style.display = 'none'
    const startIndex = (currentPage - 1) * dataPerPage;
    startLoading();
    await axios.get(`${APIURL}/orders/view-pending-orders/${currentPage-1}/${dataPerPage}?${url}`, {
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
    totalPagesDoc.innerText = totalPages
    currentPageInput.value = currentPage;
    tableBody.innerHTML = "";

    //plantData.slice(startIndex, endIndex);
    if(len == 0){
      console.log(len);
      noRecordsMessage.style.display = 'table-row';
    }
    orderData.forEach((order, index) => {
        const row = document.createElement("tr");
        row.innerHTML = `
            <td>${order.orderNum}</td>
            <td>${order.orderDate.slice(0,10)}</td>
            <td>${order.totalPlant}</td>
            <td>${order.totalPrice}</td>
            <td>${order.state}</td>
            <td>${order.district}</td>
            <td>${order.taluk}</td>
            <td><button class="details-button approve-button" data-index="${index}">Approve</button></td>
        `;
        tableBody.appendChild(row);
    });
    prevBtn.disabled = currentPage === 1;
    nextBtn.disabled = currentPage === totalPages;
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
    console.log(event.target.getAttribute("data-index"))
    if (event.target.classList.contains("details-button") && event.target.classList.contains("approve-button")) {
        const index = parseInt(event.target.getAttribute("data-index"));
        if (!isNaN(index) && index >= 0) {
            // const selectedPlant = plantData[index];
            // alert(`Plant Name: ${selectedPlant.name}\nSeed Price: ${selectedPlant.seedPrice}\nPlant Price: ${selectedPlant.plantPrice}`);
            window.location.href = `orderDetails.html?order=${encodeURIComponent(orderData[index].id)}`;
        }
    }
  });
  
  displayData();
  
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

    url="status=PENDING&"
    if (stateInput.value){
      url += `state=${stateInput.value}&`;
    }
    if (districtInput.value){
      url += `district=${districtInput.value}&`;
    }
    if (talukInput.value){
      url += `taluk=${talukInput.value}&`;
    }
    if (fromDateInput.value && toDateInput.value){
      const from = fromDateInput.value.replaceAll('-', '/');
      const to = toDateInput.value.replaceAll('-', '/');

      url += `from=${from}&to=${to}`;

    }
    else if(fromDateInput.value || toDateInput.value){
      alert('Both Dates needed')
    }
    console.log(url)
    displayData();

    
  }
  function downloadAsExcelFunc(){
    axios({
      method: 'get',
      url: `${APIURL}/orders/download?type=XLS&${url}`, // Replace with your server endpoint
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
  function downloadAsPDFFunc(){
    axios({
      method: 'get',
      url: `${APIURL}/orders/download?type=PDF&${url}`, // Replace with your server endpoint
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


  applyFilter.addEventListener("click", doFilter);
  reset.addEventListener("click", function(){
    window.location.reload();
  })
  downloadAsExcel.addEventListener("click", downloadAsExcelFunc);
  downloadAsPDF.addEventListener("click", downloadAsPDFFunc);

  
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