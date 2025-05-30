const form = document.getElementById("myForm");
const unsuccessMessage = document.getElementById("unsuccessMessage");
let phoneflag=true;
let addressflag=true;
document.addEventListener("DOMContentLoaded", () => {
    const checklocation=document.getElementById("checklocation");
    checklocation.addEventListener("click", async function(){
            await myLocation();        
            showmapbutton();
        }
    );
    const mymap=document.getElementById("seeonmap");
    mymap.addEventListener("click", async function() {
        await myLocation();
        showmap();  
        addMarker(glat, glon, gdisplay);
        
    });

    const formElements = document.querySelectorAll('input, select');
    formElements.forEach(element => {
        element.addEventListener("keydown", moveToNextField);
    });

    function showmap(){
        if(location_true!==0){
            document.getElementById("Map").style.display="block";
        }else{
            document.getElementById("Map").style.display="none";
        }
    }

    function showmapbutton(){
        if(isvalidlocation!==0){
            document.getElementById("seeonmap").style.display="block";
        }else{
            document.getElementById("seeonmap").style.display="none";
            document.getElementById("Map").style.display="none";
        }
    }
    
    function displayFormDataAsJSON(){
        const formData = {
            incident_type: document.getElementById("incident_type").value,
            description: document.getElementById("description").value,
            user_type: document.querySelector('input[name="user_type"]:checked').value,
            user_phone: document.getElementById("telephone").value,
            address: document.getElementById("address").value,
            prefecture: document.getElementById("prefecture").value,
            municipality: document.getElementById("Municipality").value,
            lat: glat,
            lon :glon
        };
        const jsonData = JSON.stringify(formData, null, 2);
        console.log(jsonData);
        return jsonData;
    }
    
    form.addEventListener("submit", async (event) => {
        event.preventDefault();
        const isValid = validateForm();
        if(isValid){
           if(phoneflag && addressflag){
                var data=displayFormDataAsJSON();
                submitRegister(data);
                unsuccessMessage.textContent ="";
                document.getElementById("seeonmap").style.display="none";
                document.getElementById("Map").style.display="none";
                document.getElementById("locationMessage").innerHTML = "";
                successfunc();
            }else{
                console.log("error");
                unsuccessfulfunc();
            }
        }else{
            console.log("error");
            unsuccessfulfunc();
        }
    });
});

function successfunc(){
    const successMessage = document.getElementById("successMessage");
    successMessage.textContent = "Registration submitted successfully!";
    successMessage.style.display = "block";
    successMessage.style.color = "green";
    form.reset();
    window.scrollTo({ top: 0, behavior: 'smooth' });
    setTimeout(() => {
        successMessage.style.display = "none";
    }, 5000);
}

function unsuccessfulfunc(){
    unsuccessMessage.textContent = "Registration submitted unsuccessfully!";
    unsuccessMessage.style.display = "block";
    unsuccessMessage.style.color = "red";
    window.scrollTo({ top: 0, behavior: 'smooth' });
}

async function submitRegister(data){
    try{
        const incidentResponse = await fetch("http://localhost:8080/finalproject/submitIncident",{
            method: "POST",
            body: data
        });
        if(incidentResponse.ok){
            console.log("Incident submitted successfully.");
            const incidentData = await incidentResponse.json();
            const message = {
                incident_id: 0,
                message: `A new incident has been added to the database with ID: ${0}.`,
                sender: "System",
                recipient: "admin"
            };
            console.log(message);
        }else{
            console.error("Error submitting incident:", incidentResponse.statusText);
        }
    }catch(error){
        console.error("Error submitting incident:", error);
    }
}

function moveToNextField(event){
    if(event.key === "Enter"){
        event.preventDefault();
        const currentField = event.target;
        if(!validateCurrentField(currentField)){
            return;
        }
        const inputs = Array.from(document.querySelectorAll('input, select, button'));
        const currentIndex = inputs.indexOf(currentField);
        if(currentIndex >= 0 && currentIndex < inputs.length - 1){
            const nextInput = inputs[currentIndex + 1];
            if(nextInput) nextInput.focus();
        }
    }
}

