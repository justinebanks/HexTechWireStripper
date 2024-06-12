
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


function moveTopBlade(distance) {
    dist = LIN_MOT_STEP_DIST * distance;
    return `${LIN_MOT_ONE}_move_${distance};${LIN_MOT_TWO}_move_${dist}`;
}

function moveWire(distance) {
    dist = EXTRUDER_STEP_DIST * distance;
    return `${EXTRUDER_STEPPER}_move_${dist}`;
}


function evaluateCommand() {
    moveWire(leftStripLength.value);
    moveTopBlade((WIRE_Y_POSITION - recordedCurrentPosition) + stripDepth);
    moveTopBlade(1000);
    
}


function onSubmit() {
    console.log("Submit Button Clicked");
    command = evaluateCommand();

    client.publish(
        "hextech/hextech-justin/commands",
        "led.bl_off"
    );
}