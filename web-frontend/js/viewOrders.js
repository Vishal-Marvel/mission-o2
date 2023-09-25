document.addEventListener('DOMContentLoaded', async function () {
    const tableBody = document.getElementById("tableBody");
    const prevBtn = document.getElementById("prevBtn");
    const nextBtn = document.getElementById("nextBtn");
    const dataPerPageInput = document.getElementById("dataPerPage");
    const currentPageInput = document.getElementById("currentPage");
    const totalPagesDoc = document.getElementById("totalPage");
    const search = document.getElementById("search");
    const noRecordsMessage = document.getElementById('noRecordsMessage');
    const urlParams = new URLSearchParams(window.location.search);
    const taluk = urlParams.get('taluk');

    let currentPage = 1;
    let dataPerPage = dataPerPageInput.value;
    let orderData, totalPages, url = "";
    const loadingOverlay = document.getElementById('loadingOverlay');

    function startLoading() {
        loadingOverlay.style.display = 'flex';
    }

    function stopLoading() {
        loadingOverlay.style.display = 'none';
    }

    function showData() {
        noRecordsMessage.style.display = 'none'
        tableBody.innerHTML = "";
        const len = Object.keys(orderData).length;
        if (len === 0) {
            noRecordsMessage.style.display = 'table-row';
        }
        orderData.forEach((order, index) => {
            let colourStyle;
            if (order.orderStatus === "APPROVED") {
                colourStyle = "green";
            } else if (order.orderStatus === "COMPLETED") {
                colourStyle = "blue";
            } else {
                colourStyle = "red";
            }
            const row = document.createElement("tr");
            row.innerHTML = `
            <td>${order.orderNum}</td>
            <td>${order.orderDate.slice(0, 10)}</td>
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
        nextBtn.disabled = currentPage === totalPages || totalPages === 0;
    }

    //ADDING NEW SECTION

    async function getData() {
        startLoading();
        await axios.get(`${APIURL}/orders/view-orders/${currentPage - 1}/${dataPerPage}?${url}`, {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        }).then(response => {
            orderData = response.data.data;
            totalPages = response.data.totalPages;

        }).catch(error => {
            console.error(error);
        })
        stopLoading();

        totalPagesDoc.innerText = totalPages;
        currentPageInput.value = currentPage;
        showData();
    }


    function updateDataPerPage() {
        const value = parseInt(dataPerPageInput.value);
        if (!isNaN(value) && value > 0) {
            dataPerPage = value;
            currentPage = 1; // Reset to the first page
            getData();
        }
    }

    function updatePageNumber() {
        const value = parseInt(currentPageInput.value);
        if (!isNaN(value) && value > 0 && value <= totalPages) {
            currentPage = value;
            getData();
        } else {
            alert("Page Number exceeded");
        }
    }

    prevBtn.addEventListener("click", () => {
        if (currentPage > 1) {
            currentPage--;
            currentPageInput.value = currentPage;
            getData();
        }
    });

    nextBtn.addEventListener("click", () => {
        //if (currentPage < Math.ceil(orderData.length / dataPerPage))
        if (currentPage < totalPages) {
            currentPage++;
            currentPageInput.value = currentPage;
            getData();
        }
    });

    dataPerPageInput.addEventListener("change", updateDataPerPage);
    currentPageInput.addEventListener("change", updatePageNumber);

    tableBody.addEventListener("click", async (event) => {
        if (event.target.classList.contains("details-button") && event.target.classList.contains("approve-button")) {
            const index = parseInt(event.target.getAttribute("data-index"));
            if (!isNaN(index) && index >= 0) {
                // const selectedPlant = plantData[index];
                // alert(`Plant Name: ${selectedPlant.name}\nSeed Price: ${selectedPlant.seedPrice}\nPlant Price: ${selectedPlant.plantPrice}`);
                window.location.href = `orderDetails.html?order=${encodeURIComponent(orderData[index].id)}`;
            }
        }
    });

    search.addEventListener("input", async function () {
        doFilter();

    });
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
    if (taluk !== null){
        url+= "taluk="+taluk+"&";
        talukInput.value = taluk;

    }
    await getData();
    function generateUrl() {

        const selectedStatus = document.querySelector('input[name="status"]:checked').value;

        url = ""
        if (search.value) {
            url += `orderNum=${search.value}&`
        }
        if (stateInput.value) {
            url += `state=${stateInput.value}&`;
        }
        if (districtInput.value) {
            url += `district=${districtInput.value}&`;
        }
        if (talukInput.value) {
            url += `taluk=${talukInput.value}&`;
        }
        if (selectedStatus !== "ALL") {
            url += `status=${selectedStatus}&`
        }
        if (fromDateInput.value && toDateInput.value) {
            const from = fromDateInput.value.replaceAll('-', '/');
            const to = toDateInput.value.replaceAll('-', '/');

            url += `from=${from}&to=${to}`;

        } else if (fromDateInput.value || toDateInput.value) {
            alert('Both Dates needed')
        }

    }

    function doFilter() {
        generateUrl()
        getData();

    }

    async function downloadAsExcelFunc() {
        generateUrl()
        await axios({
            method: 'get',
            url: `${APIURL}/orders/download?type=XLS&${url}`, // Replace with your server endpoint
            responseType: 'blob', // This specifies the response type as a binary blob
            headers: {
                Authorization: `Bearer ${token}`, // Include the authorization token here
            },

        })
            .then(response => {
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
            .catch(error => {
                console.error(error)
            })

    }

    async function downloadAsPDFFunc() {
        generateUrl()
        await axios({
            method: 'get',
            url: `${APIURL}/orders/download?type=PDF&${url}`, // Replace with your server endpoint
            responseType: 'blob', // This specifies the response type as a binary blob
            headers: {
                Authorization: `Bearer ${token}`, // Include the authorization token here
            },

        })
            .then(response => {
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
            .catch(error => {
                console.error(error)
            })

    }


    applyFilter.addEventListener("click", doFilter);
    reset.addEventListener("click", function () {
        window.location.reload();
    })
    downloadAsExcel.addEventListener("click", downloadAsExcelFunc);
    downloadAsPDF.addEventListener("click", downloadAsPDFFunc);

    toggle.addEventListener("click", function () {
        this.classList.toggle("active");
        // Toggle the visibility of the filter section by changing its display style
        if (filterSection.style.maxHeight) {
            filterSection.style.maxHeight = null;
            filterSection.style.padding = 0;
            filterSection.style.maxWidth = null;


        } else {
            filterSection.style.maxHeight = filterSection.scrollHeight + "px";
            filterSection.style.padding = "20px";
        }
    });

});