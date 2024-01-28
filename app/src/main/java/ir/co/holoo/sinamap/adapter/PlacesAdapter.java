package ir.co.holoo.sinamap.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ir.co.holoo.sinamap.R;
import ir.co.holoo.sinamap.model.Place;


public abstract class PlacesAdapter extends RecyclerView.Adapter<PlacesAdapter.ViewHolder> {
    private final ArrayList<Place> list;

    public PlacesAdapter(ArrayList<Place> listData) {
        this.list = listData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        final View listItem = layoutInflater.inflate(R.layout.item_place, parent, false);
        return new ViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Place item = list.get(position);
        holder.tvName.setText(item.getName());

        holder.itemView.setOnClickListener(v -> {
            onClick(item);
        });
    }

    public abstract void onClick(Place place);

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvName;

        public ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
        }
    }
}