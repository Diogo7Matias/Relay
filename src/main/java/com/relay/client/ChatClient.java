package com.relay.client;

import java.io.IOException;

import com.relay.client.controller.ChatController;
import com.relay.client.controller.LoginController;
import com.relay.client.net.ServerConnection;

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

    private Stage stage;
    private final ServerConnection svConnection = new ServerConnection();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        Thread netThread = new Thread(svConnection);
        netThread.start();
        
        this.stage = stage;
        displayLoginView();
    }

    private void displayLoginView() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
        Scene scene = new Scene(loader.load(), SCENE_WIDTH, SCENE_HEIGHT);
        LoginController controller = loader.getController();

        controller.setServerConnection(svConnection);
        svConnection.setOnUsernameChosen(
            msg -> Platform.runLater(
                () -> displayChatView()
            )
        );
        svConnection.setOnErrorReceived(
            msg -> Platform.runLater(
                () -> controller.displayErrorMessage(msg)
            )
        );
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());

        this.stage.setTitle("Relay - Log In");
        this.stage.setScene(scene);
        this.stage.show();
    }

    private void displayChatView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("chat.fxml"));
            Scene scene = new Scene(loader.load(), SCENE_WIDTH, SCENE_HEIGHT);
            ChatController controller = loader.getController();
            
            controller.setServerConnection(svConnection);
            svConnection.setOnMessageReceived(
                msg -> Platform.runLater(
                    () -> controller.updateChatHistory(msg)
                )
            );
            scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());

            this.stage.setTitle("Relay Client");
            this.stage.setScene(scene);
        } catch (IOException e) {
            System.err.println("Failed to load chat view.\n" + e.getMessage());
        }
    }

    @Override
    public void stop() {
        svConnection.closeConnection();
    }
}
