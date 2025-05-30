const form = document.getElementById("myForm");
const unsuccessMessage = document.getElementById("unsuccessMessage");
let flag1=true;
let flag2=true;
let flag3=true;
let addrflag=true;
let munflag=true;
let passMatchflag=true;
let passRestrictedflag=true;
let passlengthflag=true;
let passweakflag=true;
document.addEventListener("DOMContentLoaded", () => {
    const checklocation=document.getElementById("checklocation");
    checklocation.addEventListener("click", async function(){
        await myLocation();        
        showmapbutton();
    }
);
    const mymap=document.getElementById("seeonmap");
    mymap.addEventListener("click", async function() {
        await myLocation();
        showmap();  
        addMarker(glat, glon, gdisplay);
        
    });

    const passwordInput = document.getElementById("password1");
    passwordInput.addEventListener("input", showPasswordStrength);

    const togglePassword1Btn = document.getElementById("togglePassword1");
    const togglePassword2Btn = document.getElementById("togglePassword2");

    togglePassword1Btn.addEventListener("click", () => passwordVisibility('password1', 'togglePassword1'));
    togglePassword2Btn.addEventListener("click", () => passwordVisibility('password2', 'togglePassword2'));

    const userTypeRadios = document.querySelectorAll('input[name="type"]');
    userTypeRadios.forEach(radio => radio.addEventListener("change", toggleVolunteerOptions));

    const birthdateInput = document.getElementById("birthdate");
    birthdateInput.addEventListener("change", validateAgeForVolunteer);

    const confirmPasswordInput = document.getElementById("password2");
    confirmPasswordInput.addEventListener("input", validatePasswordsMatch);

    const formElements = document.querySelectorAll('input, select');
    formElements.forEach(element => {
        element.addEventListener("keydown", moveToNextField);
    });

    function showmap(){
        if(location_true!=0){
            document.getElementById("Map").style.display="block";
        }else{
            document.getElementById("Map").style.display="none";
        }
    }

    function showmapbutton(){
        if(isvalidlocation!=0){
            document.getElementById("seeonmap").style.display="block";
        }else{
            document.getElementById("seeonmap").style.display="none";
            document.getElementById("Map").style.display="none";
        }
    }
    function formatAsJson(){
        const formData = {
            firstname: document.getElementById("firstname").value,
            lastname: document.getElementById("lastname").value,
            username:document.getElementById("username").value,
            password: document.getElementById("password1").value,
            email: document.getElementById("email").value,
            birthdate: document.getElementById("birthdate").value,
            gender:getCheckedGender(),
            afm: document.getElementById("afm").value,
            userType: document.querySelector('input[name="type"]:checked').value,
            volunteer_type: document.querySelector('input[name="role"]:checked').value || null,
            height: document.getElementById("height").value || null,
            weight: document.getElementById("weight").value || null,
            country: document.getElementById("country").value,
            prefecture: document.getElementById("prefecture").value,
            municipality: document.getElementById("Municipality").value,
            address: document.getElementById("address").value,
            job: document.getElementById("job").value,
            telephone: document.getElementById("telephone").value,
            lat: glat,
            lon :glon
        };
        const jsonData = JSON.stringify(formData, null, 2);
        return jsonData;
    }



    
    form.addEventListener("submit", async (event) => {
        event.preventDefault();
        const agreementCheckbox = document.getElementById('agreementCheckbox');
        const errorCheckBoxMessage = document.getElementById("checkBoxError");
        if(!agreementCheckbox.checked){
           errorCheckBoxMessage.textContent="You must agree to the terms before submitting.";
            return;
        }else{
            errorCheckBoxMessage.textContent="";
        }
        const isValid = validateForm();
        if(isValid){
            // displayFormDataAsJSON();
            var data = formatAsJson();
            console.log(await checkDuplicates());
            const type = document.querySelector('input[name="type"]:checked').value;
            if(type === 'volunteer'){
                const isvalidvolunteer = validateVolunteerFields();
                if(isvalidvolunteer){
                    if(await checkDuplicates()){
                        if(flag1 && flag2 && flag3 && munflag && addrflag && (passMatchflag && passRestrictedflag && passlengthflag && passweakflag)){
                            submitRegister(data);
                            unsuccessMessage.textContent ="";
                            document.getElementById("seeonmap").style.display="none";
                            document.getElementById("Map").style.display="none";
                            document.getElementById("locationMessage").innerHTML = "";
                            volunteerFields.style.display = "none";
                            strengthMessage.textContent = "";
                            checkboxLabel.textContent = "Απαγορεύεται η άσκοπη χρήση της εφαρμογής. Συμφωνώ πως η άσκοπη χρήση της θα διώκεται ποινικά.";
                            successfunc();
                        }
                    }else{
                        console.log("error");
                        unsuccessfulfunc();
                    }
                }                
            }else if(type === 'user'){
                if(await checkDuplicates()){
                    if(flag1 && flag2 && flag3 && munflag && addrflag && (passMatchflag && passRestrictedflag && passlengthflag && passweakflag)){
                        submitRegister(data);
                        unsuccessMessage.textContent ="";
                        document.getElementById("seeonmap").style.display="none";
                        document.getElementById("Map").style.display="none";
                        document.getElementById("locationMessage").innerHTML = "";
                        volunteerFields.style.display = "none";
                        strengthMessage.textContent = "";
                        
                        checkboxLabel.textContent = "Απαγορεύεται η άσκοπη χρήση της εφαρμογής. Συμφωνώ πως η άσκοπη χρήση της θα διώκεται ποινικά.";
                        successfunc();
                    }
                }else{
                    console.log("error");
                    unsuccessfulfunc();
                }
            }
        }
    });
});

