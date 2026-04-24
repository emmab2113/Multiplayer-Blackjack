package MBClient;

import java.io.Serializable;

import enums.MessageStatus;
import enums.MessageType;

public class Message implements Serializable {
    protected MessageType type;
    protected MessageStatus status;
    protected String text;

    public Message() {
        this.type = MessageType.Test;
        this.status = MessageStatus.Test;
        this.text = "Undefined";
    }

    public Message(MessageType type, MessageStatus status, String text) {
        this.type = type;
        this.status = status;
        this.text = text;
    }

    public MessageType getType() {
    	return type; 
    }

    public MessageStatus getStatus() {
    	return status;
    }

    public String getText() {
    	return text;
    }

}
