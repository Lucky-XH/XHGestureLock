package com.lucky.library.gesturelock.listener;

/**
 *
 * @author xhao
 * @date 2017/11/23
 */

public interface GestureLockPathListener extends GestureLockListener {
    /**
     * 手势密码
     * @param result
     */
    void onGestureResult(String result);
}
