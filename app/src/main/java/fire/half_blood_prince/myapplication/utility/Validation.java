package fire.half_blood_prince.myapplication.utility;

import android.support.design.widget.TextInputLayout;

import java.util.Locale;

/**
 * Created by Half-Blood-Prince on 1/10/2017.
 * Validation class used to validate input field's
 */

public class Validation {


    private static Validation sInstance;

    private Validation() {
        // Private Constructor
    }

    public static Validation getInstance() {
        if (null == sInstance) {
            synchronized (Validation.class) {
                if (null == sInstance)
                    sInstance = new Validation();
            }
        }
        return sInstance;
    }


    private static boolean isEmpty(String data, String errorMessage, TextInputLayout til) {
        if (data.isEmpty()) {
            if (null != til) til.setError(errorMessage);
            return true;
        }

        if (null != til) {
            til.setError(null);
            til.setErrorEnabled(false);
        }
        return false;
    }

    private static boolean checkMinLength(String data, String errorMessage, TextInputLayout til, int minLength) {

        if (data.length() < minLength) {
            til.setError(errorMessage);
            return false;
        }
        til.setError(null);
        til.setErrorEnabled(false);
        return true;
    }

    private static boolean checkMaxLength(String data, String errorMessage, TextInputLayout til, int maxLength) {

        if (data.length() > maxLength) {
            til.setError(errorMessage);
            return false;
        }
        til.setError(null);
        til.setErrorEnabled(false);
        return true;
    }

    public static boolean isPasswordValid(String data, TextInputLayout til, int minLength, int maxLength) {
        return (
                (!isEmpty(data, "Password is Required", til)) &&
                        (checkMinLength(data, String.format(Locale.getDefault(), "Password should be atleast %d letters long", minLength), til, minLength) &&
                                checkMaxLength(data, String.format(Locale.getDefault(), "Password should not exceeed %d letters", maxLength), til, maxLength))
        );
    }

    public static boolean isEmailValid(String emailID, TextInputLayout textInputLayout) {

        if (!isEmpty(emailID, "Email T_ID is Required", textInputLayout)) {
            String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
            java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
            java.util.regex.Matcher m = p.matcher(emailID);
            if (!m.matches()) {
                textInputLayout.setError("Please enter a valid Email T_ID");
                return false;
            }
            textInputLayout.setErrorEnabled(false);
            return true;
        }
        return false;
    }

    public static boolean isNameValid(String name, TextInputLayout textInputLayout) {

        if (!isEmpty(name, "Name is Required", textInputLayout)) {
            if (checkMinLength(name, "Name is too short", textInputLayout, 3)) {
                int charCount = 0;
                char currentChar;
                for (int i = 0; i < name.length(); i++) {
                    currentChar = name.charAt(i);
                    if ((currentChar >= 'a' && currentChar <= 'z') || (currentChar >= 'A' && currentChar <= 'Z') || currentChar == ' '
                            || currentChar == '.') {
                        charCount++;
                    }
                }
                if (charCount < (name.length() - 1)) {
                    textInputLayout.setError("Please enter a valid name");
                    return false;
                } else
                    return true;
            }
        }
        return false;
    }
}
