package com.snilius.suchquick.data;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.snilius.suchquick.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author Victor Häggqvist
 * @since 10/31/15
 */
public class IconListAdapter extends RecyclerView.Adapter<IconListAdapter.ViewHolder>  {
    private int[] icons;
    private OnItemClickListener listener;

    public IconListAdapter(int[] icons, OnItemClickListener listener) {

        this.icons = icons;
        this.listener = listener;
    }

    @Override
    public IconListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.icon_list_item, parent, false);
        return new ViewHolder(v, listener);
    }

    @Override
    public void onBindViewHolder(IconListAdapter.ViewHolder holder, int position) {
        holder.bind(icons[position]);
    }

    @Override
    public int getItemCount() {
        return icons.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final OnItemClickListener listener;
        @Bind(R.id.icon)
        ImageView icon;
        public ViewHolder(View itemView, OnItemClickListener listener) {
            super(itemView);
            this.listener = listener;
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        public void bind(int iconResource) {
            try {
                icon.setImageResource(iconResource);
            }catch (Exception e){}
        }

        @Override
        public void onClick(View v) {
            listener.OnItemClick(getAdapterPosition());
        }
    }

    public interface OnItemClickListener {
        void OnItemClick(int position);
    }
}