function validateVolunteerFields() {
    let isValidvol = true;
    const role = document.querySelector('input[name="role"]:checked').value;
    const height = document.getElementById("height").value;
    const weight = document.getElementById("weight").value;
    const elementrole = document.getElementById("roleError");
    elementrole.textContent = "";
    const elementheight = document.getElementById("heightError");
    elementheight.textContent = "";
    const elementweight = document.getElementById("weightError");
    elementweight.textContent = "";
    if(!role){
        isValidvol = false;
        elementrole.textContent = "Role is required for volunteers";
        const rolefield = document.getElementById("role");
        rolefield.scrollIntoView();
    }
    if(!height){
        isValidvol = false;
        elementheight.textContent = "Height is required for volunteers";
        const heightfield = document.getElementById("height");
        heightfield.scrollIntoView();
    }
    if(!weight){
        isValidvol = false;
        elementweight.textContent = "Weight is required for volunteers";
        const weightfield = document.getElementById("weight");
        weightfield.scrollIntoView();
    }
    return isValidvol;
}
    
usernameField = document.getElementById("username");
emailField = document.getElementById("email");
telephoneField = document.getElementById("telephone");

usernameField.addEventListener("change",async (event) => {
    const isDuplicate = await checkDuplicates();
    console.log(isDuplicate);
    const dupusername = document.getElementById("dupusername");
    dupusername.textContent = "";
    if(!isDuplicate){
        const elementdupuser = document.getElementById("username");
        dupusername.textContent = "This username already exists";
        elementdupuser.scrollIntoView();
    }
});

emailField.addEventListener("change",async (event) => {
    const isDuplicate = await checkDuplicates();
    console.log(isDuplicate);
    dupemail = document.getElementById("dupemail");
    dupemail.textContent = "";
    if(!isDuplicate){
        const elementdupemail = document.getElementById("email");
        dupemail.textContent = "This email already exists";
        elementdupemail.scrollIntoView();
    }
});

telephoneField.addEventListener("change",async (event) => {
    const isDuplicate = await checkDuplicates();
    console.log(isDuplicate);
    const duptelephone = document.getElementById("duptelephone");   
    duptelephone.textContent = "";
    if(!isDuplicate){
        const elementduptele = document.getElementById("telephone");
        duptelephone.textContent = "This telephone already exists";
        elementduptele.scrollIntoView();
    }
});



async function checkDuplicates() {
    const urlParams = new URLSearchParams({
        username: usernameField.value,
        email: emailField.value,
        telephone: telephoneField.value
    }).toString();
    const response = await fetch("http://localhost:8080/finalproject/CheckDuplicate?"+ urlParams);
    if(response.status === 200){
        return true;
    }
    return false;
}

