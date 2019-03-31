package lu.freakbase.imapviewer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author Goujon
 */
public class PageConfig {
    
    public static final String LIB_DIR = "lib/";
    
    private String title = "";
    private boolean preferMinified = false;
    private List<WebResource> css = new ArrayList<>();
    private List<WebResource> js = new ArrayList<>();

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isPreferMinified() {
        return preferMinified;
    }

    public void setPreferMinified(boolean preferMinified) {
        this.preferMinified = preferMinified;
    }
    
    public void addCss(String url, String minUrl) {
        addResource(css, url, minUrl);
    }
    
    public void addCssLib(String url, String minUrl) {
        addResource(css, LIB_DIR + url, LIB_DIR + minUrl);
    }
    
    public List<String> getCss() {
        return getResources(css, preferMinified);
    }
    
    public void addJs(String url, String minUrl) {
        addResource(js, url, minUrl);
    }
    
    public void addJsLib(String url, String minUrl) {
        addResource(js, LIB_DIR + url, LIB_DIR + minUrl);
    }
    
    public List<String> getJs() {
        return getResources(js, preferMinified);
    }
    
    private void addResource(Collection<WebResource> res, String url, String minUrl) {
        res.add(new WebResource(url, minUrl));
    }
    
    private List<String> getResources(Collection<WebResource> res, boolean preferMinified) {
        return res.stream().map(wr -> wr.get(preferMinified)).collect(Collectors.toList());
    }
    
}
