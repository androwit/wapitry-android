package fr.fitoussoft.wapisdk.helpers;

/**
 * Created by emmanuel.fitoussi on 13/11/2014.
 */
public class Log {
    /**
     * Logs a message on debug mode.
     *
     * @param message message text to log.
     */
    public static void d(String message) {
        android.util.Log.d("WAPI", message);
    }

    /**
     * Logs an error.
     *
     * @param message message text to log.
     */
    public static void e(String message) {
        android.util.Log.e("WAPI", message);
    }

}
