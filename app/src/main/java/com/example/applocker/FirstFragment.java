package com.example.applocker;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.applocker.databinding.FragmentFirstBinding;

public class FirstFragment extends Fragment {

    private static final String TAG = "FirstFragment";
    private FragmentFirstBinding binding;
    private String selectedApp;

    // Used to get the stored apps and shit onto main fragment
    private ListView mListAppInfo;
    private DatabaseHelper mDatabaseHelper;

    // For the popup menu
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private EditText passwordField;
    private Button remove_btn, cancel_btn, update_btn;
    private ImageView icon;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        Log.d(TAG, "onCreate: Started");
        binding = FragmentFirstBinding.inflate(inflater, container, false);
        
        // Altered the original layout to print the apps that are saved here instead.
        mDatabaseHelper = new DatabaseHelper(getContext());
        Log.d(TAG, "onCreate: Database helper was correctly initialized");
        
        // Use the binding to locate the list view then apply the adapter to it
        mListAppInfo = binding.savedAppsTest;
        AppInfoAdapter adapter = new AppInfoAdapter(getContext(), Utilities.getStoredApps(getContext(),
                mDatabaseHelper), super.getActivity().getPackageManager());
        mListAppInfo.setAdapter(adapter);
        Log.d(TAG, "onCreateView: List View correctly updated");

        /*PopupWindow menu = new PopupWindow(this.getContext());
        LinearLayout PopUpLayout = new LinearLayout(this.getContext());
        InputMethodManager imm = (InputMethodManager) this.getContext().getSystemService(this.getContext().INPUT_METHOD_SERVICE);

        EditText input = new EditText(this.getContext());
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

        TextView tv = new TextView(this.getContext());
        Button update_btn = new Button(this.getContext());
        Button remove_btn = new Button(this.getContext());
        Button cancel_btn = new Button(this.getContext());
        ImageView iconImage = new ImageView(this.getContext());

        update_btn.setText("Update");
        remove_btn.setText("Remove");
        cancel_btn.setText("Cancel");

        tv.setTextSize(20);
        tv.setTextColor(Color.WHITE);

        PopUpLayout.addView(iconImage);
        PopUpLayout.addView(tv);
        PopUpLayout.addView(input.getRootView());

        PopUpLayout.addView(update_btn.getRootView());
        PopUpLayout.addView(remove_btn.getRootView());
        PopUpLayout.addView(cancel_btn.getRootView());

        update_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabaseHelper.updateCol(selectedApp, "testing this shit");
                toastMessage("Password Updated");
                menu.dismiss();
            }
        });

        remove_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabaseHelper.deleteApp(selectedApp);
                toastMessage("Password Removed");
                menu.dismiss();
                AppInfoAdapter adapter = new AppInfoAdapter(v.getContext(), Utilities.getStoredApps(v.getContext(), mDatabaseHelper), v.getContext().getPackageManager());
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
        this.getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        menu.update(0, 0, displayMetrics.widthPixels - 250, displayMetrics.heightPixels - 1300);

*/
        // Make it possible to remove apps from here directly
        mListAppInfo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ApplicationInfo appinfo = (ApplicationInfo)parent.getItemAtPosition(position);
                // Passes Drawable icon, and the package name
                createNewPopup(appinfo.loadIcon(getContext().getPackageManager()), appinfo.packageName);
            }
        });
        
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), LaunchApps.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void createNewPopup(Drawable appIconImage, String packageName) {
        Log.d(TAG, "createNewPopup: Generating Popup to Manage Apps");
        dialogBuilder = new AlertDialog.Builder(super.getContext());
        final View popupView = getLayoutInflater().inflate(R.layout.manage_saved_app_popup, null);

        // Get the edittext, icon, and buttons attached from layout
        passwordField = (EditText) popupView.findViewById(R.id.update_password_field);
        remove_btn = (Button) popupView.findViewById(R.id.remove_btn);
        cancel_btn = (Button) popupView.findViewById(R.id.cancel_btn);
        update_btn = (Button) popupView.findViewById(R.id.update_btn);
        icon = (ImageView) popupView.findViewById(R.id.savedAppIcon);

        // Set the image to the icon provided
        icon.setImageDrawable(appIconImage);

        // Create the popup view
        dialogBuilder.setView(popupView);
        dialog = dialogBuilder.create();
        dialog.show();

        // Add button functionality for both buttons
        update_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtain password and package name and store in DB
                String password = passwordField.getText().toString();
                mDatabaseHelper.updateCol(packageName, password);
                toastMessage("Password Updated");
                dialog.dismiss();
            }
        });
        remove_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabaseHelper.deleteApp(packageName);
                toastMessage("Password Removed");
                dialog.dismiss();

                // "Refreshes" the list view to show changes
                AppInfoAdapter adapter = new AppInfoAdapter(v.getContext(), Utilities.getStoredApps(v.getContext(), mDatabaseHelper), v.getContext().getPackageManager());
                mListAppInfo.setAdapter(adapter);
            }
        });
        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void toastMessage(String message) {
        Toast.makeText(this.getContext(), message, Toast.LENGTH_SHORT).show();
    }
}