# AutoToll
This repository features the frontend and backend of the Android mobile application which is a component to the overall project. AutoToll is a mockup of an automatic payment system seen in various toll roads in freeways or highways, such as FasTrak. 

### Hardware:
- Arduino and motor to handle the opening and closing of the gate
- Sensor to detect the NFC sticker, send message to Arduino with WiFi component to the server, which does its server-side processing
- NFC sticker as a label/identification of the car, simulating a license plate

### Software:
- Android Studio as the development environment for the Android app
- Java and XML on the frontend and UI
  - Registration: User registers the car including the NFC ID, username of choice, and password of choice
  - Login: User who has registered can login using their credentials
  - Account Info: Screen contains the account balance as well as the username and deposit money feature
- NodeJS and SQL on the backend
  - Receives the NFC sticker ID which updates the database accordingly (deducting account balance)


### Contributors (last name alphabetical)
##### Hardware
- Zhaohao Mai
- Jeffrey Wang
##### Software
- Yaowei Guo
- Brian Shen
- Kyle Wong

