package asr.proyectoFinal.models;

import java.util.Date;

public class YahooNew {
    // Attributes
    private String link;
    private String description;
    private Guid guid;
    private String title;
    private Date pubDate;
    private Double score;

    // INNER CLASS
    private class Guid {
        // Attributes
        private boolean isPermaLink;
        private String content;

        // Constructor
        public Guid(boolean isPermaLink, String content) {
            this.setPermaLink(isPermaLink);;
            this.setContent(content);
		}
		
		public boolean isPermaLink() {
			return isPermaLink;
		}

		public void setPermaLink(boolean isPermaLink) {
			this.isPermaLink = isPermaLink;
		}

		public String getContent() {
			return content;
		}

		public void setContent(String content) {
			this.content = content;
		}
    }

    public String getLink() {
        return link;
	}
	
	public void setLink(String link) {
		this.link = link;
	}

	public Date getPubDate() {
		return pubDate;
	}

	public void setPubDate(Date pubDate) {
		this.pubDate = pubDate;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Guid getGuid() {
		return guid;
	}

	public void setGuid(Guid guid) {
		this.guid = guid;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Double getScore() {
		return score;
	}

	public void setScore(Double score) {
		this.score = score;
	}
}