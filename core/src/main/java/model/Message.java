package model;

import com.fasterxml.jackson.annotation.JsonInclude;
import protocol.MessageType;

import java.util.Map;

public record Message(
        MessageType messageType,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        Long status,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        Map<String, Object> content) {}
