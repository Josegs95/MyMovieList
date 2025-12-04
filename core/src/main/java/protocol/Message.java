package protocol;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Map;
import java.util.Optional;

public record Message(
        MessageType messageType,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        Long status,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        Object content) {

    public Message(MessageType messageType, Object content) {
        this(messageType, null, content);
    }

    @JsonIgnore
    public Optional<String> getErrorMessage() {
        if (content instanceof Map<?,?> errorContent) {
            if (errorContent.containsKey("error_message")) {
                return Optional.ofNullable((String) errorContent.get("error_message"));
            }
        }

        return Optional.empty();
    }
}
