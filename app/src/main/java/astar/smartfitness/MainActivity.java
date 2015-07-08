package astar.smartfitness;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;

import astar.smartfitness.model.ProfileView;
import astar.smartfitness.model.User;
import astar.smartfitness.profile.caregiver.EditProfileFragment;
import astar.smartfitness.widget.CircleTransform;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.iwf.photopicker.PhotoPickerActivity;
import me.iwf.photopicker.utils.PhotoPickerIntent;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        showCreateProfileFragment();
    }

    private void showCreateProfileFragment() {
        User user = Utils.getCurrentUser();
        String role = user.getRoles().get(0);

        //TODO: Open the correct fragment based on role

        replaceFragment(new EditProfileFragment(), false);
    }

    private void replaceFragment(Fragment f) {
        replaceFragment(f, true);
    }

    private void replaceFragment(Fragment f, boolean addToBackStack) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        FrameLayout container = (FrameLayout) findViewById(R.id.container);
        if (container.getChildCount() == 0)
            ft.add(R.id.container, f);
        else {
            ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, R.anim.slide_in_right, R.anim.slide_out_left);
            ft.replace(R.id.container, f);
        }
        if (addToBackStack)
            ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean onBackPressed(FragmentManager fm) {
        if (fm != null) {
            if (fm.getBackStackEntryCount() > 0) {
                fm.popBackStack();
                return true;
            }

            List<Fragment> fragList = fm.getFragments();
            int size = fragList.size();
            if (fragList != null && fragList.size() > 0) {
                for (int i = 0; i < size; i++) {
                    Fragment frag = fragList.get(i);
                    if (frag == null) {
                        continue;
                    }
                    if (frag.isVisible()) {
                        if (frag instanceof EditProfileFragment) {
                            EditProfileFragment f = ((EditProfileFragment) frag);
                            f.onBackButtonClicked();
                            if (f.getCurrentState() == EditProfileFragment.PageState.BASIC)
                                return false;
                            else
                                return true;
                        } else if (onBackPressed(frag.getChildFragmentManager())) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        if (onBackPressed(fm)) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
