package com.example.compair;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;


import java.util.List;


public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {
    private List<CartItem> cartItemList;
    private Context context;
    private OnItemRemoveListener removeListener;


    public interface OnItemRemoveListener {
        void removeItem(int position);
    }


    public CartAdapter(List<CartItem> cartItemList, Context context, OnItemRemoveListener removeListener) {
        this.cartItemList = cartItemList;
        this.context = context;
        this.removeListener = removeListener;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CartItem item = cartItemList.get(position);
        holder.name.setText(item.getName());
        holder.description.setText(item.getDescription());
        holder.price.setText("£" + item.getPrice());
        Glide.with(context).load(item.getImageUrl()).into(holder.image);


        holder.removeButton.setOnClickListener(v -> removeListener.removeItem(position));
    }


    @Override
    public int getItemCount() {
        return cartItemList.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, description, price;
        ImageView image, removeButton;


        ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.item_name);
            description = itemView.findViewById(R.id.item_description);
            price = itemView.findViewById(R.id.item_price);
            image = itemView.findViewById(R.id.item_image);
            removeButton = itemView.findViewById(R.id.btn_remove);
        }
    }
}

