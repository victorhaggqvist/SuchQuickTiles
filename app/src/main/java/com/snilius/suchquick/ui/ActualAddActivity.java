package com.snilius.suchquick.ui;

import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.snilius.suchquick.R;
import com.snilius.suchquick.util.BitmapUtil;

import org.parceler.Parcels;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cyanogenmod.app.CMStatusBarManager;
import cyanogenmod.app.CustomTile;
import timber.log.Timber;

public class ActualAddActivity extends AppCompatActivity {

    public static final String EXTRA_TILENAME = "ActualAddActivity.EXTRA_TILENAME";
    private static final int REQUEST_CODE_SELECT_ICON = 287;
    private static final int REQUEST_CODE_SELECT_APP = 478;

    @Bind(R.id.task_icon)
    LinearLayout mTaskIcon;
    @Bind(R.id.task_click)
    LinearLayout mTaskClick;
    @Bind(R.id.show_icon)
    ImageView mShowIcon;
    @Bind(R.id.show_label)
    TextView mShowLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actual_add);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }

    @OnClick(R.id.task_icon)
    void taskIcon() {
        startActivityForResult(new Intent(this, IconSelectorActivity.class), REQUEST_CODE_SELECT_ICON);
    }

    @OnClick(R.id.task_click)
    void taskClick() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose action")
                .setItems(R.array.actions_array, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 1:
                                startActivityForResult(
                                        new Intent(ActualAddActivity.this, AppSelectorActivity.class),
                                        REQUEST_CODE_SELECT_APP);
                                break;
                        }
                        // The 'which' argument contains the index position
                        // of the selected item
                    }
                });
        builder.create().show();
    }

    private static final int TILE = 667;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SELECT_ICON) {
            if (resultCode == RESULT_OK) {
                int iconRes = data.getIntExtra(IconSelectorActivity.EXTRA_ICON_ID, -1);
                mShowIcon.setImageResource(iconRes);
            }
        } else if (requestCode == REQUEST_CODE_SELECT_APP) {
            if (resultCode == RESULT_OK) {
                String label = data.getStringExtra(AppSelectorActivity.EXTRA_LABEL);
                String packageName = data.getStringExtra(AppSelectorActivity.EXTRA_PACKAGENAME);

//                AppSelectorActivity.AppInfo appInfo = Parcels.unwrap(parcelable);
                Intent laIntent = getPackageManager().getLaunchIntentForPackage(packageName);
                Drawable icon = null;
                try {
                    icon = getPackageManager().getActivityIcon(laIntent);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                mShowIcon.setImageDrawable(icon);
                mShowLabel.setText(label);

                if(cyanogenmod.os.Build.CM_VERSION.SDK_INT >= cyanogenmod.os.Build.CM_VERSION_CODES.BOYSENBERRY) {

//                    PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0,
//                            actionIntent , PendingIntent.FLAG_UPDATE_CURRENT);

                    CustomTile customTile = new CustomTile.Builder(this)

//                            .setOnClickIntent(pendingIntent)                      // set the pending intent
                            .setContentDescription("Nope nothing so see here. Just click it.")
                            .setLabel(label)           // display current state
                            .shouldCollapsePanel(true)
                            .setIcon(BitmapUtil.drawableToBitmap(icon))
                            .build();                                             // build


                    CMStatusBarManager.getInstance(this)
                            .publishTile(TILE, customTile); //Integer.parseInt(shortcutId.toString())
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_actual_add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_done) {

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
