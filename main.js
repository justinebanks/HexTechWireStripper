
const client = mqtt.connect("ws://mqtt.hextronics.cloud:8083/mqtt", {
    username: "hextech-justin",
    password: "justin"
});

const leftStripLength = document.getElementById("left_strip_length");
const wireLength = document.getElementById("wire_length");
const rightStripLength = document.getElementById("right_strip_length");
const wireQuantity = document.getElementById("wire_quantity");
const stripDepth = document.getElementById("strip_depth");
const submitButton = document.getElementById("left_strip_length");

const LIN_MOT_ONE = "stepper.00"; // rywb4-rwyb6
const LIN_MOT_TWO = "stepper.01"; // bwyr4-rwyb6
const EXTRUDER_STEPPER = "stepper.02"; // rywb4-rwyb6

// To Be Defined
const WIRE_Y_POSITION = 0;
const LIN_MOT_STEP_DIST = 1;
const EXTRUDER_STEP_DIST = 1;
const BOTTOM_BLADE_POSITION = -7000;

let commandArray = [];
let bladeHistory = [];


function emergencyStop() {
    runSerialCommand("P_stepper.00_stop;P_stepper.01_stop;P_stepper.02_stop");
}

function initializeCurrent() {
    runSerialCommand("stepper.00_rms_600;stepper.01_rms_600;stepper.02_rms_300");
}


function getBladeRelativePosition() {
    let distFromHome = 0;

    for (let i = 0; i < bladeHistory.length; i++) {
        distFromHome += bladeHistory[i];
    }

    return distFromHome;
}


function goHome() {
    runSerialCommand(moveTopBlade(-getBladeRelativePosition()));
    bladeHistory = [];
}


// Turns commandArray Into A Single Command String
function commandArrayToString() {
    let returnString = "";

    for (let i = 0; i < commandArray.length; i++) {
        returnString = returnString + "|" + commandArray[i];
    }

    return returnString;
}


function runSerialCommand(command) {
    if (typeof command == "object") {
        command = commandArrayToString(command);
    }

    client.publish(
        "hextech/hextech-justin/commands",
        command
    );
}


function moveTopBlade(distance) { // Positive for Up, Negative for Down
    dist = LIN_MOT_STEP_DIST * distance;
    command = `${LIN_MOT_ONE}_move_${distance*-1};${LIN_MOT_TWO}_move_${dist}`;

    commandArray.push(command);
    bladeHistory.push(distance);

    return command;
}


function moveWire(distance) { // Positive for Forward, Negative for Backward
    dist = EXTRUDER_STEP_DIST * distance
    command = `${EXTRUDER_STEPPER}_move_${dist}`;
    commandArray.push(command);
    return command;
}


function cutWire() {
    return moveTopBlade(BOTTOM_BLADE_POSITION - getBladeRelativePosition())
}


function stripWire() {
    return moveTopBlade(BOTTOM_BLADE_POSITION - getBladeRelativePosition() + parseInt(stripDepth.value))
}


function testSequence() {
    commandArray = []
    moveWire(2000);
    moveTopBlade(-5000);
    moveWire(-1500);
    moveTopBlade(5000);
    runSerialCommand(commandArray);
}


function runSequence() {
    commandArray = [];

    moveWire(1000);
    stripWire();
    moveTopBlade(1000);

    runSerialCommand(commandArray);
}


function onSubmit() {
    console.log("Submit Button Clicked");
    runSequence();

    console.log("Finished");
}

// Brings Top Blade to "home" position when page is closed or refreshed
window.onbeforeunload = () => {
    //goHome();
}

// Should Start Printing Response Messages from MQTT Server
client.on('message', function (topic, message) {
    // message is Buffer
    console.log("MQTT Response: " + message.toString())
    client.end()
})
