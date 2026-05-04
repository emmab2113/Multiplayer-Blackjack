package enums;

import java.io.Serializable;

public class Message implements Serializable {
	private static int Counter;
	private /*final*/ String ID;
	private /*final*/ boolean serverSent;
	private /*final*/ ErrorType error;
	private /*final*/ MessageType messageType;
	private /*final*/ MessageStatus messageStatus;
	private /*final*/ String info;
	
    protected final String type;
    protected final String status;
    protected final String text;

    public Message(){
		this.type = "Undefined";
        this.status = "Undefined";
        this.text = "Undefined";
    }

    public Message(String type, String status, String text){
		this.type = type;
        this.status = status;
        this.text = text;
    }
    
    public Message(boolean serverSent, ErrorType error, MessageType messageType, MessageStatus messageStatus, String info) {
    	this.serverSent = serverSent;
    	this.error = error;
    	this.messageType = messageType;
    	this.messageStatus = messageStatus;
    	this.info = info;
    	
    	this.type = "Undefined";
        this.status = "Undefined";
        this.text = "Undefined";
    }

    public String getType(){
    	return type;
    }

    public String getStatus(){
    	return status;
    }

    public String getText(){
    	return text;
    }
    
    public boolean getServerSent(){
    	return serverSent;
    }
    
    public ErrorType getError(){
    	return error;
    }
    
    public MessageType getMessageType(){
    	return messageType;
    }
    
    public MessageStatus getMessageStatus(){
    	return messageStatus;
    }
    
    public String getInfo(){
    	return info;
    }
}
