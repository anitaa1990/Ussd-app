package com.an.ussdapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.an.ussdapp.R;
import com.an.ussdapp.databinding.ItemUSSDBinding;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class NetworkAdapter extends RecyclerView.Adapter<NetworkAdapter.CustomViewHolder> {

    private Context context;
    private List<String> items;
    public NetworkAdapter(Context context, List<String> items) {
        this.items = items;
        this.context = context;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemUSSDBinding binding = ItemUSSDBinding.inflate(layoutInflater, parent, false);
        CustomViewHolder viewHolder = new CustomViewHolder(binding);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        String title = getItem(position);
        holder.binding.itemTitle.setText(title);

        String desc = (position == 0) ? context.getString(R.string.item_desc_generic) :
                String.format(context.getString(R.string.item_desc), title);
        holder.binding.itemDesc.setText(desc);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public String getItem(int position) {
        return items.get(position);
    }

    protected class CustomViewHolder extends RecyclerView.ViewHolder {

        private ItemUSSDBinding binding;
        public CustomViewHolder(ItemUSSDBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
