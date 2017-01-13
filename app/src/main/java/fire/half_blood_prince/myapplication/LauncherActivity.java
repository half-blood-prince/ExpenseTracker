package fire.half_blood_prince.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import fire.half_blood_prince.myapplication.login.Login;

public class LauncherActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (isUserLoggedIn()) {
            startActivity(new Intent(this, HomeActivity.class));
        } else {
            startActivity(new Intent(this, Login.class));
        }

    }

    private boolean isUserLoggedIn() {
        return (FirebaseAuth.getInstance()).getCurrentUser() != null;
    }


}
