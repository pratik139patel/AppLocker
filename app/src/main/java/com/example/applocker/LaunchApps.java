package com.example.applocker;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.pm.ApplicationInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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
    private DatabaseHelper mDatabaseHelper;
    private PopupWindow menu = null;
    private String selectedApp = "";

    @SuppressLint("ClickableViewAccessibility")
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
        InputMethodManager imm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);

        EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT); // | TYPE_PASSWORD
        input.setBackgroundColor(Color.BLACK);
        input.setText("Enter Password");

        menu.setOutsideTouchable(true);

        input.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                input.requestFocus();
                input.setFocusableInTouchMode(true);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
//                if(!imm.isActive()) { imm.showSoftInput(input.getRootView(), InputMethodManager.SHOW_IMPLICIT); }
                return false;
            }
        });

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
        PopUpLayout.addView(input.getRootView());

        PopUpLayout.addView(ok_btn.getRootView());
        PopUpLayout.addView(cancel_btn.getRootView());
        ok_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabaseHelper.addData(selectedApp, "");
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
        menu.update(0, 0, displayMetrics.widthPixels - 250, displayMetrics.heightPixels - 1300);

        // implement event when an item on list view is selected
        mListAppInfo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int pos, long id)
            {
                ApplicationInfo appinfo = (ApplicationInfo) ((AppInfoAdapter) parent.getAdapter()).getItem(pos);
                selectedApp = appinfo.packageName;
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