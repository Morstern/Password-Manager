package erlang;

import com.ericsson.otp.erlang.*;
import data.EntryData;
import exceptions.GetPasswordException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ErlangServerConnector {

        private String ownNodeName = "javaserver@KAMIL";
        private String ownMailbox = "java";
        private String cookie = "password";

        private String anotherNode = "server";
        private String anotherService = "passwordmanager";

        private OtpErlangPid ownPid;

        private OtpNode otpNode;
        private OtpMbox otpMbox;

        private OtpErlangAtom operation;
        private OtpErlangObject msg;
        private OtpErlangObject reply;

    public ErlangServerConnector() throws IOException {
        otpNode = new OtpNode(ownNodeName, cookie);
        otpMbox = otpNode.createMbox(ownMailbox);
        ownPid = otpMbox.self();
    }


    public List<EntryData> getData() throws OtpErlangExit, OtpErlangDecodeException, IOException {
        List<EntryData> data = new ArrayList<>();
        operation = new OtpErlangAtom("getData");
        
        OtpErlangObject[] messageElements = {ownPid, operation, new OtpErlangString("")};
        msg = new OtpErlangTuple(messageElements);

        otpMbox.send(anotherService, anotherNode, msg);

        reply = otpMbox.receive(1000);

        if (reply instanceof OtpErlangString) {
            otpMbox.close();
            otpNode.close();
            return new ArrayList<>();
        } else {
            OtpErlangList response = (OtpErlangList) reply;
            for (OtpErlangObject object : response) {
                data.add(new EntryData(
                        Integer.valueOf(((OtpErlangTuple) object).elementAt(0).toString().replace("\"", "")),
                        ((OtpErlangTuple) object).elementAt(1).toString().replace("\"",""),
                        ((OtpErlangTuple) object).elementAt(2).toString().replace("\"",""))
                );
            }
            otpMbox.close();
            otpNode.close();
            return data;
        }
    }

    public Optional<String> getPassword(String id, String passwordTry) throws GetPasswordException, OtpErlangExit, OtpErlangDecodeException {
        Optional<String> password;
        operation = new OtpErlangAtom("getPassword");

        OtpErlangString _id = new OtpErlangString(id);
        OtpErlangString _password = new OtpErlangString(passwordTry);
        OtpErlangObject[] dataElements = {_id, _password};

        OtpErlangObject[] messageElements = { ownPid, operation, new OtpErlangTuple(dataElements)};
        msg = new OtpErlangTuple(messageElements);

        // {server, 'passwordmanager@KAMIL'} ! {..., ..., {..., ...}}
        otpMbox.send(anotherService, anotherNode, msg);

        reply = otpMbox.receive(1000);

        OtpErlangTuple response = (OtpErlangTuple)reply;

        String result = response.elementAt(0).toString().replace("\"","");

        if(result.equals("not_found")){
            throw new GetPasswordException("Nie znaleziono danych");
        }else if(result.equals("uncorrect")){
            throw new GetPasswordException("Niepoprawne hasło");
        }else{
            password = Optional.of( response.elementAt(1).toString().replace("\"",""));
        }
        return password;
    }


    public EntryData createPassword(String serviceName, String login, String password) throws OtpErlangExit, OtpErlangDecodeException, IOException {
        operation = new OtpErlangAtom("createPassword");

        OtpErlangString _serviceName = new OtpErlangString(serviceName);
        OtpErlangString _login = new OtpErlangString(login);
        OtpErlangString _password = new OtpErlangString(password);
        OtpErlangObject[] dataElements = {_serviceName, _login, _password};

        OtpErlangObject[] messageElements = { ownPid, operation, new OtpErlangTuple(dataElements)};
        msg = new OtpErlangTuple(messageElements);
        otpMbox.send(anotherService, anotherNode, msg);

        reply = otpMbox.receive(1000);


        OtpErlangTuple response = (OtpErlangTuple)reply;
        otpMbox.close();
        otpNode.close();
        return new EntryData(Integer.valueOf(response.elementAt(0).toString().replace("\"","")), response.elementAt(1).toString(), response.elementAt(2).toString());
    }

    public void removePassword(int id) throws IOException {
        operation = new OtpErlangAtom("removePassword");
        
        OtpErlangString _id = new OtpErlangString(String.valueOf(id));
        OtpErlangObject[] dataElements = {_id};

        OtpErlangObject[] messageElements = { ownPid, operation, new OtpErlangTuple(dataElements)};
        msg = new OtpErlangTuple(messageElements);
        otpMbox.send(anotherService, anotherNode, msg);

        otpMbox.close();
        otpNode.close();
    }

    public void closeConnections(){
        otpMbox.close();
        otpNode.close();
    }
}
