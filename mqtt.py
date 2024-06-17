import time
import paho.mqtt.client as mqtt

USERNAME = "hextech-justin"
PASSWORD = "justin"
TOPIC = "hextech/hextech-justin/commands"
COMMANDS = ["led.wh_off", "led.bl_off"]


def on_publish(client, userdata, mid, reason_code, properties):
    try:
        print("Published")
        userdata.remove(mid)
    except KeyError:
        print("Got Key Error?")


def on_connect(client, userdata, flags, reason_code, properties):
    print("Connected")
    client.subscribe("$SYS/#")


unacked_publish = set()

mqttc = mqtt.Client(mqtt.CallbackAPIVersion.VERSION2)
mqttc.on_publish = on_publish
mqttc.on_connect = on_connect

mqttc.user_data_set(unacked_publish)
mqttc.username_pw_set(username=USERNAME,password=PASSWORD)
mqttc.connect("mqtt.hextronics.cloud", 1883)
mqttc.loop_start()

for command in COMMANDS:
    # Our application produce some messages
    msg_info = mqttc.publish(TOPIC, command)
    unacked_publish.add(msg_info.mid)

    # Safe way to wait for all publishes
    msg_info.wait_for_publish()


# Wait for all message to be published
while len(unacked_publish):
    time.sleep(0.1)


mqttc.disconnect()
mqttc.loop_stop()
