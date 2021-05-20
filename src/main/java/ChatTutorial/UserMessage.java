package ChatTutorial;

public class UserMessage {

    /**
     * A user posted this message.
     */
    private String user;

    /**
     * A content of this message.
     */
    private String content;

    public String getUser(){
        return this.user;
    }

    public String getContent(){
        return this.content;
    }

    public UserMessage(String user, String content){
        this.content = content;
        this.user = user;
    }
}