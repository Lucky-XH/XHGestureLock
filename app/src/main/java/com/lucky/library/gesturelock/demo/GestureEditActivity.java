package com.lucky.library.gesturelock.demo;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.lucky.library.gesturelock.listener.GestureLockPathListener;
import com.lucky.library.gesturelock.view.DotViewGroup;
import com.lucky.library.gesturelock.view.GestureLockViewGroup;

import java.util.List;

/**
 * @author xhao
 */
public class GestureEditActivity extends AppCompatActivity implements View.OnClickListener{


    String mPassword = "";

    GestureLockViewGroup gesture_edit_lockView;

    DotViewGroup dotViewGroup;

    TextView resetTv;

    TextView gesture_edit_tip;

    public static final int EDIT_INIT = 0;
    public static final int EDIT_FIRST = 1;
    public static final int EDIT_NOT_MATCH = 2;
    public static final int EDIT_MATCH = 3;

    public int mEditStatus = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gesture_edit);
        initView();
    }

    private void initView() {
        resetTv= (TextView) findViewById(R.id.rest_tv);
        resetTv.setOnClickListener(this);
        gesture_edit_tip= (TextView) findViewById(R.id.gesture_edit_tip);
        dotViewGroup= (DotViewGroup) findViewById(R.id.dotView);

        gesture_edit_lockView = (GestureLockViewGroup) findViewById(R.id.gesture_edit_lockView);
        gesture_edit_lockView.setOnGestureLockListener(new GestureLockPathListener() {

            @Override
            public void onGestureResult(String result) {
                if(result == null ||  "".equals(result)){
                    return;
                }
                if(mEditStatus == EDIT_INIT){
                    //第一次编辑
                    dotViewGroup.setPath(result);
                    mPassword = result;
                    resetTv.setVisibility(View.VISIBLE);
                    mEditStatus = EDIT_FIRST;
                    gesture_edit_lockView.setPassword(mPassword);

                }else{
                    //比较和第一的手势
                    Log.i("GestureLock",result.toString()+""+mPassword.toString());
                    if(result.compareTo(mPassword) == 0){
                        //两次一致
                        SharePreferencesUtil.put(GestureEditActivity.this,"password",result);
                        mEditStatus = EDIT_MATCH;
                        gesture_edit_lockView.showSuccess();
                        Toast.makeText(GestureEditActivity.this,"设置成功",Toast.LENGTH_SHORT).show();
                        finish();
                        //TODO
                    }else {
                        //提示不一致
                        gesture_edit_lockView.showFailure();
                        mEditStatus = EDIT_NOT_MATCH;
                    }

                }
                makeTip(mEditStatus);
            }

        });

    }

    public static  boolean compare(List<Integer> a, List<Integer> b) {
        if(a.size() != b.size()){
            return false;
        }

        for(int i=0;i<a.size();i++){
            if(!a.get(i).equals(b.get(i))){
                return false;
            }

        }
        return true;
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.rest_tv) {
            resetTv.setVisibility(View.INVISIBLE);
            mPassword = "";
            mEditStatus = EDIT_INIT;
            dotViewGroup.reset();
            makeTip(mEditStatus);

        }
    }

    private void makeTip(int value){
        Animation animation = AnimationUtils.loadAnimation(GestureEditActivity.this,R.anim.shake);
        switch (value){
            case EDIT_INIT:
                gesture_edit_tip.setText("绘制解锁图案");
                gesture_edit_tip.setTextColor(0xFF979999);
                break;
            case EDIT_FIRST:
                gesture_edit_tip.setText("再次绘制解锁图案");
                break;
            case EDIT_NOT_MATCH:
                gesture_edit_tip.setText("两次绘制图案不一致，请重新绘制");
                gesture_edit_tip.setTextColor(Color.RED);
                gesture_edit_tip.startAnimation(animation);
                break;
            case EDIT_MATCH:
                gesture_edit_tip.setText("设置成功");
                gesture_edit_tip.setTextColor(0xff108ee9);
                gesture_edit_tip.startAnimation(animation);
                break;
            default:
                break;
        }

    }
}
