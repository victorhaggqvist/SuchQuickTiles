package com.snilius.suchquick.data;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.snilius.suchquick.R;
import com.snilius.suchquick.entity.DaoSession;
import com.snilius.suchquick.entity.Launcher;
import com.snilius.suchquick.entity.Shortcut;
import com.snilius.suchquick.entity.Tile;
import com.snilius.suchquick.ui.TilesListActivity;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author Victor Häggqvist
 * @since 10/31/15
 */
public class TileListAdapter extends RecyclerView.Adapter<TileListAdapter.ViewHolder> {


    private final DaoSession session;
    private Context context;
    private List<TilesListActivity.ListItem> tiles;
    private OnItemClickListener listener;

    public TileListAdapter(List<TilesListActivity.ListItem> tiles, OnItemClickListener listener) {
        this.tiles = tiles;
        this.listener = listener;
        session = DbConnection.getSession();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tile_list_item, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(tiles.get(position));
    }

    @Override
    public int getItemCount() {
        return tiles.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @Bind(R.id.icon)
        ImageView icon;
        @Bind(R.id.name)
        TextView name;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        public void bind(TilesListActivity.ListItem listItem) {
            name.setText(listItem.getLabel());

            if (listItem.getIcon() != null) {
                icon.setImageDrawable(listItem.getIcon());
            } else if (listItem.getIconRes() != -1) {
                icon.setImageResource(listItem.getIconRes());
            }
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