function successfunc(){
    const successMessage = document.getElementById("successMessage");
    successMessage.textContent = "Registration submitted successfully!";
    successMessage.style.display = "block";
    successMessage.style.color = "green";
    form.reset();
    window.scrollTo({ top: 0, behavior: 'smooth' });
    setTimeout(() => {
        successMessage.style.display = "none";
    }, 5000);
}

function unsuccessfulfunc(){
    unsuccessMessage.textContent = "Registration submitted unsuccessfully!";
    unsuccessMessage.style.display = "block";
    unsuccessMessage.style.color = "red";
    window.scrollTo({ top: 0, behavior: 'smooth' });
}

function submitRegister(data) {
    fetch("http://localhost:8080/finalproject/Register",{
        method: "POST",
        body: data
    }).then((response) => {
        console.log("sosto");
    }).catch((error) => {
        console.error("lathos");
    });
}

function getCheckedGender(){
    const genderRadioButtons = document.getElementsByName('gender');
    for(const radio of genderRadioButtons){
        if (radio.checked) {
            return radio.value;
        }
    }
    return null;
}

function moveToNextField(event){
    if(event.key === "Enter"){
        event.preventDefault();
        const currentField = event.target;
        if(!validateCurrentField(currentField)){
            return;
        }
        const inputs = Array.from(document.querySelectorAll('input, select, button'));
        const currentIndex = inputs.indexOf(currentField);
        if(currentIndex >= 0 && currentIndex < inputs.length - 1){
            const nextInput = inputs[currentIndex + 1];
            if(nextInput) nextInput.focus();
        }
    }
}


