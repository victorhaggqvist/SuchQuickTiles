package com.snilius.suchquick.ui;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.snilius.suchquick.R;
import com.snilius.suchquick.data.DbConnection;
import com.snilius.suchquick.data.TileListAdapter;
import com.snilius.suchquick.data.TileType;
import com.snilius.suchquick.entity.DaoSession;
import com.snilius.suchquick.entity.Launcher;
import com.snilius.suchquick.entity.Shortcut;
import com.snilius.suchquick.entity.Tile;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TilesListActivity extends AppCompatActivity implements TileListAdapter.OnItemClickListener {

    @Bind(R.id.no_tiles)
    TextView mNoTiles;

    @Bind(R.id.tile_list)
    RecyclerView mList;

    private DaoSession session;
    private List<ListItem> mListItems;

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

        mListItems = new ArrayList<>();
        TileListAdapter adapter = new TileListAdapter(mListItems, this);
        mList.setAdapter(adapter);
    }

    @OnClick(R.id.fab)
    void fabClick() {
        startActivity(new Intent(this, AddActivity.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<Tile> tiles = session.getTileDao().queryBuilder().build().listLazy();

        mListItems.clear();
        for (Tile t:tiles) {
            ListItem item = new ListItem();
            item.setLabel(t.getLabel());

            if (t.getIconIsPackageDrawable()) {
                if (t.getClickType().equals(TileType.LAUNCHER)) {
                    Launcher launcher = session.getLauncherDao().load(t.getClickActionId());
                    Intent launchIntentForPackage = getPackageManager().getLaunchIntentForPackage(launcher.getPackageName());
                    try {
                        Drawable drawable = getPackageManager().getActivityIcon(launchIntentForPackage);
                        item.setIcon(drawable);
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                } else if (t.getClickType().equals(TileType.SCHORTCUT)) {
                    Shortcut shortcut = session.getShortcutDao().load(t.getClickActionId());
                    Intent intent = new Intent().setComponent(new ComponentName(shortcut.getPackageName(), shortcut.getClassName()));
                    try {
                        Drawable drawable = getPackageManager().getActivityIcon(intent);
                        item.setIcon(drawable);
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            } else if (t.getIconResource() != null) {
                item.setIconRes(t.getIconResource());
            }
            mListItems.add(item);
        }
    }

    @Override
    public void OnItemClick(int position) {
        // yeah I know, I should implement this
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_tiles_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {
            startActivity(new Intent(this, AboutActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public class ListItem {

        private Drawable icon;
        private String label;
        private int iconRes = -1;

        public ListItem(Drawable icon, String label) {
            this.icon = icon;
            this.label = label;
        }

        public ListItem() {

        }

        public Drawable getIcon() {
            return icon;
        }

        public void setIcon(Drawable icon) {
            this.icon = icon;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public int getIconRes() {
            return iconRes;
        }

        public void setIconRes(int iconRes) {
            this.iconRes = iconRes;
        }
    }
}
