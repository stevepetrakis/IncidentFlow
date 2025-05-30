window.onload = function() {
    const volunteerData = localStorage.getItem("volunteer");
    if(volunteerData){
        const parsedData = JSON.parse(volunteerData);    
        const volunteerUsername = parsedData.username;
        fetchdata(volunteerUsername);
    }else{
        alert("No volunteer data found in localStorage!");
    }
};

async function fetchdata(volunteerUsername) {
    try{
        const response = await fetch(`GetParticipations?username=${volunteerUsername}`);
        if(response.ok){
            const textResponse = await response.text();
            if(textResponse){
                const result = JSON.parse(textResponse);
                displayTable(result);
                if(result.success){
                    console.log("Participations found:", result.participations);
                }else{
                    console.warn(result.message || "No participations found.");
                }
            }else{
                console.error("Response body is empty.");
            }
        }else{
            console.error(`Error in response: ${response.status} - ${response.statusText}`);
        }
    }catch(error){
        console.error("Fetch error:", error);
    }
}

function displayTable(data) {
    const container = document.getElementById("jsonOutput");
    container.innerHTML = "";
    if(data.participations && Array.isArray(data.participations) && data.participations.length > 0){
        let table = document.createElement("table");
        table.classList.add("table", "table-bordered", "table-striped");
        table.style.width = "100%";
        table.style.borderCollapse = "collapse";
        const headers = ["Incident ID", "Participant ID", "Volunteer Username", "Volunteer Type", "Status", "Success", "Comment"];
        let headerRow = document.createElement("tr");
        let firstHeader = document.createElement("th");
        firstHeader.textContent = "";
        firstHeader.style.width = "20%";
        headerRow.appendChild(firstHeader);
        data.participations.forEach((_, index) => {
            let th = document.createElement("th");
            th.textContent = `Incident ${index + 1}`;
            headerRow.appendChild(th);
        });
        table.appendChild(headerRow);
        headers.forEach(header => {
            let row = document.createElement("tr");
            let th = document.createElement("th");
            th.textContent = header;
            row.appendChild(th);
            data.participations.forEach(participation => {
                let td = document.createElement("td");
                switch(header){
                    case "Incident ID":
                        td.textContent = participation.incident_id || "N/A";
                        break;
                    case "Participant ID":
                        td.textContent = participation.participant_id || "N/A";
                        break;
                    case "Volunteer Username":
                        td.textContent = participation.volunteer_username || "N/A";
                        break;
                    case "Volunteer Type":
                        td.textContent = participation.volunteer_type || "N/A";
                        break;
                    case "Status":
                        td.textContent = participation.status || "N/A";
                        break;
                    case "Success":
                        td.textContent = participation.success || "N/A";
                        break;
                    case "Comment":
                        td.textContent = participation.comment || "N/A";
                        break;
                }
                row.appendChild(td);
            });

            table.appendChild(row);
        });
        container.appendChild(table);
        container.style.display = "block";
    }else{
        const message = document.createElement("p");
        message.textContent = "No participations found.";
        container.appendChild(message);
    }
}