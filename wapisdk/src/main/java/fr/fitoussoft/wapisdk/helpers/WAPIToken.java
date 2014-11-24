package fr.fitoussoft.wapisdk.helpers;

import android.content.SharedPreferences;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by emmanuel.fitoussi on 24/11/2014.
 */
public class WAPIToken {
    private final static String PREF_ACCESS_TOKEN = "accessToken";
    private final static String PREF_REFRESH_TOKEN = "refreshToken";
    private final static String PREF_EXPIRE_TIME = "expireTime";
    private final static String PREF_SUB_DEBUG = "debug_";

    private boolean isDebug = false;

    private String refreshToken = "";
    private String accessToken = "";
    private Calendar expireDate;
    private SharedPreferences prefs;

    public WAPIToken(SharedPreferences prefs, boolean isDebug) {
        this.prefs = prefs;
        this.isDebug = isDebug;
        this.loadTokens();
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setTokens(String accessToken, String refreshToken, int expireIn) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        expireDate = Calendar.getInstance();
        expireDate.add(Calendar.SECOND, expireIn);
        this.saveTokens();
        Log.d("accessToken=" + this.accessToken);
        Log.d("refreshToken=" + this.refreshToken);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Log.d("expires=" + sdf.format(expireDate.getTime()));
    }

    private String getKey(String key) {
        return (isDebug ? PREF_SUB_DEBUG : "") + key;
    }

    public void saveTokens() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(getKey(PREF_ACCESS_TOKEN), accessToken);
        editor.putString(getKey(PREF_REFRESH_TOKEN), refreshToken);
        editor.putLong(getKey(PREF_EXPIRE_TIME), expireDate.getTimeInMillis());
        editor.commit();
    }

    public void loadTokens() {
        refreshToken = prefs.getString(getKey(PREF_REFRESH_TOKEN), "");
        accessToken = prefs.getString(getKey(PREF_ACCESS_TOKEN), "");
        long expireTime = prefs.getLong(getKey(PREF_EXPIRE_TIME), 0);
        expireDate = Calendar.getInstance();
        expireDate.setTimeInMillis(expireTime);
    }

    public void resetTokens() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(getKey(PREF_ACCESS_TOKEN));
        editor.remove(getKey(PREF_REFRESH_TOKEN));
        editor.remove(getKey(PREF_EXPIRE_TIME));
        editor.commit();
    }

    public boolean hasAccessToken() {
        return accessToken != null && !accessToken.isEmpty();
    }

    public boolean hasRefreshToken() {
        return refreshToken != null && !refreshToken.isEmpty();
    }

    public boolean hasExpired() {
        return expireDate == null || expireDate.getTimeInMillis() <= Calendar.getInstance().getTimeInMillis();
    }
}
