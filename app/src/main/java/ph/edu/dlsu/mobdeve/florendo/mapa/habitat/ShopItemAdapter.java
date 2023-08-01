package ph.edu.dlsu.mobdeve.florendo.mapa.habitat;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;



public class ShopItemAdapter extends RecyclerView.Adapter<ShopItemAdapter.ViewHolder> {

    private ArrayList<ShopItems> shopItemsList;

    private OnItemClickListener itemClickListener;


    // Constructor to receive the data
    public ShopItemAdapter(Context context, ArrayList<ShopItems> shopItemsList) {
        this.shopItemsList = shopItemsList;
    }

    // ViewHolder class to hold the views of each item
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvItemName;
        private TextView tvItemPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemName = itemView.findViewById(R.id.item_name_display); // Replace with the actual TextView ID in your item layout
            tvItemPrice = itemView.findViewById(R.id.item_price_display); // Replace with the actual TextView ID in your item layout


        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout and create a ViewHolder
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.shop_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Bind the data to the views in each item
        ShopItems shopItem = shopItemsList.get(position);
        holder.tvItemName.setText(shopItem.getItemName());
        holder.tvItemPrice.setText(String.valueOf(shopItem.getPrice()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (itemClickListener != null) {
                    itemClickListener.onItemClick(shopItem); // Notify the listener that an item was clicked
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return shopItemsList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(ShopItems shopItem);
    }

    // Method to set the click listener from outside the adapter
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }
}

