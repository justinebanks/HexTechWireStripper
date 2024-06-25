package justin;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MqttConnector 
{
    public String broker;
    public String username;
    public String password;

    public static void main( String[] args ) {
        new MqttTester();
    }

    public MqttConnector(String broker, String username, String password) {
        this.broker = broker;
        this.username = username;
        this.password = password;
    }


    public void publishMessage(String topic, String msg) {
        try {
            // Creates Connection
            MqttClient client = new MqttClient(this.broker, "demo_client");

            MqttConnectOptions options = new MqttConnectOptions();
            options.setUserName(this.username);
            options.setPassword(this.password.toCharArray());

            client.connect(options);

            if (client.isConnected()) {
                // Defines Functions Run At Specific States
                client.setCallback(new MqttCallback() {
                    public void messageArrived(String topic, MqttMessage message) throws Exception {
                        System.out.println("Topic: " + topic);
                        System.out.println("Command: " + new String(message.getPayload()));
                    }
                    
                    public void connectionLost(Throwable cause) {
                        System.out.println("connectionLost: " + cause.getMessage());
                    }
                    
                    public void deliveryComplete(IMqttDeliveryToken token) {
                        System.out.println("deliveryComplete: " + token.isComplete());
                    }
                });

                // Publishes Message
                MqttMessage message = new MqttMessage(msg.getBytes());
                message.setQos(1);
                client.publish(topic, message);
            }

            // Close Connection
            client.disconnect();
            client.close();
        }

        catch (MqttException e) {
            System.out.println(e);
        }
    }
}
