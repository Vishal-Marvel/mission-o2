document.addEventListener("DOMContentLoaded", function (){
    const userForm = document.getElementById('userForm');
    const loadingOverlay = document.getElementById('loadingOverlay');

    function startLoading() {
        loadingOverlay.style.display = 'flex';
    }

    function stopLoading() {
        loadingOverlay.style.display = 'none';
    }
    userForm.addEventListener('submit', async function(event) {
        event.preventDefault();

        const userName = document.getElementById('userName').value;
        const email = document.getElementById('userEmail').value;
        const password = document.getElementById('password').value;
        const confirmPassword = document.getElementById('confirmPass').value;

        if (password !== confirmPassword){
            alert("Passwords Didnt Match");
            return
        }
        // Store data in a FormData object


        // Send FormData to server
        try {
            startLoading();
            const response = await axios.post(`${APIURL}/admin/create-user`, {
                'name': userName,
                'email': email,
                'password' : password
            }, {
                headers :{
                    'Authorization': `Bearer ${token}`
                }
            });
            stopLoading()
            if (response.status===200) {

                alert('User Created!');
                userForm.reset();
            } else {
               alert('Failed to create user');
            }
        } catch (error) {
            console.error(error);
            alert(error.response.data.message);
        } finally {
            // window.location.href = 'panel.html';
        }
    });
})