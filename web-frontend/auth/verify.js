document.addEventListener("DOMContentLoaded", async function (){
    const API_URL = "https://api.missiono2.com:8080/api/v1";
    const urlParams = new URLSearchParams(window.location.search);
    const code = urlParams.get('code');
    const task = urlParams.get('task');
    const loadingOverlay = document.getElementById('loadingOverlay');
    const success = document.getElementById("success");
    const successReset = document.getElementById("successReset");
    const failure = document.getElementById("failure");
    const enterEmail = document.getElementById("Get-Email");
    const reset = document.getElementById("Reset-Password");
    const sent = document.getElementById("Email-Sent");
    const tryAgainButton = document.getElementById("try-again");
    const sendMailButton = document.getElementById("send-mail");
    const resetPasswordButton = document.getElementById("resetPassword");
    const emailLabel = document.getElementById("email");
    const passwordInput = document.getElementById("password");
    const cPasswordInput = document.getElementById("c-password");


    function startLoading() {
        loadingOverlay.style.display = 'flex';
    }

    function stopLoading() {
        loadingOverlay.style.display = 'none';
    }
    tryAgainButton.addEventListener("click" , function (){
        failure.style.display = "none";
        enterEmail.style.display = "flex";
    })

    sendMailButton.addEventListener("click" , async function (){
        startLoading();
        await axios.post(`${API_URL}/user/dynamic-verify`, {
            "email":emailLabel.value
        })
            .then(()=>{
                enterEmail.style.display = "none";
                sent.style.display = "flex";
            })
            .catch((error) => {
                console.log(error)
                alert(error.response.data.message);
            })
        stopLoading();


    })
    if (task === "resetPassword"){
        reset.style.display = "flex";
    }else{
        startLoading();
        await axios.post(`${API_URL}/user/verify-email/${code}`)
            .then(()=>{
                success.style.display = "flex";
            })
            .catch((error) => {
                failure.style.display = "flex";
            })

        stopLoading()
    }
    resetPasswordButton.addEventListener("click", async function(){
        if (passwordInput.value !== cPasswordInput.value ){
            alert("Passwords didn't match");
        }
        else{
            startLoading();
            await axios.post(`${API_URL}/user/reset-password?code=${code}`, {
                "newPassword" : passwordInput.value
            })
                .then(()=>{
                    reset.style.display = "none";
                    successReset.style.display = "flex";
                })
                .catch((error) => {
                    alert(error.response.data.message);
                })

            stopLoading()
        }
    })


})