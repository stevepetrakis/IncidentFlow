function createTableFromJSON(data, editable = false){
    var html = "<table><tr><th>Category</th><th>Value</th></tr>";
    const nonEditableFields = ["username", "afm", "telephone"];
    for(const key in data){
        const value = data[key];
        if(editable && !nonEditableFields.includes(key)){
            html += `<tr>
                        <td>${key}</td>
                        <td><input type="text" id="${key}" value="${value}" /></td>
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

function parseQueryParams(){
    const queryString = window.location.search;
    const urlParams = new URLSearchParams(queryString);
    const userData = {};
    urlParams.forEach((value, key) => {
        userData[key] = value;
    });
    return userData;
}

function loadVolunteerData(editable = false){
    const volunteerData = JSON.parse(localStorage.getItem('volunteer'));
    console.log(volunteerData);
    document.getElementById("ajaxContent").innerHTML = createTableFromJSON(volunteerData, editable);
}

if (window.location.pathname.endsWith("volunteer_edit_info.html")) {
    window.onload = loadVolunteerData(false);
}

function handleLoginVolunteer(){
    const elementlogin = document.getElementById("loginError");
    elementlogin.textContent="";
    var xhr = new XMLHttpRequest();
    xhr.onload = function (){
        if(xhr.readyState === 4 && xhr.status === 200){
            const response = JSON.parse(xhr.responseText);
            if(response.success){
                console.log(response.volunteer);
                localStorage.setItem('volunteer', JSON.stringify(response.volunteer));
                localStorage.setItem("runIncidentCheck", "true");
                localStorage.setItem("runNotifyCheck", "true");
                window.location.href = "volunteer_menu.html";
            }else{
                $("#ajaxContent").html("Volunteer does not exist or incorrect password.");
                elementlogin.textContent="Volunteer does not exist or incorrect password.";
            }
        }else if(xhr.status !== 200){
            $("#ajaxContent").html("Error occurred during login.");
        }
    };
    var data = $('#loginForm').serialize();
    xhr.open('GET', 'GetVolunteer?' + data);
    xhr.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
    xhr.send();
}

function checkNearByIncidents(){
    const userData = JSON.parse(localStorage.getItem('volunteer'));
    const userMunicipality = userData.municipality;
    const userAddress = userData.address;
    const userLat = parseFloat(userData.lat);
    const userLon = parseFloat(userData.lon);
    const proximityThreshold = 0.02;
    const xhr = new XMLHttpRequest();
    xhr.onload = function (){
        if(xhr.readyState === 4 && xhr.status === 200){
            const response = JSON.parse(xhr.responseText);
            const incidents = response.results;
            let nearbyIncidents = [];
            incidents.forEach((incident) => {
                if(incident.status === "running"){
                    if(incident.municipality === userMunicipality || incident.address === userAddress){
                        nearbyIncidents.push(incident);
                    }else if(incident.lat && incident.lon){
                        const distanceLat = Math.abs(incident.lat - userLat);
                        const distanceLon = Math.abs(incident.lon - userLon);
                        if (distanceLat <= proximityThreshold && distanceLon <= proximityThreshold) {
                            nearbyIncidents.push(incident);
                        }
                    }
                }
            });
            if(nearbyIncidents.length > 0){
                alert(
                    `There are ${nearbyIncidents.length} running incidents near your location: \n` +
                    nearbyIncidents
                        .map(
                            (incident) =>
                                `Type: ${incident.incident_type}, Address: ${incident.address}, Municipality: ${incident.municipality}`
                        )
                        .join("\n")
                );
            }else{
                alert("No running incidents nearby.");
            }
        }else if(xhr.status !== 200){
            alert("Error fetching incident data.");
        }
    };
    xhr.open("POST", "ViewIncident", true);
    xhr.setRequestHeader("Content-Type", "application/json");
    xhr.send(JSON.stringify({ status: "running", municipality: userMunicipality }));
}

function checkVolunteerNotifications(){
    const volunteerData = JSON.parse(localStorage.getItem("volunteer"));
    if(!volunteerData || !volunteerData.username){
        console.error("No volunteer data found in localStorage.");
        return;
    }
    const username = volunteerData.username;
    const volunteer_type = volunteerData.volunteer_type;
    const xhr = new XMLHttpRequest();
    xhr.onload = function () {
        if (xhr.readyState === 4 && xhr.status === 200) {
            const response = JSON.parse(xhr.responseText);
            if (response.success && response.notifications.length > 0) {
                const notifications = response.notifications;
                notifications.forEach((incident, index) => {         
                    let message = "You have been notified for the following incidents:\n";           
                    message += `- Incident: ${incident.incident_type}, Location: ${incident.address}\n`;
                    message += `  Description: ${incident.description}, Danger: ${incident.danger}\n`;
                    const accept = confirm(message + "\nDo you want to participate?");
                    if(accept){
                        acceptParticipation(username, notifications, volunteer_type);
                    }else{
                        alert("You declined to participate.");
                    }
                    deleteNotification(username, incident.incident_id);
                });                
            }
        } else if (xhr.status !== 200) {
            console.error("Error checking notifications.");
        }
    };

    xhr.open("GET", "GetNotifiedVolunteers?username=" + username, true);
    xhr.send();
}

function acceptParticipation(username, notifications, volunteer_type) {
    const xhr = new XMLHttpRequest();
    xhr.onload = function () {
        if (xhr.readyState === 4 && xhr.status === 200) {
            const response = JSON.parse(xhr.responseText);
            if (response.success) {
                alert("Your participation has been confirmed.");
                notifications.forEach((incident) => {
                    updateFiremenVehicles(incident.incident_id, volunteer_type);
                });
            } else {
                alert("Error confirming participation.");
            }
            window.location.href = "volunteer_menu.html";
        } else if (xhr.status !== 200) {
            console.error("Error accepting participation.");
        }
    };

    xhr.open("POST", "UpdateParticipants", true);
    xhr.setRequestHeader("Content-Type", "application/json");
    xhr.send(JSON.stringify({ username: username, notifications: notifications, volunteer_type: volunteer_type }));
}

function updateFiremenVehicles(incident_id, volunteer_type) {
    const xhr = new XMLHttpRequest();
    xhr.onload = function () {
        if (xhr.readyState === 4 && xhr.status === 200) {
            const response = JSON.parse(xhr.responseText);
            if (response.success) {
                console.log(`Incident ${incident_id} updated successfully.`);
            } else {
                console.error(`Failed to update incident ${incident_id}: ${response.message}`);
            }
        } else if (xhr.status !== 200) {
            console.error(`Error updating incident ${incident_id}.`);
        }
    };

    xhr.open("POST", "UpdateFiremenVehicles", true);
    xhr.setRequestHeader("Content-Type", "application/json");
    xhr.send(JSON.stringify({ incident_id: incident_id, volunteer_type: volunteer_type }));
}

function deleteNotification(username, incident_id) {
    const xhr = new XMLHttpRequest();
    xhr.onload = function (){
        if(xhr.readyState === 4 && xhr.status === 200){
            const response = JSON.parse(xhr.responseText);
            if(!response.success){
                console.error("Failed to delete notification.");
            }
        }else if(xhr.status !== 200){
            console.error("Error deleting notification.");
        }
    };
    xhr.open("POST", "DeleteNotification", true);
    xhr.setRequestHeader("Content-Type", "application/json");
    xhr.send(JSON.stringify({ username: username, incident_id: incident_id }));
}

function showEditButtons(){
    document.getElementById('mainButtons').style.display = 'none';
    document.getElementById('secondButtons').style.display = 'flex';
    loadVolunteerData(true);
}

function showDefaultButtons(){
    const confirmation = confirm("You are about to exit without saving your changes. Are you sure you want to exit?");
    if(confirmation){
        document.getElementById('secondButtons').style.display = 'none';
        document.getElementById('mainButtons').style.display = 'flex';
        loadVolunteerData(false);
    }
}

function saveChanges(){
    const updatedData = {};
    const inputs = document.querySelectorAll("input");
    inputs.forEach(input => {
        updatedData[input.id] = input.value;
    });
    const volunteerData = JSON.parse(localStorage.getItem('volunteer'));
    updatedData['username'] = volunteerData.username;
    const xhr = new XMLHttpRequest();
    xhr.onload = function (){
        if(xhr.readyState === 4 && xhr.status === 200) {
            const response = JSON.parse(xhr.responseText);
            if (response.success) {
                const updatedVolunteer = response.updatedVolunteer;
                localStorage.setItem('volunteer', JSON.stringify(updatedVolunteer));
                loadVolunteerData(false);
                document.getElementById('secondButtons').style.display = 'none';
                document.getElementById('mainButtons').style.display = 'flex';
                alert("Changes saved successfully!");
            } else {
                alert("Error saving changes: " + response.message);
            }
        } else if (xhr.status !== 200) {
            alert("Error saving changes.");
        }
    };
    xhr.open("POST", "UpdateVolunteer");
    xhr.setRequestHeader("Content-Type", "application/json");
    xhr.send(JSON.stringify(updatedData));
}

function logoutVolunteer(){
    localStorage.clear();
    window.location.href = "volunteer_login.html";
}