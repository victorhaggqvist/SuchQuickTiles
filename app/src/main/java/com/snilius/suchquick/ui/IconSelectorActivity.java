package com.snilius.suchquick.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.snilius.suchquick.R;
import com.snilius.suchquick.data.IconListAdapter;
import com.snilius.suchquick.util.MaterialIcons;

import butterknife.Bind;
import butterknife.ButterKnife;

public class IconSelectorActivity extends AppCompatActivity implements IconListAdapter.OnItemClickListener {

    public static final String EXTRA_ICON_ID = "IconSelectorActivity.EXTRA_ICON_ID";

    @Bind(R.id.icon_list)
    RecyclerView mList;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    private int[] mIcons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_icon_selector);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 8);
        mList.setLayoutManager(layoutManager);

        mIcons = MaterialIcons.getArray();
        IconListAdapter adapter = new IconListAdapter(mIcons, this);
        mList.setAdapter(adapter);
    }

    @Override
    public void OnItemClick(int position) {
        Intent result = new Intent();
        result.putExtra(EXTRA_ICON_ID, mIcons[position]);
        setResult(RESULT_OK, result);
        finish();
    }
}
