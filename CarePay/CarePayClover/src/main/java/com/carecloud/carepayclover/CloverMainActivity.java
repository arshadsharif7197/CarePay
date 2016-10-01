package com.carecloud.carepayclover;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

public class CloverMainActivity extends AppCompatActivity {

TextView checkedInCounterTextview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_clover);
        checkedInCounterTextview= (TextView) findViewById(R.id.checkedInCounterTextview);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        /*getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);*/
        ((TextView) findViewById(R.id.checkinTextView)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent checkedInIntent = new Intent(CloverMainActivity.this, CheckedInActivity.class);
                checkedInIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(checkedInIntent);
            }
        });
        registerReceiver(newCheckedInReceiver, new IntentFilter("NEW_CHECKEDIN_NOTIFICATION"));
    }
    BroadcastReceiver newCheckedInReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int count=Integer.parseInt(checkedInCounterTextview.getText().toString())+1;
            checkedInCounterTextview.setText(String.valueOf( count));
        }
    };
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(newCheckedInReceiver);
    }

   /* private class LongOperation extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                // Your Notification model


                 CloverAuth.AuthResult authorization = CloverAuth.authenticate(CloverMainActivity.this, CloverAccount.getAccount(CloverMainActivity.this));
                 GenericUrl uri = new GenericUrl(authorization.baseUrl +
                        "/v3/apps/" + authorization.appId +
                        "/merchants/" + authorization.merchantId +
                        "/notifications");

                Log.d("NotificationURL",uri.toString());

                Log.d("NotificationAuth",authorization.authToken);



            } catch (Exception e) {
                Log.d("NotificationURL","TestError");
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }*/

}
