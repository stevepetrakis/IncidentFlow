document.addEventListener("DOMContentLoaded", () => {
    const form = document.querySelector("form");
    const viewButton = document.getElementById("viewButton");
    
    const map = L.map("map").setView([35.2163, 24.9108], 9);
    L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
        attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
    }).addTo(map);

    const markerLayer = L.layerGroup().addTo(map);
    const geocodeCache = {};
    
    async function geocodeAddress(municipality, address) {
        const queryParts = [];
        if(address) queryParts.push(address);
        if(municipality) queryParts.push(municipality);        
        const query = queryParts.join(", ");
        const cacheKey = query;
        if(geocodeCache[cacheKey]){
            return geocodeCache[cacheKey];
        }
        const encodedQuery = encodeURIComponent(query);
        const url = `https://nominatim.openstreetmap.org/search?format=json&q=${encodedQuery}`;
        try{
            const response = await fetch(url);
            const data = await response.json();
            if(data.length > 0){
                const { lat, lon } = data[0];
                const coordinates = { lat: parseFloat(lat), lon: parseFloat(lon) };
                geocodeCache[cacheKey] = coordinates;
                return coordinates;
            }else{
                console.warn(`No coordinates found for: ${address}, ${municipality}`);
                return null;
            }
        }catch(error) {
            console.error("Geocoding error:", error);
            return null;
        }
    }
    
    async function addIncidentMarkers(incidents) {
        markerLayer.clearLayers();
        for(const incident of incidents){
            const { incident_type, incident_id, municipality, address, lon, lat } = incident;
            let coordinates = { lat, lon };

            if (!lon || !lat) {
                coordinates = await geocodeAddress(municipality, address);
            }

            if (coordinates && coordinates.lat && coordinates.lon) {
                L.marker([coordinates.lat, coordinates.lon])
                    .addTo(map)
                    .bindPopup(`
                        <b>Incident Type:</b> ${incident_type}<br>
                        <b>Incident ID:</b> ${incident_id} <br>
                        <b>Municipality:</b> ${municipality}<br>
                        <b>Address:</b> ${address}<br>
                    `)
                    .addTo(markerLayer);
            } else {
                console.warn(`Skipping incident due to missing coordinates:`, incident);
            }
        }
    }

    async function fetchdata(data) {
        try {
            const response = await fetch("http://localhost:8080/finalproject/ViewHisoryIncident", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: data
            });
            if(response.ok){
                const result = await response.json();
                displayJson(result);
                if(result.results && result.results.length > 0){
                    await addIncidentMarkers(result.results);
                }else{
                    console.warn("No incidents found to display on the map.");
                }
                console.log("Response received:", result);
            }else{
                console.error("Error in response:", response.statusText);
            }
        } catch(error){
            console.error("Fetch error:", error);
        }
    }
    
    
    viewButton.addEventListener("click", async (event) => {
        event.preventDefault();
        const data=searchIncidents();
        markerLayer.clearLayers();
        await fetchdata(data);
    });
});


function searchIncidents(){
    const type = document.getElementById("incident_type").value;
    const prefecture = document.getElementById("prefecture").value;
    const startDateTime = document.getElementById("start_datetime").value;
    const endDateTime = document.getElementById("end_datetime").value;
    const numFiremen = document.getElementById("num_firemen").value.trim();
    const numVehicles = document.getElementById("num_vehicles").value.trim();
    if(!type){
        alert("Please select an incident type.");
        throw new Error("Incident type is required.");
    }
    if(!startDateTime){
        alert("Please select a start date and time.");
        throw new Error("Start date and time are required.");
    }
    if(!endDateTime){
        alert("Please select an end date and time.");
        throw new Error("End date and time are required.");
    }
    if(new Date(startDateTime) > new Date(endDateTime)){
        alert("Start date and time cannot be later than the end date and time.");
        throw new Error("Invalid date range.");
    }
    const requestData = {
        incident_type: type,
        prefecture: prefecture,
        start_datetime: startDateTime,
        end_datetime: endDateTime,
        firemen: parseInt(numFiremen, 10),
        vehicles: parseInt(numVehicles, 10),
        status: "finished"
    };
    const jsonData = JSON.stringify(requestData, null, 2);
    return jsonData;
}

function displayJson(data) {
    const jsonOutputDiv = document.getElementById("jsonOutput");
    if(!jsonOutputDiv){
        console.error("Element not found.");
        return;
    }
    jsonOutputDiv.innerHTML = "";
    const incidents = data.results;
    if(!incidents || incidents.length === 0){
        jsonOutputDiv.innerHTML = "<p>No incidents found.</p>";
        return;
    }
    const keyMapping = {
        "Incident ID": "incident_id",
        "Incident Type": "incident_type",
        "Address": "address",
        "Prefecture": "prefecture",
        "Start Date/Time": "start_datetime",
        "Municipality": "municipality",
        "Description": "description",
        "Vehicles": "vehicles",
        "Longitude": "lon",
        "Latitude": "lat",
        "Danger": "danger",
        "User Type": "user_type",
        "User Phone": "user_phone",
        "Firemen": "firemen",
        "Status": "status",
        "Final Result": "finalResult"
    };
    const keys = [
        "Incident ID",
        "Incident Type",
        "Description",
        "User Type",
        "User Phone",
        "Prefecture",
        "Municipality",
        "Address",
        "Longitude",
        "Latitude",
        "Start Date/Time",
        "Danger",
        "Status",
        "Firemen",
        "Vehicles",
        "Final Result"
    ];
    const incidentKeys = Object.keys(incidents[0]);
    const table = document.createElement("table");
    table.style.width = "100%";
    table.style.borderCollapse = "collapse";
    keys.forEach((key, index) => {
        const row = document.createElement("tr");
        const th = document.createElement("th");
        th.textContent = key;
        th.style.border = "1px solid #ddd";
        th.style.padding = "8px";
        th.style.backgroundColor = index % 2 === 0 ? "#d9f1ff" : "#fffae6";
        th.style.color = "#333";
        row.appendChild(th);
        incidents.forEach(incident => {
            const td = document.createElement("td");
            const keyValue = incident[keyMapping[key]];
            td.textContent = keyValue === null || keyValue === "null" ? "N/A" : keyValue;            
            td.style.border = "1px solid #ddd";
            td.style.padding = "8px";
            td.style.backgroundColor = index % 2 === 0 ? "#f0faff" : "#fffde0";
            td.style.color = "#555";
            row.appendChild(td);
        });
        table.appendChild(row);
    });
    jsonOutputDiv.appendChild(table);
    jsonOutputDiv.style.display = "block";
}

function createTableFromJSON(data){
    let html = `<table class="styled-table">
                    <thead>
                        <tr>
                            <th>Category</th>
                            <th>Value</th>
                        </tr>
                    </thead>
                    <tbody>`;
    for(const key in data){
        const value = data[key];
        if(typeof value === "object" && value !== null){
            html += `<tr>
                        <td>${key}</td>
                        <td>${JSON.stringify(value, null, 2)}</td>
                     </tr>`;
        }else{
            html += `<tr>
                        <td>${key}</td>
                        <td>${value}</td>
                     </tr>`;
        }
    }
    html += "</table>";
    return html;
}