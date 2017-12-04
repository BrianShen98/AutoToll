//PLEASE USE THIS VERSION FOR DEMO
#include <SPI.h>
#include <Adafruit_PN532.h>
#include <Wire.h>
#include <WiFi101.h>
//#include "arduino_secrets.h" 
#define SECRET_SSID "UCLA_WEB"
#define SECRET_PASS ""

//Enter your Network SSID & PASSWORD IN arduino_secrets.h
char ssid[] = SECRET_SSID;        // your network SSID (name)
char pass[] = SECRET_PASS;    // your network password (use for WPA, or use as key for WEP)
int status = WL_IDLE_STATUS;     // the WiFi radio's status
uint8_t match = 0;

//here is waht i add
uint8_t feedback=0;

//Server Parameters
byte mac[] = {
  0xDE, 0xAD, 0xBE, 0xEF, 0xFE, 0xED };
char server[] = "172.29.3.250"; //IP Address of Server

//NFC Shield Setup
#define PN532_IRQ   (2)
#define PN532_RESET (3)
Adafruit_PN532 nfc(PN532_IRQ, PN532_RESET);


//Gate Controls
int waitlight = 3;
int greenlight = 5;
int INB=10;
int INA=9;
int value = 100;  //UNKNOWN YET, NEED TO TEST VALUE 
int go=0;

 //Initialize the Wifi Client
  WiFiClient client;

void setup() {
  WiFi.setPins(8,7,4,2);
  pinMode(INB, OUTPUT);              //pin D10
  pinMode(INA, OUTPUT);              //pin D9
  pinMode(waitlight, OUTPUT);        //pin D0
  pinMode(greenlight, OUTPUT);       //pin D1  
  
  //digital ina and inb to low
  digitalWrite(INB, LOW);       
  digitalWrite(INA, LOW); 
  analogWrite(INA,0);
  analogWrite(INB,0);
  
  Serial.begin(9600);    
  //Set up the Wifi module
  // Initialise the Wifi module 
      Serial.println(F("\nInitializing..."));
        
        // attempt to connect to WiFi network:
    while ( status != WL_CONNECTED) {
      Serial.print(F("Attempting to connect to WPA SSID: "));
      Serial.println(ssid);
    // Connect to WPA/WPA2 network:
      status = WiFi.begin(ssid);

    // wait 10 seconds for connection:
    delay(10000);
    }
      Serial.println(F("Connected!"));
      printCurrentNet();
      printWiFiData();

      //Set up the NFC module
      Serial.println(F("Starting NF-Reader..."));
      nfc.begin();
      uint32_t versiondata = nfc.getFirmwareVersion();
      if (! versiondata) {
        Serial.print(F("Didn't find PN53x board"));
        while (1); // halt
      }
      // Got ok data, print it out!
      Serial.print(F("Found chip PN5")); Serial.println((versiondata>>24) & 0xFF, HEX); 
      Serial.print(F("Firmware ver. ")); Serial.print((versiondata>>16) & 0xFF, DEC); 
      Serial.print('.'); Serial.println((versiondata>>8) & 0xFF, DEC);
            
      // configure board to read RFID tags
      nfc.SAMConfig();
      
      Serial.println(F("NF-Reader started!"));
}

void loop() {
  while(go==0)
  {
  Serial.print(F("Please input your NFC tag to continue."));
  //digitalWrite(waitlight, HIGH);
  uint8_t success;
  uint8_t uid[] = { 0, 0, 0, 0, 0, 0, 0 };  // Buffer to store the returned UID
  uint8_t uidServer[] = { 0, 0, 0, 0, 0, 0, 0 };  // Buffer to store the returned UID
  uint8_t uidLength;                        // Length of the UID (4 or 7 bytes depending on ISO14443A card type)
    
  // Wait for an NTAG203 card.  When one is found 'uid' will be populated with
  // the UID, and uidLength will indicate the size of the UUID (normally 7)
  success = nfc.readPassiveTargetID(PN532_MIFARE_ISO14443A, uid, &uidLength);
  
  if(success)
      {
        Serial.println("Found an ISO14443A card");
        Serial.print("  UID Length: ");Serial.print(uidLength, DEC);Serial.println(" bytes");
        Serial.print("  UID Value: ");
        nfc.PrintHex(uid, uidLength);
        Serial.println("");
        /*
        //send the uid in byte form to server if the server is avaliable
        while(1)
        {
          if (client.connect(server, 80)) //Why port 80?
          {
            client.write(uid[1]); //second byte
            client.write(uid[2]); //third byte
            client.write(uid[6]); // seventh byte
            break;
          }
        }*/
        //switch uint8_t to char array 
        String myString= (char*)uid;
        
        //send the uid in char form to server if the server is avaliable
        /*while(1)
        {
          if (client.connect(server, 80)) //Why port 80?
          {
            client.write(myString[1]); //second element
            client.write(myString[2]); //third element
            client.write(myString[6]); // seventh element
            break;
          }
        }*/
        if(checkMatch(myString) == 1){
          go=1;
          break;
        }
        else
          go=0;
      }
  }
    while(go==1)
    {
      digitalWrite(waitlight, LOW);       // red light is off when the inforamtion is verified
      openGate();
      digitalWrite(greenlight, HIGH);       // green light is on when the inforamtion is verified
      delay(3000); //10s delay for driver to react
      go = 0; //reset go 
      closeGate();
    }
      // Wait a bit before trying again
    Serial.println(F("\n\nSend Another Tag!"));
    Serial.flush();
    Serial.flush();    
}

