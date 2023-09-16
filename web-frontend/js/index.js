document.addEventListener("DOMContentLoaded", async function () {
  const loginButton = document.getElementById("login-button");
  let images;
  // const adminPanelButton = document.getElementById("admin-panel-button");

  loginButton.addEventListener("click", function () {
    window.location.href = "login.html";
  });
  // adminPanelButton.addEventListener('click', function() {
  //     if (token){
  //     window.location.href = 'panel.html';
  //     }else{
  //         window.location.href = "login.html";
  //     }
  // });

  const counter = document.getElementById("count");

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
  let flipData;
  await axios
  .get(`${APIURL}/orders/analysis`)
  .then((response) => {
    flipData = response.data;
  })
  .catch((error) => {
    console.log(error);
  });

  function updateFlipCard(cardId, frontContent, backContent) {
    const flipCard = document.getElementById(cardId);
    if (flipCard) {
      const front = flipCard.querySelector('.flip-card-front h2');
      const back = flipCard.querySelector('.flip-card-back h2');
  
      if (front && back) {
        front.textContent = frontContent;
        back.textContent = backContent;
      }
    }
    // flipCard.style.height = "fit-content";
  }
  
  // Example usage:
  // console.log(typeof(flipData.state));
  updateFlipCard('flipCard1', flipData.user.split(' ')[0], flipData.userOrderCount); 
  updateFlipCard('flipCard2', flipData.state.split(' ')[0], flipData.stateOrderCount); 
  updateFlipCard('flipCard3', flipData.district.split(' ')[0], flipData.districtOrderCount); 
  updateFlipCard('flipCard4', flipData.taluk.split(' ')[0], flipData.talukOrderCount); 
  updateFlipCard('flipCard5', flipData.plant.split(' ')[0], flipData.plantOrderCount); 
  
  let orderSumm;
  await axios
  .get(`${APIURL}/orders/get-order-summary`)
  .then((response) => {
    orderSumm = response.data;
  })
  .catch((error) => {
    console.log(error);
  });
  orderSumm.sort((a, b) => {
    const dateA = new Date(a.date);
    const dateB = new Date(b.date);
    return dateA - dateB;
  });

  const xLabels = []; 
  const yLabels = []; 

  orderSumm.forEach(item => {
    const dateObj = new Date(item.date);
    const formattedDate = `${dateObj.getDate()}/${dateObj.getMonth() + 1}/${dateObj.getFullYear()}`;
    xLabels.push(formattedDate);
    yLabels.push(item.totalPlants);
  });
  //for graph
  const graphData = {
    labels: xLabels,
    datasets: [{
        label: 'Daily Sales',
        fill: false,
        lineTension: 0,
        data: yLabels,
        backgroundColor: 'rgba(37, 209, 89, 1.0)',
        borderColor: "rgba(9, 255, 0, 0.6)",
        borderWidth: 2
    }]
  };
  const ctx = document.getElementById('myChart').getContext('2d');
  const myChart = new Chart(ctx, {
      type: 'line',
      data: graphData,
      options: {
        scales: {
            x: {
              ticks :{
                font :{
                  size : 18
                } 
              },
                beginAtZero: false
            },
            y: {
              ticks :{
                font :{
                  size : 18
                }
              },
                beginAtZero: false
            }
        }
    }
  });

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
    .get(`${APIURL}/orders/index-gallery`)
    .then((response) => {
      images = response.data.elements;
    })
    .catch((error) => {
      console.log(error);
    });

  const carouselList = document.querySelector(".image-grid");
  let i=0;
  for (const element of images){
    if (element) {
      await axios.get(`${APIURL}/plant/image/${element}`).then((response) => {
        
        const image = document.createElement("img");
        image.src =
          "https://storage.googleapis.com/proudcity/mebanenc/uploads/2021/03/placeholder-image.png";
        image.alt = "Loading...";
        image.onload = () => {
          image.src = "data:image/jpg;base64," + response.data.image;
          image.alt = response.data.fileName;
        };
        carouselList.appendChild(image);
      });
      i++;
      if (i>5){
        break
      }
    }
  }
  

  
  // const carouselList = document.querySelector(".carousel-list");
  // images.forEach(async (element) => {
  //   if (element) {
  //     await axios.get(`${APIURL}/plant/image/${element}`).then((response) => {
  //       const list = document.createElement("li");
  //       const image = document.createElement("img");
  //       image.src =
  //         "https://storage.googleapis.com/proudcity/mebanenc/uploads/2021/03/placeholder-image.png";
  //       image.alt = "Loading...";
  //       image.onload = () => {
  //         image.src = "data:image/jpg;base64," + response.data.image;
  //         image.alt = response.data.fileName;
  //       };
  //       list.appendChild(image)
  //       carouselList.appendChild(list);
  //     });
  //   }
  // });
  // const prevButton = document.querySelector(".prev-button");
  // const nextButton = document.querySelector(".next-button");

  // let currentIndex = 0;

  // const itemWidth = carouselList.clientWidth / 4.5; // 4 items at a time
  // console.log(carouselList.clientWidth)
  // function goToSlide(index) {
  //   if (index < 0) {
  //     index = 0;
  //   } else if (index > carouselList.children.length - 4) {
  //     index = carouselList.children.length + 4;
  //   }
  //   currentIndex = index;
  //   const translateX = -currentIndex * itemWidth;
  //   console.log(translateX)
  //   carouselList.style.transform = `translateX(${translateX}px)`;
  // }

  // function nextSlide() {
  //   if (currentIndex < carouselList.children.length - 4) {
  //     goToSlide(currentIndex + 1);
  //   } else {
  //     // If it's at the end, reset to the first item
  //     goToSlide(0);
  //   }
  // }

  // function prevSlide() {
  //   if (currentIndex > 0) {
  //     goToSlide(currentIndex - 1);
  //   } else {
  //     // If it's at the beginning, loop to the last item
  //     goToSlide(carouselList.children.length - 4);
  //   }
  // }
  // let intervalId; // Store the interval ID

  // function startCarouselInterval() {
  //   intervalId = setInterval(nextSlide, 3000); // Start the interval and store the ID
  // }

  // function stopCarouselInterval() {
  //   clearInterval(intervalId); // Clear the interval using the stored ID
  // }

  // nextButton.addEventListener("click", () => {
  //   nextSlide();
  //   stopCarouselInterval(); // Stop the interval when the button is clicked
  //   startCarouselInterval(); // Start a new interval
  // });

  // prevButton.addEventListener("click", () => {
  //   prevSlide();
  //   stopCarouselInterval(); // Stop the interval when the button is clicked
  //   startCarouselInterval(); // Start a new interval
  // });

  // // Start the initial interval
  // startCarouselInterval();
});