function validateCurrentField(field) {
    const fieldId = field.id;
    const value = field.value.trim();
    const element = document.getElementById(fieldId);
    const elementMessage1 = document.getElementById("phoneError");
    const elementMessage2 = document.getElementById("addressError");
    if(fieldId === "telephone"){
        if (!/^[0-9]{10,14}$/.test(value)) {
            element.scrollIntoView();
            elementMessage1.textContent = "It has to be 10 to 14 numbers";
            phoneflag=false;
        }else{
            elementMessage1.textContent ="";
            phoneflag=true;
            return /^[0-9]{10,14}$/.test(value);
        } 
    }
    if(fieldId === "address"){
        if (!/^[a-zA-Z0-9\s]{3,150}$/.test(value)) {
            element.scrollIntoView();
            elementMessage2.textContent = "It has to be 3 to 150 characters";
            addressflag=false;
        }else{
            elementMessage2.textContent ="";
            addressflag=true;
            return /^[a-zA-Z0-9\s]{3,150}$/.test(value);
        } 
    }
    return true;
}


function validateForm(){
    const formElements = document.querySelectorAll('input, select');
    let isValid = true;
    formElements.forEach((input) => {
        if (!validateCurrentField(input)) {
            isValid = false;
        }
    });
    return isValid;
}

var glat=0;
var glon=0;
var gdisplay=0;
var location_true=0;
var isvalidlocation=0;
var map = new OpenLayers.Map("Map");
var mapnik = new OpenLayers.Layer.OSM();
map.addLayer(mapnik);
var markers = new OpenLayers.Layer.Markers("Markers");
map.addLayer(markers);

function setPosition(lat, lon) {
    var fromProjection = new OpenLayers.Projection("EPSG:4326"); 
    var toProjection = new OpenLayers.Projection("EPSG:900913");
    var position = new OpenLayers.LonLat(lon, lat).transform(fromProjection, toProjection);
    return position;
}

function addMarker(lat, lon, message){
    markers.clearMarkers();
    while(map.popups.length > 0){
        map.removePopup(map.popups[0]);
    }
    var position = setPosition(lat, lon);
    var marker = new OpenLayers.Marker(position);
    markers.addMarker(marker);
    marker.events.register('mousedown', marker, function(evt){ 
        var popup = new OpenLayers.Popup.FramedCloud(
            "Popup",
            position,
            null,
            message,
            null,
            true
        );
        map.addPopup(popup);
    });
    const zoom = 12;
    map.setCenter(position, zoom);
}

async function myLocation(){
    const country="Greece";
    const address=document.getElementById("address").value;
    const municipality=document.getElementById("Municipality").value;

    const url = `https://forward-reverse-geocoding.p.rapidapi.com/v1/forward?format=json&street=${address}&state=${municipality}&country=${country}&addressdetails=1&accept-language=en&namedetails=0&limit=1&bounded=0&polygon_text=0&polygon_kml=0&polygon_svg=0&polygon_geojson=0&polygon_threshold=0.0`;
    const options = {
        method: 'GET',
        headers: {
            'x-rapidapi-key': '9d99b98e81msh0bc67efb67b2bfap197ceajsn5bd843b4e217',
            'x-rapidapi-host': 'forward-reverse-geocoding.p.rapidapi.com'
        }
    };
    
    try {
        const response = await fetch(url, options);
        const result = await response.json();
        console.log(result);

        if (result.length === 0) {
            document.getElementById("locationMessage").innerHTML = "Location not found!";
            location_true=0;
            isvalidlocation=0;
            return;
        }
        
        const location = result[0];
        const lat = location.lat;
        const lon = location.lon;
        const displayName = location.display_name;

        glat=lat;
        glon=lon;
        gdisplay=displayName;

        if (displayName.includes("Crete")) {
            document.getElementById("locationMessage").innerHTML = `Location found at cords: (${lat}, ${lon})`;
            location_true=1;
            isvalidlocation=1;
      
        } else {
            document.getElementById("locationMessage").innerHTML = "You can use it only for Crete.";
            location_true=0;
            isvalidlocation=0;
        }
    } catch (error) {
        console.error(error);
        document.getElementById("locationMessage").innerHTML = "Error , location not found.";
        location_true=0;
        isvalidlocation=0;
    }
}