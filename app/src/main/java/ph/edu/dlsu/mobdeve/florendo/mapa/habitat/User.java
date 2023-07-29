package ph.edu.dlsu.mobdeve.florendo.mapa.habitat;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String userId;
    private String username;
    private int userGem;
    private int userFreeze;
    private List<String> purchasedItems;

    public User() {
        // Empty constructor needed for Firestore deserialization
    }

    public User(String userId, String username) {
        this.userId = userId;
        this.username = username;
        this.userGem = 0; // this might cause problem when retrieving data, not sure
        this.userFreeze = 0;
        this.purchasedItems = new ArrayList<>(); // initialize these items

    }


    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public int getUserGem(){return userGem;}

    public int getUserFreeze(){return userFreeze;}

    // Add method to add a purchased item ID to the list
    public void addPurchasedItem(String itemId) {
        purchasedItems.add(itemId);
    }

    // Add method to remove a purchased item ID from the list
    public void removePurchasedItem(String itemId) {
        purchasedItems.remove(itemId);
    }

    // Add a getter to access the list of purchased items
    public List<String> getPurchasedItems() {
        return purchasedItems;
    }
}