package justin;

import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.JButton;
import javax.swing.JLabel;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;


public class App extends JFrame implements ActionListener {

    final String LIN_MOT_ONE = "stepper.00"; // rywb4-rwyb6
    final String LIN_MOT_TWO = "stepper.01"; // bwyr4-rwyb6
    final String EXTRUDER_STEPPER = "stepper.02"; // rywb4-rywb6

    final int LIN_MOT_STEP_DIST = 1;
    final int EXTRUDER_STEP_DIST = 1;
    final int BOTTOM_BLADE_POSITION = -7000;

    ArrayList<String> commandArray = new ArrayList<String>();
    ArrayList<Integer> bladeHistory = new ArrayList<Integer>();

    // MQTT Information
    MqttConnector mqtt = new MqttConnector("ws://mqtt.hextronics.cloud:8083/mqtt", "hextech-justin", "justin");
    String mqttTopic = "hextech/hextech-justin/commands";

    // Global Spring Components
    SpringLayout layout = new SpringLayout();
    JButton submitButton = new JButton("Submit");

    JTextField inputBox = new JTextField();
    JTextField leftStripLength = new JTextField();
    JTextField wireLength = new JTextField();
    JTextField rightStripLength = new JTextField();
    JTextField stripDepth = new JTextField();
    JTextField wireQuantity = new JTextField();

    JLabel lslLabel = new JLabel("Left Strip Length:");
    JLabel wlLabel = new JLabel("Wire Length:");
    JLabel rslLabel = new JLabel("Right Strip Length:");
    JLabel sdLabel = new JLabel("Strip Depth:");
    JLabel wqLabel = new JLabel("Wire Quantity:");

    public static void main(String[] args) {
        App app = new App();
        String message = app.moveTopBlade(-7000);

        app.initializeCurrent();
        app.mqtt.publishMessage(app.mqttTopic, message);
    }


    public App() {
        this.setPreferredSize(new Dimension(540, 360));
        this.setLayout(layout);
        this.setLocationRelativeTo(null);

        Dimension preferredSize = new Dimension(200, 20);
        leftStripLength.setPreferredSize(preferredSize);
        wireLength.setPreferredSize(preferredSize);
        rightStripLength.setPreferredSize(preferredSize);
        stripDepth.setPreferredSize(preferredSize);
        wireQuantity.setPreferredSize(preferredSize);

        submitButton.addActionListener(this);

        addComponents();
        manageLayout();

        this.pack();
        this.setVisible(true);
    }


    private void manageLayout() {
        layout.putConstraint(SpringLayout.NORTH, lslLabel, 40, SpringLayout.NORTH, this);
        layout.putConstraint(SpringLayout.WEST, lslLabel, 80, SpringLayout.WEST, this);
        layout.putConstraint(SpringLayout.NORTH, leftStripLength, 0, SpringLayout.NORTH, lslLabel);
        layout.putConstraint(SpringLayout.WEST, leftStripLength, 50, SpringLayout.EAST, lslLabel);

        layout.putConstraint(SpringLayout.NORTH, wlLabel, 40, SpringLayout.NORTH, lslLabel);
        layout.putConstraint(SpringLayout.WEST, wlLabel, 0, SpringLayout.WEST, lslLabel);
        layout.putConstraint(SpringLayout.NORTH, wireLength, 0, SpringLayout.NORTH, wlLabel);
        layout.putConstraint(SpringLayout.WEST, wireLength, 0, SpringLayout.WEST, leftStripLength);

        layout.putConstraint(SpringLayout.NORTH, rslLabel, 40, SpringLayout.NORTH, wlLabel);
        layout.putConstraint(SpringLayout.WEST, rslLabel, 0, SpringLayout.WEST, lslLabel);
        layout.putConstraint(SpringLayout.NORTH, rightStripLength, 0, SpringLayout.NORTH, rslLabel);
        layout.putConstraint(SpringLayout.WEST, rightStripLength, 0, SpringLayout.WEST, leftStripLength);

        layout.putConstraint(SpringLayout.NORTH, sdLabel, 40, SpringLayout.NORTH, rslLabel);
        layout.putConstraint(SpringLayout.WEST, sdLabel, 0, SpringLayout.WEST, lslLabel);
        layout.putConstraint(SpringLayout.NORTH, stripDepth, 0, SpringLayout.NORTH, sdLabel);
        layout.putConstraint(SpringLayout.WEST, stripDepth, 0, SpringLayout.WEST, leftStripLength);

        layout.putConstraint(SpringLayout.NORTH, wqLabel, 40, SpringLayout.NORTH, sdLabel);
        layout.putConstraint(SpringLayout.WEST, wqLabel, 0, SpringLayout.WEST, lslLabel);
        layout.putConstraint(SpringLayout.NORTH, wireQuantity, 0, SpringLayout.NORTH, wqLabel);
        layout.putConstraint(SpringLayout.WEST, wireQuantity, 0, SpringLayout.WEST, leftStripLength);

        layout.putConstraint(SpringLayout.NORTH, submitButton, 50, SpringLayout.NORTH, wqLabel);
        layout.putConstraint(SpringLayout.WEST, submitButton, 0, SpringLayout.WEST, lslLabel);
    }


