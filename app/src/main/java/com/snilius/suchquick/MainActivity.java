package com.snilius.suchquick;

import android.Manifest;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.facebook.stetho.Stetho;
import com.kcoppock.broadcasttilesupport.BroadcastTileIntentBuilder;
import com.snilius.suchquick.data.DbConnection;
import com.snilius.suchquick.entity.DaoSession;
import com.snilius.suchquick.entity.IntentExtra;
import com.snilius.suchquick.entity.Shortcut;
import com.snilius.suchquick.ui.AddActivity;
import com.snilius.suchquick.ui.TilesListActivity;


import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cyanogenmod.app.CMStatusBarManager;
import cyanogenmod.app.CustomTile;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_SHORTCUT = 0001;
    @Bind(R.id.settile)
    Button butt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Timber.plant(new Timber.DebugTree());
        ButterKnife.bind(this);
        Stetho.initializeWithDefaults(this);

//        ContextCompat.checkSelfPermission(this, Manifest.permission.SETT)
//        String url = "content://com.android.launcher2.settings/favorites?Notify=true";
//        ContentResolver resolver = getContentResolver();
//        Cursor cursor = resolver.query(Uri.parse(url), null, null, null, null);
//        if (cursor.getCount() > 0) {
//            if (cursor.moveToFirst()) {
//                do {
//                    cursor.getString(1);
//                } while (cursor.moveToNext());
//            }
//        }
//        Timber.d(String.valueOf(cursor.getCount()));
//
//        Intent intent = new Intent(Intent.ACTION_MAIN);
//        intent.addCategory(Intent.CATEGORY_HOME);
//        ResolveInfo resolveInfo = getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
//        String currentHomePackage = resolveInfo.activityInfo.packageName;
//        Timber.d(currentHomePackage);
//
//        List<PackageInfo> packs = getPackageManager().getInstalledPackages(PackageManager.GET_PROVIDERS);
//        System.out.println();
//
//        final PackageManager pm = getPackageManager();
//        Intent main = new Intent(Intent.ACTION_MAIN, null);
//        main.addCategory(Intent.CATEGORY_LAUNCHER);
//        List<ApplicationInfo> installedApplications = pm.getInstalledApplications(PackageManager.GET_META_DATA);
//        List<ResolveInfo> packages = pm.queryIntentActivities(main, 0);
//
//        for (ResolveInfo resolve_info : packages)
//        {
//            try
//            {
//                String package_name = resolve_info.activityInfo.packageName;
//                ApplicationInfo applicationInfo = pm.getApplicationInfo(package_name, PackageManager.GET_META_DATA);
//
////                String app_name = (String)pm.getApplicationLabel(pm.getApplicationInfo(package_name, PackageManager.GET_META_DATA));
////
////                Timber.d("package = <" + package_name + "> name = <" + app_name + ">");
//            }
//            catch(Exception e)
//            {
//                //package not found -- should never happen
//            }
//        }
    }

    @OnClick(R.id.list)
    void muu() {
        startActivity(new Intent(this, TilesListActivity.class));
    }

    @OnClick(R.id.add)
    void add() {
        startActivity(new Intent(this, AddActivity.class));
    }

    @OnClick(R.id.settile)
    public void setTile() {

        Toast.makeText(this, "set", Toast.LENGTH_SHORT).show();
        Intent build = new BroadcastTileIntentBuilder(this, "smurf")
                .setVisible(true)
                .setLabel("Miao")
                .setContentDescription("pony")
                .build();

        sendBroadcast(build);
        Toast.makeText(this, "set", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.cut)
    public void cut() {
        Intent intent = new Intent(Intent.ACTION_CREATE_SHORTCUT);
//        intent.setAction(Intent.ACTION_CREATE_SHORTCUT);
        startActivityForResult(intent, REQUEST_CODE_SHORTCUT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data == null) return;

        Bundle bundle = data.getExtras();
        if (bundle == null) return;

        for (String key : bundle.keySet()) {
            Object value = bundle.get(key);
            Timber.d(String.format("%s %s (%s)", key,
                    value.toString(), value.getClass().getName()));
        }


        if (requestCode == REQUEST_CODE_SHORTCUT) {
            Timber.d(String.valueOf(requestCode));
            Bundle extras = data.getExtras();

            Intent sIntent = (Intent) extras.get("android.intent.extra.shortcut.INTENT");
            String sName = extras.getString("android.intent.extra.shortcut.NAME");
            Long shortcutId = setSchortcut(sName, sIntent);
            Timber.d(sIntent.getComponent().flattenToString());

            Intent inte = new Intent(sIntent.getAction());
            String comp = sIntent.getComponent().flattenToString();

            String className = sIntent.getComponent().getClassName();
            String packageName = sIntent.getComponent().getPackageName();
            inte.setComponent(new ComponentName(packageName, className));
//            startActivity(inte);

            try {
                bundle = sIntent.getExtras();
                for (String key : bundle.keySet()) {
                    Object value = bundle.get(key);
                    Timber.d(String.format("%s %s (%s)", key,
                            value.toString(), value.getClass().getName()));
                }
            } catch (Exception e){}
            Bitmap sIcon = (Bitmap) extras.get("android.intent.extra.shortcut.ICON");
            ImageView imageView = new ImageView(this);
            Drawable iconResource = new BitmapDrawable(sIcon);
            imageView.setImageDrawable(iconResource);
//            imageView.setId(1337);


//            Bundle sIntent = extras.getBundle("android.intent.extra.shortcut.INTENT");


//            startActivity(sIntent);

//            TaskStackBuilder.create(this).addNextIntent(sIntent).
            Intent actionIntent = new Intent(PublicBroadcastReceiver.ACTION_CLICK);
            actionIntent.putExtra(PublicBroadcastReceiver.EXTRA_SHORTCUT_ID, shortcutId);

            if (BuildConfig.VERSION_CODE >= Build.VERSION_CODES.M) {
                PendingIntent onLongClickPendingIntent = PendingIntent.getBroadcast(
                        this, 0, actionIntent, PendingIntent.FLAG_CANCEL_CURRENT);

//            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 00, sIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                Intent build = new BroadcastTileIntentBuilder(this, "smurf")
                        .setOnClickBroadcast(actionIntent)
//                            .setOnClickPendingIntent(onLongClickPendingIntent)
//                    .setIconResource()
                        .setVisible(true)
                        .setLabel(sName)
                        .setContentDescription("pony")
                        .build();

                sendBroadcast(build);
            } else if(cyanogenmod.os.Build.CM_VERSION.SDK_INT >= cyanogenmod.os.Build.CM_VERSION_CODES.BOYSENBERRY) {
                Timber.i("using cyan tab SDK "+cyanogenmod.os.Build.CM_VERSION.SDK_INT);
                // Define an intent that has an action of toggling a state
//                Intent intent = new Intent();
//                intent.setAction(ACTION_TOGGLE_STATE);
// initialize this state to off
//                intent.putExtra(MainActivity.STATE, States.STATE_OFF);

// Retrieve a pending intent from the system to be fired when the
// clicks the custom tile

                PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0,
                        actionIntent , PendingIntent.FLAG_UPDATE_CURRENT);

// Instantiate a builder object
                CustomTile customTile = new CustomTile.Builder(this)

                        .setOnClickIntent(pendingIntent)                      // set the pending intent
                        .setContentDescription("Nope nothing so see here. Just click it.")
                        .setLabel(sName)           // display current state
                        .shouldCollapsePanel(true)
                        .setIcon(sIcon)
//                        .setIcon(R.drawable.ic_launcher)
                        .build();                                             // build

//Publish our tile to the status bar panel with CUSTOM_TILE_ID defined elsewhere

                CMStatusBarManager.getInstance(this)
                        .publishTile(1, customTile); //Integer.parseInt(shortcutId.toString())
            }
//            if (requestCode == RESULT_OK) {
//                Toast.makeText(this, "good shortcut, launching it", Toast.LENGTH_SHORT).show();
//                startActivity(data);
//            } else {
//                Toast.makeText(this, "shortcut aborted", Toast.LENGTH_SHORT).show();
//            }
        }
    }

    private Long setSchortcut (String name, Intent intent) {
        DbConnection.setContext(this);
        DaoSession session = DbConnection.getSession();


//        Bundle extras = data.getExtras();

//        Intent sIntent = (Intent) extras.get("android.intent.extra.shortcut.INTENT");
//        Timber.d(sIntent.getComponent().flattenToString());

//        Intent inte = new Intent(intent.getAction());
//        String comp = intent.getComponent().flattenToString();

        String className = intent.getComponent().getClassName();
        String packageName = intent.getComponent().getPackageName();

        Shortcut shortcut = new Shortcut(null,"smurf", name, className, packageName);
        session.getShortcutDao().insert(shortcut);


        try {
            Bundle bundle = intent.getExtras();
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
                session.insert(intentExtra);

                Object value = bundle.get(key);
                Timber.d(String.format("%s %s (%s)", key,
                        value.toString(), value.getClass().getName()));
            }
        } catch (Exception e) {}
        return shortcut.getId();
    }
}
