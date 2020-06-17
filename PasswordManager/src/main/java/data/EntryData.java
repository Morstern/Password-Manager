package data;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Button;

public class EntryData {
    private final SimpleIntegerProperty id;
    private final SimpleStringProperty serviceName;
    private final SimpleStringProperty login;
    private Button deletePasswordButton;
    private Button getPasswordButton;

    public EntryData(Integer id, String serviceName, String login) {

        this.id = new SimpleIntegerProperty(id);
        this.serviceName = new SimpleStringProperty(serviceName);
        this.login = new SimpleStringProperty(login);
        this.deletePasswordButton = new Button("Usuń wpis");
        this.getPasswordButton = new Button("Pobierz");
    }

    public String getServiceName() {
        return serviceName.get();
    }

    public SimpleStringProperty serviceNameProperty() {
        return serviceName;
    }

    public String getLogin() {
        return login.get();
    }

    public int getId() {
        return id.get();
    }

    public SimpleIntegerProperty idProperty() {
        return id;
    }

    public SimpleStringProperty loginProperty() {
        return login;
    }

    public Button getDeletePasswordButton() {
        return deletePasswordButton;
    }

    public Button getGetPasswordButton() {
        return getPasswordButton;
    }

    @Override
    public String toString() {
        return "EntryData{" +
                "id=" + id +
                ", serviceName=" + serviceName +
                ", login=" + login +
                '}';
    }

}
