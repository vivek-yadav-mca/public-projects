package dummydata.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.ServerTimestamp;

public class StoryModel {
    private String id;
    private String name;
    private String imageUrl;
    private String contentUrl;
    @ServerTimestamp
    private Timestamp createdAt;

    public StoryModel() {
    }

    public StoryModel(String id, String name, String imageUrl, String contentUrl) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.contentUrl = contentUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getContentUrl() {
        return contentUrl;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }
}
