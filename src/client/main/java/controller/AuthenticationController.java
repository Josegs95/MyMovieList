package controller;

import exception.RegisterValidationException;
import io.MessageType;
import io.SocketCommunication;
import model.ServerResponse;
import security.Security;

import javax.swing.*;
import java.io.IOException;
import java.util.Map;

public class AuthenticationController{
    public ServerResponse registerUser(Map<String, Object> userData) throws RegisterValidationException {
        if (!checkRegisterFields(userData))
            return null;

        try(SocketCommunication socketCommunication = new SocketCommunication()){
            userData.put("password", Security.hashString(userData.get("password").toString()));
            userData.remove("password2");
            if (userData.get("email").toString().isEmpty())
                userData.put("email", null);

            return socketCommunication.writeToServer(userData, MessageType.REGISTER);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(
                    null,
                    "Error al intentar comunicarse con el servidor",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            throw new RuntimeException(e);
        }
    }

    public ServerResponse loginUser(Map<String, Object> userData){
        try(SocketCommunication socketCommunication = new SocketCommunication()){
            userData.put("password", Security.hashString(userData.get("password").toString()));

            return socketCommunication.writeToServer(userData, MessageType.LOGIN);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(
                    null,
                    "Error al intentar comunicarse con el servidor",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            throw new RuntimeException(e);
        }
    }

    private boolean checkRegisterFields(Map<String, Object> userData) throws RegisterValidationException {
        //Checks
        String username = userData.get("username").toString();
        String password = userData.get("password").toString();
        String passwordRepeat = userData.get("password2").toString();
        String email = userData.get("email").toString();

        //Required fields

        if (username.isEmpty() || password.isEmpty() || passwordRepeat.isEmpty())
            throw new RegisterValidationException("Los campos 'Username', 'Password' y 'Repeat Password' son obligatorios");

        //Password

        if (password.length() < 3)
            throw new RegisterValidationException("La contraseña debe tener mas de 3 carácteres");
        else if (!password.equals(passwordRepeat))
            throw new RegisterValidationException("Las contraseñas no coinciden entre sí");

        //Email
        if (!email.isEmpty()){
            String emailPattern = "^\\w+([.\\-_]?\\w+)*@\\w+(\\w+)?\\.\\w{2,3}$";

            if (!email.matches(emailPattern))
                throw new RegisterValidationException("El email no es válido. Revise su sintaxis");
        }

        return true;
    }
}
