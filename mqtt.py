import time
import paho.mqtt.client as mqtt

def on_publish(client, userdata, mid, reason_code, properties):
    # reason_code and properties will only be present in MQTTv5. It's always unset in MQTTv3
    try:
        print("Published")
        userdata.remove(mid)
    except KeyError:
        print("on_publish() is called with a mid not present in unacked_publish")
        print("This is due to an unavoidable race-condition:")
        print("* publish() return the mid of the message sent.")
        print("* mid from publish() is added to unacked_publish by the main thread")
        print("* on_publish() is called by the loop_start thread")
        print("While unlikely (because on_publish() will be called after a network round-trip),")
        print(" this is a race-condition that COULD happen")
        print("")
        print("The best solution to avoid race-condition is using the msg_info from publish()")
        print("We could also try using a list of acknowledged mid rather than removing from pending list,")
        print("but remember that mid could be re-used !")


def on_connect(client, userdata, flags, reason_code, properties):
    print("Connected")
    client.subscribe("$SYS/#")

unacked_publish = set()
mqttc = mqtt.Client(mqtt.CallbackAPIVersion.VERSION2)
mqttc.on_publish = on_publish
mqttc.on_connect = on_connect

mqttc.user_data_set(unacked_publish)
mqttc.username_pw_set(username="hextech-justin",password="justin")
mqttc.connect("mqtt.hextronics.cloud", 8083)
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


'''
mqttc = mqtt.Client(mqtt.CallbackAPIVersion.VERSION2)


def on_connect(client, userdata, flags, reason_code, properties):
    print("Connected")
    client.subscribe("$SYS/#")

def on_message(client, userdata, msg):
    print(client)
    print(msg)


mqttc.on_connect = on_connect
mqttc_on_message = on_message
mqttc.connect("mqtt.hextronics.cloud", 8083)

mqttc.username_pw_set(username="hextech-justin",password="justin")
#mqttc.publish("hextech/hextech-justin/commands", "led.bl_on")

mqttc.loop_forever()
'''