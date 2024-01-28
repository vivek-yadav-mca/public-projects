package dummydata.android.model;

public class ContactUsModel {

    String contactName;
    String contactEmail;
    String contactMobile;
    String contactGameId;
    String contactMessage;

    public ContactUsModel(String contactName, String contactEmail, String contactMobile, String contactGameId, String contactMessage) {
        this.contactName = contactName;
        this.contactEmail = contactEmail;
        this.contactMobile = contactMobile;
        this.contactGameId = contactGameId;
        this.contactMessage = contactMessage;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getContactMobile() {
        return contactMobile;
    }

    public void setContactMobile(String contactMobile) {
        this.contactMobile = contactMobile;
    }

    public String getContactGameId() {
        return contactGameId;
    }

    public void setContactGameId(String contactGameId) {
        this.contactGameId = contactGameId;
    }

    public String getContactMessage() {
        return contactMessage;
    }

    public void setContactMessage(String contactMessage) {
        this.contactMessage = contactMessage;
    }
}
