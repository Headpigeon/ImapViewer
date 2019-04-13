package lu.freakbase.imapviewer;

import java.util.Collections;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.mail.Address;
import javax.mail.Message;
import javax.mvc.Controller;
import javax.mvc.Models;
import javax.mvc.View;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

/**
 *
 * @author Goujon
 */
@Controller
@Path("")
@View("/viewer.jsp")
public class ImapViewerController {

    @EJB
    private ImapService imap;
    
    @Inject
    private Models models;
    
    @GET
    public void listMessages() {
        configure();
        
        MessageListModel modList = new MessageListModel();
        
        try {
            Message[] messages = imap.getMessages();
            for (Message msg : messages) {
                MessageModel mod = new MessageModel();
                mod.setNumber(msg.getMessageNumber());
                Address[] addresses = msg.getFrom();
                if (addresses != null) {
                    mod.setSender(MailUtil.joinAddresses(addresses));
                }
                mod.setSubject(msg.getSubject());
                mod.setDate(msg.getSentDate());
                mod.setContents(msg.toString());
                modList.getMessages().add(mod);
            }
            Collections.sort(modList.getMessages(), (a, b) -> -compareDates(a.getDate(), b.getDate()));
        } catch (Exception ex) {
            Logger.getLogger(ImapViewerController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        models.put("msgs", modList);
    }
    
    private void configure() {
        PageConfig config = new PageConfig();
        
        config.setTitle("IMAP Viewer");
        
        config.addJsLib("jquery-3.3.1.js", "jquery-3.3.1.min.js");
        
        config.addJsLib("bootstrap.bundle.js", "bootstrap.bundle.min.js");
        config.addCssLib("bootstrap.css", "bootstrap.min.css");
        
        // Font Awesome Solid Icons
        config.addCssLib("fontawesome.css", "fontawesome.min.css");
        config.addCssLib("solid.css", "solid.min.css");
        
        config.addJs("imap-viewer.js", null);
        config.addCss("imap-viewer.css", null);

        models.put("config", config);
    }
    
    public static int compareDates(Date a, Date b) {
        if (a == null) {
            if (b == null) {
                return 0;
            } else {
                return 1;
            }
        } else if (b == null) {
            return -1;
        } else {
            return a.compareTo(b);
        }
    }
    
}
