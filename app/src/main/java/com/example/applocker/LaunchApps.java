package com.example.applocker;

import androidx.appcompat.app.AppCompatActivity;
import android.content.pm.ApplicationInfo;
import android.graphics.Color;
import android.graphics.drawable.Icon;
import android.media.Image;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;


public class LaunchApps extends AppCompatActivity {

    private static final String TAG = "LaunchApps";
    private ListView mListAppInfo;
    DatabaseHelper mDatabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: Started");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mDatabaseHelper = new DatabaseHelper(this);

        setContentView(R.layout.activity_launch_apps);
        // load list application
        mListAppInfo = (ListView) findViewById(R.id.lvApps);
        // create new adapter
        AppInfoAdapter adapter = new AppInfoAdapter(this, Utilities.getInstalledApplication(this), getPackageManager());
        // set adapter to list view
        mListAppInfo.setAdapter(adapter);

        PopupWindow menu = new PopupWindow(this);
        LinearLayout PopUpLayout = new LinearLayout(this);
        EditText input = new EditText(this);
        input.setBackgroundColor(Color.GREEN);
        input.setTextSize(100);
        TextView tv = new TextView(this);
        Button ok_btn = new Button(this);
        Button cancel_btn = new Button(this);

        ImageView iconImage = new ImageView(this);

        ok_btn.setText("OK");
        cancel_btn.setText("Cancel");

        tv.setTextSize(20);
        tv.setTextColor(Color.WHITE);

        PopUpLayout.addView(iconImage);
        PopUpLayout.addView(tv);
        PopUpLayout.addView(ok_btn.getRootView());
        PopUpLayout.addView(cancel_btn.getRootView());
        PopUpLayout.addView(input.getRootView());

        ok_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabaseHelper.addData(tv.getText().toString(), "");
                toastMessage("Password Saved");
                menu.dismiss();
                AppInfoAdapter adapter = new AppInfoAdapter(v.getContext(), Utilities.getInstalledApplication(v.getContext()), getPackageManager());
                mListAppInfo.setAdapter(adapter);
            }
        });

        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menu.dismiss();
            }
        });

        PopUpLayout.setOrientation(LinearLayout.VERTICAL);
        menu.setContentView(PopUpLayout);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        menu.update(0, 0, displayMetrics.widthPixels - 250, displayMetrics.heightPixels - 1000);

        // implement event when an item on list view is selected
        mListAppInfo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int pos, long id)
            {
                if(menu.isShowing())
                {
                    menu.dismiss();
                    return;
                }

                ApplicationInfo appinfo = (ApplicationInfo) ((AppInfoAdapter) parent.getAdapter()).getItem(pos);
                tv.setText(appinfo.loadLabel(getPackageManager()));
                iconImage.setImageDrawable(appinfo.loadIcon(getPackageManager()));
                menu.showAtLocation(PopUpLayout, Gravity.CENTER, 0, 0);
            }
        });
    }

    /*
    * toastMessage:
    *  Prints the relevant message on the screen for the user.
    *  Duration is short.
    * @param: message to be displayed
    */
    private void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}