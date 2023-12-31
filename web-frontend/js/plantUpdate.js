function base64ToBlob(base64Data, contentType) {
    const sliceSize = 1024;
    const byteCharacters = atob(base64Data);
    const byteArrays = [];

    for (let offset = 0; offset < byteCharacters.length; offset += sliceSize) {
        const slice = byteCharacters.slice(offset, offset + sliceSize);
        const byteNumbers = new Array(slice.length);

        for (let i = 0; i < slice.length; i++) {
            byteNumbers[i] = slice.charCodeAt(i);
        }

        const byteArray = new Uint8Array(byteNumbers);
        byteArrays.push(byteArray);
    }

    return new Blob(byteArrays, {type: contentType});
}

document.addEventListener("DOMContentLoaded", async function () {
    const updateForm = document.getElementById("updateForm");
    const plantNameInput = document.getElementById("plantName");
    const plantPriceInput = document.getElementById("plantPrice");
    const seedPriceInput = document.getElementById("seedPrice");
    const imagesInput = document.getElementById("images");
    const currentImagesContainer = document.getElementById(
        "currentImagesContainer"
    );
    const urlParams = new URLSearchParams(window.location.search);
    const plantId = urlParams.get("plant");

    let plant;
    const loadingOverlay = document.getElementById("loadingOverlay");

    function startLoading() {
        loadingOverlay.style.display = "flex";
    }

    function stopLoading() {
        loadingOverlay.style.display = "none";
    }

    startLoading();
    await axios.get(`${APIURL}/plant/${plantId}`).then((response) => {
        plant = response.data;
    });

    // console.log(plant);
    // Prefill form with data
    plantNameInput.value = plant.name;
    plantPriceInput.value = plant.plantPrice;
    seedPriceInput.value = plant.seedPrice;

    // Display current images
  for (const imageUrl of plant.images) {
    try {
      const response = await axios.get(`${APIURL}/plant/image/${imageUrl}`);
      const imageData = response.data.image;

      const imageContainer = document.createElement("div");
      imageContainer.classList.add("image-container");

      const image = document.createElement("img");
      image.src = "data:image/jpg;base64," + imageData;
      image.id = imageUrl;
      image.alt = plant.name;

      const deleteButton = document.createElement("button");
      deleteButton.id = imageUrl;
      deleteButton.textContent = "Delete";
      deleteButton.style.backgroundColor = "#db0505";
      deleteButton.style.fontWeight = "bold";
      deleteButton.addEventListener("click", function () {
        try {
          plant.images = plant.images.filter((element) => element !== imageUrl);
          currentImagesContainer.removeChild(imageContainer);
        } catch (e) {
          console.error(e);

        }
      });

      imageContainer.appendChild(image);
      imageContainer.appendChild(deleteButton);

      currentImagesContainer.appendChild(imageContainer);
    } catch (error) {
      console.log(error.message);
    }
  }

  stopLoading();
    updateForm.addEventListener("submit", async function (event) {
        event.preventDefault();

        // Retrieve updated data
        const updatedPlantName = plantNameInput.value;
        const updatedPlantPrice = parseFloat(plantPriceInput.value);
        const updatedSeedPrice = parseFloat(seedPriceInput.value);
        const newImages = imagesInput.files;

        const formData = new FormData();
        formData.append("name", updatedPlantName);
        formData.append("plantPrice", updatedPlantPrice);
        formData.append("seedPrice", updatedSeedPrice);

        // Append multiple image files
        for (const file of newImages) {
            formData.append("images", file);
        }
        plant.images.forEach((image) => {

            const data = document.getElementById(image);
            formData.append(
                "images",
                base64ToBlob(data.getAttribute("src").substring(22), "image/jpg")
            );
        });
        if (formData.getAll("images").length ===0){
          alert("Image is required");
          return;
        }
        startLoading();
        await axios
            .put(`${APIURL}/plant/${plantId}`, formData, {
                headers: {
                    "Content-Type": "multipart/form-data",
                    Authorization: `Bearer ${token}`,
                },
            })
            .then((response) => {
                if (response.status === 200) {
                    alert("Plant details updated successfully!");
                    window.location.reload();
                }
            })
            .catch((error) => {
                alert(error.response.data.message);
                console.error(error);
            });
        stopLoading();
    });
});
