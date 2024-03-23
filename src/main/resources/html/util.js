const convertTime12to24 = (time12h) => {
    const [time, modifier] = time12h.split(' ');
  
    let [hours, minutes] = time.split(':');
  
    if (hours === '12') {
      hours = '00';
    }
  
    if (modifier === 'PM') {
      hours = parseInt(hours, 10) + 12;
    }
  
    return `${hours}:${minutes}`;
  }

var CURRENT_TIME = ""
var GOLDEN_HOUR = ""
var SUNSET_TIME = ""

async function getTimeOfDay() {
    let date = new Date()
    let hours = date.getHours()
    CURRENT_TIME = date.getHours() + ":" +date.getMinutes()
    CURRENT_TIME = "12:00"
    await getSunset()
}

async function getSunset() {
    const response = await fetch("https://api.sunrisesunset.io/json?lat=50.3886&lng=13.18342")
        .then((response) => response.json())
        .then((data) => {
            GOLDEN_HOUR = convertTime12to24(data.results.golden_hour)
            SUNSET_TIME = convertTime12to24(data.results.sunset)

            console.log("Golden Hour: " +GOLDEN_HOUR)
            console.log("Sunset: " +SUNSET_TIME)
            console.log("Current: " +CURRENT_TIME)
        })
}

async function updateWhenAPIReady() {
    await getTimeOfDay()
}