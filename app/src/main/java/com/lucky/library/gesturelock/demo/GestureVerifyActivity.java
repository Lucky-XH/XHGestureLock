package com.lucky.library.gesturelock.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.lucky.library.xhgesturelock.listener.GestureLockVerifyListener;
import com.lucky.library.xhgesturelock.view.GestureLockViewGroup;


/**
 *
 * @author xhao
 */
public class GestureVerifyActivity extends AppCompatActivity {

    GestureLockViewGroup gestureLockViewGroup;

    TextView gesture_verify_tip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gesture_verify);
        initView();
        String password =  SharePreferencesUtil.get(GestureVerifyActivity.this, "password","123456").toString();
        gestureLockViewGroup.setPassword(password);
        gestureLockViewGroup.setOnGestureLockListener(new GestureLockVerifyListener() {
            @Override
            public void onGestureResult(boolean matched) {
                if(!matched){
                    //设置动画
                    Animation anim= AnimationUtils.loadAnimation(GestureVerifyActivity.this,R.anim.shake);
                    gesture_verify_tip.setVisibility(View.VISIBLE);
                    gesture_verify_tip.startAnimation(anim);
                    gestureLockViewGroup.showFailure();
                }else {
                    gesture_verify_tip.setVisibility(View.INVISIBLE);
                    Toast.makeText(GestureVerifyActivity.this,"校验成功",Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });


    }

    private void initView() {
        gestureLockViewGroup= (GestureLockViewGroup) findViewById(R.id.gesture_verify_lockView);
        gesture_verify_tip= (TextView) findViewById(R.id.gesture_verify_tip);
    }

}