    private void addComponents() {
        this.add(lslLabel);
        this.add(wlLabel);
        this.add(rslLabel);
        this.add(sdLabel);
        this.add(wqLabel);

        this.add(leftStripLength);
        this.add(wireLength);
        this.add(rightStripLength);
        this.add(stripDepth);
        this.add(wireQuantity);

        this.add(submitButton);
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        runProgram();
    }


    public void initializeCurrent() {
        mqtt.publishMessage(mqttTopic, "stepper.00_rms_600;stepper.01_rms_600;stepper.02_rms_200");
    }


    public void emergencyStop() {
        mqtt.publishMessage(mqttTopic, "P_stepper.00_stop;P_stepper.01_stop;P_stepper.02_stop");
    }


    public int getBladeRelativePosition() {
        int distFromHome = 0;
    
        for (int i = 0; i < bladeHistory.size(); i++) {
            distFromHome += bladeHistory.get(i);
        }
    
        return distFromHome;
    }


    public void goHome() {
        mqtt.publishMessage(mqttTopic, moveTopBlade(-getBladeRelativePosition()));
        bladeHistory.clear();
    }


    public String commandArrayToString() {
        String returnString = "";
    
        for (int i = 0; i < commandArray.size(); i++) {
            returnString = returnString + "|" + commandArray.get(i);
        }
    
        return returnString;
    }


    public String moveTopBlade(int distance) { // Positive for Up, Negative for Down
        int dist = LIN_MOT_STEP_DIST * distance;
        String command = String.format("%1$s_move_%2$d;%3$s_move_%4$d", LIN_MOT_ONE, dist*-1, LIN_MOT_TWO, dist);
    
        commandArray.add(command);
        bladeHistory.add(distance);
    
        return command;
    }


    public String moveWire(int distance) { // Positive for Forward, Negative for Backward
        int dist = EXTRUDER_STEP_DIST * distance;
        String command = String.format("%1$s_move_%2$d", EXTRUDER_STEPPER, dist);
        commandArray.add(command);
    
        return command;
    }


    public String cutWire() {
        return moveTopBlade(BOTTOM_BLADE_POSITION - getBladeRelativePosition());
    }
    
    
    public String stripWire() {
        return moveTopBlade(BOTTOM_BLADE_POSITION - getBladeRelativePosition() + Integer.parseInt(stripDepth.getText()));
    }
    
    
    public void testSequence() {
        commandArray.clear();
        moveWire(2000);
        moveTopBlade(-5000);
        moveWire(-1500);
        moveTopBlade(5000);

        mqtt.publishMessage(mqttTopic, commandArrayToString());
    }


    public void runProgram() {
        commandArray.clear();
    
        moveWire(Integer.parseInt(leftStripLength.getText()));
        stripWire();
        moveTopBlade(2000);
    
        moveWire(Integer.parseInt(wireLength.getText()));
        stripWire();
        moveTopBlade(2000);
    
        moveWire(Integer.parseInt(rightStripLength.getText()));
        cutWire();
        moveTopBlade(1000);
    
        mqtt.publishMessage(mqttTopic, commandArrayToString());
    }
}
