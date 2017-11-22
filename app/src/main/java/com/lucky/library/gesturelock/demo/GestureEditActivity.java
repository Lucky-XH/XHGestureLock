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

import com.lucky.library.xhgesturelock.view.DotViewGroup;
import com.lucky.library.xhgesturelock.view.GestureLockViewGroup;

import java.util.ArrayList;
import java.util.List;

import static com.lucky.library.gesturelock.demo.GestureEditEnum.EDIT_FIRST;
import static com.lucky.library.gesturelock.demo.GestureEditEnum.EDIT_INIT;
import static com.lucky.library.gesturelock.demo.GestureEditEnum.EDIT_MATCH;
import static com.lucky.library.gesturelock.demo.GestureEditEnum.EDIT_NOT_MATCH;


/**
 *
 * @author xhao
 */
public class GestureEditActivity extends AppCompatActivity implements View.OnClickListener{


    List<Integer> password = new ArrayList<Integer>();

    GestureLockViewGroup gesture_edit_lockView;

    DotViewGroup dotViewGroup;

    TextView resetTv;

    TextView gesture_edit_tip;

    GestureEditEnum editStatu = EDIT_INIT;
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
        gesture_edit_lockView.setOnGestureLockListener(new GestureLockViewGroup.OnGestureLockResultListener() {

            @Override
            public void onGestureResult(List<Integer> result) {
                if(result == null || result.size() == 0){
                    return;
                }
                if(editStatu == EDIT_INIT){//第一次编辑
                    dotViewGroup.setPath(result);
                    password.addAll(result);
                    resetTv.setVisibility(View.VISIBLE);
                    editStatu = EDIT_FIRST;
                    gesture_edit_lockView.setPassword(password);

                }else{
                    //比较和第一的手势
                    Log.i("GestureLock",result.toString()+""+password.toString());
                    if(compare(result,password)){
                        //两次一致
                        editStatu = EDIT_MATCH;
                        gesture_edit_lockView.showSuccess();
                        Toast.makeText(GestureEditActivity.this,"设置成功",Toast.LENGTH_SHORT).show();
                        finish();
                        //TODO
                    }else {
                        //提示不一致
                        gesture_edit_lockView.showFailure();
                        editStatu= EDIT_NOT_MATCH;
                    }

                }
                makeTip(editStatu);
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
            password.clear();
            editStatu = EDIT_INIT;
            dotViewGroup.reset();
            makeTip(editStatu);

        }
    }

    private void makeTip(GestureEditEnum value){
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
