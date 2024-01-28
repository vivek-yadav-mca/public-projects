package dummydata.android.model;

public class HomeFragItemModel {

    String itemId, itemName;
    int itemImage;

    public HomeFragItemModel() {
    }

    public HomeFragItemModel(String itemId, String itemName, int itemImage) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.itemImage = itemImage;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getItemImage() {
        return itemImage;
    }

    public void setItemImage(int itemImage) {
        this.itemImage = itemImage;
    }


}
