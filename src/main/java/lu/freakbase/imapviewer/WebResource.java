package lu.freakbase.imapviewer;

/**
 *
 * @author Goujon
 */
public class WebResource {
    
    private String url;
    private String minUrl;

    public WebResource(String url, String minUrl) {
        this.url = url;
        this.minUrl = minUrl;
    }
    
    public String get(boolean preferMinified) {
        if (url != null) {
            if (minUrl != null && preferMinified) {
                return minUrl;
            } else {
                return url;
            }
        } else {
            return minUrl;
        }
    }

    public String getUrl() {
        return url;
    }

    public String getMinUrl() {
        return minUrl;
    }

}
