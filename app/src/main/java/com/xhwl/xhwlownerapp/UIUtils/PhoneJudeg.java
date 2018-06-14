package com.xhwl.xhwlownerapp.UIUtils;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

/**
 * Created by Administrator on 2017/8/14.
 */

public class PhoneJudeg {
    private Context context;
    public PhoneJudeg(Context context){
        this.context = context;
    }

    /**
     * 判断手机号是否合理
     *
     * @param phoneNums
     */
    public boolean judgePhoneNums(String phoneNums) {
        if (isMatchLength(phoneNums, 11)
                && isMobileNO(phoneNums)) {
            return true;
        }
        Toast.makeText(context, "手机号输入有误", Toast.LENGTH_SHORT).show();
        return false;
    }

    /**
     * 判断一个字符串的位数
     * @param str
     * @param length
     * @return
     */
    public static boolean isMatchLength(String str, int length) {
        if (str.isEmpty()) {
            return false;
        } else {
            return str.length() == length ? true : false;
        }
    }

    /**
     * 验证手机格式
     */
    public static boolean isMobileNO(String mobileNums) {
            /*
             *  移动：134、135、136、137、138、139、150、151、152、157(TD)、158、159、178(新)、182、184、187、188
             *  联通：130、131、132、152、155、156、185、186
             *  电信：133、153、170、173、177、180、181、189、（1349卫通）
             * 总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
             */
        String telRegex = "[1][3456789]\\d{9}";// "[1]"代表第1位为数字1，"[3578]"代表第二位可以为3、5、7、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        if (TextUtils.isEmpty(mobileNums))
            return false;
        else
            return mobileNums.matches(telRegex);
    }
}
