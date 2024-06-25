# HexTechAutoWireStripper
---

### Explanation
This project is a machine built to automatically cut and strip wires. This project was inspired by Programming with Red's version of it, but has been changed to work with the HexTech circuit board.

### MQTT Server
The HexTech circuit board is subscribed to an [MQTT server](https://mqtt.org/) in the cloud. The software can send publish messages to the server and the HexTech board's ESP32 microcontroller will recieve it and run the command specified. In Javascript, I used the MQTT.js CDN to very easily connect to the server.

The problem, though, was that the HexTech board wouldn't recieve a command if the command string contained too many commands. To bypass this, I had to send bits of the commands separated by 2 second delays. To do this, I translated my code to Java and used Java Swing instead of HTML, CSS, & Javascript. Because of this the full finished project is within the src/java directory

### Project Organization
The src directory contains to main parts: web and java. The user interface for this project was meant to be in the form of a website, but a problem with the MQTT Server led me to switch to Java.

The src/java direcory is a Maven project that can be downloaded and built with the VSCode "Extension Pack for Java" plugin. Any YouTube tutorial on Maven in VSCode will help get you started.

The src/web directory contains the old relic that is the Javascript project. If you open it in the browser, though, you can accessGoogle Chrome's DevTools Console by pressing Ctrl+Shift+I and navigating to the console window. This gives you access to 2 important functions for debugging: ```moveTopBlade(int)``` and ```moveWire(int)```. If the 2 linear motion motors become unbalanced, it can be fixed by running ```runSerialCommand("stepper.00_move_400")``` to move the left side down to use the other 2 functions, you need to pass their output to the ```runSerialCommand(String)``` function

### Testing and Debugging
In addition to using the DevTools Console to debug, the Java Project has an MqttTester class, which is instatiating in the MqttConnector file. By running MqttConnector.java, it will open a simple GUI application where you can manually send commands to the HexTech board through the MQTT server.
