package lu.freakbase.imapviewer;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Goujon
 */
public class MessageListModel {
    
    private List<MessageModel> messages = new ArrayList<>();

    
    public List<MessageModel> getMessages() {
        return messages;
    }

    public void setMessages(List<MessageModel> messages) {
        this.messages = messages;
    }
    
}
