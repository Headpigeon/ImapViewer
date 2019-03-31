package lu.freakbase.imapviewer;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.internet.MimeMultipart;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

/**
 *
 * @author Goujon
 */
@Path("api")
public class ImapViewerApi {
    
    @EJB
    private ImapService imap;
    
    private Part getPartByType(Part part, String mimeType) {
        try {
            if (part.isMimeType(mimeType)) {
                return part;
            } else if (part.isMimeType("multipart/*")) {
                MimeMultipart mp = (MimeMultipart)part.getContent();
                for (int i = 0; i < mp.getCount(); i++) {
                    Part result = getPartByType(mp.getBodyPart(i), mimeType);
                    if (result != null) {
                        return result;
                    }
                }
                return null;
            } else {
                return null;
            }
        } catch (IOException | MessagingException ex) {
            Logger.getLogger(ImapViewerApi.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    @GET
    @Path("text/{msgNumber}")
    public Response getText(@PathParam("msgNumber") int msgNumber) {
        Message msg = imap.getMessage(msgNumber);
        if (msg == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        Part part = getPartByType(msg, "text/plain");
        if (part == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        
        try {
            return Response.status(Response.Status.OK).type("text/plain").entity(part.getContent().toString()).build();
        } catch (IOException | MessagingException ex) {
            Logger.getLogger(ImapViewerApi.class.getName()).log(Level.SEVERE, null, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GET
    @Path("html/{msgNumber}")
    public Response getHtml(@PathParam("msgNumber") int msgNumber) {
        Message msg = imap.getMessage(msgNumber);
        if (msg == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        Part part = getPartByType(msg, "text/html");
        if (part == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        
        try {
            return Response.status(Response.Status.OK).type("text/html").entity(part.getContent().toString()).build();
        } catch (IOException | MessagingException ex) {
            Logger.getLogger(ImapViewerApi.class.getName()).log(Level.SEVERE, null, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GET
    @Path("attachment/{msgNumber}/{contentId}")
    public Response getAttachment(@PathParam("msgNumber") int msgNumber, @PathParam("contentId") String contentId) {
        Part part = imap.getPart(msgNumber, contentId);
        if (part == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        try {
            return Response.status(Response.Status.OK).type(part.getContentType()).entity(part.getContent()).build();
        } catch (MessagingException | IOException ex) {
            Logger.getLogger(ImapViewerApi.class.getName()).log(Level.SEVERE, null, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    
}
