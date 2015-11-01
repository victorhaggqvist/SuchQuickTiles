package com.snilius.suchquick.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.snilius.suchquick.BuildConfig;
import com.snilius.suchquick.R;
import com.snilius.suchquick.data.DbConnection;
import com.snilius.suchquick.entity.DaoSession;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;


public class AddActivity extends AppCompatActivity {

    @Bind(R.id.mode_header)
    TextView mModeHeader;
    @Bind(R.id.help)
    TextView mHelp;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    private String mTilename;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        DaoSession session = DbConnection.getSession();
        long count = session.getShortcutDao().count();

        mTilename = "sc" + (count + 1);
        Timber.d("tilename "+mTilename);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Timber.d("aosp mode");
            mModeHeader.setText(getString(R.string.mode_aosp));
            mHelp.setText(getString(R.string.help_m, mTilename));
        } else if (cyanogenmod.os.Build.CM_VERSION.SDK_INT > cyanogenmod.os.Build.CM_VERSION_CODES.BOYSENBERRY) {
            mModeHeader.setText(getString(R.string.mode_cm));
            mHelp.setText(R.string.help_cm);
        }
    }

    @OnClick(R.id.goto_add)
    void add() {
        Intent intent = new Intent(this, ActualAddActivity.class);
        intent.putExtra(ActualAddActivity.EXTRA_TILENAME, mTilename);
        startActivity(intent);
    }

    @OnClick(R.id.copy_tile_name)
    void copy() {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData data = ClipData.newPlainText("text", mTilename);
        clipboard.setPrimaryClip(data);
        Toast.makeText(this, "Tile name copied", Toast.LENGTH_SHORT).show();
    }
}
