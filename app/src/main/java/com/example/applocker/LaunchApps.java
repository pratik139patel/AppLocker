package com.example.applocker;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ApplicationInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;


public class LaunchApps extends AppCompatActivity {

    private static final String TAG = "LaunchApps";
    private ListView mListAppInfo;
    DatabaseHelper mDatabaseHelper;

    // For the popup menu
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private EditText passwordField;
    private Button save_btn, cancel_btn;
    private ImageView icon;

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

        // implement event when an item on list view is selected
        ImageView iconImage = new ImageView(this);
        mListAppInfo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int pos, long id) {
                ApplicationInfo selectedApp = (ApplicationInfo) mListAppInfo.getItemAtPosition(pos);
                // Passes Drawable icon, and the package name
                createNewPopup(selectedApp.loadIcon(getPackageManager()), selectedApp.packageName);
            }
        });
    }

    public void createNewPopup(Drawable appIconImage, String packageName) {
        Log.d(TAG, "createNewPopup: Generating popup");
        dialogBuilder = new AlertDialog.Builder(this);
        final View popupView = getLayoutInflater().inflate(R.layout.popup_menu, null);

        // Get the edittext, icon, and buttons attached from layout
        passwordField = (EditText) popupView.findViewById(R.id.password_field);
        save_btn = (Button) popupView.findViewById(R.id.save);
        cancel_btn = (Button) popupView.findViewById(R.id.cancel);
        icon = (ImageView) popupView.findViewById(R.id.appIcon);

        // Set the image to the icon provided
        icon.setImageDrawable(appIconImage);

        // Create the popup view
        dialogBuilder.setView(popupView);
        dialog = dialogBuilder.create();
        dialog.show();

        // Add button functionality for both buttons
        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtain password and package name and store in DB
                String password = passwordField.getText().toString();
                mDatabaseHelper.addData(packageName, password);
                toastMessage("App Saved");
                dialog.dismiss();
            }
        });
        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
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