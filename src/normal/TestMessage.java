package normal;
import java.io.Serializable;

public class TestMessage implements Serializable {
    protected final String type;
    protected final String status;
    protected final String text;

    public TestMessage(){
        this.type = "Undefined";
        this.status = "Undefined";
        this.text = "Undefined";
    }

    public TestMessage(String type, String status, String text){
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
