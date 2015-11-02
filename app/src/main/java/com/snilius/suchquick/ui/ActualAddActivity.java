package com.snilius.suchquick.ui;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kcoppock.broadcasttilesupport.BroadcastTileIntentBuilder;
import com.snilius.suchquick.PublicBroadcastReceiver;
import com.snilius.suchquick.R;
import com.snilius.suchquick.data.DbConnection;
import com.snilius.suchquick.data.TileType;
import com.snilius.suchquick.entity.DaoSession;
import com.snilius.suchquick.entity.IntentExtra;
import com.snilius.suchquick.entity.Launcher;
import com.snilius.suchquick.entity.Shortcut;
import com.snilius.suchquick.entity.Tile;
import com.snilius.suchquick.entity.TileDao;
import com.snilius.suchquick.util.BitmapUtil;

import java.util.ArrayList;
import java.util.List;

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
    private static final int REQUEST_CODE_SELECT_SHORTCUT = 119;

    @Bind(R.id.task_icon)   LinearLayout mTaskIcon;
    @Bind(R.id.task_click)  LinearLayout mTaskClick;
    @Bind(R.id.task_long_click) LinearLayout mTaskLongClick;
    @Bind(R.id.show_icon)   ImageView mShowIcon;
    @Bind(R.id.show_label)  TextView mShowLabel;

    private Tile mTile;
    private Shortcut mShortcut;
    private Shortcut mLongShortcut;
    private Launcher mLauncher;
    private Launcher mLongLauncher;
    private List<IntentExtra> mIntentExtras;
    private List<IntentExtra> mLongIntentExtras;
    private DaoSession mSession;
    private String mSystemTileName;
    private Drawable mIconResource;

    private static final int REQUEST_LONG = 360;
    private static final int REQUEST_CLICK = 144;
    private int mTaskRequestType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actual_add);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        mSystemTileName = intent.getStringExtra(EXTRA_TILENAME);
        mSession = DbConnection.getSession();

        if(cyanogenmod.os.Build.CM_VERSION.SDK_INT >= cyanogenmod.os.Build.CM_VERSION_CODES.BOYSENBERRY) {
            mTaskLongClick.setVisibility(View.GONE);
        }
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
                            case 0:
                                Tile tile = getTile();
                                tile.setClickType(null);
                                break;
                            case 1:
                                mTaskRequestType = REQUEST_CLICK;
                                startActivityForResult(
                                        new Intent(ActualAddActivity.this, AppSelectorActivity.class),
                                        REQUEST_CODE_SELECT_APP);
                                break;
                            case 2:
                                mTaskRequestType = REQUEST_CLICK;
                                startActivityForResult(
                                        new Intent(Intent.ACTION_CREATE_SHORTCUT),
                                        REQUEST_CODE_SELECT_SHORTCUT);
                                break;
                        }
                    }
                });
        builder.create().show();
    }

    @OnClick(R.id.task_long_click)
    void taskLongClick() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose action")
                .setItems(R.array.actions_array, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                Tile tile = getTile();
                                tile.setLongClickType(null);
                                break;
                            case 1:
                                mTaskRequestType = REQUEST_LONG;
                                startActivityForResult(
                                        new Intent(ActualAddActivity.this, AppSelectorActivity.class),
                                        REQUEST_CODE_SELECT_APP);
                                break;
                            case 2:
                                mTaskRequestType = REQUEST_LONG;
                                startActivityForResult(
                                        new Intent(Intent.ACTION_CREATE_SHORTCUT),
                                        REQUEST_CODE_SELECT_SHORTCUT);
                                break;
                        }
                    }
                });
        builder.create().show();
    }

    @OnClick(R.id.task_title)
    void taskTitle() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Title");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String text = input.getText().toString();
                Tile tile = getTile();
                tile.setLabel(text);
                mShowLabel.setText(text);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private Tile getTile() {
        if (mTile == null) mTile = new Tile(null);
        return mTile;
    }

    private Launcher getLauncher() {
        if (mLauncher == null) mLauncher = new Launcher(null);
        return mLauncher;
    }

    private Launcher getLongLauncher() {
        if (mLongLauncher == null) mLongLauncher = new Launcher(null);
        return mLongLauncher;
    }

    private Shortcut getShortcut() {
        if (mShortcut == null) mShortcut = new Shortcut(null);
        return mShortcut;
    }

    private Shortcut getLongShortcut() {
        if (mLongShortcut == null) mLongShortcut = new Shortcut(null);
        return mLongShortcut;
    }

