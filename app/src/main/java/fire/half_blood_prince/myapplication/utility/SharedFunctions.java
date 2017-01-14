package fire.half_blood_prince.myapplication.utility;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import java.util.Arrays;
import java.util.Calendar;

/**
 * Created by Half-Blood-Prince on 1/10/2017.
 * Common functions used everywhere in the app
 */

public class SharedFunctions {


    /**
     * @param number number in string type
     * @return int number upon successful parsing , 0 otherwise
     */
    public static Double parseDouble(String number) {
        try {

            return Double.parseDouble(number);
        } catch (Exception ignored) {
            return 0.0;
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

    /**
     * Hide the keypad in screen
     *
     * @param context context obj
     * @param view    currently focused view
     */
    public static void hideKeypad(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * Show Date picker dialog and propagate the click events to the @onDateSetListener object
     *
     * @param context           context obj
     * @param onDateSetListener listener implementation obj
     */
    public static void datePicker(Context context, DatePickerDialog.OnDateSetListener onDateSetListener) {
        Calendar calendar = Calendar.getInstance();
        final int YEAR = calendar.get(Calendar.YEAR);
        final int MONTH = calendar.get(Calendar.MONTH);
        final int DAY = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePicker = new DatePickerDialog(context, onDateSetListener, YEAR, MONTH, DAY);
        datePicker.show();
    }

    public static void showSingleChoiceDialog(Context context, String title, final String[] mDataSet, String selected, DialogInterface.OnClickListener listener) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setSingleChoiceItems(mDataSet, Arrays.asList(mDataSet).indexOf(selected), listener)
                .show();
    }

    public static void showAlertDialog(Context context, String title, String message, String posBtn, String negBtn, DialogInterface.OnClickListener listener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        if (null != title) builder.setTitle(title);
        if (null != message) builder.setMessage(message);
        if (null != posBtn) builder.setPositiveButton(posBtn, listener);
        if (null != negBtn) builder.setNegativeButton(negBtn, listener);

        builder.create().show();

    }


}
