package ph.edu.dlsu.mobdeve.florendo.mapa.habitat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import com.bumptech.glide.Glide;

public class CollectionAdapter extends RecyclerView.Adapter<CollectionAdapter.ViewHolder> {

    private ArrayList<ShopItems> purchasedItemsList;

    // Constructor to receive the data
    public CollectionAdapter(ArrayList<ShopItems> purchasedItemsList) {
        this.purchasedItemsList = purchasedItemsList;
    }

    // ViewHolder class to hold the views of each item
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvItemName;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemName = itemView.findViewById(R.id.item_name_collection);
           // picture???
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout and create a ViewHolder
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.purchased_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Bind the data to the views in each item

        ShopItems purchasedItem = purchasedItemsList.get(position);
        holder.tvItemName.setText(purchasedItem.getItemName());



    }

    @Override
    public int getItemCount() {
        return purchasedItemsList.size();
    }

    public void setShopItemsList(ArrayList<ShopItems> shopItemsList) {
        this.purchasedItemsList = shopItemsList;
        notifyDataSetChanged();
    }
}
