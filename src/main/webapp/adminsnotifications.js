document.addEventListener("DOMContentLoaded", () => {
    const jsonOutputDiv = document.getElementById("jsonOutput");
    function fetchNotifications() {
        fetch("http://localhost:8080/finalproject/NotifyAdmin")
            .then((response) => {
                if (!response.ok) {
                    throw new Error("Failed to fetch notifications");
                }
                return response.json();
            })
            .then((data) => {
                if(data.status === "success" && data.notifications){
                    fetchDetailsForNotifications(data.notifications);
                }else{
                    jsonOutputDiv.innerHTML = `<p class="text-danger">Error: Unable to load notifications.</p>`;
                }
            })
            .catch((error) => {
                console.error("Error fetching notifications:", error);
                jsonOutputDiv.innerHTML = `<p class="text-danger">Error fetching notifications.</p>`;
            });
    }

    function fetchDetailsForNotifications(notifications) {
        const promises = notifications.map((notification) => {
            const { incidentId, username } = notification;
            const incidentPromise = fetch(`http://localhost:8080/finalproject/GetIncident?incident_id=${incidentId}`)
                .then((response) => {
                    if(!response.ok){
                        throw new Error(`Failed to fetch incident with ID ${incidentId}`);
                    }
                    return response.json();
                })
                .catch((error) => {
                    console.error(`Error fetching incident ${incidentId}:`, error);
                    return null;
                });
            const volunteerPromise = fetch(`http://localhost:8080/finalproject/GetRequestedVolunteers?username=${username}`)
                .then((response) => {
                    if(!response.ok){
                        throw new Error(`Failed to fetch volunteer with username ${username}`);
                    }
                    return response.json();
                })
                .catch((error) => {
                    console.error(`Error fetching volunteer ${username}:`, error);
                    return null;
                });
            return Promise.all([incidentPromise, volunteerPromise]).then(([incident, volunteer]) => {
                return {
                    ...notification,
                    incidentDetails: incident && incident.incident || null,
                    volunteerDetails: volunteer && volunteer.volunteer || null
                };
            });
        });
        Promise.all(promises).then((detailedNotifications) => {
            displayNotifications(detailedNotifications);
        });
    }

    function handleAction(action, incidentId, username, volunteerType){
        if(action === "decline"){
            fetch(`http://localhost:8080/finalproject/NotifyAdmin?incident_id=${incidentId}&username=${username}`, {
                method: "DELETE"
            })
                .then((response) => {
                    if (!response.ok) {
                        throw new Error("Failed to decline notification");
                    }
                    return response.json();
                })
                .then(() => {
                    alert("Notification declined successfully");
                    location.reload();
                })
                .catch((error) => {
                    console.error("Error declining notification:", error);
                    alert("Failed to decline notification");
                });
        }else if(action === "accept"){
            fetch(`http://localhost:8080/finalproject/UpdateParticipantsByAdmin`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    username: username,
                    incident_id: incidentId
                })
            })
                .then((response) => {
                    if(!response.ok){
                        throw new Error("Failed to accept notification");
                    }
                    return response.json();
                })
                .then((data) => {
                    if(data.success){
                        alert("Volunteer added to participants successfully");
                        updateFiremenVehicles(incidentId, volunteerType);   
                        fetch(`http://localhost:8080/finalproject/NotifyAdmin?incident_id=${incidentId}&username=${username}`, {
                            method: "DELETE"
                        })
                            .then((response) => {
                                if (!response.ok) {
                                    throw new Error("Failed to decline notification");
                                }
                                return response.json();
                            })
                            .then(() => {
                                console.log("Notification remved successfully");
                                location.reload();
                            })
                            .catch((error) => {
                                console.error("Error declining notification:", error);
                                alert("Failed to decline notification");
                            });
                    }else{
                        alert("Failed to add volunteer to participants");
                    }
                })
                .catch((error) => {
                    console.error("Error accepting notification:", error);
                    alert("Failed to accept notification");
                });
        }
    }
    
    function updateFiremenVehicles(incidentId, volunteerType){
        if(!incidentId || !volunteerType){
            console.error("Missing incidentId or volunteerType for updating firemen vehicles.");
            return Promise.reject(new Error("Invalid data for UpdateFiremenVehicles."));
        }
        return fetch(`http://localhost:8080/finalproject/UpdateFiremenVehicles`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ incident_id: incidentId, volunteer_type: volunteerType })
        })
        .then((response) => {
            if(!response.ok){
                throw new Error("Failed to update firemen vehicles");
            }
            return response.json();
        })
        .then((response) => {
            if(response.success){
                console.log("Firemen vehicles updated successfully");
            }else{
                throw new Error("Failed to update firemen vehicles: " + response.message);
            }
        });
    }

    function displayNotifications(detailedNotifications) {
        jsonOutputDiv.innerHTML = "";
        if(!detailedNotifications || detailedNotifications.length === 0){
            jsonOutputDiv.innerHTML = "<p>No notifications found.</p>";
            return;
        }
        const keys = [
            "#",
            "Incident ID",
            "Incident Type",
            "Description",
            "Address",
            "Municipality",
            "Danger Level",
            "Volunteer Firstname",
            "Volunteer Lastname",
            "Volunteer Type",
            "Telephone",
            "Actions"
        ];
        const table = document.createElement("table");
        table.style.width = "100%";
        table.style.borderCollapse = "collapse";
        const thead = document.createElement("thead");
        const headerRow = document.createElement("tr");
        const thEmpty = document.createElement("th");
        thEmpty.textContent = "";
        thEmpty.style.border = "1px solid #ddd";
        thEmpty.style.padding = "8px";
        thEmpty.style.backgroundColor = "#343a40";
        thEmpty.style.color = "#fff";
        headerRow.appendChild(thEmpty);
        detailedNotifications.forEach((_, index) => {
            const th = document.createElement("th");
            th.textContent = index + 1; 
            th.style.border = "1px solid #ddd";
            th.style.padding = "8px";
            th.style.backgroundColor = "#343a40";
            th.style.color = "#fff";
            headerRow.appendChild(th);
        });
        thead.appendChild(headerRow);
        table.appendChild(thead);
        const tbody = document.createElement("tbody");
        keys.forEach((key, keyIndex) => {
            const row = document.createElement("tr");
            const tdKey = document.createElement("td");
            tdKey.textContent = key;
            tdKey.style.border = "1px solid #ddd";
            tdKey.style.padding = "8px";
            tdKey.style.backgroundColor = "#f1f1f1";
            tdKey.style.color = "#333";
            tdKey.style.fontWeight = "bold";
            row.appendChild(tdKey);
            detailedNotifications.forEach((notification) => {
                const { incidentDetails, volunteerDetails } = notification;
                let cellData;
                const td = document.createElement("td");
                switch (keyIndex) {
                    case 0:
                        cellData = notification.incidentId || "N/A";
                        break;
                    case 1: 
                        cellData = notification.incidentId || "N/A";
                        break;
                    case 2:
                        cellData = (incidentDetails && incidentDetails.incident_type) || "N/A";
                        break;
                    case 3:
                        cellData = (incidentDetails && incidentDetails.description) || "N/A";
                        break;
                    case 4: 
                        cellData = (incidentDetails && incidentDetails.address) || "N/A";
                        break;
                    case 5:
                        cellData = (incidentDetails && incidentDetails.municipality) || "N/A";
                        break;
                    case 6: 
                        cellData = (incidentDetails && incidentDetails.danger) || "N/A";
                        break;
                    case 7: 
                        cellData = (volunteerDetails && volunteerDetails.firstname) || "N/A";
                        break;
                    case 8: 
                        cellData = (volunteerDetails && volunteerDetails.lastname) || "N/A";
                        break;
                    case 9: 
                        cellData = (volunteerDetails && volunteerDetails.volunteer_type) || "N/A";
                        break;
                    case 10: 
                        cellData = (volunteerDetails && volunteerDetails.telephone) || "N/A";
                        break;
                    case 11: 
                        const acceptButton = document.createElement("button");
                        acceptButton.textContent = "Accept";
                        acceptButton.style.marginRight = "5px";
                        acceptButton.classList.add("btn", "btn-success");
                        acceptButton.onclick = () => handleAction("accept", notification.incidentId, volunteerDetails && volunteerDetails.username, volunteerDetails && volunteerDetails.volunteer_type);
                        const declineButton = document.createElement("button");
                        declineButton.textContent = "Decline";
                        declineButton.classList.add("btn", "btn-danger");
                        declineButton.onclick = () => handleAction("decline", notification.incidentId, volunteerDetails && volunteerDetails.username, volunteerDetails && volunteerDetails.volunteer_type);
                        td.appendChild(acceptButton);
                        td.appendChild(declineButton);
                        cellData = "";
                        break;
                    default:
                        cellData = "N/A";
                }
                if (keyIndex !== 11) td.textContent = cellData;
                td.style.border = "1px solid #ddd";
                td.style.padding = "8px";
                td.style.backgroundColor = "#fff";
                td.style.color = "#333";
                row.appendChild(td);
            });
            tbody.appendChild(row);
        });
        table.appendChild(tbody);
        jsonOutputDiv.appendChild(table);
        jsonOutputDiv.style.display = "block";
    } 
    fetchNotifications();
});