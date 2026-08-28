package com.relay.client;

import java.io.IOException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * The entry-point class for the client app.
 * 
 * Responsible for initializing the networking thread that handles the 
 * connection to the server and bootstrapping the application UI.
 */
public class ChatClient extends Application {
    private final double SCENE_WIDTH = 1200;
    private final double SCENE_HEIGHT = 900;

    @Override
    public void start(Stage stage) throws IOException {
        ServerConnection connection = new ServerConnection();
        Thread netThread = new Thread(connection);
        netThread.start();
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource("chat.fxml"));
        Scene scene = new Scene(loader.load(), SCENE_WIDTH, SCENE_HEIGHT);
        ChatController controller = loader.getController();
        
        controller.setServerConnection(connection);
        connection.setOnMessageReceived(
            msg -> Platform.runLater(
                () -> controller.updateChatHistory(msg)
            )
        );

        stage.setTitle("Relay Client");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
