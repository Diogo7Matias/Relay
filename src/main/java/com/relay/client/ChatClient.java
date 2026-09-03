package com.relay.client;

import java.io.IOException;
import java.util.function.Consumer;

import com.relay.client.controller.HomepageController;
import com.relay.client.controller.LoginController;
import com.relay.client.controller.ViewController;
import com.relay.client.model.ChatSummary;
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
    private final String DEFAULT_TITLE = "Relay Client";

    private Stage stage;
    private final ServerConnection svConnection = new ServerConnection();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        this.stage = stage;

        svConnection.setOnServerStatusChange(
            isUp -> Platform.runLater(() -> {
                if (isUp) {
                    displayLoginView();
                } else {
                    displayServerDownView();
                }
            })
        );

        if (svConnection.connect()) {
            Thread netThread = new Thread(svConnection);
            netThread.start();
            displayLoginView();
        }
    }

    /**
     * Display the view defined in the fxml file provided.
     * 
     * @param fxml a path to the fxml file
     * @param title the title of the view
     * @return the view controller or null if the method fails to load the view
     */
    private <T extends ViewController> T displayView(String fxml, String title) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));

        try {
            Scene scene = new Scene(loader.load(), SCENE_WIDTH, SCENE_HEIGHT);
            T controller = loader.getController();

            controller.setServerConnection(svConnection);
            scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
            
            this.stage.setTitle(title);
            this.stage.setScene(scene);
            this.stage.show();
            return controller;
        } catch (IOException e) {
            System.err.println("Failed to load view <" + fxml + ">: " + e.getMessage());
            return null;
        }
    }

    private void displayLoginView() {
        LoginController controller = displayView("login.fxml", "Relay - Log In");
        controller.setOnLoginSuccess(() -> Platform.runLater(() -> displayHomeView()));
    }

    private void displayHomeView() {
        HomepageController controller = displayView("homepage.fxml", DEFAULT_TITLE);
        
        Consumer<String> newChatCallback = chatID -> Platform.runLater(() -> {
                ChatSummary chatSummary = new ChatSummary(chatID);
                controller.updateChatsList(chatSummary);
            }
        );
        controller.setOnNewChat(newChatCallback);
        svConnection.setOnChatCreated(newChatCallback);
        controller.setOnJoinChat(chatID -> Platform.runLater(() -> controller.openChatSection(chatID)));
    }
    
    private void displayServerDownView() {
        displayView("server-down.fxml", DEFAULT_TITLE);
    }

    @Override
    public void stop() {
        svConnection.closeConnection();
    }
}