function validateCurrentField(field) {
    const fieldId = field.id;
    const value = field.value.trim();
    const errorMessage = document.getElementById("passwordError");
    const element = document.getElementById(fieldId);
    const elementMessage1 = document.getElementById("firtError");
    const elementMessage2 = document.getElementById("lastError");
    const elementMessage3 = document.getElementById("userError");
    const elementMessage4 = document.getElementById("emailError");
    const elementMessage5 = document.getElementById("afmError");
    const elementMessage6 = document.getElementById("phoneError");
    const elementMessage7 = document.getElementById("jobError");
    const elementMessage8 = document.getElementById("addressError");
    const elementMessage9 = document.getElementById("munError");
    
  
    
    if(fieldId === "firstname"){        
        if (!/^[A-Za-z]{3,30}$/.test(value)) {
            element.scrollIntoView();
            elementMessage1.textContent = "It has to be 3 to 30 characters and cannot contain numbers";
        }else{
            elementMessage1.textContent ="";
            return /^[A-Za-z]{3,30}$/.test(value);
        }
    }

    if(fieldId === "lastname"){        
        if (!/^[A-Za-z]{3,30}$/.test(value)) {
            element.scrollIntoView();
            elementMessage2.textContent = "It has to be 3 to 30 characters and cannot contain numbers";
        }else{
            elementMessage2.textContent ="";
            return /^[A-Za-z]{3,30}$/.test(value);
        }
    }

    if(fieldId === "username"){
        if (!/^[A-Za-z][A-Za-z0-9]{7,}$/.test(value)) {
            element.scrollIntoView();
            elementMessage3.textContent = "It has to be at least 8 characters starting with a letter";
            flag1=false;
        }else{
            elementMessage3.textContent ="";
            flag1=true;
            return /^[A-Za-z][A-Za-z0-9]{7,}$/.test(value);
        }        
    }

    if(fieldId === "password1"){
        showPasswordStrength();
        const strength = checkPasswordStrength(value);
        if(strength === "weak"){
            errorMessage.textContent = "Password is too weak.";
            element.scrollIntoView();
            passweakflag=false;
            return false;
        }
        passweakflag=true;
        return true;
    }
    if(fieldId === "password2"){
        const password = document.getElementById("password1").value;
        const confirmPassword = document.getElementById("password2").value;
        validatePasswordsMatch();
        return password === confirmPassword;
    }
    if(fieldId === "email"){
        if (!/^[a-z0-9._%+\-]+@[a-z0-9.\-]+\.[a-z]{2,}$/.test(value)) {
            element.scrollIntoView();
            elementMessage4.textContent = "Wrong email";
            flag2=false;
        }else{
            elementMessage4.textContent ="";
            flag2=true;
            return /^[a-z0-9._%+\-]+@[a-z0-9.\-]+\.[a-z]{2,}$/.test(value);
        }        
    }
    if(fieldId === "afm"){
        if (!/^[0-9]{10}$/.test(value)) {
            element.scrollIntoView();
            elementMessage5.textContent = "It has to be 10 numbers";
        }else{
            elementMessage5.textContent ="";
            return /^[0-9]{10}$/.test(value);
        } 
    }

    if(fieldId === "birthdate"){
        const type = document.querySelector('input[name="type"]:checked').value;
        const ageError = document.getElementById("ageError");
        const volunteerFields = document.getElementById("volunteerFields"); // Assuming extra options have this ID
        const age = calculateAge(value);
        ageError.textContent = "";
        if(type === "volunteer"){
            if(age < 18 || age > 55){
                ageError.textContent = "Firefighters must be between 18 and 55 years old.";
                ageError.style.display = "block";
                if(volunteerFields){
                    volunteerFields.style.display = "none";
                }
                return false;
            }else{
                ageError.textContent = "";
                ageError.style.display = "none";
                if(volunteerFields){
                    volunteerFields.style.display = "block";
                }
            }
        }
        return true;
    }
    if(fieldId === "telephone"){
        if (!/^[0-9]{10,14}$/.test(value)) {
            element.scrollIntoView();
            elementMessage6.textContent = "It has to be 10 to 14 numbers";
            flag3=false;
        }else{
            elementMessage6.textContent ="";
            flag3=true;
            return /^[0-9]{10,14}$/.test(value);
        } 
    }
    if(fieldId === "address"){
        if (!/^[a-zA-Z0-9\s]{3,150}$/.test(value)) {
            element.scrollIntoView();
            elementMessage8.textContent = "It has to be 3 to 150 characters";
            addrflag=false;
        }else{
            elementMessage8.textContent ="";
            addrflag=true;
            return /^[a-zA-Z0-9\s]{3,150}$/.test(value);
        } 
    }
    if(fieldId === "job"){
        if (!/^[A-Za-z]{3,30}$/.test(value)) {
            element.scrollIntoView();
            elementMessage7.textContent = "It has to be 3 to 30 characters and cannot contain numbers";
        }else{
            elementMessage7.textContent ="";
            return /^[A-Za-z]{3,30}$/.test(value);
        } 
    }

    if(fieldId === "Municipality"){
        if (!/^[A-Za-z]{3,30}$/.test(value)) {
            element.scrollIntoView();
            elementMessage9.textContent = "It has to be 3 to 30 characters and cannot contain numbers";
            munflag=false;
        }else{
            elementMessage9.textContent ="";
            munflag=true;
            return /^[A-Za-z]{3,30}$/.test(value);
        } 
    }
    return true;
}




