package astar.smartfitness.profile.caregiver;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import astar.smartfitness.MainActivity;
import astar.smartfitness.R;
import butterknife.Bind;
import butterknife.ButterKnife;

public class EditProfileFragment extends Fragment {

    @Bind(R.id.viewpager)
    ViewPager viewPager;

    @Bind(R.id.sliding_tabs)
    TabLayout tabLayout;

    private ProfileFragmentPagerAdapter pagerAdapter;

    public EditProfileFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_caregiver_edit_profile, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Create Profile");

        pagerAdapter = new ProfileFragmentPagerAdapter(getFragmentManager(), getActivity());
        viewPager.setAdapter(pagerAdapter);

        tabLayout.setupWithViewPager(viewPager);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (!(activity instanceof MainActivity)) {
            throw new ClassCastException("Activity must be MainActivity");
        }
    }

}