//    private static final int TILE = 667;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SELECT_ICON) {
            if (resultCode == RESULT_OK) {
                int iconRes = data.getIntExtra(IconSelectorActivity.EXTRA_ICON_ID, -1);
                mShowIcon.setImageResource(iconRes);

                Tile tile = getTile();
                tile.setIconResource(iconRes);
                tile.setIconIsPackageDrawable(false);
            }
        } else if (requestCode == REQUEST_CODE_SELECT_APP) {
            if (resultCode == RESULT_OK) {
                String label = data.getStringExtra(AppSelectorActivity.EXTRA_LABEL);
                String packageName = data.getStringExtra(AppSelectorActivity.EXTRA_PACKAGENAME);

                Intent laIntent = getPackageManager().getLaunchIntentForPackage(packageName);
                Drawable icon = null;
                try {
                    icon = getPackageManager().getActivityIcon(laIntent);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }

                Tile tile = getTile();
                if (mTaskRequestType == REQUEST_CLICK) {
//                    tile.setClickType(TileType.LAUNCHER);
                    mShowLabel.setText(label);
                    tile.setLabel(label);
                    if (cyanogenmod.os.Build.CM_VERSION.SDK_INT >= cyanogenmod.os.Build.CM_VERSION_CODES.BOYSENBERRY) {
                        mShowIcon.setImageDrawable(icon);
                        tile.setIconIsPackageDrawable(true);
                    } else {
                        tile.setIconIsPackageDrawable(false);
                    }

                    Launcher launcher = getLauncher();
                    launcher.setPackageName(packageName);
                } else if (mTaskRequestType == REQUEST_LONG) {
                    Launcher launcher = getLongLauncher();
                    launcher.setPackageName(packageName);
                }



//                if(cyanogenmod.os.Build.CM_VERSION.SDK_INT >= cyanogenmod.os.Build.CM_VERSION_CODES.BOYSENBERRY) {
//
////                    PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0,
////                            actionIntent , PendingIntent.FLAG_UPDATE_CURRENT);
//
//                    CustomTile customTile = new CustomTile.Builder(this)
//
////                            .setOnClickIntent(pendingIntent)                      // set the pending intent
//                            .setContentDescription("Nope nothing so see here. Just click it.")
//                            .setLabel(label)           // display current state
//                            .shouldCollapsePanel(true)
//                            .setIcon(BitmapUtil.drawableToBitmap(icon))
//                            .build();                                             // build
//
//
//                    CMStatusBarManager.getInstance(this)
//                            .publishTile(TILE, customTile); //Integer.parseInt(shortcutId.toString())
//                }
            }
        } else if (requestCode == REQUEST_CODE_SELECT_SHORTCUT) {
            Bundle extras = data.getExtras();

            Intent sIntent = (Intent) extras.get("android.intent.extra.shortcut.INTENT");
            String sName = extras.getString("android.intent.extra.shortcut.NAME");
////////////////////

            String className = sIntent.getComponent().getClassName();
            String packageName = sIntent.getComponent().getPackageName();

            Tile tile = getTile();
            if (mTaskRequestType == REQUEST_CLICK) {
                tile.setLabel(sName);
                mShowLabel.setText(sName);
                if (cyanogenmod.os.Build.CM_VERSION.SDK_INT >= cyanogenmod.os.Build.CM_VERSION_CODES.BOYSENBERRY) {
                    try {
                        mIconResource = getPackageManager().getActivityIcon(sIntent);
                        mShowIcon.setImageDrawable(mIconResource);
                        tile.setIconIsPackageDrawable(true);
                        // save the shortcuts icon, since you cant get it via package manager
//                    Bitmap sIcon = BitmapUtil.drawableToBitmap(mIconResource);
//                    File filesDir = getFilesDir();
//
//                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                    sIcon.compress(Bitmap.CompressFormat.PNG, 100, stream);
//                    try {
//                        File iconFile = new File(filesDir, sName);
//                        OutputStream outputStream = new FileOutputStream(iconFile);
//                        outputStream.write(stream.toByteArray());
//                        outputStream.close();
//                        tile.setIconPath(iconFile.getPath());
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                } else {
                    tile.setIconIsPackageDrawable(false);
                }
            }


            Shortcut shortcut = null;
            if (mTaskRequestType == REQUEST_CLICK) {
                shortcut = getShortcut();
            } else if (mTaskRequestType == REQUEST_LONG) {
                shortcut = getLongShortcut();
            }
            shortcut.setPackageName(packageName);
            shortcut.setClassName(className);

            mIntentExtras = new ArrayList<>();
            mLongIntentExtras = new ArrayList<>();
            try {
                Bundle bundle = sIntent.getExtras();
                if (bundle == null) throw new Exception();

                IntentExtra intentExtra = new IntentExtra(null);

                for (String key : bundle.keySet()) {
                    intentExtra.setKeyName(key);
                    try {
                        String str = bundle.getString(key);
                        intentExtra.setStringProp(str);
                    } catch (Exception e) {
                        Integer intg = bundle.getInt(key);
                        intentExtra.setIntProp(intg);
                    }
                    intentExtra.setShortcutId(shortcut.getId());

                    if (mTaskRequestType == REQUEST_CLICK) {
                        mIntentExtras.add(intentExtra);
                    } else if (mTaskRequestType == REQUEST_LONG) {
                        mLongIntentExtras.add(intentExtra);
                    }


                    Object value = bundle.get(key);
                    Timber.d(String.format("%s %s (%s)", key,
                            value.toString(), value.getClass().getName()));
                }
            } catch (Exception e) {}

            // ///////////////////////////////////////////
//            Long shortcutId = setSchortcut(sName, sIntent);
//            Timber.d(sIntent.getComponent().flattenToString());
//
//            Intent inte = new Intent(sIntent.getAction());
//            String comp = sIntent.getComponent().flattenToString();
//
//            String className = sIntent.getComponent().getClassName();
//            String packageName = sIntent.getComponent().getPackageName();
//            inte.setComponent(new ComponentName(packageName, className));
        }
        mTaskRequestType = -1;
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
            saveTile();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void saveTile() {
        if (mTile == null) {
            Toast.makeText(this, "Tile is not configured", Toast.LENGTH_LONG).show();
            return;
        }


        if (null == mTile.getLabel()) {
            mTile.setLabel("Title");
        }


        mTile.setSystemTileName(mSystemTileName);
        mTile.setEnabled(true);
        TileDao tileDao = mSession.getTileDao();

        if (mTile.getId() == null) tileDao.insert(mTile);
        else tileDao.update(mTile);


        if (mLauncher != null) {
            if (mLauncher.getId() == null) mSession.insert(mLauncher);
            else mSession.update(mLauncher);

            mTile.setClickType(TileType.LAUNCHER);
            mTile.setClickActionId(mLauncher.getId());
            tileDao.update(mTile);
        }

        if (mShortcut != null) {
            if (mShortcut.getId() == null) mSession.insert(mShortcut);
            else mSession.update(mShortcut);

            mTile.setClickType(TileType.SCHORTCUT);
            mTile.setClickActionId(mShortcut.getId());

            for (IntentExtra ie: mIntentExtras) {
                ie.setShortcutId(mShortcut.getId());

                if (ie.getId() == null) mSession.insert(ie);
                else mSession.update(ie);
            }
        }

        if (mLongShortcut != null) {
            if (mLongShortcut.getId() == null) mSession.insert(mLongShortcut);
            else mSession.update(mLongShortcut);

            mTile.setLongClickType(TileType.SCHORTCUT);
            mTile.setLongClickActionId(mLongShortcut.getId());

            for (IntentExtra ie: mLongIntentExtras) {
                ie.setShortcutId(mLongShortcut.getId());

                if (ie.getId() == null) mSession.insert(ie);
                else mSession.update(ie);
            }
        }

        if (mLongLauncher != null) {
            if (mLongLauncher.getId() == null) mSession.insert(mLongLauncher);
            else mSession.update(mLongLauncher);

            mTile.setLongClickType(TileType.LAUNCHER);
            mTile.setLongClickActionId(mLauncher.getId());
            tileDao.update(mTile);
        }

        Intent clickAction = null;
        Intent longClickAction = null;
        Drawable icon = null;
        int iconResource = 0;

        if (null == mTile.getClickType()) {
            clickAction = new Intent();
            if (!mTile.getIconIsPackageDrawable()) {
                iconResource = mTile.getIconResource();
            } else {
                mTile.setIconResource(-1);
            }

        } else if (mTile.getClickType().equals(TileType.LAUNCHER)) {
            Intent launcherIntent = getPackageManager().getLaunchIntentForPackage(mLauncher.getPackageName());
            clickAction = new Intent(PublicBroadcastReceiver.ACTION_LAUNCHER);
            clickAction.putExtra(PublicBroadcastReceiver.EXTRA_TILE_ID, mTile.getId());

            if (mTile.getIconIsPackageDrawable()){
                try {
                    icon = getPackageManager().getActivityIcon(launcherIntent);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            } else {
                if (mTile.getIconResource() != null) {
                    iconResource = mTile.getIconResource();
                }
//                else if(!mTile.getIconPath().equals(null)) {
//                    icon = mIconResource;
//                }

            }

        } else if (mTile.getClickType().equals(TileType.SCHORTCUT)) {
            clickAction = new Intent(PublicBroadcastReceiver.ACTION_SHORTCUT);
            clickAction.putExtra(PublicBroadcastReceiver.EXTRA_SHORTCUT_ID, mShortcut.getId());

            if (mTile.getIconIsPackageDrawable()) {
                Intent iconFetchIntent = new Intent()
                        .setComponent(new ComponentName(
                                mShortcut.getPackageName(),
                                mShortcut.getClassName()
                        ));
                try {
                    icon = getPackageManager().getActivityIcon(iconFetchIntent);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            } else {
                if (mTile.getIconResource() != null) {
                    iconResource = mTile.getIconResource();
                }
            }
        }

        if (null == mTile.getLongClickType()) {
            longClickAction = new Intent();
        } else if (mTile.getLongClickType().equals(TileType.LAUNCHER)) {
            longClickAction = new Intent(PublicBroadcastReceiver.ACTION_LAUNCHER);
            longClickAction.putExtra(PublicBroadcastReceiver.EXTRA_TILE_ID, mTile.getId());
            longClickAction.putExtra(PublicBroadcastReceiver.EXTRA_IS_LONG_CLICK, 1);
        } else if (mTile.getLongClickType().equals(TileType.SCHORTCUT)) {
            longClickAction = new Intent(PublicBroadcastReceiver.ACTION_SHORTCUT);
            longClickAction.putExtra(PublicBroadcastReceiver.EXTRA_SHORTCUT_ID, mLongShortcut.getId());
            longClickAction.putExtra(PublicBroadcastReceiver.EXTRA_IS_LONG_CLICK, 1);
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Timber.d("Publishing AOSP tile for "+mTile.getLabel());

            Intent build = new BroadcastTileIntentBuilder(this, mTile.getSystemTileName())
                    .setOnClickBroadcast(clickAction)
                    .setOnLongClickBroadcast(longClickAction)
                    .setIconResource(iconResource)
                    .setVisible(true)
                    .setLabel(mTile.getLabel())
                    .setContentDescription(mTile.getSystemTileName())
                    .build();

            sendBroadcast(build);
        } else if(cyanogenmod.os.Build.CM_VERSION.SDK_INT >= cyanogenmod.os.Build.CM_VERSION_CODES.BOYSENBERRY) {
            Timber.d("Publishing CM tile for "+mTile.getLabel());

            PendingIntent onClickPendingIntent = PendingIntent.getBroadcast(
                    this, 0, clickAction, PendingIntent.FLAG_CANCEL_CURRENT);

            PendingIntent onLongClickPendingIntent = PendingIntent.getBroadcast(
                    this, 0, longClickAction, PendingIntent.FLAG_CANCEL_CURRENT);

            CustomTile.Builder builder = new CustomTile.Builder(this)
                    .setOnClickIntent(onClickPendingIntent)
                    .setContentDescription("Nope nothing so see here. Just click it.")
                    .setLabel(mTile.getLabel())
                    .shouldCollapsePanel(true);

            if (icon != null) {
                builder.setIcon(BitmapUtil.drawableToBitmap(icon));
            } else {
                builder.setIcon(iconResource);
            }

            int id = Integer.parseInt(mTile.getId().toString());
            CMStatusBarManager.getInstance(this).publishTile(id, builder.build());
        }

    }
}
