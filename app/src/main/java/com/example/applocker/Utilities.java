package com.example.applocker;

import java.util.ArrayList;
import java.util.List;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class Utilities {

    DatabaseHelper mDatabaseHelper;

    /*
     * Get all installed application on mobile and return a list
     * @param   c   Context of application
     * @return  list of installed applications
     */
    public static List getInstalledApplication(Context c) {
        List<ApplicationInfo> apps = c.getPackageManager().getInstalledApplications(PackageManager.GET_META_DATA);

        final Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        final List<ResolveInfo> AppInAppDrawer = c.getPackageManager().queryIntentActivities(intent, 0);
        boolean flag = true;

        for (int i = apps.size() - 1; i >= 0; --i, flag = true)
        {
            for(int j = 0; j < AppInAppDrawer.size(); ++j)
            {
                if(AppInAppDrawer.get(j).toString().contains(apps.get(i).packageName))
                {
                    flag = false;
                    break;
                }
            }

            if(flag)
            {
                apps.remove(i);
            }
        }

        return apps;
    }

    /*
     * Launch an application
     * @param   c   Context of application
     * @param   pm  the related package manager of the context
     * @param   pkgName Name of the package to run
     */
    public static boolean launchApp(Context c, PackageManager pm, String pkgName) {
        // query the intent for lauching
        Intent intent = pm.getLaunchIntentForPackage(pkgName);
        // if intent is available
        if(intent != null) {
            try {
                // launch application
                c.startActivity(intent);
                // if succeed
                return true;

                // if fail
            } catch(ActivityNotFoundException ex) {
                // quick message notification
                Toast toast = Toast.makeText(c, "Application Not Found", Toast.LENGTH_LONG);
                // display message
                toast.show();
            }
        }
        // by default, fail to launch
        return false;
    }

    /*
    * getStoredApps:
    *  using the data base helper, locate all the stored app names and
    *   create a list by obtaining the actual apps.
    * @param   c   Context of application
    * @param   mDataBaseHelper  database
    */
    public static List getStoredApps(Context c, DatabaseHelper mDatabaseHelper) {
        List<ApplicationInfo> apps = new ArrayList<>();

        // Obtain the data from db
        Cursor data = mDatabaseHelper.getData();
        ArrayList<String> listData = new ArrayList<>();

        // Use the strings to get app information
        while (data.moveToNext()) {
            String app = data.getString(1);

            // Locate the app and add to list
            try {
                ApplicationInfo a = c.getPackageManager().getApplicationInfo(app, 0);
                apps.add(a);
            } catch (PackageManager.NameNotFoundException e) {
                // Alert for any errors
                Toast.makeText(c, "error in getting icon", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
        return apps;
    }
}
