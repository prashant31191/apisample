package com.azapps;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.azapps.recycler.ActNewsListing;

/**
 * A login screen that offers login via email/password.
 */
public class ActFeedback extends AppCompatActivity {


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
                if (strPassword != null && strPassword.length() > 0) {
                    App.showLog("==email==" + strEmail);
                    App.showLog("==password==" + strPassword);

                    if(strEmail.equalsIgnoreCase("test@gmail.com") && strPassword.equalsIgnoreCase("111111"))
                    {
                        startActivity( new Intent(ActFeedback.this, ActNewsListing.class));

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

}

