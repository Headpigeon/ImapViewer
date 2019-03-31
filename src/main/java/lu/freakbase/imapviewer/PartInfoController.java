package lu.freakbase.imapviewer;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.mail.Message;
import javax.mvc.Controller;
import javax.mvc.Models;
import javax.mvc.View;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

/**
 *
 * @author Goujon
 */
@Controller
@Path("part-info")
@View("/part-info.jsp")
public class PartInfoController {

    @EJB
    private ImapService imap;
    
    @Inject
    private Models models;
    
    @GET
    @Path("/{msgNumber}")
    public void getPartInfo(@PathParam("msgNumber") int msgNumber) {
        Message msg = imap.getMessage(msgNumber);
        if (msg == null) {
            return;
        }
        models.put("part", new PartModel(msg));
    }
    
}
