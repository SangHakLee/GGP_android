package com.example.myapplication6;

import android.accounts.Account;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private GoogleApiClient mGoogleApiClient;
    private TextView mStatus;
    private static final int RC_SIGN_IN = 9001;
    private Button btn_disconnect;
    private SignInButton sign_in_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sign_in_button = (SignInButton) findViewById(R.id.sign_in_button);
        btn_disconnect = (Button)findViewById(R.id.btn_discon);
        mStatus = (TextView) findViewById(R.id.status);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(new Scope(Scopes.PROFILE))
                .build();

        //로그인
        sign_in_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mGoogleApiClient.connect();
            }
        });

        //로그아웃
        btn_disconnect.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if (mGoogleApiClient.isConnected()){
                    Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
                    Plus.AccountApi.revokeAccessAndDisconnect(mGoogleApiClient);
                    mGoogleApiClient.disconnect();
                    mStatus.setText("");
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnected(Bundle bundle) {
        Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
        String accountName = Plus.AccountApi.getAccountName(mGoogleApiClient);
        Account account = new Account(accountName, GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);

        if (currentPerson != null) {
            mStatus.append("name : "+currentPerson.getDisplayName());
            mStatus.append("\n");
            mStatus.append("EMAIL : "+accountName);
            mStatus.append("\n");
            mStatus.append("ID : "+currentPerson.getId());
            mStatus.append("\n");
            mStatus.append("Language : "+currentPerson.getLanguage());
            mStatus.append("\n");
            mStatus.append("URL : "+currentPerson.getUrl());


        } else {
            Log.w("Main", "error");
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(this, RC_SIGN_IN);

            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {

        }

    }
}
