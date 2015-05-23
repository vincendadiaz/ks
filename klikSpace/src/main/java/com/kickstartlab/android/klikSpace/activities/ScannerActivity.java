package com.kickstartlab.android.klikSpace.activities;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.kickstartlab.android.klikSpace.R;
import com.kickstartlab.android.klikSpace.events.ScannerEvent;
import com.kickstartlab.android.klikSpace.fragments.ScannerFragment;

import de.greenrobot.event.EventBus;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

public class ScannerActivity extends ActionBarActivity {

    Toolbar mToolbar;

    SmoothProgressBar mProgressBar;

    TextView mProgressIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mProgressBar = (SmoothProgressBar) findViewById(R.id.loadProgressBar);
        mProgressIndicator = (TextView) findViewById(R.id.progressMessage);

        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
        }

        mProgressBar.setVisibility(View.GONE);
        mProgressIndicator.setVisibility(View.GONE);

        EventBus.getDefault().register(this);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, ScannerFragment.newInstance("noid", "getcode"))
                    .commit();
        }
    }

    public void onEvent(ScannerEvent se){
        if( "sendCode".equalsIgnoreCase( se.getCommand() ) ){
            Bundle bundle = new Bundle();
            bundle.putString("resultText", se.getResult().getText() );
            Intent intent = new Intent();
            intent.putExtras(bundle);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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

}
