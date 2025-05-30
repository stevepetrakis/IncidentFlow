async function filterContacts() {
    const searchInput = document.getElementById("searchContacts").value.trim();
    const contactsList = document.getElementById("contactsList");
    contactsList.innerHTML = "";
    if(searchInput === ""){
        console.log("Empty search input, returning.");
        return;
    }
    try{
        const response = await fetch(`GetEveryOneByUsername?username=${encodeURIComponent(searchInput)}`);
        if(response.ok){
            const contacts = await response.json();
            if(contacts.exists){
                const contactDiv = document.createElement("div");
                contactDiv.className = "contact-item";
                contactDiv.textContent = searchInput;
                contactDiv.addEventListener("click", () => {
                    startChatWith({ username: searchInput });
                });
                contactsList.appendChild(contactDiv);
            }else{
                const noResultsDiv = document.createElement("div");
                noResultsDiv.textContent = "No contacts found.";
                noResultsDiv.className = "no-results";
                contactsList.appendChild(noResultsDiv);
            }
        }else{
            console.error("Error fetching contacts:", response.statusText);
        }
    }catch(error){
        console.error("Error occurred while fetching contacts:", error);
    }
}

function startChatWith(contact){
    const chatTitle = document.getElementById("messagetitle");
    chatTitle.textContent = `Chat with ${contact.username}`;
    console.log(`Starting chat with ${contact.username}`);    
    chatTitle.style.left = "40%";
    fetchChatHistory(contact.username);
}

async function fetchChatHistory(contactUsername) {
    const userInfo = JSON.parse(localStorage.getItem("user"));
    const myUsername = userInfo.username;
    if(!myUsername){
        console.error("Username not found in local storage.");
        return;
    }
    try{
        const response = await fetch(`GetSendMessages?user1=${encodeURIComponent(myUsername)}&user2=${encodeURIComponent(contactUsername)}`);
        if(response.ok){
            try{
                const data = await response.json(); 
                if(data.success){
                    if(data.messages.length === 0) {
                        const messagesContainer = document.getElementById("messages");
                        messagesContainer.innerHTML = "No messages between these users.";
                    }else{
                        console.log("Messages fetched for user:", data.messages);                        
                        displayMessages(data.messages);
                    }
                }else{
                    console.error("Error: Response indicates failure despite status 200.", data);
                }
            }catch(error){
                    console.error("Error parsing JSON response:", error);
                    alert("There was an issue parsing the response data.");
            }
        }else{
            console.error("Error fetching messages:", response.statusText);
        }
    }catch(error){
        console.error("Error occurred while fetching messages:", error);
    }
}

function displayMessages(messages) {
    const messagesContainer = document.getElementById("messages");
    messagesContainer.innerHTML = "";
    if(messages.length === 0){
        messagesContainer.textContent = "No messages to display.";
        return;
    }
    const userInfo = JSON.parse(localStorage.getItem("user"));
    const myUsername = userInfo.username;
    messages.forEach((message) => {
        const messageDiv = document.createElement("div");
        const senderSpan = document.createElement("span");
        const messageContent = document.createElement("span");
        senderSpan.textContent = `${message.sender}: `;
        senderSpan.style.fontWeight = "bold";
        senderSpan.style.color = message.sender === myUsername ? "green" : "red";
        messageContent.textContent = message.message;
        messageDiv.appendChild(senderSpan);
        messageDiv.appendChild(messageContent);
        messagesContainer.appendChild(messageDiv);
    });
}

