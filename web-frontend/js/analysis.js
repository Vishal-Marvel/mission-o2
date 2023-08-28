document.addEventListener('DOMContentLoaded',  async function(){
    const urlParams = new URLSearchParams(window.location.search);
    const state = urlParams.get('state');
    const district = urlParams.get('district');
    let url = "";
    const loadingOverlay = document.getElementById('loadingOverlay');

    function startLoading() {
      loadingOverlay.style.display = 'flex';
    }

    function stopLoading() {
      loadingOverlay.style.display = 'none';
    }
let placeData;
if (state){
    url += "?state=" + state;
}
if (district){
    url += "&district="+district;
}
startLoading();
await axios.get(`${APIURL}/orders/view-count${url}`, {
    headers:{
        Authorization: `Bearer ${token}`
    }
}).then(response=>{
    placeData = response.data
})
console.log(placeData);
stopLoading();
  const placeGrid = document.getElementById("placeGrid");
  
  placeData.forEach(placeObj => {
    const place = placeObj.place;
    const count = placeObj.count;
    const placeCard = document.createElement("div");
    placeCard.classList.add("place-card");
  
    const placeName = document.createElement("p");
    placeName.classList.add("place-name");
    placeName.textContent = place;
    const placeCount = document.createElement("p");
    placeCount.classList.add("place-count");
    placeCount.textContent = count;
    placeCard.appendChild(placeName);
    placeCard.appendChild(placeCount);
    if (district){
   

    }else if (state){
        const placeButton = document.createElement("button");
        placeButton.classList.add("place-button");
        placeButton.textContent = "View Taluks";
        placeCard.appendChild(placeButton);
        placeButton.addEventListener("click", () => {
            let subUrl = url;
            if (!state){
                subUrl += "?state=" + place;
            }else if (state){
                subUrl += "&district=" + place;
            }
          // Handle button click for place details
          window.location.href = "analysis.html" + subUrl;
        });
        
    }else{
        const placeButton = document.createElement("button");
    placeButton.classList.add("place-button");
    placeButton.textContent = "View Districts";
    placeCard.appendChild(placeButton);
    placeButton.addEventListener("click", () => {
        let subUrl = url;
        if (!state){
            subUrl += "?state=" + place;
        }else if (state){
            subUrl += "&district=" + place;
        }
      // Handle button click for place details
      window.location.href = "analysis.html" + subUrl;
    });

    }    
    placeGrid.appendChild(placeCard);
      
  });
});