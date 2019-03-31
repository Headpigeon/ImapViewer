package lu.freakbase.imapviewer;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.ejb.EJB;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.internet.MimeMultipart;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.json.JSONArray;

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
    @Path("part/{msgNumber}/{contentId}")
    public Response getPart(@PathParam("msgNumber") int msgNumber, @PathParam("contentId") String contentId) {
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
    
    @GET
    @Path("attachments/{msgNumber}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAttachments(@PathParam("msgNumber") int msgNumber) {
        Message msg = imap.getMessage(msgNumber);
        if (msg == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        
        List<Part> attachments = MailUtil.filterPartsByDisposition(msg, "attachment");
        JSONArray result = new JSONArray();
        for (Part part : attachments) {
            String name = getFilename(part);
            if (name != null) {
                result.put(name);
            }
        }
        
        return Response.status(Response.Status.OK).entity(result.toString()).build();
    }
    
    @GET
    @Path("attachment/{msgNumber}/{name}")
    public Response getAttachment(@PathParam("msgNumber") int msgNumber, @PathParam("name") String name) {
        Message msg = imap.getMessage(msgNumber);
        if (msg == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        
        List<Part> attachments = MailUtil.filterPartsByDisposition(msg, "attachment");
        for (Part part : attachments) {
            String filename = getFilename(part);
            if (filename == null || !filename.equals(name)) {
                continue;
            }
            try {
                return Response.status(Response.Status.OK)
                        .type(MediaType.APPLICATION_OCTET_STREAM)
                        .entity(part.getContent())
                        .build();
            } catch (IOException | MessagingException ex) {
                Logger.getLogger(ImapViewerApi.class.getName()).log(Level.SEVERE, null, ex);
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }
        }
        
        return Response.status(Response.Status.NOT_FOUND).build();
    }
    

    private static final Pattern CONTENT_TYPE_NAME_PATTERN
            = Pattern.compile("\\bname=['\"]?([^\"]+)['\"]?\\b");
    
    private static String getFilename(Part part) {
        try {
            if (part.getFileName() != null) {
                return part.getFileName();
            } else if (part.getContentType() != null) {
                Matcher m = CONTENT_TYPE_NAME_PATTERN.matcher(part.getContentType());
                if (m.find()) {
                    return m.group(1);
                }
            }
        } catch (MessagingException ex) {
            Logger.getLogger(ImapViewerApi.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
}
