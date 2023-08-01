package ph.edu.dlsu.mobdeve.florendo.mapa.habitat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class Shop extends AppCompatActivity {
    public TextView gemCount;
    private FirebaseFirestore db;
    private FirebaseAuth cAuth;
    private RecyclerView recyclerView; // recycler view

    private ShopItemAdapter SAdapter; // Shop adapter
    private ArrayList<ShopItems> ShopList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        this.gemCount = findViewById(R.id.gem_display_shop);

        recyclerView = findViewById(R.id.shop_item_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();
        cAuth = FirebaseAuth.getInstance();
        CollectionReference usersRef = db.collection("users"); // getuser
        ShopList = new ArrayList<>();
        SAdapter = new ShopItemAdapter(this, ShopList);
        recyclerView.setAdapter(SAdapter);
        //retrieve shop items
        retrieveShopItemsData();
        // retrieve data from firestore
        //retrieve gemcount
        retrieve();
        // item click listener

        //update when clicked





    }


    // Method to handle item click events
    public void handleItemClick(ShopItems shopItem) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Check if the user is logged in
        if (currentUser != null) {
            String userId = currentUser.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            // Retrieve the logged-in user's data
            DocumentReference userRef = db.collection("users").document(userId);
            userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            // Get the purchasedItems array and userGem from the user's document
                            ArrayList<String> purchasedItems = (ArrayList<String>) document.get("purchasedItems");
                            long userGem = document.getLong("userGem");

                            // Check if the clicked item's item_id is already in purchasedItems
                            if (purchasedItems == null || !purchasedItems.contains(shopItem.getItem_id())) {
                                int itemPrice = shopItem.getPrice();
                                if (userGem >= itemPrice) {
                                    // Item is not purchased yet, so add it to the purchasedItems array
                                    if (purchasedItems == null) {
                                        purchasedItems = new ArrayList<>();
                                    }
                                    purchasedItems.add(shopItem.getItem_id());

                                    // Deduct the item price from the userGem

                                    userGem -= itemPrice;
                                    long userFreeze = document.getLong("userFreeze");

                                    if (shopItem.getItem_id().equals("9I9f1ln3yAa0RLAwUhfG")) {
                                        userFreeze++;
                                    }

                                    // Update the user's purchasedItems and userGem in Firestore
                                    Map<String, Object> updates = new HashMap<>();
                                    updates.put("purchasedItems", purchasedItems);
                                    updates.put("userGem", userGem);
                                    updates.put("userFreeze", userFreeze);


                                    userRef.update(updates)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    // Update successful, show a toast and refresh the adapter
                                                    Toast.makeText(Shop.this, "ITEM PURCHASED", Toast.LENGTH_SHORT).show();
                                                    SAdapter.notifyDataSetChanged();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    // Update failed, show a toast
                                                    Toast.makeText(Shop.this, "Failed to purchase item.", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }else{
                                    Toast.makeText(Shop.this, "YOU DO NOT HAVE ENOUGH MONEY", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                // Item is already purchased, show a toast
                                Toast.makeText(Shop.this, "You already purchased this.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            });
        }
    }


    private void retrieveShopItemsData() {
        // Get a reference to the "shopItems" collection in your Firestore database
        CollectionReference shopItemsRef = FirebaseFirestore.getInstance().collection("shopitems");

        // Query the shopItems collection to retrieve all documents
        shopItemsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ShopList.clear(); // Clear the list before adding new data

                // Iterate through the documents to populate the shopItemsList
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String item_id = document.getId();
                    String name = document.getString("name");
                    int price = document.getLong("price").intValue();

                    String imageUrl = getItemImageUrl(item_id); // Implement a method to fetch the image URL

                    ShopItems shopItem = new ShopItems(item_id, name, price, imageUrl);
                    ShopList.add(shopItem);
                }

                // Create the adapter and set it to the RecyclerView
                SAdapter = new ShopItemAdapter(this, ShopList);
                recyclerView.setAdapter(SAdapter);
                SAdapter.setOnItemClickListener(new ShopItemAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(ShopItems shopItem) {
                        //Toast.makeText(Shop.this, "CLICK", Toast.LENGTH_SHORT).show();
                        handleItemClick(shopItem);
                        retrieve();
                    }
                });
            } else {
                Toast.makeText(this, "FAIL TO RETRIEVE", Toast.LENGTH_SHORT).show();
            }
        });
    }



    //get user gemcount
    public void retrieve(){

        FirebaseUser cUser = FirebaseAuth.getInstance().getCurrentUser();

        String userId = cUser.getUid(); // Get the user ID

        //get from collections
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docref = db.collection("users").document(userId);
        docref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                Long userGemCount = documentSnapshot.getLong("userGem");

                gemCount.setText(String.valueOf(userGemCount) + " gems");

            }
        });

    }

    private String getItemImageUrl(String itemId) {
        // Replace "your_bucket_name" with the actual name of your Firebase Storage bucket
        // Assuming the images are stored in a folder called "items" within the bucket
        String storageReference = "gs://habitat-14e37.appspot.com/" + itemId + ".png"; // Replace with the appropriate file extension

        // You can also use HTTPS URL by calling getDownloadUrl() and handling its callback.
        // For simplicity, we'll use the storageReference directly.
        return storageReference;
    }

    public void launchCreateHabit (View CView){
        Intent CIntent = new Intent(Shop.this, CreateHabit.class);
        this.startActivity(CIntent);
        this.finish();
    }

    public void launchCollection(View CView){
        Intent CIntent = new Intent(Shop.this, Collection.class);
        this.startActivity(CIntent);
        this.finish();
    }

}