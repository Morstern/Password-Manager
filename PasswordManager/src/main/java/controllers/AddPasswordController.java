package controllers;

import erlang.ErlangServerConnector;
import com.ericsson.otp.erlang.OtpErlangDecodeException;
import com.ericsson.otp.erlang.OtpErlangExit;
import data.EntryData;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AddPasswordController implements Initializable {

    ErlangServerConnector erlangServerConnector;

    @FXML
    private TextField serviceNameField;

    @FXML
    private TextField passwordField;

    @FXML
    private TextField loginField;

    @FXML
    void addEntry(ActionEvent actionEvent) {
        try {
            erlangServerConnector.createPassword(serviceNameField.getText(), loginField.getText(), passwordField.getText());
            ((Node)actionEvent.getSource()).getScene().getWindow().hide();
        } catch (OtpErlangExit otpErlangExit) {
            otpErlangExit.printStackTrace();
        } catch (OtpErlangDecodeException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            erlangServerConnector.closeConnections();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            initializeErlangConnector();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initializeErlangConnector() throws IOException{
        erlangServerConnector = new ErlangServerConnector();
    }
}
