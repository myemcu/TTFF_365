package com.myemcu.ttff_365.utils;

import android.app.Activity;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hui on 2016/8/26.
 */
public class ActivityManagerUtil {

    private Map<String,Activity>  mAcitivities;

    public static ActivityManagerUtil mInstance;// static代表放在共享区里面只有一份

    // 1.整个应用中只有单个实例    new 肯定不能让其他类实例化对象
    private ActivityManagerUtil(){ // 把构造函数私有化
        mAcitivities = new HashMap<>();
    }

    // 2.其他地方如果需要使用通过什么方式
    public static ActivityManagerUtil getInstance(){
        if(mInstance == null){
            synchronized (ActivityManagerUtil.class){// 你要考虑到同步 线程并发的问题
                if(mInstance == null){
                    mInstance = new ActivityManagerUtil();
                }
            }
        }
        return mInstance;
    }

    /**
     * 添加activity activity
     */
    public void addActivity(Activity activity){
        // （key   com.ffu365.MainActivity  , value MainActivity）
        mAcitivities.put(activity.getClass().getName() ,activity);
    }


    /**
     * 关闭Activity
     */
    public void finishActivity(Activity activity){
        Activity finishActivity = mAcitivities.get(activity.getClass().getName());
        finishActivity.finish();
        mAcitivities.remove(activity.getClass().getName());
    }

    /**
     * 关闭activity
     */
    public void finishActivity(Class<? extends  Activity> activityClazz){
        Activity finishActivity = mAcitivities.get(activityClazz.getName());
        finishActivity.finish();
        mAcitivities.remove(activityClazz.getName());
    }
}
