package model;

import io.MessageType;
import json.JSONMessageProtocol;

import java.util.Map;

public class ServerResponse {
    final private MessageType MESSAGE_TYPE;
    final private int status;
    final private Map<String, Object> data;

    public ServerResponse(String serverMessage){
        Map<String, Object> messageData = JSONMessageProtocol.createMapFromJSONString(serverMessage);

        MESSAGE_TYPE = MessageType.valueOf(messageData.get("message_type").toString());
        status = Integer.parseInt(messageData.get("status").toString());
        data = (Map<String, Object>) messageData.get("data");
    }

    public MessageType getMessageType() {
        return MESSAGE_TYPE;
    }

    public int getStatus() {
        return status;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public String getMessageError(){
        if (status == 200)
            return null;

        return data.get("error_message").toString();
    }
}
