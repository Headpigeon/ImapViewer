package lu.freakbase.imapviewer;

import java.util.Enumeration;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;

/**
 *
 * @author Goujon
 */

@Singleton
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class Configuration {
    
    @Inject
    private ServletContext servletContext;
    
    public static final String SYSTEM_PROPERTY_PREFIX = "imap-viewer.";
    public static final String CONTEXT_PARAM_PREFIX = "imap-viewer.";
    
    private String resource = "prop/imapViewer";
    private String host = "localhost";
    private int port = 143;
    private String username;
    private String password;
    
    public void init(@Observes @Initialized(ApplicationScoped.class) Object init) {
        reload();
    }
    
    public void reload() {
        resource = getConfigString("resource", null);
        Properties res = null;
        if (resource != null) {
            try {
                InitialContext ctx = new InitialContext();
                res = (Properties)ctx.lookup(resource);
            } catch (NamingException ex) {
                Logger.getLogger(Configuration.class.getName()).log(Level.FINEST, null, ex);
            }
        }
        host = getConfigString("host", res);
        port = getConfigInt("port", res);
        username = getConfigString("username", res);
        password = getConfigString("password", res);
    }
    
    private String getConfigString(String key, Properties res) {
        String value = null;
        if (res != null) {
            value = res.getProperty(key);
            if (value != null && !value.isEmpty()) {
                return value;
            }
        }
        value = System.getProperty(SYSTEM_PROPERTY_PREFIX + key);
        if (value != null && !value.isEmpty()) {
            return value;
        }
        value = servletContext.getInitParameter(CONTEXT_PARAM_PREFIX + key);
        if (value != null && !value.isEmpty()) {
            return value;
        }
        return null;
    }
    
    private int getConfigInt(String key, Properties res) {
        String str = getConfigString(key, res);
        if (str != null) {
            try {
                return Integer.parseInt(str);
            } catch (NumberFormatException ex) {
                Logger.getLogger(Configuration.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return -1;
    }
    
    

    public String getResource() {
        return resource;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
    
}
