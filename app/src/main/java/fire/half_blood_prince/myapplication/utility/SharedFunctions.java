package fire.half_blood_prince.myapplication.utility;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

/**
 * Created by Half-Blood-Prince on 1/10/2017.
 * Common functions used everywhere in the app
 */

public class SharedFunctions {


    /**
     * @param number number in string type
     * @return int number upon successful parsing , 0 otherwise
     */
    public static int parseInt(String number) {
        try {
            return Integer.parseInt(number);
        } catch (Exception ignored) {
            return 0;
        }
    }

    /**
     * @param log log to print
     */
    public static void printLog(String log) {
        Log.d("garu", log);
    }

    /**
     * @param context activity instance
     * @return true when data network is connected ,  false otherwise
     */
    public static boolean isDataNetworkConnected(Context context) {
        NetworkInfo activeNetworkInfo = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /**
     * @param view child view of textview
     * @param <T>  Actual View
     * @return String inside the view
     */
    public static <T extends TextView> String getText(@NonNull T view) {
        return view.getText().toString().trim();
    }

    public static void hideKeypad(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}
