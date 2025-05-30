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

function loadUserData(editable = false){
    const userData = JSON.parse(localStorage.getItem('user'));
    console.log(userData);
    document.getElementById("ajaxContent").innerHTML = createTableFromJSON(userData, editable);
}

if (window.location.pathname.endsWith("user_edit_info.html")) {
    window.onload = loadUserData(false);
}

function handleLogin(){
    const elementlogin = document.getElementById("loginError");
    elementlogin.textContent="";
    const formData = new FormData(document.getElementById("loginForm"));
    const username = formData.get("username");
    const password = formData.get("password");
    if(username === "admin" && password === "admin123"){
        elementlogin.textContent = "Login with these credentials is not allowed.";
        return;
    }
    var xhr = new XMLHttpRequest();
    xhr.onload = function (){
        if(xhr.readyState === 4 && xhr.status === 200){
            const response = JSON.parse(xhr.responseText);
            if(response.success){
                console.log(response.user);
                localStorage.setItem('user', JSON.stringify(response.user));                
                localStorage.setItem("runIncidentCheck", "true");             
                window.location.href = "user_menu.html";
            }else{
                $("#ajaxContent").html("User does not exist or incorrect password.");
                elementlogin.textContent="User does not exist or incorrect password.";
            }
        }else if(xhr.status !== 200){
            $("#ajaxContent").html("Error occurred during login.");
        }
    };
    var data = $('#loginForm').serialize();
    xhr.open('GET', 'GetUser?' + data);
    xhr.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
    xhr.send();
}

function checkNearByIncidents(){
    const userData = JSON.parse(localStorage.getItem('user'));
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


function showEditButtons(){
    document.getElementById('mainButtons').style.display = 'none';
    document.getElementById('secondButtons').style.display = 'flex';
    loadUserData(true);
}

function showDefaultButtons(){
    const confirmation = confirm("You are about to exit without saving your changes. Are you sure you want to exit?");
    if(confirmation){
        document.getElementById('secondButtons').style.display = 'none';
        document.getElementById('mainButtons').style.display = 'flex';
        loadUserData(false);
    }
}

function saveChanges(){
    const updatedData = {};
    const inputs = document.querySelectorAll("input");
    inputs.forEach(input => {
        updatedData[input.id] = input.value;
    });
    const userData = JSON.parse(localStorage.getItem('user'));
    updatedData['username'] = userData.username;
    const xhr = new XMLHttpRequest();
    xhr.onload = function (){
        if (xhr.readyState === 4 && xhr.status === 200) {
            const response = JSON.parse(xhr.responseText);
            if (response.success){
                const updatedUser = response.updatedUser;
                localStorage.setItem('user', JSON.stringify(updatedUser));
                loadUserData(false);
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
    xhr.open("POST", "UpdateUser");
    xhr.setRequestHeader("Content-Type", "application/json");
    xhr.send(JSON.stringify(updatedData));
}

function logoutUser(){
    localStorage.clear();
    window.location.href = "user_login.html";
}