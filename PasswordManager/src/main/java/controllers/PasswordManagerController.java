package controllers;


import erlang.ErlangServerConnector;
import com.ericsson.otp.erlang.OtpErlangDecodeException;
import com.ericsson.otp.erlang.OtpErlangExit;
import data.EntryData;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class PasswordManagerController implements Initializable {

    private ObservableList<EntryData> data = FXCollections.observableArrayList();

    @FXML
    private TableView<EntryData> passwordTable = new TableView<>();

    @FXML
    private TableColumn<EntryData, String> serviceNameColumn = new TableColumn<>();

    @FXML
    private TableColumn<EntryData, String> loginColumn= new TableColumn<>();

    @FXML
    private TableColumn<EntryData, Button> deletePasswordColumn= new TableColumn<>();

    @FXML
    private TableColumn<EntryData, Button> getPasswordColumn= new TableColumn<>();

    @FXML
    void addPassword(ActionEvent event) throws IOException, OtpErlangExit, OtpErlangDecodeException, InterruptedException {
        Stage stage = new Stage();
        Parent root = FXMLLoader.load(AddPasswordController.class.getClassLoader().getResource("views/AddPassword.fxml"));
        stage.setScene(new Scene(root));
        stage.setTitle("Dodaj wpis");
        stage.initModality(Modality.WINDOW_MODAL);
        stage.show();

        stage.setOnHiding((windowEvent)-> {
            try {
                fetchPasswords();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (OtpErlangExit otpErlangExit) {
                otpErlangExit.printStackTrace();
            } catch (OtpErlangDecodeException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            fetchPasswords();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (OtpErlangExit otpErlangExit) {
            otpErlangExit.printStackTrace();
        } catch (OtpErlangDecodeException e) {
            e.printStackTrace();
        }
        initializeColumns();
    }

    private void fetchPasswords() throws IOException, OtpErlangExit, OtpErlangDecodeException {
        ErlangServerConnector erlangServerConnector = new ErlangServerConnector();
        data = FXCollections.observableArrayList();
        data.addAll(erlangServerConnector.getData());

        for (EntryData ed: data) {
            ed.getGetPasswordButton().setOnAction((e)-> {
                try {
                    addEntryButtonAction(ed.getId(),ed.getServiceName(), ed.getLogin());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });

            ed.getDeletePasswordButton().setOnAction((e)-> {
                try {
                    removeEntryButtonAction(ed.getId());
                } catch (IOException | OtpErlangExit | OtpErlangDecodeException ex) {
                    ex.printStackTrace();
                }
            });
        }
        passwordTable.setItems(data);
    }



    private void addEntryButtonAction(int id, String serviceName, String login) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/GetPassword.fxml"));
        Parent root = loader.load();

        GetPasswordController controller = loader.getController();
        controller.initData(id, serviceName, login);


        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Dodaj wpis");
        stage.initModality(Modality.WINDOW_MODAL);
        stage.show();

    }

    private void removeEntryButtonAction(int id) throws IOException, OtpErlangExit, OtpErlangDecodeException {
        ErlangServerConnector erlangServerConnector = new ErlangServerConnector();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Usuń wpis");
        alert.setHeaderText("");
        alert.setContentText("Czy na pewno chcesz usunąć?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            erlangServerConnector.removePassword(id);
            fetchPasswords();
        } else {

        }
    }

    private void initializeColumns() {
        serviceNameColumn.setCellValueFactory(
                new PropertyValueFactory<EntryData, String>("serviceName")
        );
        loginColumn.setCellValueFactory(
                new PropertyValueFactory<EntryData, String>("login")
        );
        deletePasswordColumn.setCellValueFactory(
                new PropertyValueFactory<EntryData, Button>("deletePasswordButton")
        );
        getPasswordColumn.setCellValueFactory(
                new PropertyValueFactory<EntryData, Button>("getPasswordButton")
        );
    }

}
