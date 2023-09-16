document.addEventListener("DOMContentLoaded", async function () {
  const urlParams = new URLSearchParams(window.location.search);
  const state = urlParams.get("state");
  const district = urlParams.get("district");
  let url = "";
  const loadingOverlay = document.getElementById("loadingOverlay");
  const description = document.querySelector(".description")
  const searchInput = document.getElementById("searchInput");

  function startLoading() {
    loadingOverlay.style.display = "flex";
  }

  function stopLoading() {
    loadingOverlay.style.display = "none";
  }
  let placeData;
  if (state) {
    url += "?state=" + state;
    const a = document.createElement('a')
    a.href = url;
    a.innerText = state;
    const slash = document.createElement('p')
    slash.innerText = "/";
    slash.style.padding = "0px 10px";
    description.appendChild(slash)
    description.appendChild(a)
    searchInput.setAttribute('placeHolder', "Seach District...") ;
  }
  if (district) {
    url += "&district=" + district;
    const a = document.createElement('a')
    a.href = url;
    a.innerText = district
    const slash = document.createElement('p')
    slash.innerText = "/";
    slash.style.padding = "0px 10px";
    description.appendChild(slash)
    description.appendChild(a)
    searchInput.setAttribute('placeHolder', "Seach Taluk...") ;

  }


  startLoading();
  await axios
    .get(`${APIURL}/orders/view-count${url}`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    })
    .then((response) => {
      placeData = response.data;
    });
  // console.log(placeData);
  stopLoading();

  const placeGrid = document.getElementById("placeGrid");

  placeData.forEach((placeObj) => {
    const place = placeObj._id;
    const count = placeObj.count;

    // Create a div for the place
    const placeCard = document.createElement("div");
    if (state && district) {
      placeCard.classList.add("null-button");
    } else {
      placeCard.classList.add("place-card-button");
    } // Use the button-like style
    
    const number = document.createElement("span");
    number.textContent = count;
    number.style.padding = "5px";
    const placeName = document.createElement("span");
    placeName.textContent = place;
    placeName.style.textAlign = "center"
    // Set the text content
    placeCard.appendChild(number);
    placeCard.appendChild(placeName);

    // Add a click event listener
    placeCard.addEventListener("click", () => {
      let subUrl = url;
      if (!state) {
        subUrl += "?state=" + place;
        window.location.href = "analysis.html" + subUrl;
      } else if (state && !district) {
        subUrl += "&district=" + place;
        window.location.href = "analysis.html" + subUrl;
      }

    });

    placeGrid.appendChild(placeCard);
  });

  searchInput.addEventListener("input", function () {
    const searchTerm = searchInput.value.toLowerCase();

    // Filter placeData based on the search term
    const filteredPlaceData = placeData.filter((placeObj) => {
      const placeName = placeObj._id.toLowerCase();
      return placeName.includes(searchTerm);
    });

    // Clear the current placeGrid content
    placeGrid.innerHTML = "";

    // Create and append place cards for the filtered places
    filteredPlaceData.forEach((placeObj) => {
      const place = placeObj._id;
      const count = placeObj.count;

      // Create a div for the place
      const placeCard = document.createElement("div");
      if (state && district) {
        placeCard.classList.add("null-button");
      } else {
        placeCard.classList.add("place-card-button");
      } // Use the button-like style

      // Set the text content
      const number = document.createElement("span");
      number.textContent = count;
      number.style.padding = "5px";
      const placeName = document.createElement("span");
      placeName.textContent = place;
      placeName.style.textAlign = "center"
      // Set the text content
      placeCard.appendChild(number);
      placeCard.appendChild(placeName);

      // Add a click event listener
      placeCard.addEventListener("click", () => {
        let subUrl = url;
        if (!state) {
          subUrl += "?state=" + place;
          window.location.href = "analysis.html" + subUrl;
        } else if (state && !district) {
          subUrl += "&district=" + place;
          window.location.href = "analysis.html" + subUrl;
        }

        // Handle button click for place details
      });

      placeGrid.appendChild(placeCard);
    });
  });
});
