document.addEventListener("DOMContentLoaded", () => {
    const findButton = document.getElementById("findButton");
    findButton.addEventListener("click", async (event) => {
        event.preventDefault();
        const statusType = document.querySelector('input[name="status_type"]:checked').value;
        const incidents = await fetchIncidentsByStatus(statusType);
        if(incidents.length > 0){
            const tableHtml = createTableFromJSON(incidents, true);
            document.getElementById("ajaxContent").innerHTML = tableHtml;
        }else{
            document.getElementById("ajaxContent").innerHTML = "<p>No incidents found for the selected status.</p>";
        }
        document.getElementById("messageBox").innerHTML = "";
    });
});

function displayMessage(message, type = "success"){
    const messageBox = document.getElementById("messageBox");    
    messageBox.innerHTML = `<div class="alert alert-${type}">${message}</div>`;
    setTimeout(() => {
        messageBox.innerHTML = "";
    }, 3000);
    messageBox.scrollIntoView({ behavior: "smooth", block: "end" });
}

async function fetchIncidentsByStatus(statusType) {
    try {
        const response = await fetch("http://localhost:8080/finalproject/ViewIncident", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ status: statusType })
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

function createTableFromJSON(data, editable = false) {
    let html = "<table class='table table-bordered'><thead><tr><th>Field</th>";
    data.forEach((incident) => {
        html += `<th>Incident ID: ${incident.incident_id}</th>`;
    });
    html += "</tr></thead><tbody>";
    const fields = [
        { label: "Incident Type", key: "incident_type" },
        { label: "Description", key: "description" },
        { label: "User Phone", key: "user_phone" },
        { label: "User Type", key: "user_type" },
        { label: "Address", key: "address" },
        { label: "Latitude", key: "lat" },
        { label: "Longitude", key: "lon" },
        { label: "Municipality", key: "municipality" },
        { label: "Prefecture", key: "prefecture" },
        { label: "Start Date & Time", key: "start_datetime" },
        { label: "End Date & Time", key: "end_datetime" },
        { label: "Danger Level", key: "danger" },
        { label: "Status", key: "status" },
        { label: "Final Result", key: "finalResult" },
        { label: "Vehicles", key: "vehicles" },
        { label: "Firemen", key: "firemen" }
    ];
    fields.forEach((field) => {
        html += `<tr><td><b>${field.label}</b></td>`;
        data.forEach((incident) => {
            const value = incident[field.key];
            if(editable && field.key === "incident_type"){
                html += `<td><select id="${field.key}_${incident.incident_id}">
                            <option value="accident" ${value === "accident" ? "selected" : ""}>Accident</option>
                            <option value="fire" ${value === "fire" ? "selected" : ""}>Fire</option>
                        </select></td>`;
            }else if(editable && field.key === "user_type"){
                html += `<td><select id="${field.key}_${incident.incident_id}">
                            <option value="user" ${value === "user" ? "selected" : ""}>User</option>
                            <option value="guest" ${value === "guest" ? "selected" : ""}>Guest</option>
                            <option value="admin" ${value === "admin" ? "selected" : ""}>Admin</option>
                        </select></td>`;
            }else if(editable && field.key === "prefecture"){
                html += `<td><select id="${field.key}_${incident.incident_id}">
                            <option value="unknown" ${value === "unknown" ? "selected" : ""}>unknown</option>
                            <option value="Chania" ${value === "Chania" ? "selected" : ""}>Chania</option>
                            <option value="Heraklion" ${value === "Heraklion" ? "selected" : ""}>Heraklion</option>
                            <option value="Rethumno" ${value === "Rethumno" ? "selected" : ""}>Rethumno</option>
                            <option value="Agios Nikolas" ${value === "Agios Nikolas" ? "selected" : ""}>Agios Nikolas</option>
                        </select></td>`;
            }else if(editable && field.key === "danger"){
                html += `<td><select id="${field.key}_${incident.incident_id}">
                            <option value="unknown" ${value === "unknown" ? "selected" : ""}>unknown</option>
                            <option value="low" ${value === "low" ? "selected" : ""}>Low</option>
                            <option value="medium" ${value === "medium" ? "selected" : ""}>Medium</option>
                            <option value="high" ${value === "high" ? "selected" : ""}>High</option>
                        </select></td>`;
            }else if(editable && field.key === "status"){
                html += `<td><select id="${field.key}_${incident.incident_id}">
                            <option value="submitted" ${value === "submitted" ? "selected" : ""}>submitted</option>
                            <option value="fake" ${value === "fake" ? "selected" : ""}>fake</option>
                            <option value="running" ${value === "running" ? "selected" : ""}>running</option>
                            <option value="finished" ${value === "finished" ? "selected" : ""}>finished</option>
                        </select></td>`;
            }else if(editable) {
                html += `<td><input type="text" id="${field.key}_${incident.incident_id}" value="${value}" /></td>`;
            } else {
                html += `<td>${value}</td>`;
            }
        });
        html += "</tr>";
    });
    if(editable){
        html += "<tr><td><b>Actions</b></td>";
        data.forEach((incident) => {
            html += `<td>
                        <button class="action-btn" data-action="save" data-id="${incident.incident_id}">Save</button>
                        <button class="action-btn" data-action="delete" data-id="${incident.incident_id}">Delete</button>`;
            if (incident.status === "running") {
                html += `<button class="action-btn" data-action="notify" data-id="${incident.incident_id}">Open Positions</button>`;
            }
            html += `</td>`;
        });
        html += "</tr>";
    }
    html += "</tbody></table>";
    setTimeout(() => {
        document.querySelectorAll(".action-btn").forEach((button) => {
            button.addEventListener("click", (event) => {
                event.preventDefault();
                const action = button.getAttribute("data-action");
                const id = button.getAttribute("data-id");
                if (action === "save") {
                    const success =  saveIncidentChanges(id);
                    if(success){
                        setTimeout(() => location.reload(), 1500);
                    }
                }else if(action === "delete"){
                    const success = deleteIncident(id);
                    if(success){
                        setTimeout(() => location.reload(), 1500);
                    }
                } else if (action === "notify"){
                    notifyVolunteers(id);
                }
            });
        });
    }, 0);
    return html;
}

async function saveIncidentChanges(incidentId) {
    const updatedData = { incident_id: incidentId };
    let changesDetected = false;
    const inputs = document.querySelectorAll(`input[id$="_${incidentId}"]`);    
    inputs.forEach((input) => {
        const key = input.id.substring(0, input.id.lastIndexOf('_'));
        const currentValue = input.value;
        const originalValue = input.defaultValue;
        if(currentValue !== originalValue){
            updatedData[key] = currentValue;
            changesDetected = true;
        }
    });
    const selects = document.querySelectorAll(`select[id$="_${incidentId}"]`);
    selects.forEach((select) => {
        const key = select.id.substring(0, select.id.lastIndexOf('_'));
        const currentValue = select.value;
        const selectedOption = select.querySelector('option[selected]');
        const originalValue = selectedOption ? selectedOption.value : select.defaultValue;
        if(currentValue !== originalValue) {
            updatedData[key] = currentValue;
            changesDetected = true;
        }
    });
    if(!changesDetected){
        displayMessage("No changes detected. Please modify the fields before saving.", "warning");
        return;
    }
    try{
        const response = await fetch("http://localhost:8080/finalproject/UpdateIncident", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(updatedData)
        });
        if(response.ok){
            const result = await response.json();
            if(result.success){
                displayMessage("Incident updated successfully!");
                return;
            }else{
                displayMessage(`Error updating incident: ${result.message || "Unknown error from server"}`, "danger");
            }
        }else{
            const errorText = await response.text();
            displayMessage(`Error saving changes to incident. Server responded with: ${response.statusText || errorText}`, "danger");
        }
    }catch(error){
        displayMessage("An error occurred while saving changes.", "danger");
    }
}

