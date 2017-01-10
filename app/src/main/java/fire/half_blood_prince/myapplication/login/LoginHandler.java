package fire.half_blood_prince.myapplication.login;

/**
 * Created by Half-Blood-Prince on 1/10/2017.
 * This class provide callback methods to activity hosting login and register fragment
 */

public interface LoginHandler {

    void login(String email, String password);

    void register(String name, String email, String password);

    void launchProgressDialog(String title, String message);

    void dismissProgressDialog();
}
