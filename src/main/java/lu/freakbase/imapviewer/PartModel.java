package lu.freakbase.imapviewer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.internet.MimeMultipart;

/**
 *
 * @author Goujon
 */
public class PartModel {
    
    private Date sentDate;
    private Date receivedDate;
    private String contentType;
    private String description;
    private String disposition;
    private String filename;
    private Integer size;
    private List<PartModel> subParts = new ArrayList<>();

    public PartModel(Part part) {
        try {
            setContentType(part.getContentType());
            setDescription(part.getDescription());
            setDisposition(part.getDisposition());
            setFilename(part.getFileName());
            setSize(part.getSize());
            if (part instanceof Message) {
                Message msg = (Message)part;
                setSentDate(msg.getSentDate());
                setReceivedDate(msg.getReceivedDate());
            }
            if (part.getContent() instanceof MimeMultipart) {
                MimeMultipart mp = (MimeMultipart)part.getContent();
                for (int i = 0; i < mp.getCount(); i++) {
                    getSubParts().add(new PartModel(mp.getBodyPart(i)));
                }
            }
        } catch (MessagingException | IOException ex) {
            Logger.getLogger(PartModel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDisposition() {
        return disposition;
    }

    public void setDisposition(String disposition) {
        this.disposition = disposition;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public List<PartModel> getSubParts() {
        return subParts;
    }

    public void setSubParts(List<PartModel> subParts) {
        this.subParts = subParts;
    }

    public Date getSentDate() {
        return sentDate;
    }

    public void setSentDate(Date sentDate) {
        this.sentDate = sentDate;
    }

    public Date getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(Date receivedDate) {
        this.receivedDate = receivedDate;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }
    
}
