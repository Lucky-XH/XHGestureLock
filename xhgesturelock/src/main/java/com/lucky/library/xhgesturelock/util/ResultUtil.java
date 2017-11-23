package com.lucky.library.xhgesturelock.util;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author xhao
 * @date 2017/11/23
 */

public class ResultUtil {
    public static List<Integer> string2list(String result){
        List<Integer> list = new ArrayList<>();

        for (int i = 0; i < result.length(); i++) {
            list.add(Integer.valueOf(result.substring(i,i+1)));
        }
        return list;
    }
    public static String list2string(List<Integer> result){
        StringBuffer sb = new StringBuffer();
        for (Integer position : result) {
            sb.append(position);
        }
        return sb.toString();
    }
}
