package com.komal.template_backend.model;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "social_links")
public class SocialLink {
    @Id
    private String id;
    private String platform;
    private String url;
    private String icon;

    public SocialLink() {}

    public SocialLink(String platform, String url, String icon) {
        this.platform = platform;
        this.url = url;
        this.icon = icon;
    }

    // Getters & setters
    public String getId() { return id; }
    public String getPlatform() { return platform; }
    public String getUrl() { return url; }
    public String getIcon() { return icon; }

    public void setId(String id) { this.id = id; }
    public void setPlatform(String platform) { this.platform = platform; }
    public void setUrl(String url) { this.url = url; }
    public void setIcon(String icon) { this.icon = icon; }
}
