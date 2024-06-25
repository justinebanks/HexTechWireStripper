
const leftStripLengthInput = document.getElementById("left_strip_length");
const wireLengthInput = document.getElementById("wire_length");
const rightStripLengthInput = document.getElementById("right_strip_length");
const wireQuantityInput = document.getElementById("wire_quantity");
const stripDepthInput = document.getElementById("strip_depth");
const submitButton = document.getElementById("left_strip_length");


function makePUTRequest(url, data, callback) {
    var xhr = new XMLHttpRequest();

    // Configure it: PUT-request for the provided URL
    xhr.open("PUT", url, true);

    // Set the content type of the request to JSON
    xhr.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
    xhr.setRequestHeader("Access-Control-Allow-Origin", "*");

    // Define the callback function to handle the response
    xhr.onload = function () {
        if (xhr.status >= 200 && xhr.status < 300) {
            // If the request was successful, call the callback with the response
            callback(null, xhr.responseText);
        } else {
            // If there was an error, call the callback with an error message
            callback("Error: " + xhr.status + " - " + xhr.statusText + " - " + xhr.responseText, null);
        }
    };

    // Handle network errors
    xhr.onerror = function () {
        callback("Network error", null);
    };

    // Convert the data object to a JSON string and send the request
     xhr.send(JSON.stringify(data));
}


function onSubmit() {
    let url = "http://www.msftconnecttest.com/latest/drivers";
    let data = { value: "P_led.bl_on" }

    makePUTRequest(url, data, function (error, response) {
        if (error) {
            console.error("Request Failed:", error);
        }

        console.log(response);
    })
}