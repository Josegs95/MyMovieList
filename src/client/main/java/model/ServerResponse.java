package model;

import io.MessageType;
import json.JSONMessageProtocol;

import java.util.Map;

public class ServerResponse {
    private final MessageType messageType;
    private final int status;
    private final Map<String, Object> data;

    private final String jsonString;

    @SuppressWarnings("unchecked")
    public ServerResponse(String serverMessage) {

        // Borrar
        System.out.println("From server: " + serverMessage);

        Map<String, Object> messageData = JSONMessageProtocol.createMapFromJSONString(serverMessage);

        messageType = MessageType.valueOf(messageData.get("message_type").toString());
        status = (int) messageData.getOrDefault("status", 0);
        data = (Map<String, Object>) messageData.get("data");
        jsonString = serverMessage;

    }

    public MessageType getMessageType() {
        return messageType;
    }

    public int getStatus() {
        return status;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public String getErrorMessage(){
        if (status == 200)
            return null;

        return data.get("error_message").toString();
    }

    public int getErrorCode(){
        if (status == 200)
            return -1;

        return (int) data.getOrDefault("error_code", -1);
    }

    public String getAsJsonString() {
        return jsonString;
    }
}
