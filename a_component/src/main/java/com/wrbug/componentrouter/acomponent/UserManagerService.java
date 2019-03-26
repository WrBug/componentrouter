package com.wrbug.componentrouter.acomponent;

import android.content.Context;
import android.content.SharedPreferences;

import com.wrbug.componentrouter.annotation.ConstructorRouter;
import com.wrbug.componentrouter.annotation.MethodRouter;
import com.wrbug.componentrouter.annotation.ObjectRoute;
import com.wrbug.componentrouter.annotation.SingletonRouter;

@ObjectRoute("/a/userManager")
public class UserManagerService {
    private static volatile UserManagerService instance;
    private Context mContext;
    private SharedPreferences mUserSharedPreferences;

    private UserManagerService(Context context) {
        mContext = context.getApplicationContext();
        mUserSharedPreferences = mContext.getSharedPreferences("user", Context.MODE_PRIVATE);
    }

    @SingletonRouter
    public static UserManagerService getInstance(Context context) {
        if (instance == null) {
            synchronized (UserManagerService.class) {
                if (instance == null) {
                    instance = new UserManagerService(context);
                }
            }
        }
        return instance;
    }

    @MethodRouter("saveUsername")
    public void saveUsername(String username) {
        mUserSharedPreferences.edit().putString("username", username).apply();
    }

    @MethodRouter("getUsername")
    public String getUsername() {
        return mUserSharedPreferences.getString("username", "");
    }

}
