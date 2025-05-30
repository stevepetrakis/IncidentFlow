document.addEventListener("DOMContentLoaded", () => {
    const viewButton = document.getElementById("viewButton");
    viewButton.addEventListener("click", async (event) => {
        event.preventDefault();
        const chartType = document.querySelector('input[name="chart_type"]:checked').value;
        if(chartType === "incident_type"){
            await displayIncidentTypeChart();
        }else if(chartType === "user_volunteer"){
            await displayUserVolunteerChart();
        }else if(chartType === "vehicle_firemen"){
            await displayVehiclesFiremenChart();
        }
    });
});

async function fetchVehiclesFiremen() {
    try {
        const response = await fetch("http://localhost:8080/finalproject/GetVecFir", {
            method: "GET",
            headers: {
                "Content-Type": "application/json"
            }
        });
        if (response.ok) {
            const result = await response.json();
            return { vehicleCount: result.vehicleCount || 0, firemanCount: result.firemanCount || 0 };
        } else {
            console.error("Error fetching vehicles and firemen:", response.statusText);
            return { vehicleCount: 0, firemanCount: 0 };
        }
    } catch (error) {
        console.error("Fetch error:", error);
        return { vehicleCount: 0, firemanCount: 0 };
    }
}

async function displayVehiclesFiremenChart() {
    const { vehicleCount, firemanCount } = await fetchVehiclesFiremen();
    if (vehicleCount === 0 && firemanCount === 0) {
        alert("No vehicles or firemen found.");
        return;
    }
    const labels = ["Vehicles", "Firemen"];
    const data = [vehicleCount, firemanCount];
    renderBarChart(labels, data, "Number of Vehicles/Firemen");
}

async function fetchEveryone(){
    try {
        const response = await fetch("http://localhost:8080/finalproject/GetEveryone", {
            method: "GET",
            headers: {
                "Content-Type": "application/json"
            }
        });
        if(response.ok){
            const result = await response.json();
            return {userCount: result.userCount || 0, volunteerCount: result.volunteerCount || 0};
        }else{
            console.error("Error fetching users:", response.statusText);
            return {userCount: 0, volunteerCount: 0};
        }
    }catch (error){
        console.error("Fetch error:", error);
        return {userCount: 0, volunteerCount: 0};
    }
}

async function displayUserVolunteerChart(){
    const { userCount, volunteerCount } = await fetchEveryone();  
    const newuserCount= userCount - 1;
    if(newuserCount < 0 && volunteerCount === 0){
        alert("No users or volunteers found.");
        return;
    }
    const labels = ["Users", "Volunteers"];
    const data = [Math.max(newuserCount, 0), volunteerCount];
    renderBarChart(labels, data, "Number of Users/Volunteers");
}

async function fetchIncidents(){
    try{
        const response = await fetch("http://localhost:8080/finalproject/ViewIncident", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({})
        });
        if(response.ok){
            const result = await response.json();
            return result.results || [];
        }else{
            console.error("Error fetching incidents:", response.statusText);
            return [];
        }
    }catch(error){
        console.error("Fetch error:", error);
        return [];
    }
}

async function displayIncidentTypeChart(){
    const incidents = await fetchIncidents();
    const relevantIncidents = incidents.filter(
        (incident) =>
            incident.status === "running" || incident.status === "finished"
    );
    const incidentCounts = {
        accident: 0,
        fire: 0
    };
    relevantIncidents.forEach((incident) => {
        if(incident.incident_type === "accident"){
            incidentCounts.accident++;
        }else if(incident.incident_type === "fire"){
            incidentCounts.fire++;
        }
    });
    const labels = ["Accident", "Fire"];
    const data = [incidentCounts.accident, incidentCounts.fire];
    renderBarChart(labels, data,"Number of Incidents");
}

function renderBarChart(labels, data, chartlabel){
    const ctx = document.getElementById("incidentChart").getContext("2d");
    if(window.currentChart){
        window.currentChart.destroy();
    }
    window.currentChart = new Chart(ctx, {
        type: "bar",
        data: {
            labels: labels,
            datasets: [
                {
                    label: chartlabel,
                    data: data,
                    backgroundColor: ["#FF6384", "#36A2EB"],
                    borderColor: ["#FF6384", "#36A2EB"],
                    borderWidth: 1,
                    barThickness: 50
                }
            ]
        },
        options: {
            responsive: true,
            plugins: {
                legend: {
                    display: true,
                    position: "top"
                }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    ticks: {
                        stepSize: 1
                    }
                }
            }
        }
    });
}