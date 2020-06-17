import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;


public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource("views/PasswordManager.fxml"));
        GridPane gridpane = loader.load();
        Scene scene = new Scene(gridpane);
        stage.setResizable(false);
        stage.setTitle("PasswordManger");
        stage.setScene(scene);
        stage.show();
    }

}

