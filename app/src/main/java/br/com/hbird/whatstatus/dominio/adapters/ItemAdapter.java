package br.com.hbird.whatstatus.dominio.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import br.com.hbird.whatstatus.R;

import java.io.File;
import java.util.ArrayList;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    private ArrayList<File> itens;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private int itemDPParam;
    private float scale;

    // data is passed into the constructor
    public ItemAdapter(Context context, ArrayList<File> itens, int itemDPParam) {
        this.scale = context.getResources().getDisplayMetrics().density;

        this.mInflater = LayoutInflater.from(context);
        this.itens = itens;
        this.itemDPParam = itemDPParam;
    }

    // inflates the cell layout from xml when needed
    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_thumb, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each cell
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (itens.get(position).getName().contains(".mp4")) {
            holder.imgPlay.setVisibility(View.VISIBLE);
        } else {
            holder.imgPlay.setVisibility(View.GONE);
        }
        Glide.with(holder.imgThumb.getContext())
             .load(itens.get(position))
             .into(holder.imgThumb);
    }

    // total number of cells
    @Override
    public int getItemCount() {
        return itens.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imgThumb;
        ImageView imgPlay;

        ViewHolder(View itemView) {
            super(itemView);

            itemView.getLayoutParams().height = (int) (itemDPParam * scale);
            itemView.getLayoutParams().width = (int) (itemDPParam * scale);

            imgThumb = itemView.findViewById(R.id.img_thumb);
            imgPlay = itemView.findViewById(R.id.img_play);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    File getItem(int position) {
        return itens.get(position);
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}