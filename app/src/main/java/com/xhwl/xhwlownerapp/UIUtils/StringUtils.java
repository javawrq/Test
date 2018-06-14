package com.xhwl.xhwlownerapp.UIUtils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by carey on 2016/5/31 0031.
 */
public class StringUtils {
    /**
     * 判断字符串是否为空 包括 null字符串
     *
     * @param str
     * @return
     */
    public static boolean isEmpty(String str) {
        return (str == null || str.trim().length() == 0 || str.equals("null"));
    }

    /***
     * 获取短信验证码的（yzm）参数
     *
     * @return
     */
    public static String getValidateCodeYzm() {
        String strMd5 = StringUtils.getMD5("wl");
        String subStr = strMd5.substring(0, 16);
        long milli = System.currentTimeMillis() / 1000L + 1;
        String milliMd5 = StringUtils.getMD5(String.valueOf(milli));
        String milliStr = milliMd5.substring(milliMd5.length() - 16, milliMd5.length());
        return subStr + milliStr;
    }

    /***
     * 获取当前的时间戳
     */
    public static long getCurrentTime() {
        return (System.currentTimeMillis() / 1000L + 1);
    }

    /**
     * 是否到达某个日期（与当前时间比）
     * @param date ""表示过期
     */
    public static boolean isExpired(String date) {
        Date nowDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        boolean bRet = true;
        Date mDate = null;
        try {
            if (!isEmpty(date)) {
                mDate = sdf.parse(date);
                bRet = mDate.before(nowDate);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return bRet;
    }

    /**
     * 是否大于某个日期
     */
    public static boolean isBefore(String startDateStr, String endDateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        boolean bRet = true;
        Date startDate = null;
        Date endDate = null;
        try {
            if (!isEmpty(startDateStr) && !isEmpty(endDateStr)) {
                startDate = sdf.parse(startDateStr);
                endDate = sdf.parse(endDateStr);
                bRet = startDate.before(endDate);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return bRet;
    }

    public static int minDistance(Date startDate, Date endDate) {
        if (startDate == null || endDate == null) {
            return 0;
        }
        int timeLong = (int) (endDate.getTime() - startDate.getTime());
        if (timeLong < 60 * 60 * 1000) {
            timeLong = timeLong / 1000 / 60;
            return timeLong;
        }
        return 0;
    }

    /**
     * 计算两个日期型的时间相差多少时间
     * @param startDate  开始日期
     * @param endDate    结束日期
     * @return
     */
    public static String dateDistance(Date startDate, Date endDate) {
        if (startDate == null || endDate == null) {
            return null;
        }
        long timeLong = endDate.getTime() - startDate.getTime();
        if (timeLong < 60 * 1000) {
            return timeLong / 1000 + "秒前";
        } else if (timeLong < 60 * 60 * 1000) {
            timeLong = timeLong / 1000 / 60;
            return timeLong + "分钟前";
        } else if (timeLong < 60 * 60 * 24 * 1000) {
            timeLong = timeLong / 60 / 60 / 1000;
            return timeLong + "小时前";
        } else if (timeLong < 60 * 60 * 24 * 1000 * 7) {
            timeLong = timeLong / 1000 / 60 / 60 / 24;
            return timeLong + "天前";
        } else if (timeLong < 60 * 60 * 24 * 1000 * 7 * 4) {
            timeLong = timeLong / 1000 / 60 / 60 / 24 / 7;
            return timeLong + "周前";
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
            return sdf.format(startDate);
        }
    }

    /**
     * 判断当天是否是工作日 (工作日：true；节假日：false)
     * @return
     */
    public static boolean isWorkDay() {
        //判断是否周六日
        Calendar c = Calendar.getInstance();
        int isWeek = c.get(Calendar.DAY_OF_WEEK);
        if (isWeek == Calendar.SUNDAY || isWeek == Calendar.SATURDAY) {
            return false;
        }
        return true;
    }

    /**
     * 格式化价格 返回(￥00.00)
     *
     * @param price
     * @return
     */
    public static String formatPrice(double price) {
        // 想要转换成指定国家的货币格式
        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.CHINA);
        // 把转换后的货币String类型返回
        return format.format(price);
    }

    /**
     * 定时弹出软键盘
     */
    public static void popUpKeyboard(final View view) {
        view.setFocusable(true);
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
                           public void run() {
                               InputMethodManager inputManager =
                                       (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                               inputManager.showSoftInput(view, 0);
                           }
                       },
                400);
    }


    /**
     * 验证邮箱
     *
     * @param email
     * @return
     */
    public static boolean checkEmail(String email) {
        boolean flag = false;
        try {
            String check = "^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
            Pattern regex = Pattern.compile(check);
            Matcher matcher = regex.matcher(email);
            flag = matcher.matches();
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }

    /**
     * 判断是否含有中文特殊字符
     * 英文特殊字符  _`~!@#$%^&*()+=|{}':;',\[\].<>/?~
     * @param str
     * @return true为包含，false为不包含
     */
    public static boolean isSpecialChar(String str) {
        String regEx = "[！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]|\n|\r|\t";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.find();
    }

    /**
     * 验证密码长度
     * @param str
     * @return
     */
    private static boolean checkPassword(String str){

        return true;
    }

    /**
     * 获取当前时间(时：小写的“h”是12小时制，大写的“H”是24小时制)
     * @return
     */
    public static String getTime() {
        SimpleDateFormat sDateFormat = new SimpleDateFormat("HH");
        return sDateFormat.format(new Date());
    }

    /**
     * 获取当前日期
     * @return
     */
    public static String getDate() {
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return sDateFormat.format(new Date());
    }

    /**
     * 格式化价格字符串 返回(￥00.00)
     *
     * @param priceStr
     * @return
     */
    public static String formatPrice(String priceStr) {
        double price = 0.0D;
        if (!isEmpty(priceStr)) {
            priceStr = Pattern.compile("[^0-9.]").matcher(priceStr).replaceAll(""); //替换所有数字小数点以外的字符
            try {
                price = Double.valueOf(priceStr);
            } catch (NumberFormatException ex) {
                Log.w("formatPrice", ex.getMessage());
            }
        }
        // 想要转换成指定国家的货币格式
        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.CHINA);
        // 把转换后的货币String类型返回
        return format.format(price);
    }


    /**
     * 获取图片地址字符串中的第一张图片
     *
     * @param imagesStr
     * @return
     */
    public static String getFirstImage(String imagesStr) {
        String[] imageArray = getImages(imagesStr);
        return imageArray == null ? null : imageArray[0];
    }

    /**
     * 获取图片数组
     *
     * @param imagesStr 图片地址字符串 格式：http://xcpnet.net/xxx.png|http://xcpnet.net/yyy.png
     * @return
     */
    public static String[] getImages(String imagesStr) {
        if (isEmpty(imagesStr)) {
            return null;
        } else {
            String[] images = imagesStr.split(",");
            for (int i = 0; i < images.length; i++) {
                String img = images[i];
                if (!img.startsWith("http:")) {
                    images[i] = img;
                }
            }
            return images;
        }
    }

    /**
     * 将时间戳转换为时间
     */
    public static String parseDate(long times) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(times * 1000));
    }

