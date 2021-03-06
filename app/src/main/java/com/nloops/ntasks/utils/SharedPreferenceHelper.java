package com.nloops.ntasks.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.nloops.ntasks.login.LocalUser;

public class SharedPreferenceHelper {

  private static SharedPreferenceHelper instance = null;
  private static SharedPreferences preferences;
  private static SharedPreferences.Editor editor;
  private static String SHARE_USER_INFO = "userinfo";
  private static String SHARE_KEY_NAME = "name";
  private static String SHARE_KEY_EMAIL = "email";
  private static String SHARE_KEY_AVATA = "avata";
  private static String SHARE_KEY_UID = "uid";


  private SharedPreferenceHelper() {
  }

  public static SharedPreferenceHelper getInstance(Context context) {
    if (instance == null) {
      instance = new SharedPreferenceHelper();
      preferences = context.getSharedPreferences(SHARE_USER_INFO, Context.MODE_PRIVATE);
      editor = preferences.edit();
    }
    return instance;
  }

  public void saveUserInfo(LocalUser user) {
    editor.putString(SHARE_KEY_NAME, user.name);
    editor.putString(SHARE_KEY_EMAIL, user.email);
    editor.putString(SHARE_KEY_UID, Constants.UID);
    editor.apply();
  }

  public LocalUser getUserInfo() {
    String userName = preferences.getString(SHARE_KEY_NAME, "");
    String email = preferences.getString(SHARE_KEY_EMAIL, "");
    String avatar = preferences.getString(SHARE_KEY_AVATA, "default");

    LocalUser user = new LocalUser();
    user.name = userName;
    user.email = email;
    user.avatar = avatar;

    return user;
  }

  public String getUID() {
    return preferences.getString(SHARE_KEY_UID, "");
  }

  public void updateUserUID() {
    editor.putString(SHARE_KEY_UID, "");
    editor.putString(SHARE_KEY_NAME, "");
    editor.putString(SHARE_KEY_EMAIL, "");
    editor.apply();
  }
}
