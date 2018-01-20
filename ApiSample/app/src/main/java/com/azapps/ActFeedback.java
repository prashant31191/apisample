package com.azapps;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.azapps.recycler.ActNewsChannelsListing;
import com.azapps.recycler.ActNewsListing;
import com.flurry.android.FlurryAgent;
import com.flurry.android.ads.FlurryAdErrorType;
import com.flurry.android.ads.FlurryAdInterstitial;
import com.flurry.android.ads.FlurryAdInterstitialListener;

import java.util.HashMap;
import java.util.Map;

/**
 * A login screen that offers login via email/password.
 */
public class ActFeedback extends AppCompatActivity {


    String TAG = "ActFeedback";
    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_feedback);
        // Set up the login form.
        mEmailView = (EditText) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    //asd
                    return true;
                }
                return false;
            }
        });

        mEmailView.setText("test@gmail.com");
        mPasswordView.setText("111111");
        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //asd
                checkLogin();
            }
        });

    }

    private void checkLogin() {
        String strEmail = "", strPassword = "";
        try {
            strEmail = mEmailView.getText().toString().trim();
            strPassword = mPasswordView.getText().toString().trim();

            if (strEmail != null && strEmail.length() > 0) {
                Map<String, String> articleParams = new HashMap<String, String>();
                articleParams.put("email",strEmail);
                articleParams.put("password",strPassword);
                FlurryAgent.logEvent("credential", articleParams);

                if (strPassword != null && strPassword.length() > 0) {
                    App.showLog("==email==" + strEmail);
                    App.showLog("==password==" + strPassword);

                    if(strEmail.equalsIgnoreCase("test@gmail.com") && strPassword.equalsIgnoreCase("111111"))
                    {
                        FlurryAgent.endTimedEvent("credential");
                        //startActivity( new Intent(ActFeedback.this, ActNewsListing.class));
                        startActivity( new Intent(ActFeedback.this, ActNewsChannelsListing.class));

                    }
                    else
                    {
                        mEmailView.setError("Please enter valid email or password.");
                        mEmailView.requestFocus();
                    }


                } else {
                    mPasswordView.setError("Please enter valid password.");
                    mPasswordView.requestFocus();
                }
            } else {
                mEmailView.setError("Please enter valid email.");
                mEmailView.requestFocus();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
//    super.onBackPressed();
        showTransitionAd();
    }

    private void showTransitionAd() {
        FlurryAdInterstitial flurryAdInterstitial = new FlurryAdInterstitial(this, "INTERSTITIAL_MAIN_VIEW");
        flurryAdInterstitial.setListener(new FlurryAdInterstitialListener() {
            @Override
            public void onFetched(FlurryAdInterstitial flurryAdInterstitial) {
                Log.i(TAG, "Full screen ad fetched");
                flurryAdInterstitial.displayAd();
            }

            @Override
            public void onRendered(FlurryAdInterstitial flurryAdInterstitial) {

            }

            @Override
            public void onDisplay(FlurryAdInterstitial flurryAdInterstitial) {

            }

            @Override
            public void onClose(FlurryAdInterstitial flurryAdInterstitial) {
                ActFeedback.this.finish();
            }

            @Override
            public void onAppExit(FlurryAdInterstitial flurryAdInterstitial) {

            }

            @Override
            public void onClicked(FlurryAdInterstitial flurryAdInterstitial) {

            }

            @Override
            public void onVideoCompleted(FlurryAdInterstitial flurryAdInterstitial) {

            }

            @Override
            public void onError(FlurryAdInterstitial flurryAdInterstitial,
                                FlurryAdErrorType flurryAdErrorType, int i) {
                Log.e(TAG, "Full screen ad load error - Error type: " + flurryAdErrorType + " Code: " + i);
                Toast.makeText(ActFeedback.this, "Ad load failed", Toast.LENGTH_SHORT).show();
                ActFeedback.this.finish();
            }
        });

        FlurryAgent.onStartSession(this, App.FlurryApiKey);
        // fetch and prepare ad for this ad space. won’t render one yet


        flurryAdInterstitial.fetchAd();
    }

}

