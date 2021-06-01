package com.example.applocker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.applocker.databinding.ActivityMainBinding;


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


        //NIGGA=======================================================================================================================
        PopupWindow menu = new PopupWindow(this);
        LinearLayout PopUpLayout = new LinearLayout(this);
        TextView tv = new TextView(this);
        tv.setBackgroundColor(Color.WHITE);
        PopUpLayout.addView(tv);
        PopUpLayout.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus)
                {
                    menu.dismiss();
                }
            }
        });

        PopUpLayout.setOrientation(LinearLayout.VERTICAL);
        menu.setContentView(PopUpLayout);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        menu.update(0, 0, displayMetrics.widthPixels - 250, displayMetrics.heightPixels - 1000);


        //NIGGA=======================================================================================================================


        // implement event when an item on list view is selected
        mListAppInfo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int pos, long id) {

                //NIGGA=======================================================================================================================
                if(menu.isShowing())
                {
                    menu.dismiss();
                    return;
                }

                //NIGGA=======================================================================================================================

                // get the list adapter
                AppInfoAdapter appInfoAdapter = (AppInfoAdapter) parent.getAdapter();
                // get selected item on the list
                ApplicationInfo appInfo = (ApplicationInfo) appInfoAdapter.getItem(pos);
                // launch the selected application
                if (appInfo.packageName.length() != 0) {
                    AddData(appInfo.packageName);
                }

                tv.setText(appInfo.packageName);
                menu.showAtLocation(PopUpLayout, Gravity.CENTER, 0, 0);
            }
        });
    }

    /*
    * AddData:
    *  This will add the provided app name to the database.
    * Prints toast depending on the status of the data entry.
    * @param: the new apps name
    */
    public void AddData(String newApp) {

        // Toast message of data insertion status
        switch(mDatabaseHelper.addData(newApp)) {
            case 0:
                toastMessage("App was not successfulyl added");
                break;
            case 1:
                toastMessage("App was succesfully added");
                break;
            case 2:
                toastMessage("App is already locked");
                break;
            default:  // Never should be triggered
                toastMessage("Error in process");
        }
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