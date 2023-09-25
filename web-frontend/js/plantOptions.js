document.addEventListener('DOMContentLoaded',async function() {
    const tableBody = document.getElementById("tableBody");
    const prevBtn = document.getElementById("prevBtn");
    const nextBtn = document.getElementById("nextBtn");
    const dataPerPageInput = document.getElementById("dataPerPage");
    const currentPageInput = document.getElementById("currentPage");
    const totalPagesDoc = document.getElementById("totalPage");
    const confirmationModal = document.getElementById('confirmationModal');
    const confirmDeleteButton = document.getElementById('confirmDelete');
    const cancelDeleteButton = document.getElementById('cancelDelete');

    let currentPage = 1;
    let dataPerPage = 5; 
    let plantData, totalPages;
    const loadingOverlay = document.getElementById('loadingOverlay');

    function startLoading() {
      loadingOverlay.style.display = 'flex';
    }

    function stopLoading() {
      loadingOverlay.style.display = 'none';
    }
    async function display() {
        if (role === "ROLE_ADMIN") {
            const dels = document.getElementsByClassName("delete");
            Array.from(dels).forEach((element) => {
                element.removeAttribute("disabled");
                element.classList.remove("delete")
            });
            // console.log(dels)

        }
    }
    //ADDING NEW SECTION
    async function displayData() {
      startLoading();
      await axios.get(`${APIURL}/plant/view/all/${currentPage-1}/${dataPerPage}`)
      .then(response=>{
        plantData = response.data.data;
        totalPages =response.data.totalPages
      }).catch(error=>{
        console.error(error);
      })
      stopLoading();
      totalPagesDoc.innerText = totalPages
      tableBody.innerHTML = "";
      const startIndex = (currentPage - 1) * dataPerPage;
      
      plantData.forEach((plant, index) => {
          const row = document.createElement("tr");
          row.innerHTML = `
              <td>${plant.name}</td>
              <td>${plant.seedPrice}</td>
              <td>${plant.plantPrice}</td>
              <td><button class="details-button update-button" data-index="${index}">Update</button></td>
              <td><button class="details-button delete-button delete" data-index="${index}" disabled>Delete</button></td>
          `;
          tableBody.appendChild(row);
      });
      await display();
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
      if (currentPage < totalPages) {
          currentPage++;
          currentPageInput.value = currentPage;
          displayData();
      }
    });
    
    dataPerPageInput.addEventListener("change", updateDataPerPage);
    currentPageInput.addEventListener("change", updatePageNumber);
    
    tableBody.addEventListener("click", async (event) => {
      if (event.target.classList.contains("details-button") && event.target.classList.contains("update-button")) {
          const index = parseInt(event.target.getAttribute("data-index"));
          if (!isNaN(index) && index >= 0 && index < plantData.length) {
              // const selectedPlant = plantData[index];
              // alert(`Plant Name: ${selectedPlant.name}\nSeed Price: ${selectedPlant.seedPrice}\nPlant Price: ${selectedPlant.plantPrice}`);
              window.location.href = `plantUpdate.html?plant=${encodeURIComponent(plantData[index].id)}`;
          }
      }
      else if(event.target.classList.contains("details-button") && event.target.classList.contains("delete-button")){
          const row = event.target.closest("tr");
          const index = parseInt(event.target.getAttribute("data-index"));
          if (!isNaN(index) && index >= 0 && index < plantData.length) {
              let i = 0;
              confirmationModal.style.display = 'block';
            cancelDeleteButton.addEventListener('click', () => {
              confirmationModal.style.display = 'none';
              i=1;
            });
            if(i===0){
              confirmDeleteButton.addEventListener('click', async () => {
                await axios.delete(`${APIURL}/plant/${plantData[index].id}`, {
                  headers :{
                    'Authorization': `Bearer ${token}`
                  }
                }).then(response=>{
                  if (response.status===200){
                    alert('Plant deleted successfully!');
                    row.remove();
                  }
                }).catch(error=>{
                  alert (error.response.data.message)
                  console.error(error);
                });
                confirmationModal.style.display = 'none';
              });
          }
        }
      }
    });
    
    displayData();
    //ENDING NEW SECTION
  });