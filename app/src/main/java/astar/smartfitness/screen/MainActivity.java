package astar.smartfitness.screen;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import java.util.List;

import astar.smartfitness.R;
import astar.smartfitness.model.User;
import astar.smartfitness.screen.profile.caregiver.EditProfileFragment;
import astar.smartfitness.screen.search.SearchContainerFragment;
import astar.smartfitness.util.OnBackPressedListener;
import astar.smartfitness.util.Utils;
import butterknife.Bind;
import butterknife.ButterKnife;
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

//        showCreateProfileFragment();
        showSearchFragment();
    }

    public void showCreateProfileFragment() {
        User user = Utils.getCurrentUser();
        String role = user.getRoles().get(0);

        //TODO: Open the correct fragment based on role

        replaceFragment(new EditProfileFragment(), false);
    }

    public void showSearchFragment() {
        User user = Utils.getCurrentUser();
        String role = user.getRoles().get(0);

        //TODO: Check if user is patient

        replaceFragment(new SearchContainerFragment(), false);
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
                        if (frag instanceof OnBackPressedListener) {
                            ((OnBackPressedListener) frag).onBackPressed();
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
