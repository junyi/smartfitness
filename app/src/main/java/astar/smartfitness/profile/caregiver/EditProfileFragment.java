package astar.smartfitness.profile.caregiver;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import astar.smartfitness.MainActivity;
import astar.smartfitness.R;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EditProfileFragment extends Fragment {
    enum PageState {BASIC, SKILLS, SERVICES}

    @Bind(R.id.back_button)
    Button backButton;

    @Bind(R.id.next_button)
    Button nextButton;

    private PageState currentState = PageState.BASIC;

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

        ((MainActivity) getActivity()).getSupportActionBar().hide();
        checkButtonStates();

        showBasicSection();
    }


    private void showBasicSection() {
        replaceFragment(new BasicSectionFragment());
    }

    private void showServicesSection() {
        replaceFragment(new ServicesSectionFragment());
    }

    private void showSkillsSection() {
        replaceFragment(new SkillsSectionFragment());
    }

    private void checkButtonStates() {
        switch (currentState) {
            case BASIC:
                backButton.setEnabled(false);
                nextButton.setEnabled(true);
                break;
            case SERVICES:
                backButton.setEnabled(true);
                nextButton.setEnabled(false);
                break;
            default:
                backButton.setEnabled(true);
                nextButton.setEnabled(true);
                break;
        }
    }

    private Fragment getFragment(PageState state) {
        switch (state) {
            default:
            case BASIC:
                return new BasicSectionFragment();
            case SERVICES:
                return new ServicesSectionFragment();
            case SKILLS:
                return new SkillsSectionFragment();
        }
    }

    @OnClick(R.id.back_button)
    public void onBackButtonClicked() {
        int stateOrdinal = currentState.ordinal();
        if (stateOrdinal == PageState.BASIC.ordinal()) {
            throw new RuntimeException("This should never happen!");
        } else if (stateOrdinal > PageState.BASIC.ordinal()) {
            currentState = PageState.values()[stateOrdinal - 1];
            getChildFragmentManager().popBackStack();
        }

        checkButtonStates();
    }

    @OnClick(R.id.next_button)
    public void onNextButtonClicked() {
        int stateOrdinal = currentState.ordinal();
        if (stateOrdinal == PageState.SERVICES.ordinal()) {
            throw new RuntimeException("This should never happen!");
        } else if (stateOrdinal < PageState.SERVICES.ordinal()) {
            currentState = PageState.values()[stateOrdinal + 1];
            replaceFragment(getFragment(currentState));
        }

        checkButtonStates();
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (!(activity instanceof MainActivity)) {
            throw new ClassCastException("Activity must be MainActivity");
        }
    }

    private void replaceFragment(Fragment f) {
        replaceFragment(f, true);
    }

    private void replaceFragment(Fragment f, boolean addToBackStack) {
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, R.anim.slide_in_right, R.anim.slide_out_left);
        FrameLayout container = (FrameLayout) getView().findViewById(R.id.container);
        if (container.getChildCount() == 0)
            ft.add(R.id.container, f);
        else
            ft.replace(R.id.container, f);
        if (addToBackStack)
            ft.addToBackStack(null);
        ft.commit();
    }

}
