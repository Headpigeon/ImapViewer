package lu.freakbase.imapviewer;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
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
                    mod.setSender(Arrays.stream(addresses).map(Address::toString).collect(Collectors.joining(", ")));
                }
                mod.setSubject(msg.getSubject());
                mod.setDate(msg.getSentDate());
                mod.setContents(msg.toString());
                modList.getMessages().add(mod);
            }
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
    
}
