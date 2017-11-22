package com.lucky.library.gesturelock.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * @author xhao
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button setLock;
    Button verifyLock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setLock = (Button) findViewById(R.id.setLock);
        setLock.setOnClickListener(this);
        verifyLock = (Button) findViewById(R.id.verifyLock);
        verifyLock.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent=new Intent();
        int i = v.getId();
        if (i == R.id.setLock) {
            intent.setClass(this, GestureEditActivity.class);

        } else if (i == R.id.verifyLock) {
            intent.setClass(this, GestureVerifyActivity.class);


        }
        startActivity(intent);

    }
}
