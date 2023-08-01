package ph.edu.dlsu.mobdeve.florendo.mapa.habitat;

public class ShopItems {

    private String item_id;
    private String name;
    private int price;
    private String imageUrl;

    public ShopItems() {
        // Default constructor is necessary for Firestore to map data to objects.
    }

    public ShopItems(String item_id, String name, int price, String imageUrl) {

        this.item_id = item_id;
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    public String getItem_id() {
        return item_id;
    }

    public int getPrice() {
        return price;
    }

    public String getItemName() {
        return name;
    }

    public String getImageUrl(){return imageUrl;}
    public void setImageUrl(String imageUrl){
        this.imageUrl = imageUrl;
    }

    public void setItem_id(String item_id) {
        this.item_id = item_id;
    }

    public void setItemName(String name) {
        this.name = name;
    }
    public void setPrice(int price) {
        this.price = price;
    }
}
