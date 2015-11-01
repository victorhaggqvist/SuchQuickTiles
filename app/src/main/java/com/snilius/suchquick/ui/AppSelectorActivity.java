package com.snilius.suchquick.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.snilius.suchquick.R;
import com.snilius.suchquick.data.AppListAdapter;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AppSelectorActivity extends AppCompatActivity implements AppListAdapter.OnItemClickListener {

    public static final String EXTRA_APPINFO = "AppSelectorActivity.EXTRA_APPINFO";
    public static final String EXTRA_PACKAGENAME = "AppSelectorActivity.EXTRA_PACKAGENAME";
    public static final String EXTRA_LABEL = "AppSelectorActivity.EXTRA_LABEL";

    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.app_list)
    RecyclerView mList;
    private ArrayList<AppInfo> mAppInfoList;
    private AppListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_selector);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        mAppInfoList = new ArrayList<>();
        adapter = new AppListAdapter(mAppInfoList, this);

        mList.setLayoutManager(layoutManager);
        mList.setAdapter(adapter);

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading apps");
        progressDialog.setIndeterminate(true);
        progressDialog.show();

        new AsyncTask<Void, Void, Void>(){
            List<AppInfo> interm;

            @Override
            protected Void doInBackground(Void... params) {
                interm = new ArrayList<>();
                PackageManager pm = getPackageManager();
                List<ApplicationInfo> mApplications = pm.getInstalledApplications(PackageManager.GET_META_DATA);
                for (ApplicationInfo applicationInfo:mApplications) {

//                    Intent intent = pm.getLaunchIntentForPackage(applicationInfo.packageName);
                    Drawable icon = pm.getApplicationIcon(applicationInfo);
                    CharSequence label = pm.getApplicationLabel(applicationInfo);
                    interm.add(new AppInfo(applicationInfo.packageName, icon, label.toString()));
                }
                Collections.sort(interm, new Comparator<AppInfo>() {
                    @Override
                    public int compare(AppInfo lhs, AppInfo rhs) {
                        return lhs.getLabel().compareTo(rhs.getLabel());
                    }
                });
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                mAppInfoList.addAll(interm);
                adapter.notifyDataSetChanged();
                progressDialog.dismiss();
            }
        }.execute();

    }

    @Override
    public void OnItemClick(int position) {
        Intent intent = new Intent();
        AppInfo appInfo = mAppInfoList.get(position);
        intent.putExtra(EXTRA_LABEL, appInfo.getLabel());
        intent.putExtra(EXTRA_PACKAGENAME, appInfo.getPackageName());
        setResult(RESULT_OK, intent);
        finish();
    }

//    @org.parceler.Parcel
    public class AppInfo {

        private String packageName;
        private Drawable icon;
        private String label;

        public AppInfo() {
        }

        public AppInfo(String packageName, Drawable icon, String label) {

            this.packageName = packageName;
            this.icon = icon;
            this.label = label;
        }

        public String getPackageName() {
            return packageName;
        }

        public Drawable getIcon() {
            return icon;
        }

        public String getLabel() {
            return label;
        }
    }
}
