package gov.cdc.stdtxguide;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent;

                if (!AppManager.pref.getBoolean(STDTxGuidePreferences.AGREED_TO_EULA, false))
                    intent = new Intent(getApplicationContext(), EulaActivity.class);
                else
                    intent = new Intent(getApplicationContext(), ConditionListActivity.class);

                startActivity(intent);
                finish();

            }
        }, 750);

    }

}
