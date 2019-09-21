package com.skhu.cse.promiss.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class BasicDB {


        static final String PREF_USER_ID = "user_id";
        static final String PREF_USER_PW = "password";
        static final String PREF_ID="id";
        static final String PREF_Appoint="appoint";

        public static SharedPreferences getSharedPreferences(Context ctx) {
            return PreferenceManager.getDefaultSharedPreferences(ctx);
        }

        // 계정 정보 저장
        public static void setUserInfo(Context ctx, String userId, String userPw,int id) {
            SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
            editor.putInt(PREF_ID,id);
            editor.putString(PREF_USER_ID, userId);
            editor.putString(PREF_USER_PW, userPw);
            editor.apply();
        }
    // 저장된 정보 가져오기
        public static boolean getAppoint(Context ctx) {
        return getSharedPreferences(ctx).getBoolean(PREF_Appoint, false);
        }

        public static boolean setAppoint(Context ctx,boolean isAppoint)
        {
            SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
            editor.putBoolean(PREF_Appoint, isAppoint);
            editor.apply();
            return true;
        }

        // 저장된 정보 가져오기
        public static String getUserId(Context ctx) {
            return getSharedPreferences(ctx).getString(PREF_USER_ID, "");
        }

        public static int getID(Context ctx)
        {
            return getSharedPreferences(ctx).getInt(PREF_ID,-1);
        }
        public static boolean setUserId(Context ctx,String ID)
        {
            SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
            editor.putString(PREF_USER_ID, ID);

            editor.apply();
            return true;
        }
        public static boolean setUserPW(Context ctx,String pw)
        {
            SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
            editor.putString(PREF_USER_PW, pw);

            editor.apply();
            return true;
        }
        public static String getUserPw(Context ctx) {
            return getSharedPreferences(ctx).getString(PREF_USER_PW, "");
        }

        // 정보 삭제
        public static void clearUserInfo(Context ctx) {
            SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
            editor.clear();
            editor.apply();
        }

}
