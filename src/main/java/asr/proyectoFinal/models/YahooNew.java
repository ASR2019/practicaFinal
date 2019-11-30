package asr.proyectoFinal.models;

import java.util.Date;

public class YahooNew {
    // Attributes
    private String link;
    private String description;
    private Guid guid;
    private String title;
    private Date pubDate;

    // INNER CLASS
    private class Guid {
        // Attributes
        private boolean isPermaLink;
        private String content;

        // Constructor
        public Guid(boolean isPermaLink, String content) {
            this.isPermaLink = isPermaLink;
            this.content = content;
        }
    }

    public String getLink() {
        return link;
    }
}