package dummydata.models;

public class BookModel {

    private String pdfImageUrl;
    private String pdfName;
    private String pdfContentUrl;

    public BookModel(String pdfName, String pdfImageUrl, String pdfContentUrl) {
        this.pdfImageUrl = pdfImageUrl;
        this.pdfName = pdfName;
        this.pdfContentUrl = pdfContentUrl;
    }

    public String getPdfImageUrl() {
        return pdfImageUrl;
    }

    public void setPdfImageUrl(String pdfImageUrl) {
        this.pdfImageUrl = pdfImageUrl;
    }

    public String getPdfName() {
        return pdfName;
    }

    public void setPdfName(String pdfName) {
        this.pdfName = pdfName;
    }

    public String getPdfContentUrl() {
        return pdfContentUrl;
    }

    public void setPdfContentUrl(String pdfContentUrl) {
        this.pdfContentUrl = pdfContentUrl;
    }


}
