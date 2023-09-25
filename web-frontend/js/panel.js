document.addEventListener("DOMContentLoaded", async function () {

    let orders;

    const goToAddPlant = document.getElementById("addPlant");
    const counter = document.getElementById("count");
    const goToApprOrder = document.getElementById("apprOrder");
    const goToViewOrder = document.getElementById("viewOrder");
    const goToPlantOptions = document.getElementById("plantOptions");
    const goTostateWise = document.getElementById("stateWise");
    const goToUserProfile = document.getElementById("userProfile");
    const goToCreateUser = document.getElementById("createUser");
    const goToMessageBalance = document.getElementById("msgBalance");
    const goToViewAdmins = document.getElementById("viewAdmin");

    if (role === "ROLE_ADMIN_ASSIST") {
        goToAddPlant.style.display = "flex";
        goToPlantOptions.style.display = "flex";
        goToViewOrder.style.display = "flex";

    } else if (role === "ROLE_ADMIN") {
        goToAddPlant.style.display = "flex";
        goTostateWise.style.display = "flex";
        goToPlantOptions.style.display = "flex";
        goToViewOrder.style.display = "flex";
        goToUserProfile.style.display = "flex";
        goToCreateUser.style.display = "flex";
        goToMessageBalance.style.display = "flex";
        goToViewAdmins.style.display = "flex";

    }

    function startLoading() {
        loadingOverlay.style.display = "flex";
    }

    function stopLoading() {
        loadingOverlay.style.display = "none";
    }

    function animateCount(start, end) {
        let current = start;

        // Calculate the decrement value based on the difference
        const difference = Math.abs(start - end); // Absolute difference
        const step = Math.ceil(difference / 100); // Divide into 100 steps

        // Calculate the timeout duration based on the difference
        const duration = 1000; // Total animation duration in milliseconds
        const timeout = Math.floor(duration / difference * step);
        const timer = setInterval(() => {
            current -= step; // Use the calculated step value
            counter.innerText = Math.round(current).toLocaleString();

            // Check if we've reached the end
            if ((start < end && current >= end) || (start > end && current <= end)) {
                clearInterval(timer);
                counter.innerText = end.toLocaleString();
            }
        }, timeout); // Use the calculated timeout value
    }


    await axios
        .get(`${APIURL}/orders/total-plants`)
        .then((response) => {
            const count = +response.data.response;
            const startValue = 10000008;
            animateCount(startValue, startValue - count);
        })
        .catch((error) => {
            console.log(error);
        });

    await axios
        .get(`${APIURL}/orders/view-pending-orders/0/1000`, {
            headers: {
                Authorization: `Bearer ${token}`,
            },
        })
        .then((response) => {
            orders = response.data.total;
        })
        .catch((error) => {
            console.log(error);
            orders = 0;
        });

    goToApprOrder.innerHTML += ` (${orders})`;
    if (orders > 0) {
        goToApprOrder.style.display = "flex";
        goToApprOrder.addEventListener("click", function () {
            window.location.href = "apprOrders.html";
        });

    }
    goToAddPlant.addEventListener("click", function () {
        window.location.href = "addplant.html";
    });


    goToViewOrder.addEventListener("click", function () {
        window.location.href = "viewOrders.html";
    });

    goToPlantOptions.addEventListener("click", function () {
        window.location.href = "plantOptions.html";
    });
    goTostateWise.addEventListener("click", function () {
        window.location.href = "analysis.html";
    });
    goToUserProfile.addEventListener("click", function () {
        window.location.href = "viewUser.html";
    });
    goToCreateUser.addEventListener("click", function () {
        window.location.href = "createUser.html";
    });
    goToViewAdmins.addEventListener("click", function () {
        window.location.href = "viewAdminUsers.html";
    });
    const openPopupButton = document.getElementById("msgBalance");
    const closePopupButton = document.getElementById("closePopup");
    const popup = document.getElementById("popup");
    const msgContent = document.getElementById("msgContent");
    let msgcount;

    openPopupButton.addEventListener("click", function () {
        popup.style.display = "flex";
    });

    closePopupButton.addEventListener("click", function () {
        popup.style.display = "none";
    });
    if (role === "ROLE_ADMIN") {
        await axios
            .get(`${APIURL}/admin/sms-balance`, {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            })
            .then((response) => {
                msgcount = response.data.response;
                msgContent.textContent = msgcount;
            })
            .catch((error) => {
                console.log(error);
            });
    }
});
