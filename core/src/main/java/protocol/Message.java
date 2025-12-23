package protocol;

import com.fasterxml.jackson.annotation.JsonInclude;

@SuppressWarnings("unused")
public class Message {

    private MessageType messageType;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long status;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Object content;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private ErrorDetails errorDetail;

    public Message() {}

    public Message(MessageType messageType, Object content) {
        this(messageType, null, content);
    }

    public Message(MessageType messageType, Long status, Object content) {
        this.messageType = messageType;
        this.status = status;
        this.content = content;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }

    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }

    public ErrorDetails getErrorDetail() {
        return errorDetail;
    }

    public void setErrorDetail(ErrorDetails errorDetail) {
        this.errorDetail = errorDetail;
    }
}