async function sendMessage(){
    const messageInput = document.getElementById("messageInput");
    const messageContent = messageInput.value.trim();
    if(!messageContent){
        alert("Please type a message before sending.");
        return;
    }
    const userInfo = JSON.parse(localStorage.getItem("user"));
    const myUsername = userInfo.username;
    if(!myUsername){
        console.error("Username is missing.");
        return;
    }
    const chatTitle = document.getElementById("messagetitle").textContent;
    let recipientType, recipientId;
    if(chatTitle.startsWith("Chat with ")){
        recipientType = "user";
        recipientId = chatTitle.replace("Chat with ", "").trim();
    }else if(chatTitle.startsWith("Chat Room: ")){
        recipientType = "public";
        recipientId = "public";
    }else{
        console.error("Invalid chat context.");
        return;
    }
    const payload = {
        sender: myUsername,
        message: messageContent
    };
    if(recipientType === "user"){
        payload.recipient = recipientId; 
    }else if(recipientType === "public"){
        payload.recipient = recipientId;
        payload.incident_id = incident_id;
    }
    try{
        const response = await fetch("GetSendMessages", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(payload)
        });
        if(response.ok){
            const data = await response.json();
            console.log("Message sent:", data);
            messageInput.value = "";
            if(data.success){
                if(recipientType === "user"){
                    fetchChatHistory(recipientId);
                }else if(recipientType === "public"){
                    fetchIncidentHistory(incident_id);
                }
            }else{
                console.error("Error sending message:", data);
                alert("There was an issue sending the message.");
            }
        }else{
            console.error("Error sending message:", response.statusText);
            alert("Failed to send message.");
        }
    }catch (error){
        console.error("Error occurred while sending message:", error);
        alert("There was an error sending the message.");
    }
}

document.getElementById("sendMessage").addEventListener("click", sendMessage);



var incident_id;
async function filterIncidents() {
    const searchInput = document.getElementById("searchIncident").value.trim();
    const incidentsList = document.getElementById("incidentsList");
    incidentsList.innerHTML = "";
    if(searchInput === ""){
        console.log("Empty search input, returning.");
        return;
    }
    try{
        const response = await fetch(`GetIncident?incident_id=${encodeURIComponent(searchInput)}`);
        if(response.ok){
            const data  = await response.json();
            console.log(data);
            if(data.success){
                const incident = data.incident;
                incident_id = incident.incident_id;
                const incidentDiv  = document.createElement("div");
                incidentDiv.className = "incident-item";
                incidentDiv.textContent = `Incident: ${incident.description}`;
                incidentDiv.addEventListener("click", () => {
                    setChatRoomName(incident.description);
                    startChatWithIncident(incident);
                });
                incidentsList.appendChild(incidentDiv);
            }else{
                const noResultsDiv = document.createElement("div");
                noResultsDiv.textContent = "No contacts found.";
                noResultsDiv.className = "no-results";
                incidentsList.appendChild(noResultsDiv);
            }
        }else{
            console.error("Error fetching contacts:", response.statusText);
        }
    }catch(error){
        console.error("Error occurred while fetching contacts:", error);
    }
}

function setChatRoomName(description) {
    const messageTitle = document.getElementById("messagetitle");
    messageTitle.textContent = `Chat Room: ${description}`;
    messageTitle.style.left = "30%";
}

function startChatWithIncident(incident){
    const chatTitle = document.getElementById("messagetitle");
    chatTitle.textContent = `Chat Room: ${incident.description}`;
    console.log(`Starting chat in ${incident.description} room`);
    fetchIncidentHistory(incident.incident_id);
}

async function fetchIncidentHistory(incident_id) {
    if(!incident_id){
        console.error("Incident ID is required.");
        return;
    }
    const userInfo = JSON.parse(localStorage.getItem("user"));
    const myUsername = userInfo.username;
    if(!myUsername){
        console.error("Username not found in local storage.");
        return;
    }    
    try{
        const response = await fetch(`GetSendIncidentMessage?incident_id=${encodeURIComponent(incident_id)}`);
        if(response.ok){
            try{
                const data = await response.json(); 
                if(data.success){
                    if(data.messages.length === 0) {
                        const messagesContainer = document.getElementById("messages");
                        messagesContainer.innerHTML = "No messages between these users.";
                    }else{
                        console.log("Messages fetched for incident:", data.messages);                        
                        displayMessages(data.messages);
                    }
                }else{
                    console.error("Error: Response indicates failure despite status 200.", data);
                }
            }catch(error){
                    console.error("Error parsing JSON response:", error);
                    alert("There was an issue parsing the response data.");
            }
        }else{
            console.error("Error fetching messages:", response.statusText);
        }
    }catch(error){
        console.error("Error occurred while fetching messages:", error);
    }
}