package service;

import context.SessionContext;
import dto.UserDTO;
import dto.request.LoginRequest;
import dto.request.RegisterRequest;
import protocol.Message;
import protocol.MessageType;
import protocol.SocketCommunication;
import tools.jackson.databind.ObjectMapper;

public class AuthService {

    private final ObjectMapper mapper = new ObjectMapper();

    public void login(String username, String password) {
        LoginRequest request = new LoginRequest(username, password);

        Message serverMessage = SocketCommunication.sendMessageToServer(new Message(MessageType.LOGIN, request));

        UserDTO user = mapper.convertValue(serverMessage.content(), UserDTO.class);
        SessionContext.getInstance().setUser(user);
    }

    public void register(String username, String password, String email) {
        RegisterRequest request = new RegisterRequest(username, password, email);

        SocketCommunication.sendMessageToServer(new Message(MessageType.REGISTER, request));
    }
}
