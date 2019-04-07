package lu.freakbase.imapviewer;


import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeMultipart;


/**
 *
 * @author Goujon
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class ImapService {
    
    @EJB
    private Configuration config;
    
    private Folder getInbox() throws MessagingException {
        Session session = Session.getDefaultInstance(null);
        
        Store store = session.getStore("imap");
        store.connect(config.getHost(), config.getPort(), config.getUsername(), config.getPassword());

        Folder folder = store.getFolder("INBOX");
        folder.open(Folder.READ_ONLY);

        return folder;
    }
    
    public Message[] getMessages() {
        try {
            Folder inbox = getInbox();
            return inbox.getMessages();
        } catch (MessagingException ex) {
            Logger.getLogger(ImapService.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    public Message getMessage(int msgNumber) {
        try {
            Folder inbox = getInbox();
            return inbox.getMessage(msgNumber);
        } catch (MessagingException ex) {
            Logger.getLogger(ImapService.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    public Part getPart(int msgNumber, String contentId) {
        try {
            Folder inbox = getInbox();
            Message m = inbox.getMessage(msgNumber);
            if (m == null || !m.isMimeType("multipart/*")) {
                return null;
            }
            MimeMultipart multiPart = (MimeMultipart)m.getContent();
            return getPartByContentId(multiPart, contentId);
        } catch (MessagingException | IOException ex) {
            Logger.getLogger(ImapService.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    private Part getPartByContentId(MimeMultipart multiPart, String contentId) {
        try {
            BodyPart part = multiPart.getBodyPart(contentId);
            if (part != null) {
                return part;
            }
            if (!contentId.startsWith("<")) {
                part = multiPart.getBodyPart("<" + contentId + ">");
                if (part != null) {
                    return part;
                }
            }
            for (int i = 0; i < multiPart.getCount(); i++) {
                BodyPart child = multiPart.getBodyPart(i);
                if (child.isMimeType("multipart/*")) {
                    Part result = getPartByContentId((MimeMultipart)child.getContent(), contentId);
                    if (result != null) {
                        return result;
                    }
                }
            }
        } catch (MessagingException | IOException ex) {
            Logger.getLogger(ImapService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
}
