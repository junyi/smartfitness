package astar.smartfitness;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;

import com.parse.ParseUser;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class LaunchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        if (ParseUser.getCurrentUser() != null) {
            onLoginSuccess();
        }
        showLoginFragment();
    }

    private void showLoginFragment() {
        FrameLayout container = (FrameLayout) findViewById(R.id.container);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (container.getChildCount() == 0)
            ft.add(R.id.container, new LoginFragment());
        else
            ft.replace(R.id.container, new LoginFragment());
        ft.commit();
    }


    public void gotoPresignup() {
        replaceFragment(new PreSignupFragment());
    }

    public void gotoSignup(String role) {
        SignupFragment f = SignupFragment.newInstance(role);
        replaceFragment(f);
    }

    public void fromSignupSuccess() {
        showLoginFragment();
        Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), "Sign up success!", Snackbar.LENGTH_SHORT).show();
    }

    private void replaceFragment(Fragment f) {
        replaceFragment(f, true);
    }

    private void replaceFragment(Fragment f, boolean addToBackStack) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, R.anim.slide_in_right, R.anim.slide_out_left);
        ft.replace(R.id.container, f);
        if (addToBackStack)
            ft.addToBackStack(null);
        ft.commit();
    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public void onLoginSuccess() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }
}
