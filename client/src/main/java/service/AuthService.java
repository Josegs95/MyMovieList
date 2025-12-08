package service;

import context.SessionContext;
import protocol.Message;
import protocol.MessageType;
import protocol.SocketCommunication;
import protocol.dto.request.LoginRequest;
import protocol.dto.request.RegisterRequest;
import protocol.dto.response.LoginResponse;
import tools.jackson.databind.ObjectMapper;
import ui.event.LoginUserEvent;
import ui.event.RegisterUserEvent;
import ui.util.EventBus;

public class AuthService {

    private final ObjectMapper mapper = new ObjectMapper();

    public void login(String username, String password) {
        LoginRequest request = new LoginRequest(username, password);

        Message serverMessage = SocketCommunication.sendMessageToServer(new Message(MessageType.LOGIN, request));

        LoginResponse response = mapper.convertValue(serverMessage.content(), LoginResponse.class);
        SessionContext.getInstance().setUser(response.userDTO());

        EventBus.publish(new LoginUserEvent(response.userDTO()));
    }

    public void register(String username, String password, String email) {
        RegisterRequest request = new RegisterRequest(username, password, email);

        SocketCommunication.sendMessageToServer(new Message(MessageType.REGISTER, request));

        EventBus.publish(new RegisterUserEvent(username));
    }
}
