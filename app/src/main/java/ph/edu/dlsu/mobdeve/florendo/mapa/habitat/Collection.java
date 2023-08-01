package ph.edu.dlsu.mobdeve.florendo.mapa.habitat;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;


public class Collection extends AppCompatActivity {
    private FirebaseFirestore db;
    private FirebaseAuth cAuth;
    private RecyclerView recyclerView;
    private CollectionAdapter collectionAdapter;
    private ArrayList<ShopItems> purchasedItemsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);

        recyclerView = findViewById(R.id.collection_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();
        cAuth = FirebaseAuth.getInstance();
        purchasedItemsList = new ArrayList<>();
        collectionAdapter = new CollectionAdapter(purchasedItemsList);
        recyclerView.setAdapter(collectionAdapter);

        retrievePurchasedItems();

    }

    private void retrievePurchasedItems() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            DocumentReference userRef = db.collection("users").document(userId);
            userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            // Get the purchasedItems array from the user's document
                            ArrayList<String> purchasedItems = (ArrayList<String>) document.get("purchasedItems");
                            if (purchasedItems != null && !purchasedItems.isEmpty()) {
                                // If the user has purchased items, retrieve the ShopItems based on the item IDs
                                retrieveShopItems(purchasedItems);
                            } else {
                                // If the user has no purchased items, show a toast or handle accordingly
                                Toast.makeText(Collection.this, "No purchased items yet.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            });
        }
    }

    private void retrieveShopItems(ArrayList<String> itemIds) {
        // Get a reference to the "shopItems" collection in your Firestore database
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        ArrayList<ShopItems> shopItemsList = new ArrayList<>();

        // Iterate through the itemIds and retrieve the corresponding ShopItems
        for (String itemId : itemIds) {
            DocumentReference shopItemRef = db.collection("shopitems").document(itemId);
            shopItemRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        if (documentSnapshot.exists()) {
                            String item_id = documentSnapshot.getId();
                            String name = documentSnapshot.getString("name");
                            int price = documentSnapshot.getLong("price").intValue();
                            String imageUrl = getItemImageUrl(item_id); // Implement a method to fetch the image URL

                            ShopItems shopItem = new ShopItems(item_id, name, price, imageUrl);
                            shopItemsList.add(shopItem);


                            // Update the adapter with the retrieved data
                            collectionAdapter.setShopItemsList(shopItemsList);
                        }
                    }
                }
            });
        }
    }

    private String getItemImageUrl(String itemId) {
        // Replace "your_bucket_name" with the actual name of your Firebase Storage bucket
        // Assuming the images are stored in a folder called "items" within the bucket
        String storageReference = "gs://habitat-14e37.appspot.com" + itemId + ".png"; // Replace with the appropriate file extension

        // You can also use HTTPS URL by calling getDownloadUrl() and handling its callback.
        // For simplicity, we'll use the storageReference directly.
        return storageReference;
    }

}