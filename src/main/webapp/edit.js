document.addEventListener("DOMContentLoaded", () => {
    const findButton = document.getElementById("findButton");
    findButton.addEventListener("click", async (event) => {
        event.preventDefault();
        const userType = document.querySelector('input[name="user_type"]:checked').value;
        const data = await fetchDataByUserType(userType);
        if(data.length > 0){
            const tableHtml = createTableFromJSON(userType, data, true);
            document.getElementById("ajaxContent").innerHTML = tableHtml;
        }else{
            document.getElementById("ajaxContent").innerHTML = `<p>No ${userType}s found.</p>`;
        }
    });
});

async function fetchDataByUserType(userType) {
    try {
        const url = userType === "user" 
            ? "http://localhost:8080/finalproject/GetAllUsers?userType=user"
            : "http://localhost:8080/finalproject/GetAllUsers?userType=volunteer";
        const response = await fetch(url);
        if(response.ok){
            const result = await response.json();
            return userType === "user" ? result.users || [] : result.volunteers || [];
        }else{
            console.error(`Error fetching ${userType}s:`, response.statusText);
            return [];
        }
    } catch (error) {
        console.error("Fetch error:", error);
        return [];
    }
}

function createTableFromJSON(userType, data, editable = false) {
    let html = "<table class='table table-bordered' style='font-size: 12px; width: 100%; table-layout: fixed;'><thead><tr><th>Field</th>";
    data.forEach((item) => {
        html += `<th>ID: ${item.username}</th>`;
    });
    html += "</tr></thead><tbody>";
    const fields = [
        { label: "First Name", key: "firstname" },
        { label: "Last Name", key: "lastname" },
        { label: "Email", key: "email" },
        { label: "Birthdate", key: "birthdate" },
        { label: "Gender", key: "gender" },
        { label: "AFM", key: "afm" },
        { label: "Country", key: "country" },
        { label: "Prefecture", key: "prefecture" },
        { label: "Municipality", key: "municipality" },
        { label: "Address", key: "address" },
        { label: "Job", key: "job" },
        { label: "Telephone", key: "telephone" },
        { label: "Latitude", key: "lat" },
        { label: "Longitude", key: "lon" },
        { label: "Volunteer_type", key: "volunteer_type" },
        { label: "Height", key: "height" },
        { label: "Weight", key: "weight" }
    ];
    fields.forEach((field) => {
        html += `<tr><td><b>${field.label}</b></td>`;        
        data.forEach((item) => {
            const value = item[field.key];
            if(field.key === "email"){
                html += `<td>${value}</td>`;
            }else if(editable && field.key === "prefecture"){
                html += `<td><select id="${field.key}_${item.username}">
                            <option value="unknown" ${value === "unknown" ? "selected" : ""}>unknown</option>
                            <option value="Chania" ${value === "Chania" ? "selected" : ""}>Chania</option>
                            <option value="Heraklion" ${value === "Heraklion" ? "selected" : ""}>Heraklion</option>
                            <option value="Rethumno" ${value === "Rethumno" ? "selected" : ""}>Rethumno</option>
                            <option value="Agios Nikolas" ${value === "Agios Nikolas" ? "selected" : ""}>Agios Nikolas</option>
                        </select></td>`;
            }else if(editable && field.key === "gender"){
                html += `<td><select id="${field.key}_${item.username}">
                            <option value="male" ${value === "male" ? "selected" : ""}>Male</option>
                            <option value="female" ${value === "female" ? "selected" : ""}>Female</option>
                        </select></td>`;
            }else if(editable){
                html += `<td><input type="text" id="${field.key}_${item.username}" value="${value}" /></td>`;
            }else{
                html += `<td>${value}</td>`;
            }
        });
        html += "</tr>";
    });
    if(editable){
        html += "<tr><td><b>Actions</b></td>";
        data.forEach((item) => {
            html += `<td>
                        <button onclick="saveChanges('${item.username}', '${userType}')">Save</button>
                        <button onclick="deleteUser('${item.username}', '${userType}')">Delete</button>
                    </td>`;
        });
        html += "</tr>";
    }
    html += "</tbody></table>";
    return html;
}

async function saveChanges(itemUsername, userType){
    const updatedData = { username: itemUsername };
    let changesDetected = false;
    const inputs = document.querySelectorAll(`input[id$="_${itemUsername}"]`);
    inputs.forEach((input) => {
        const key = input.id.substring(0, input.id.lastIndexOf("_"));
        const currentValue = input.value;
        const originalValue = input.defaultValue;
        if(currentValue !== originalValue){
            updatedData[key] = currentValue;
            changesDetected = true;
        }
    });
    const selects = document.querySelectorAll(`select[id$="_${itemUsername}"]`);
    selects.forEach((select) => {
        const key = select.id.substring(0, select.id.lastIndexOf("_"));
        const currentValue = select.value;
        const originalValue = select.querySelector("option[selected]").value;
        if(currentValue !== originalValue) {
            updatedData[key] = currentValue;
            changesDetected = true;
        }
    });
    if(!changesDetected){
        alert("No changes detected. Please modify the fields before saving.");
        return;
    }
    try{
        const url = userType === "user" 
            ? "http://localhost:8080/finalproject/UpdateUser" 
            : "http://localhost:8080/finalproject/UpdateVolunteer";

        const response = await fetch(url, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(updatedData)
        });
        if(response.ok){
            const result = await response.json();
            if (result.success) {
                alert("Changes saved successfully!");
            } else {
                alert(`Error saving changes: ${result.message || "Unknown error from server"}`);
            }
        }else{
            const errorText = await response.text();
            alert(`Error saving changes. Server responded with: ${response.statusText || errorText}`);
        }
    }catch(error){
        console.error("Error saving changes:", error);
    }
}

async function deleteUser(itemUsername, userType) {
    const confirmed = confirm(`Are you sure you want to delete user with username: ${itemUsername}?`);
    if(!confirmed) return;
    try{
        let response;
        if(userType === "user"){
            response = await fetch("DeleteUser", {
                method: "POST",
                headers: {
                    "Content-Type": "application/x-www-form-urlencoded"
                },
                body: `username=${encodeURIComponent(itemUsername)}`
            });
        }else if(userType === "volunteer"){
            response = await fetch("DeleteVolunteer", {
                method: "POST",
                headers: {
                    "Content-Type": "application/x-www-form-urlencoded"
                },
                body: `username=${encodeURIComponent(itemUsername)}`
            });
        }
        if(response.ok){
            const result = await response.json();
            if(result.success){
                alert("User deleted successfully!");
                const row = document.querySelector(`#user_${itemUsername}`);
                if (row) row.remove();
            }else{
                alert(`Error deleting user: ${result.message || "Unknown error from server"}`);
            }
        }else{
            const errorText = await response.text();
            alert(`Error deleting user. Server responded with: ${response.statusText || errorText}`);
        }
    }catch(error){
        console.error("Error deleting user:", error);
    }
}