function checkPasswordStrength(password){
    const hasUpperCase = /[A-Z]/.test(password);
    const hasLowerCase = /[a-z]/.test(password);
    const hasNumber = /[0-9]/.test(password);
    const hasSymbol = /[!@#$%^&*]/.test(password);            
    if(hasUpperCase && hasLowerCase && hasNumber && hasSymbol){
        return "strong";
    }else if(hasUpperCase || hasLowerCase || hasNumber || hasSymbol){
        return "medium";
    }
    return "weak";
}

const strengthMessage = document.getElementById("passwordStrength");
function showPasswordStrength(){
    const password = document.getElementById("password1").value;
    const elementpass = document.getElementById("password1");
    const errorMessage = document.getElementById("passwordError");
    const strength = checkPasswordStrength(password);
    if(strength === "strong"){
        strengthMessage.textContent = "Strong password";
        strengthMessage.style.color = "green";        
        if(password.length < 8 || password.length > 18){
            errorMessage.textContent = "Password must be between 8 and 18 characters";
            elementpass.scrollIntoView();
            passlengthflag=false;
        }else{
            errorMessage.textContent =""; 
            passlengthflag=true;
        }
    }else if(strength === "medium"){
        strengthMessage.textContent = "Medium password";
        strengthMessage.style.color = "orange";
        if(password.length < 8 || password.length > 18){
            errorMessage.textContent = "Password must be between 8 and 18 characters";
            elementpass.scrollIntoView();
            passlengthflag=false;
        }else{
            errorMessage.textContent ="";
            passlengthflag=true;
        } 
    }else{
        strengthMessage.textContent = "Weak password";
        strengthMessage.style.color = "red";
    }
    validateRestrictedWords(password);
}

function validateRestrictedWords(password) {
    const RestrictedMessage = document.getElementById("passwordRestricted");
    const restrictedWords = ["fire", "fotia", "ethelontis", "volunteer"];
    const lowerCasePassword = password.toLowerCase();
    const elementpass = document.getElementById("password1");
    for(let word of restrictedWords){
        if (lowerCasePassword.includes(word)){
            RestrictedMessage.textContent = `Password cannot contain restricted words like "${word}".`;
            RestrictedMessage.style.display = "block";
            elementpass.scrollIntoView();
            passRestrictedflag=false;
            return;
        }
    }
    passRestrictedflag=true;
    const digitCount = password.replace(/[^0-9]/g, "").length;
    if(digitCount / password.length >= 0.5){
        RestrictedMessage.textContent = "Weak password: contains too many numbers.";
        RestrictedMessage.style.display = "block";
        return;
    }else{
        RestrictedMessage.style.display = "none";        
    }

    if(isMoreThanHalfSameCharacter(password)){
        RestrictedMessage.textContent = "Weak password: contains too many repeated characters.";
        RestrictedMessage.style.display = "block";
    }else{
        RestrictedMessage.style.display = "none";        
    }
}

function isMoreThanHalfSameCharacter(password) {
    const charCount = {};
    for(let char of password){
        charCount[char] = (charCount[char] || 0) + 1;
    }
    for(let char in charCount){
        if (charCount[char] / password.length >= 0.5) {
            return true;
        }
    }
    return false;
}

function passwordVisibility(passwordFieldId, toggleButtonId){
    const passwordField = document.getElementById(passwordFieldId);
    const toggleButton = document.getElementById(toggleButtonId);

    if(passwordField.type === "password"){
        passwordField.type = "text";
        toggleButton.textContent = "Hide";
    }else{
        passwordField.type = "password";
        toggleButton.textContent = "Show";
    }
}

function calculateAge(birthdate){
    const birthDateObj = new Date(birthdate);
    const currentDate = new Date();
    let age = currentDate.getFullYear() - birthDateObj.getFullYear();
    const monthDifference = currentDate.getMonth() - birthDateObj.getMonth();
    if(monthDifference < 0 || (monthDifference === 0 && currentDate.getDate() < birthDateObj.getDate())){
        age--;
    }
    return age;
}

function validatePasswordsMatch() {
    const password = document.getElementById("password1").value;
    const elementpass = document.getElementById("password1");
    const confirmPassword = document.getElementById("password2").value;
    const matchMessage = document.getElementById("passwordMatch");

    if(password !== confirmPassword){
        matchMessage.textContent = "Passwords do not match!";
        matchMessage.style.display = "block";
        elementpass.scrollIntoView();
        passMatchflag=false;
    }else{
        matchMessage.textContent = "";
        matchMessage.style.display = "none";
        passMatchflag=true;
    }}

const volunteerFields = document.getElementById('volunteerFields'); 
function toggleVolunteerOptions() {
    const type = document.querySelector('input[name="type"]:checked').value; 
    const ageError = document.getElementById("ageError");  
    if(type === 'volunteer'){
        validateAgeForVolunteer();
    }else{
        volunteerFields.style.display = "none";
        ageError.textContent = "";
        checkboxLabel.textContent = "Απαγορεύεται η άσκοπη χρήση της εφαρμογής. Συμφωνώ πως η άσκοπη χρήση της θα διώκεται ποινικά.";
    }
}

function validateAgeForVolunteer(){
    const birthdate = document.getElementById("birthdate").value;
    const ageError = document.getElementById("ageError");
    const volunteerFields = document.getElementById("volunteerFields");    
    const age = calculateAge(birthdate);  
    ageError.textContent = "";
    ageError.style.display = "none";
    volunteerFields.style.display = "none";

    if(document.querySelector('input[name="type"]:checked').value === 'volunteer'){
        if(age < 18 || age > 55){
            ageError.textContent = "Firefighters must be between 18 and 55 years old.";
            ageError.style.display = "block";
            volunteerFields.style.display = "none"; 
        }else{
            ageError.textContent = "";
            ageError.style.display = "none";
            volunteerFields.style.display = "block";
            checkboxLabel.textContent = "Απαγορεύεται η άσκοπη χρήση της εφαρμογής. Συμφωνώ πως η άσκοπη χρήση της θα διώκεται ποινικά. Δηλώνω υπεύθυνα ότι ανήκω στο ενεργό δυναμικό των εθελοντών πυροσβεστών.";
        }
    }
}

function validateForm(){
    const formElements = document.querySelectorAll('input, select');
    let isValid = true;

    formElements.forEach((input) => {
        if (!validateCurrentField(input)) {
            isValid = false;
        }
    });
    return isValid;
}

var glat=0;
var glon=0;
var gdisplay=0;
var location_true=0;
var isvalidlocation=0;

var map = new OpenLayers.Map("Map");
var mapnik = new OpenLayers.Layer.OSM();
map.addLayer(mapnik);

var markers = new OpenLayers.Layer.Markers("Markers");
map.addLayer(markers);

function setPosition(lat, lon) {
    var fromProjection = new OpenLayers.Projection("EPSG:4326");
    var toProjection = new OpenLayers.Projection("EPSG:900913");
    var position = new OpenLayers.LonLat(lon, lat).transform(fromProjection, toProjection);
    return position;
}

function addMarker(lat, lon, message){
    markers.clearMarkers();
    while(map.popups.length > 0){
        map.removePopup(map.popups[0]);
    }
    var position = setPosition(lat, lon);
    var marker = new OpenLayers.Marker(position);
    markers.addMarker(marker);
    marker.events.register('mousedown', marker, function(evt){ 
        var popup = new OpenLayers.Popup.FramedCloud(
            "Popup",
            position,
            null,
            message,
            null,
            true
        );
        map.addPopup(popup);
    });
    const zoom = 12;
    map.setCenter(position, zoom);
}

async function myLocation(){
    const country=document.getElementById("country").value;
    const address=document.getElementById("address").value;
    const municipality=document.getElementById("Municipality").value;

    const url = `https://forward-reverse-geocoding.p.rapidapi.com/v1/forward?format=json&street=${address}&state=${municipality}&country=${country}&addressdetails=1&accept-language=en&namedetails=0&limit=1&bounded=0&polygon_text=0&polygon_kml=0&polygon_svg=0&polygon_geojson=0&polygon_threshold=0.0`;
    const options = {
        method: 'GET',
        headers: {
            'x-rapidapi-key': '9d99b98e81msh0bc67efb67b2bfap197ceajsn5bd843b4e217',
            'x-rapidapi-host': 'forward-reverse-geocoding.p.rapidapi.com'
        }
    };
    
    try {
        const response = await fetch(url, options);
        const result = await response.json();
        console.log(result);

        if (result.length === 0) {
            document.getElementById("locationMessage").innerHTML = "Location not found!";
            location_true=0;
            isvalidlocation=0;
            return;
        }
        
        const location = result[0];
        const lat = location.lat;
        const lon = location.lon;
        const displayName = location.display_name;

        glat=lat;
        glon=lon;
        gdisplay=displayName;

        if (displayName.includes("Crete")) {
            document.getElementById("locationMessage").innerHTML = `Location found at cords: (${lat}, ${lon})`;
            location_true=1;
            isvalidlocation=1;
      
        } else {
            document.getElementById("locationMessage").innerHTML = "You can use it only for Crete.";
            location_true=0;
            isvalidlocation=0;
        }
    } catch (error) {
        console.error(error);
        document.getElementById("locationMessage").innerHTML = "Error , location not found.";
        location_true=0;
        isvalidlocation=0;
    }
}