    /**
     * 验证手机号码
     *
     * @param phoneNumber
     * @return
     */
    public static boolean validatePhoneNumber(String phoneNumber) {
        if (phoneNumber != null) {
            Pattern p = Pattern.compile("1[0-9]{10}");
            return p.matcher(phoneNumber).matches();
        }
        return false;
    }

    /***
     * 身份证号验证
     * @param IDCard
     * @return
     */
    public static boolean isIDCard(String IDCard) {
        if (IDCard != null) {
            String IDCardRegex = "(^\\d{15}$)|(^\\d{18}$)|(^\\d{17}(\\d|X|x|Y|y)$)";
            return IDCard.matches(IDCardRegex);
        }
        return false;
    }

    /**
     * 判断是否是车牌号
     */
    public static boolean isCarNo(String CarNum) {
        //匹配第一位汉字
        String str = "京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼甲乙丙己庚辛壬寅辰戍午未申";
        if (!(CarNum == null || CarNum.equals(""))) {
            String s1 = CarNum.substring(0, 1);//获取字符串的第一个字符
            if (str.contains(s1)) {
                String s2 = CarNum.substring(1, CarNum.length());
                //不包含I O i o的判断
                if (s2.contains("I") || s2.contains("i") || s2.contains("O") || s2.contains("o")) {
                    return false;
                } else {
                    if (!CarNum.matches("^[\u4e00-\u9fa5]{1}[A-Z]{1}[A-Z_0-9]{5}$")) {
                        return true;
                    }
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
        return false;
    }

    /**
     * MD5加密
     *
     * @param info
     * @return
     */
    public static String getMD5(String info) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(info.getBytes("UTF-8"));
            byte[] encryption = md5.digest();

            StringBuffer strBuf = new StringBuffer();
            for (int i = 0; i < encryption.length; i++) {
                if (Integer.toHexString(0xff & encryption[i]).length() == 1) {
                    strBuf.append("0").append(Integer.toHexString(0xff & encryption[i]));
                } else {
                    strBuf.append(Integer.toHexString(0xff & encryption[i]));
                }
            }

            return strBuf.toString();
        } catch (NoSuchAlgorithmException e) {
            return "";
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }

    /**
     * 格式化日期显示
     */
    public static String formatDate(String mDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date d = null;
        try {
            d = formatter.parse(mDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return formatter.format(d);
    }

    /**
     * 判断 用户是否安装微信客户端
     */
    public static boolean isWeixinAvilible(Context context) {
        final PackageManager packageManager = context.getPackageManager();// 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.tencent.mm")) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断 用户是否安装QQ客户端
     */
    public static boolean isQQClientAvailable(Context context) {
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                Log.e("TAG", "isQQClientAvailable: "+pn);;
                if (pn.equalsIgnoreCase("com.tencent.qqlite") || pn.equalsIgnoreCase("com.tencent.mobileqq")) {
                    return true;
                }
            }
        }
        return false;
    }
}
