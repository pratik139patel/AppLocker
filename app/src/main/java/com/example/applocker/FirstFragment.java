package com.example.applocker;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.database.Cursor;
import android.graphics.Color;
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
import androidx.fragment.app.Fragment;

import com.example.applocker.databinding.FragmentFirstBinding;

public class FirstFragment extends Fragment {

    private static final String TAG = "FirstFragment";
    private FragmentFirstBinding binding;
    private String selectedApp;

    // Used to get the stored apps and shit onto main fragment
    private ListView mListAppInfo;
    private DatabaseHelper mDatabaseHelper;

    @SuppressLint("ClickableViewAccessibility")
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
        AppInfoAdapter adapter = new AppInfoAdapter(getContext(), Utilities.getStoredApps(getContext(), mDatabaseHelper), super.getActivity().getPackageManager());
        mListAppInfo.setAdapter(adapter);
        Log.d(TAG, "onCreateView: List View correctly updated");

        PopupWindow menu = new PopupWindow(this.getContext());
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
//                mDatabaseHelper.updateDate(selectedApp, "");
                toastMessage("Password Updated");
                menu.dismiss();
            }
        });

        remove_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor data = mDatabaseHelper.getItemID(selectedApp);
                int itemID = -1;
                while (data.moveToNext())
                {
                    itemID = data.getInt(0);
                }

                Log.e(TAG, Integer.toString(itemID));
                Log.e(TAG, selectedApp);

                mDatabaseHelper.deleteApp(itemID, selectedApp);
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

        // Make it possible to remove apps from here directly
        mListAppInfo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ApplicationInfo appinfo = (ApplicationInfo)parent.getItemAtPosition(position);
                selectedApp = appinfo.packageName;
                tv.setText(appinfo.loadLabel(view.getContext().getPackageManager()));
                iconImage.setImageDrawable(appinfo.loadIcon(view.getContext().getPackageManager()));
                menu.showAtLocation(PopUpLayout, Gravity.CENTER, 0, 0);
            }
        });
        
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //NavHostFragment.findNavController(FirstFragment.this).navigate(R.id.action_FirstFragment_to_SecondFragment);
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

    private void toastMessage(String message) {
        Toast.makeText(this.getContext(), message, Toast.LENGTH_SHORT).show();
    }
}