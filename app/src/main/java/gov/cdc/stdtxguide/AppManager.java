package gov.cdc.stdtxguide;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.util.Log;
import com.pushwoosh.Pushwoosh;
import com.pushwoosh.notification.PushwooshNotificationSettings;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**AppManager.java
 * lydia-droid
 *
 * Created by jason on 11/10/15.
 * Copyright (c) 2015 Informatics Research and Development Lab. All rights reserved.
 */
public class AppManager extends Application {
    public static SharedPreferences pref;
    public static SharedPreferences.Editor editor;
    public static ConditionContent conditionContent;
    public static SiteCatalystController sc;
    public static Pushwoosh pushManager;
    public static File sexualHistoryPdf;

    @Override
    public void onCreate() {
        super.onCreate();

        //set global instance of Shared Prefs and instantiate global editor
        pref = getApplicationContext().getSharedPreferences(STDTxGuidePreferences.PREFS_NAME, 0);
        editor =  pref.edit();

        //Set pref for app version name
        editor.putString(STDTxGuidePreferences.APP_VERSION, getApplicationVersionName()).commit();

        //Create global instance of ConditionContent to reduce duplicate processing
        conditionContent = new ConditionContent(getApplicationContext());


        sexualHistoryPdf = new File(getFilesDir(), "sexualhistory.pdf");

        if(!pref.getBoolean(STDTxGuidePreferences.SET_INITIAL_SETTINGS, false)){
            try {
                copyPdfAssetsToStorage();
            } catch (IOException e){
                Log.e("copyFailed", "copyFailed", e);
            }
            setDefaultPrefs();

            editor.putBoolean(STDTxGuidePreferences.SET_INITIAL_SETTINGS, true).commit();
        }

        // Create SiteCatalystController instance and log App Launch event.
        sc = new SiteCatalystController();
        sc.trackAppLaunchEvent();



        pushManager = Pushwoosh.getInstance();

        //Register for Pushwoosh
        if (pref.getBoolean(STDTxGuidePreferences.ALLOW_PUSH_NOTIFICATIONS, true)) {
            //Register for push!
            pushManager.registerForPushNotifications();
            PushwooshNotificationSettings.setMultiNotificationMode(true);
            PushwooshNotificationSettings.setNotificationChannelName("STD DEV");
        }
    }
    private void setDefaultPrefs(){
        editor.putBoolean(STDTxGuidePreferences.ALLOW_PUSH_NOTIFICATIONS, true);
        editor.putBoolean(STDTxGuidePreferences.AGREED_TO_EULA, false);
        editor.commit();
    }
    private void copyPdfAssetsToStorage() throws IOException{
        AssetManager assetManager = getAssets();

        InputStream in = null;
        OutputStream out = null;

        try {
            in = assetManager.open("sexualhistory.pdf");
            File outFile = new File(getFilesDir(), "sexualhistory.pdf");
            out = new FileOutputStream(outFile);

            byte[] buffer = new byte[1024];
            int read;
            while((read = in.read(buffer)) != -1){
                out.write(buffer, 0, read);
            }
        } catch (IOException e){
            Log.e("copyPDF", "Failed to copy asset file", e);
        }
        finally {
            if(out != null) {
                out.close();
            }
            if(in != null) {
                in.close();
            }
        }
    }

    public String getApplicationVersionName() {
        PackageManager packageManager = getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException ex) {} catch(Exception e){}
        return "";
    }
}

