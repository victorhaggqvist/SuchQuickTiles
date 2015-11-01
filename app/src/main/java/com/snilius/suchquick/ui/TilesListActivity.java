package com.snilius.suchquick.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.snilius.suchquick.R;
import com.snilius.suchquick.data.DbConnection;
import com.snilius.suchquick.data.TileListAdapter;
import com.snilius.suchquick.entity.DaoSession;
import com.snilius.suchquick.entity.Shortcut;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.dao.query.LazyList;

public class TilesListActivity extends AppCompatActivity implements TileListAdapter.OnItemClickListener {

    @Bind(R.id.no_tiles)
    TextView mNoTiles;

    @Bind(R.id.tile_list)
    RecyclerView mList;

    private List<Shortcut> mShortcuts;
    private DaoSession session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tiles_list);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        session = DbConnection.getSession();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mList.setLayoutManager(layoutManager);

        mShortcuts = new ArrayList<>();
        TileListAdapter adapter = new TileListAdapter(mShortcuts, this);
        mList.setAdapter(adapter);
    }

    @OnClick(R.id.fab)
    void fabClick() {
        startActivity(new Intent(this, AddActivity.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<Shortcut> shortcuts = session.getShortcutDao().queryBuilder().build().listLazy();
        mShortcuts.clear();
        mShortcuts.addAll(shortcuts);
    }

    @Override
    public void OnItemClick(int position) {

    }
}
