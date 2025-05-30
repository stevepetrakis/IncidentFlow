document.addEventListener("DOMContentLoaded", () => {
    const ajaxContent = document.getElementById("ajaxContent");
    fetchParticipants();
    function fetchParticipants(){
        fetch("http://localhost:8080/finalproject/GetParticipant?userType=participant")
            .then((response) => {
                if (!response.ok) {
                    throw new Error(`HTTP error! Status: ${response.status}`);
                }
                return response.json();
            })
            .then((data) => {
                if (data.success) {
                    renderTable(data.participants);
                } else {
                    ajaxContent.innerHTML = `<p class="text-danger">${data.message}</p>`;
                }
            })
            .catch((error) => {
                ajaxContent.innerHTML = `<p class="text-danger">Error fetching participants: ${error.message}</p>`;
            });
    }

    function renderTable(participants){
        if(participants.length === 0){
            ajaxContent.innerHTML = "<p>No participants found.</p>";
            return;
        }
        const fieldNames = Object.keys(participants[0]);
        let tableHtml = `
            <table class="table table-striped">
                <thead>
                    <tr>
                        <th>Field</th>
                        ${participants.map((_, index) => `<th>Participant ${index + 1}</th>`).join("")}
                    </tr>
                </thead>
                <tbody>
        `;
        fieldNames.forEach((field) => {
            tableHtml += `
                <tr>
                    <td>${capitalize(field)}</td>
                    ${participants
                        .map((participant, index) => {
                            if (["success", "comment", "status"].includes(field)) {
                                return `
                                    <td>
                                        <input 
                                            type="text" 
                                            class="form-control" 
                                            id="${field}-${index}" 
                                            value="${participant[field] || ""}">
                                    </td>`;
                            } else {
                                return `<td>${participant[field] || "null"}</td>`;
                            }
                        })
                        .join("")}
                </tr>
            `;
        });
        tableHtml += `
            <tr>
                <td>Action</td>
                ${participants
                    .map((participant, index) => `
                        <td>
                            <button 
                                class="btn btn-success btn-sm save-button" 
                                data-index="${index}">
                                Save
                            </button>
                            <button 
                                class="btn btn-danger btn-sm delete-button" 
                                data-id="${participant.participant_id}">
                                Delete
                            </button>
                        </td>
                    `)
                    .join("")}
            </tr>
        `;
        tableHtml += `
                </tbody>
            </table>
        `;
        ajaxContent.innerHTML = tableHtml;
        document.querySelectorAll(".save-button").forEach((button) => {
            button.addEventListener("click", () => {
                const index = button.dataset.index;
                saveParticipant(index, participants[index]);
            });
        });
        document.querySelectorAll(".delete-button").forEach((button) => {
            button.addEventListener("click", () => {
                const participantId = button.dataset.id;
                deleteParticipant(participantId);
            });
        });
    }

    function saveParticipant(index, participant){
        const updatedParticipant = {
            participant_id: participant.participant_id,
            success: document.getElementById(`success-${index}`).value,
            comment: document.getElementById(`comment-${index}`).value,
            status: document.getElementById(`status-${index}`).value
        };
        fetch("http://localhost:8080/finalproject/UpdateParticipantsInfo", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(updatedParticipant)
        })
            .then((response) => {
                if(!response.ok){
                    throw new Error(`HTTP error! Status: ${response.status}`);
                }
                return response.text();
            })
            .then((responseText) => {
                try{
                    const data = JSON.parse(responseText);
                    alert(`Server response JSON: ${JSON.stringify(data)}`);
                    if(data.success){
                        alert("Participant updated successfully!");
                        fetchParticipants();
                    }else{
                        alert("Error updating participant: " + data.message);
                    }
                }catch(parseError){
                    alert(`Error parsing JSON response: ${parseError.message}\nResponse: ${responseText}`);
                }
            })
            .catch((error) => {
                alert("Error: " + error.message);
            });
    }

    function deleteParticipant(participantId) {
        if (!confirm("Are you sure you want to delete this participant?")) return;
        fetch("http://localhost:8080/finalproject/DeleteParticipant", {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded"
            },
            body: `participant_id=${participantId}`
        })
            .then((response) => {
                if (!response.ok) {
                    throw new Error(`HTTP error! Status: ${response.status}`);
                }
                return response.json();
            })
            .then((data) => {
                if(data.status === "success"){
                    alert("Participant deleted successfully!");
                    fetchParticipants();
                }else{
                    alert("Error deleting participant: " + data.message);
                }
            })
            .catch((error) => {
                alert("Error: " + error.message);
            });
    }

    function capitalize(text) {
        return text.charAt(0).toUpperCase() + text.slice(1).replace(/_/g, " ");
    }
});