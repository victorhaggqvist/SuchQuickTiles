package com.snilius.suchquick.data;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.snilius.suchquick.R;
import com.snilius.suchquick.entity.Shortcut;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author Victor Häggqvist
 * @since 10/31/15
 */
public class TileListAdapter extends RecyclerView.Adapter<TileListAdapter.ViewHolder> {


    private List<Shortcut> shortcuts;
    private OnItemClickListener listener;

    public TileListAdapter(List<Shortcut> shortcuts, OnItemClickListener listener) {

        this.shortcuts = shortcuts;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tile_list_item, parent, false);

        return new ViewHolder(v, listener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(shortcuts.get(position));
    }

    @Override
    public int getItemCount() {
        return shortcuts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @Bind(R.id.name)
        TextView name;
        public ViewHolder(View itemView, OnItemClickListener listener) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        public void bind(Shortcut shortcut) {
            name.setText(shortcut.getName());
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
