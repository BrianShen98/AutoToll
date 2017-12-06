//PLEASE USE THIS VERSION FOR DEMO
#include <SPI.h>
#include <Adafruit_PN532.h>
#include <Wire.h>
#include <WiFi101.h>
//#include "arduino_secrets.h" 
#define SECRET_SSID "IEEE 2.4GHz"
//#define SECRET_SSID "UCLA_WEB_RES"
#define SECRET_PASS "Ilovesolder"

//#define SECRET_SSID "NETGEAR85"
//#define SECRET_PASS "whateverucla2016"

//Enter your Network SSID & PASSWORD IN arduino_secrets.h
char ssid[] = SECRET_SSID;        // your network SSID (name)
char pass[] = SECRET_PASS;    // your network password (use for WPA, or use as key for WEP)
int status = WL_IDLE_STATUS;     // the WiFi radio's status
int count=0;

//Server Parameters
byte mac[] = {
  0xDE, 0xAD, 0xBE, 0xEF, 0xFE, 0xED };
char server[] = "ec2-13-59-86-172.us-east-2.compute.amazonaws.com"; //DNS converted IP Address

//NFC Shield Setup
#define PN532_IRQ   (2)
#define PN532_RESET (3)
Adafruit_PN532 nfc(PN532_IRQ, PN532_RESET);


//Gate Controls
int waitlight = 3;
int greenlight = 5;
int INB=10;
int INA=9;
int value = 200;  //UNKNOWN YET, NEED TO TEST VALUE 
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
      //status = WiFi.begin(ssid); //Use if no password for your network
      status = WiFi.begin(ssid,pass);

    // wait 10 seconds for connection:
    delay(10000);
    }
      Serial.println(F("Connected!"));
      delay(1000);
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
            
      // configure board to read RFID tags
      nfc.SAMConfig();
      
      Serial.println(F("NF-Reader started!"));
}

void loop() {
  Serial.print(F("Please input your NFC tag to continue."));
  uint8_t success;
  uint8_t uid[] = { 0, 0, 0, 0, 0, 0, 0 };  // Buffer to store the returned UID
  uint8_t uidServer[] = { 0, 0, 0, 0, 0, 0, 0 };  // Buffer to store the returned UID
  uint8_t uidLength;                        // Length of the UID (4 or 7 bytes depending on ISO14443A card type)
    
  // Wait for an NTAG203 card.  When one is found 'uid' will be populated with
  // the UID, and uidLength will indicate the size of the UUID (normally 7)
  success = nfc.readPassiveTargetID(PN532_MIFARE_ISO14443A, uid, &uidLength);
  
  if(success)
      {
        Serial.println("Found a NFC tag");
        nfc.PrintHex(uid, uidLength);
        String myString= (char*)uid;
        Serial.println(" ");
        String hexuid[uidLength];
        for(count=0;count<uidLength;count++)
        {
          if(uid[count] < 16)
          {
            hexuid[count]=String(uid[count], HEX);
            hexuid[count]=String("0" + hexuid[count]); 
          }
          else
          {
            hexuid[count]=String(uid[count], HEX);
          }
        }
       String wholeuidsend=String(hexuid[0]+hexuid[1]+hexuid[2]+hexuid[3]+hexuid[4]+hexuid[5]+hexuid[6]);
       int stuff = checkMatch(wholeuidsend);
       if(stuff == 1)
          go=1;
       else
          go=0;
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
    client.flush();
    Serial.flush();
    Serial.flush();    
    delay(1000);
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
    //Make char array with 7 letters that has null character at entry 7
    char check[7]= {'S','U','C','C','E','S','S'};
    int counter=0;
    client.print("GET /check?");
    client.print("NFC_ID=");
    client.print(uid); // whole UID value in HEX to compare with server
    client.println(" HTTP/1.1"); // Part of the GET request
    //Change IP Address of Server Accordingly
    client.println("Host: ec2-13-59-86-172.us-east-2.compute.amazonaws.com"); 
    client.println("Connection: close"); 
    client.println(); // Empty line
    client.println(); // Empty line
    //reading the server's response for SUCCESS
    while(!client.available()){}
    while(client.available()>0)
    {
      char c=client.read();
      while(c == check[counter])
      {
        counter++;
        while(client.available() == 0) {} //give delay
        c=client.read();
        if(counter=7)
          return 1;
      }   
      counter=0;
    }
    client.stop();    // Closing connection to server
    return 0;
  }
  else {
    // If Arduino can't connect to the server (your computer or web page)
    Serial.println("--> Connection Failed\n");
    return 0;
  }
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


