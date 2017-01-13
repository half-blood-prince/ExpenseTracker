package fire.half_blood_prince.myapplication.utility;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by Half-Blood-Prince on 1/12/2017.
 * SharedConstants
 */

public interface SharedConstants {

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd",Locale.getDefault());

    int H_KEY_POPULATE_DS = 0xA0;
    int H_KEY_UPDATE_DS = 0xA1;

    String MAIN_PREF = "main_pref";

    String KEY_MODE = "mode";
    String MODE_INSERT = "insert_mode";
    String MODE_VIEW = "view_mode";
    String MODE_EDIT = "edit_mode";
    String MODE_DELETE = "delete_mode";
}

