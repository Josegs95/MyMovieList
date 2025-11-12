package model;

import com.fasterxml.jackson.annotation.JsonInclude;
import protocol.MessageType;

import java.util.Map;
import java.util.Optional;

public record Message(
        MessageType messageType,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        Long status,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        Map<String, Object> content) {

        public Optional<String> getErrorMessage() {
                if (content != null && content.containsKey("error_message")) {
                        return Optional.ofNullable((String) content.get("error_message"));
                }

                return Optional.empty();
        }
}
