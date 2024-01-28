package dummydata.models;

public class SocialPostModel {

    private String post_user_photo;
    private String post_username;
    private String post_date;
    private String post_text;
    private String post_image;

    public SocialPostModel() {
    }

    public SocialPostModel(String post_user_photo, String post_username, String post_date, String post_text, String post_image) {
        this.post_user_photo = post_user_photo;
        this.post_username = post_username;
        this.post_date = post_date;
        this.post_text = post_text;
        this.post_image = post_image;
    }

    public String getPost_user_photo() {
        return post_user_photo;
    }

    public void setPost_user_photo(String post_user_photo) {
        this.post_user_photo = post_user_photo;
    }

    public String getPost_username() {
        return post_username;
    }

    public void setPost_username(String post_username) {
        this.post_username = post_username;
    }

    public String getPost_date() {
        return post_date;
    }

    public void setPost_date(String post_date) {
        this.post_date = post_date;
    }

    public String getPost_text() {
        return post_text;
    }

    public void setPost_text(String post_text) {
        this.post_text = post_text;
    }

    public String getPost_image() {
        return post_image;
    }

    public void setPost_image(String post_image) {
        this.post_image = post_image;
    }

}
