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
import android.widget.ArrayAdapter;
import android.widget.Toast;

public class Utilities {

    /*
     * Get all installed application on mobile and return a list
     * @param   c   Context of application
     * @return  list of installed applications
     */
    public static List getInstalledApplication(Context c) {
        List<ApplicationInfo> apps = c.getPackageManager().getInstalledApplications(PackageManager.GET_META_DATA);

        for (int i = 0; i < apps.size();)
        {
            if((apps.get(i).flags & ApplicationInfo.FLAG_SYSTEM) == 1)
            {
                apps.remove(i);
            }
            else
            {
                ++i;
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
}
