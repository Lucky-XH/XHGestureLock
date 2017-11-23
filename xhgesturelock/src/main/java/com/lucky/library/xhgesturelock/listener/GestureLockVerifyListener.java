package com.lucky.library.xhgesturelock.listener;

/**
 * Created by xhao on 2017/11/23.
 */

public interface GestureLockVerifyListener extends GestureLockListener {
    /**
     * 是否匹配
     * @param matched
     */
    void onGestureResult(boolean matched);
}
