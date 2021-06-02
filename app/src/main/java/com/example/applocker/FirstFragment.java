package com.example.applocker;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.applocker.databinding.FragmentFirstBinding;

public class FirstFragment extends Fragment {

    private static final String TAG = "FirstFragment";
    private FragmentFirstBinding binding;

    // Used to get the stored apps and shit onto main fragment
    private ListView mListAppInfo;
    DatabaseHelper mDatabaseHelper;

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

        // Make it possible to remove apps from here directly
        mListAppInfo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String name = parent.getItemAtPosition(position).toString();
                Log.d(TAG, "onItemClick: Clicked on " + name);

                Cursor data = mDatabaseHelper.getItemID(name);
                int itemID = -1;
                while (data.moveToNext()) {
                    itemID = data.getInt(0);
                }
                if (itemID > -1) {
                    Log.d(TAG, "onItemClick: The ID is " + itemID);
                    mDatabaseHelper.deleteApp(itemID,name);
                } else {
                    Log.e(TAG, "onItemClick: THE ID WAS NOT ABLE TO BE FOUND");
                }
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
                startActivity(intent);            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}