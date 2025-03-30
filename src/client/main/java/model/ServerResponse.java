package model;

import io.MessageType;
import json.JSONMessageProtocol;

import java.util.Map;

public class ServerResponse {
    final private MessageType MESSAGE_TYPE;
    final private int STATUS;
    final private Map<String, Object> DATA;

    final private String JSON_STRING;

    public ServerResponse(String serverMessage){
        Map<String, Object> messageData = JSONMessageProtocol.createMapFromJSONString(serverMessage);

        MESSAGE_TYPE = MessageType.valueOf(messageData.get("message_type").toString());
        STATUS = (int) messageData.getOrDefault("status", 0);
        DATA = (Map<String, Object>) messageData.get("data");
        JSON_STRING = serverMessage;
        System.out.println("From server: " + serverMessage);
    }

    public MessageType getMessageType() {
        return MESSAGE_TYPE;
    }

    public int getStatus() {
        return STATUS;
    }

    public Map<String, Object> getData() {
        return DATA;
    }

    public String getErrorMessage(){
        if (STATUS == 200)
            return null;

        return DATA.get("error_message").toString();
    }

    public int getErrorCode(){
        if (STATUS == 200)
            return -1;

        return (int) DATA.getOrDefault("error_code", -1);
    }

    public String getDataAsJsonString() {
        return JSON_STRING;
    }
}
