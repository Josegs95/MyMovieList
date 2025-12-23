package service;

import context.SessionContext;
import model.dto.UserDTO;
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

        LoginResponse response = mapper.convertValue(serverMessage.getContent(), LoginResponse.class);
        UserDTO newUserDTO = response.userDTO();

        SessionContext context = SessionContext.getInstance();
        if (context.getUser() == null) {
            context.setUser(newUserDTO);
            EventBus.publish(new LoginUserEvent(newUserDTO));

            return;
        }

        UserDTO currentUserDTO = context.getUser();
        currentUserDTO.setSessionToken(newUserDTO.getSessionToken());
    }

    public void register(String username, String password, String email) {
        RegisterRequest request = new RegisterRequest(username, password, email);

        SocketCommunication.sendMessageToServer(new Message(MessageType.REGISTER, request));

        EventBus.publish(new RegisterUserEvent(username));
    }
}
