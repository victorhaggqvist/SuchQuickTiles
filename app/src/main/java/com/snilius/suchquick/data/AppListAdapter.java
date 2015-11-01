package com.snilius.suchquick.data;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.snilius.suchquick.R;
import com.snilius.suchquick.ui.AppSelectorActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author Victor Häggqvist
 * @since 10/31/15
 */
public class AppListAdapter extends RecyclerView.Adapter<AppListAdapter.ViewHolder> {
    private ArrayList<AppSelectorActivity.AppInfo> packages;
    private OnItemClickListener listener;

    public AppListAdapter(ArrayList<AppSelectorActivity.AppInfo> packages, OnItemClickListener listener) {
        this.packages = packages;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.app_list_item, parent, false);
        return new ViewHolder(v, listener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(packages.get(position));
    }

    @Override
    public int getItemCount() {
        return packages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @Bind(R.id.icon)
        ImageView icon;
        @Bind(R.id.name)
        TextView name;
        private final OnItemClickListener listener;

        public ViewHolder(View itemView, OnItemClickListener listener) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.listener = listener;
            itemView.setOnClickListener(this);
        }

        public void bind(AppSelectorActivity.AppInfo applicationInfo) {
            icon.setImageDrawable(applicationInfo.getIcon());
            name.setText(applicationInfo.getLabel());
            System.out.printf("");
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
