document.addEventListener('DOMContentLoaded', async function () {
    const tableBody = document.getElementById("tableBody");
    const deleteConfirmationModal = document.getElementById("deleteConfirmationModal");
    const forgotPasswordModel = document.getElementById("forgotPasswordModel");
    const forgetPasswordIP1 = document.getElementById("forgetPasswordIP1");
    const forgetPasswordIP2 = document.getElementById("forgetPasswordIP2");
    const confirmDeleteButton = document.getElementById('confirmDelete');
    const cancelDeleteButton = document.getElementById('cancelDelete');
    const resetPassword = document.getElementById('resetPassword');
    const cancelReset = document.getElementById('cancelReset');
    const noRecordsMessage = document.getElementById('noRecordsMessage');

    let userData;
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
        startLoading();
        await axios.get(`${APIURL}/admin/admin-users`, {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        }).then(response => {
            userData = response.data;

        }).catch(error => {
            console.error(error);
        })
        stopLoading();
        const len = Object.keys(userData).length;
        tableBody.innerHTML = "";
        if(len === 0){
            noRecordsMessage.style.display = 'table-row';
          }
        userData.forEach((user, index) => {

            const row = document.createElement("tr");
            row.innerHTML = `
                <td>${user.name}</td>
                <td>${user.email}</td>
                <td><button class="details-button password-button" data-index="${index}">Change Password</button></td>
                <td><button class="details-button delete-button" data-index="${index}">Delete</button></td>
            `;
            tableBody.appendChild(row);
        });

    }


    tableBody.addEventListener("click", async (event) => {
        if (event.target.classList.contains("details-button") && event.target.classList.contains("password-button")) {
            const index = parseInt(event.target.getAttribute("data-index"));
            if (!isNaN(index) && index >= 0 && index < userData.length) {
                forgotPasswordModel.style.display = 'block';
                cancelReset.addEventListener('click', () => {
                    forgotPasswordModel.style.display = 'none';
                });
                resetPassword.addEventListener('click', async () => {
                    const p1 = forgetPasswordIP1.value;
                    const p2 = forgetPasswordIP2.value;
                    if (p1 !== p2) {
                        alert("Password didnt match");
                        return;
                    }
                    await axios.post(`${APIURL}/admin/change-user-password`, {
                        'id': userData[index].id,
                        'password': p1
                    }, {
                        headers: {
                            'Authorization': `Bearer ${token}`
                        }
                    }).then(response => {
                        if (response.status === 200) {

                            alert("Password Changed");

                        }
                    }).catch(error => {
                        alert(error.response.data.message)
                        console.error(error);
                    });
                    forgetPasswordIP2.value = "";

                    forgetPasswordIP1.value = "";
                    forgotPasswordModel.style.display = 'none';
                });
            }
        } else if (event.target.classList.contains("details-button") && event.target.classList.contains("delete-button")) {
            const row = event.target.closest("tr");
            const index = parseInt(event.target.getAttribute("data-index"));
            if (!isNaN(index) && index >= 0 && index < userData.length) {
                deleteConfirmationModal.style.display = 'block';
                cancelDeleteButton.addEventListener('click', () => {
                    deleteConfirmationModal.style.display = 'none';
                });
                confirmDeleteButton.addEventListener('click', async () => {
                    await axios.delete(`${APIURL}/admin/delete-user/${userData[index].id}`, {
                        headers: {
                            'Authorization': `Bearer ${token}`
                        }
                    }).then(response => {
                        if (response.status === 200) {
                            alert('User deleted successfully!');
                            row.remove();
                        }
                    }).catch(error => {
                        alert(error.response.data.message)
                        console.error(error);
                    });
                    deleteConfirmationModal.style.display = 'none';
                });

            }
        }
    });

    await displayData();

});
