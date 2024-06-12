
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

const LIN_MOT_ONE = "stepper.00";
const LIN_MOT_TWO = "stepper.01";
const EXTRUDER_STEPPER = "stepper.02";

// To Be Definted
const WIRE_Y_POSITION = 0;
const LIN_MOT_STEP_DIST = 0;
const EXTRUDER_STEP_DIST = 0

let recordedCurrentPosition = 0;
let commandArray = [];


function moveTopBlade(distance) {
    dist = LIN_MOT_STEP_DIST * distance;
    command = `${LIN_MOT_ONE}_move_${distance};${LIN_MOT_TWO}_move_${dist}`
    commandArray.push(command);
    return command;
}


function moveWire(distance) {
    dist = EXTRUDER_STEP_DIST * distance;
    command = `${EXTRUDER_STEPPER}_move_${dist}`;
    commandArray.push(command);
    return command;
}


// Evaluates commandArray Based On Input Values
function evaluateCommand() {
    commandArray.clear()

    for (let i = 0; i < wireQuantity.value; i++) {
        // Left Strip
        moveWire(leftStripLength.value);
        moveTopBlade((WIRE_Y_POSITION - recordedCurrentPosition) + stripDepth.value); // To Stripping Depth

        // Cut
        moveTopBlade(1000);
        moveWire(wireLength.value);
        moveTopBlade((WIRE_Y_POSITION - recordedCurrentPosition) + stripDepth.value); // To Stripping Depth

        // Right Strip
        moveTopBlade(1000);
        moveWire(rightStripLength.value);
        moveTopBlade((WIRE_Y_POSITION - recordedCurrentPosition)); // To Cutting Depth
    }
}


// Turns commandArray Into A Single Command String
function commandArrayToString() {
    let returnString = "";

    for (let i = 0; i < commandArray.length; i++) {
        returnString = returnString + "|" + commandArray[i]
    }

    return returnString;
}


function onSubmit() {
    console.log("Submit Button Clicked");

    evaluateCommand();
    command = commandArrayToString();

    client.publish(
        "hextech/hextech-justin/commands",
        "led.bl_off" //command
    );
}