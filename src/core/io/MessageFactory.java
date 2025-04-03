package io;

import json.JSONMessageProtocol;

import java.util.HashMap;
import java.util.Map;

public class MessageFactory {

    public static String createMessage(MessageType messageType, Map<String, Object> data){
        return createMessage(messageType, data, -1);
    }

    public static String createMessage(MessageType messageType, Map<String, Object> data,
                                       int serverStatus){
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("message_type", messageType);
        if (serverStatus != -1)
            jsonMap.put("status", serverStatus);
        jsonMap.put("data", data);
        return JSONMessageProtocol.createJSONFromMap(jsonMap);
    }
}
