from bluetooth import *
from sh import bluetoothctl
import time
import RPi.GPIO as GPIO
import Adafruit_DHT as dht

r_speaker = 23
r_fan = 24
sensor_ht = 4

GPIO.setmode(GPIO.BCM)
GPIO.setup(r_speaker, GPIO.OUT)
GPIO.setup(r_fan, GPIO.OUT)

server_socket= BluetoothSocket(RFCOMM)
GPIO.output(r_speaker, GPIO.HIGH)
GPIO.output(r_fan, GPIO.HIGH)
port = 1
server_socket.bind(("", port))
server_socket.listen(1)

client_socket, address = server_socket.accept()
print("Accepted connection from ", address)

MAC = address[0]
try:
    GPIO.output(r_speaker, GPIO.HIGH)
    GPIO.output(r_fan, GPIO.HIGH)
    while True:
        data = client_socket.recv(1024)
        data = data.decode("utf-8")

        if (data == "speaker_on"):
            GPIO.output(r_speaker, GPIO.LOW)
        elif (data == "speaker_off"):
            GPIO.output(r_speaker, GPIO.HIGH)
        elif (data == "fan_on"):
            GPIO.output(r_fan, GPIO.LOW)
        elif (data == "fan_off"):
            GPIO.output(r_fan, GPIO.HIGH)

        elif (data == "pair"):
            bluetoothctl("connect", MAC)

        elif (data == "ht"):
            send_data = list()
            send_data[0:1] = dht.read_retry(dht.AM2302,sensor_ht)
            send_data = [str(round(send_data[i],2)) for i in range(0,2)]
            print("humidity :" + send_data[0] + "Temperature :" + send_data[1])
            client_socket.send(send_data[0]+","+send_data[1])

        print("Received: %s" %data)
        if (data == "q"):
            print("Quit")
            client_socket.close()
            server_socket.close()
            GPIO.cleanup()
            break

except KeyboardInterrupt:
    client_socket.close()
    server_socket.close()
    GPIO.cleanup()
    sys.exit()

