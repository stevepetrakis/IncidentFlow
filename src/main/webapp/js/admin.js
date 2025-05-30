function handleLoginAdmin(){
    const elementLoginAdmin = document.getElementById("loginError");
    elementLoginAdmin.textContent = "";
    const username = document.getElementById("username").value;
    const password = document.getElementById("password").value;
    if(username === "admin" && password === "admin123"){
        const adminUser = {
            username: "admin",
            role: "admin"
        };
        localStorage.setItem('user', JSON.stringify(adminUser));
        localStorage.setItem("checkSubmittedIncidents", "true");
        window.location.href = "admin_menu.html";
    }else{
        elementLoginAdmin.textContent = "Invalid admin credentials. Please try again.";
    }
}

function checkSubmittedIncidents(){
    const xhr = new XMLHttpRequest();
    xhr.onload = function(){
        if(xhr.readyState === 4 && xhr.status === 200){
            const response = JSON.parse(xhr.responseText);
            const incidents = response.results;
            const submittedIncidents = incidents.filter(
                (incident) => incident.status === "submitted"
            );
        if(submittedIncidents.length > 0){
                alert(
                    `There are ${submittedIncidents.length} incidents with status "submitted":\n` +
                    submittedIncidents
                        .map(
                            (incident) =>
                                `Type: ${incident.incident_type}, Address: ${incident.address}, Municipality: ${incident.municipality}`
                        )
                        .join("\n")
                );
            }else{
                alert("No incidents with status 'submitted' at the moment.");
            }
        }else if(xhr.status !== 200){
            alert("Error fetching incident data.");
        }
    };
    xhr.open("POST", "ViewIncident", true);
    xhr.setRequestHeader("Content-Type", "application/json");
    xhr.send(JSON.stringify({ status: "Submitted" }));
}