package justin;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

public class MqttTester extends JFrame implements ActionListener {

    SpringLayout layout = new SpringLayout();

    JTextField broker = new JTextField("ws://mqtt.hextronics.cloud:8083/mqtt");
    JTextField username = new JTextField("hextech-justin");
    JTextField password = new JTextField("justin");
    JTextField topic = new JTextField("hextech/hextech-justin/commands");
    JTextField msg = new JTextField("led.bl_on");

    JLabel brokerLabel = new JLabel("Broker:");
    JLabel userLabel = new JLabel("Username:");
    JLabel pwdLabel = new JLabel("Password:");
    JLabel topicLabel = new JLabel("Topic: ");
    JLabel msgLabel = new JLabel("Message:");

    JButton sendButton = new JButton("Send Message");

    
    public MqttTester() {
        this.setSize(540, 360);
        this.setLocationRelativeTo(null);
        this.setLayout(layout);

        configureComponents();
        manageLayout();

        this.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == sendButton) {
            MqttConnector mqttc = new MqttConnector(broker.getText(), username.getText(), password.getText());
            mqttc.publishMessage(topic.getText(), msg.getText());
        }
    }

    private void configureComponents() {
        sendButton.addActionListener(this);

        Dimension preferredSize = new Dimension(200, 20);
        broker.setPreferredSize(preferredSize);
        username.setPreferredSize(preferredSize);
        password.setPreferredSize(preferredSize);
        topic.setPreferredSize(preferredSize);
        msg.setPreferredSize(preferredSize);

        this.add(broker);
        this.add(username);
        this.add(password);
        this.add(topic);
        this.add(msg);

        this.add(brokerLabel);
        this.add(userLabel);
        this.add(pwdLabel);
        this.add(topicLabel);
        this.add(msgLabel);

        this.add(sendButton);
    }

    private void manageLayout() {
        layout.putConstraint(SpringLayout.NORTH, brokerLabel, 40, SpringLayout.NORTH, this);
        layout.putConstraint(SpringLayout.WEST, brokerLabel, 80, SpringLayout.WEST, this);
        layout.putConstraint(SpringLayout.NORTH, broker, 0, SpringLayout.NORTH, brokerLabel);
        layout.putConstraint(SpringLayout.WEST, broker, 50, SpringLayout.EAST, brokerLabel);

        layout.putConstraint(SpringLayout.NORTH, userLabel, 40, SpringLayout.NORTH, brokerLabel);
        layout.putConstraint(SpringLayout.WEST, userLabel, 0, SpringLayout.WEST, brokerLabel);
        layout.putConstraint(SpringLayout.NORTH, username, 0, SpringLayout.NORTH, userLabel);
        layout.putConstraint(SpringLayout.WEST, username, 0, SpringLayout.WEST, broker);

        layout.putConstraint(SpringLayout.NORTH, pwdLabel, 40, SpringLayout.NORTH, userLabel);
        layout.putConstraint(SpringLayout.WEST, pwdLabel, 0, SpringLayout.WEST, brokerLabel);
        layout.putConstraint(SpringLayout.NORTH, password, 0, SpringLayout.NORTH, pwdLabel);
        layout.putConstraint(SpringLayout.WEST, password, 0, SpringLayout.WEST, broker);

        layout.putConstraint(SpringLayout.NORTH, topicLabel, 40, SpringLayout.NORTH, pwdLabel);
        layout.putConstraint(SpringLayout.WEST, topicLabel, 0, SpringLayout.WEST, brokerLabel);
        layout.putConstraint(SpringLayout.NORTH, topic, 0, SpringLayout.NORTH, topicLabel);
        layout.putConstraint(SpringLayout.WEST, topic, 0, SpringLayout.WEST, broker);

        layout.putConstraint(SpringLayout.NORTH, msgLabel, 40, SpringLayout.NORTH, topicLabel);
        layout.putConstraint(SpringLayout.WEST, msgLabel, 0, SpringLayout.WEST, brokerLabel);
        layout.putConstraint(SpringLayout.NORTH, msg, 0, SpringLayout.NORTH, msgLabel);
        layout.putConstraint(SpringLayout.WEST, msg, 0, SpringLayout.WEST, broker);

        layout.putConstraint(SpringLayout.NORTH, sendButton, 50, SpringLayout.NORTH, msgLabel);
        layout.putConstraint(SpringLayout.WEST, sendButton, 0, SpringLayout.WEST, brokerLabel);
    }

}
