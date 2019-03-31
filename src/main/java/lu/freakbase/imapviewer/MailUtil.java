package lu.freakbase.imapviewer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.internet.MimeMultipart;

/**
 *
 * @author Goujon
 */
public class MailUtil {
    
    private MailUtil() {
    }
    
    public static String joinAddresses(Address[] addresses) {
        if (addresses == null) {
            return null;
        }
        return Arrays.stream(addresses)
                .map(Address::toString)
                .collect(Collectors.joining(", "));
    }
    
    public static List<Part> filterParts(Part part, Function<Part, Boolean> filter) {
        List<Part> result = new ArrayList<>();
        filterParts(result, part, filter);
        return result;
    }
    
    public static void filterParts(List<Part> result, Part part, Function<Part, Boolean> filter) {
        if (filter.apply(part)) {
            result.add(part);
        }
        try {
            if (part.getContent() instanceof MimeMultipart) {
                MimeMultipart mp = (MimeMultipart)part.getContent();
                for (int i = 0; i < mp.getCount(); i++) {
                    filterParts(result, mp.getBodyPart(i), filter);
                }
            }
        } catch (IOException | MessagingException ex) {
            Logger.getLogger(MailUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static List<Part> filterPartsByDisposition(Part part, String disposition) {
        return filterParts(part, p -> {
            try {
                return (p.getDisposition() != null && p.getDisposition().equalsIgnoreCase(disposition));
            } catch (MessagingException ex) {
                Logger.getLogger(MailUtil.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
        });
    }
    
}