void printWiFiData() {
  // print your WiFi shield's IP address:
  IPAddress ip = WiFi.localIP();
  Serial.print(F("IP Address: "));
  Serial.println(ip);
  Serial.println(ip);

  // print your MAC address:
  byte mac[6];
  WiFi.macAddress(mac);
  Serial.print(F("MAC address: "));
  Serial.print(mac[5], HEX);
  Serial.print(F(":"));
  Serial.print(mac[4], HEX);
  Serial.print(F(":"));
  Serial.print(mac[3], HEX);
  Serial.print(F(":"));
  Serial.print(mac[2], HEX);
  Serial.print(F(":"));
  Serial.print(mac[1], HEX);
  Serial.print(F(":"));
  Serial.println(mac[0], HEX);

}

void openGate(){
    
    
    analogWrite(INA, value);
    digitalWrite(INB, LOW);       
    delay(50);
    analogWrite(INA, 0);
    digitalWrite(INB, LOW);       
    digitalWrite(INA, LOW);       
    Serial.println("You are fine to go. Have a nice day and a safe drive!"); 
}

void closeGate(){
  digitalWrite(INA, LOW);
  analogWrite(INB, value);       
  delay(50);
  analogWrite(INB, 0);
  digitalWrite(INB, LOW);       
  digitalWrite(INA, LOW);      
  digitalWrite(greenlight, LOW);       
}   

//This function checks for success from the server
int checkMatch(String uid){
  // Connect to the server (your computer or web page)  
  if (client.connect(server, 3000)) 
  {
    client.print("GET /check");
    client.print("NFC_ID="); 
    client.print(uid); // UID value to compare with server

    
    client.println(" HTTP/1.1"); // Part of the GET request
    //Change IP Address of Server Accordingly
    client.println("Host: 172.29.3.250"); 
    client.println("Connection: close"); 
    client.println(); // Empty line
    client.println(); // Empty line
    //reading the server's feedback
    feedback=client.read();
    client.stop();    // Closing connection to server
    //return 1;
    return feedback;
  }

  else {
    // If Arduino can't connect to the server (your computer or web page)
    Serial.println("--> connection failed\n");
    return 0;
  }
 
  // Give the server some time to recieve the data and store it. I used 10 seconds here. Be advised when delaying. If u use a short delay, the server might not capture data because of Arduino transmitting new data too soon.
  delay(10000);
}


void printCurrentNet() {
  // print the SSID of the network you're attached to:
  Serial.print(F("SSID: "));
  Serial.println(WiFi.SSID());

  // print the MAC address of the router you're attached to:
  byte bssid[6];
  WiFi.BSSID(bssid);
  Serial.print(F("BSSID: "));
  Serial.print(bssid[5], HEX);
  Serial.print(F(":"));
  Serial.print(bssid[4], HEX);
  Serial.print(F(":"));
  Serial.print(bssid[3], HEX);
  Serial.print(F(":"));
  Serial.print(bssid[2], HEX);
  Serial.print(F(":"));
  Serial.print(bssid[1], HEX);
  Serial.print(F(":"));
  Serial.println(bssid[0], HEX);

  // print the received signal strength:
  long rssi = WiFi.RSSI();
  Serial.print(F("signal strength (RSSI):"));
  Serial.println(rssi);

  // print the encryption type:
  byte encryption = WiFi.encryptionType();
  Serial.print(F("Encryption Type:"));
  Serial.println(encryption, HEX);
  Serial.println();
}

