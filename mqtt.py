import time
import paho.mqtt.client as mqtt

def on_publish(client, userdata, mid, reason_code, properties):
    print("Published")


unacked_publish = set()
mqttc = mqtt.Client(mqtt.CallbackAPIVersion.VERSION2)
mqttc.on_publish = on_publish

mqttc.user_data_set(unacked_publish)
mqttc.username_pw_set(username="hextech-justin",password="justin")
mqttc.connect("mqtt.hextronics.cloud", 1883)
mqttc.loop_start()

# Our application produce some messages
msg_info = mqttc.publish("hextech/hextech-justin/commands", "led.bl_on", qos=1)
unacked_publish.add(msg_info.mid)

msg_info2 = mqttc.publish("hextech/hextech-justin/commands", "led.wh_on", qos=1)
unacked_publish.add(msg_info2.mid)

# Wait for all message to be published
while len(unacked_publish):
    time.sleep(0.1)

# Due to race-condition described above, the following way to wait for all publish is safer
msg_info.wait_for_publish()
msg_info2.wait_for_publish()

mqttc.disconnect()
mqttc.loop_stop()