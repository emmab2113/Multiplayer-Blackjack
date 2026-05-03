package enums;

import java.io.Serializable;

public class Message implements Serializable {
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

    public String getType(){
    	return type;
    }

    public String getStatus(){
    	return status;
    }

    public String getText(){
    	return text;
    }

}
