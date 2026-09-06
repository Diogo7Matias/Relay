# Relay
 
A native chat application built in Java, using raw TCP sockets for
networking, JavaFX for the desktop client UI and SQLite for persistence.
 
## Features
 
- Real-time messaging over TCP sockets (custom JSON-based message protocol)
- Message history persisted in SQLite and loaded when a client needs it
- Automatic server discovery on a local network via UDP broadcast
- A JavaFX desktop client

## Building
 
From the project root:
 
```
mvn compile
```
 
## Running
 
The server and client are separate entry points and need to be started independently.
 
**Start the server:**
 
```
mvn exec:java -Dexec.mainClass="com.relay.server.ChatServer"
```
 
**Start a client:**
 
```
mvn javafx:run
```
 
You can start multiple clients on the same machine, or on other devices on the same local
network (the client will find the server automatically).
