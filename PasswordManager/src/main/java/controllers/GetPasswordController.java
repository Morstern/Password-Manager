package controllers;

import com.ericsson.otp.erlang.OtpErlangDecodeException;
import com.ericsson.otp.erlang.OtpErlangExit;
import erlang.ErlangServerConnector;
import exceptions.GetPasswordException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.input.ClipboardContent;
import javafx.scene.text.Text;
import javafx.scene.input.Clipboard;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class GetPasswordController  {

    ErlangServerConnector erlangServerConnector;

    private int id;

    @FXML
    private TextField serviceField;

    @FXML
    private TextField loginField;

    @FXML
    private TextField passwordField;

    @FXML
    private Text completionStatus;

    @FXML
    void getPassword(ActionEvent actionEvent) {
        try {
            erlangServerConnector = new ErlangServerConnector();
            Optional<String> password = erlangServerConnector.getPassword(String.valueOf(id), passwordField.getText());
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent clipboardContent = new ClipboardContent();
            clipboardContent.putString(password.get());
            clipboard.setContent(clipboardContent);
            ((Node)actionEvent.getSource()).getScene().getWindow().hide();
        } catch (GetPasswordException | IOException e) {
            completionStatus.setText(e.getMessage());
        } catch (OtpErlangExit otpErlangExit) {
            otpErlangExit.printStackTrace();
        } catch (OtpErlangDecodeException e) {
            e.printStackTrace();
        } finally{
            erlangServerConnector.closeConnections();
        }
    }

    public void initData(int id, String serviceName, String login){
        this.id=id;
        this.serviceField.setText(serviceName);
        this.loginField.setText(login);
    }

}