async function deleteIncident(incidentId){
    const confirmed = confirm(`Are you sure you want to delete incident with id: ${incidentId}?`);
    if(!confirmed) return;
    try{
        let response = await fetch("DeleteIncident", {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded"
            },
            body: `incident_id=${encodeURIComponent(incidentId)}`
        });        
        if(response.ok){
            const result = await response.json();
            if(result.success){
                displayMessage("Incident deleted successfully!");
                const row = document.querySelector(`#incident_${incidentId}`);
                if (row) row.remove();
            }else{
                displayMessage(`Error deleting incident: ${result.message || "Unknown error from server"}`, "danger");
            }
        }else{
            const errorText = await response.text();
            displayMessage(`Error deleting incident. Server responded with: ${response.statusText || errorText}`, "danger");
        }
    }catch(error){
        displayMessage("An error occurred while deleting the incident.", "danger");
    }
}

async function notifyVolunteers(incidentId){
    const volunteerType = prompt("Enter volunteer type ('simple' or 'driver'):");
    if(!["simple", "driver"].includes(volunteerType)){
        displayMessage("Invalid volunteer type. Please enter 'simple' or 'driver'.", "warning");
        return;
    }
    try{
        const response = await fetch("http://localhost:8080/finalproject/NotifyVolunteers", {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded"
            },
            body: `incident_id=${encodeURIComponent(incidentId)}&volunteer_type=${encodeURIComponent(volunteerType)}`
        });
        if(response.ok){
            const result = await response.json();
            if(result.success){
                displayMessage("Volunteers notified successfully!");
            }else{
                displayMessage(`Error notifying volunteers: ${result.message || "Unknown error from server"}`, "danger");
            }
        }else{
            const errorText = await response.text();
            displayMessage(`Error notifying volunteers. Server responded with: ${response.statusText || errorText}`, "danger");
        }
    }catch(error){
        displayMessage("An error occurred while notifying volunteers.", "danger");
    }
}