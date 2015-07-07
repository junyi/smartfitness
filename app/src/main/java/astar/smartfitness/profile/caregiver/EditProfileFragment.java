package astar.smartfitness.profile.caregiver;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.HashMap;

import astar.smartfitness.MainActivity;
import astar.smartfitness.R;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EditProfileFragment extends Fragment implements OnSectionChangedListener {
    @Override
    public void onSectionChanged() {
        checkButtonStates();
    }

    public enum PageState {BASIC, SKILLS, SERVICES}

    @Bind(R.id.back_button)
    Button backButton;

    @Bind(R.id.next_button)
    Button nextButton;

    @Bind(R.id.current_page_text_view)
    TextView currentPageTextView;

    private PageState currentState = PageState.BASIC;
    private HashMap<PageState, Bundle> dataMap = new HashMap<>();

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
        replaceFragment(new BasicSectionFragment(), PageState.BASIC);
    }

    private void showServicesSection() {
        replaceFragment(new ServicesSectionFragment(), PageState.SERVICES);
    }

    private void showSkillsSection() {
        replaceFragment(new SkillsSectionFragment(), PageState.SKILLS);
    }

    private String getTitle(PageState state) {
        switch (state) {
            default:
            case BASIC:
                return "Basic Info";
            case SERVICES:
                return "Caregiver Services";
            case SKILLS:
                return "Caregiver Skills";
        }
    }

    private void checkButtonStates() {
        nextButton.setEnabled(false);

        switch (currentState) {
            case BASIC:
                nextButton.setText("Next");
                backButton.setEnabled(false);
                break;
            case SERVICES:
                nextButton.setText("Submit");
                backButton.setEnabled(true);
                break;
            default:
                nextButton.setText("Next");
                backButton.setEnabled(true);
        }

        int pageNum = currentState.ordinal() + 1;
        int totalPage = PageState.values().length;

        String currentPage = String.format("%s [%d/%d]", getTitle(currentState), pageNum, totalPage);
        currentPageTextView.setText(currentPage);

        final SectionFragment f = getFragment(currentState);
        if (f.isVisible()) {
            boolean isValid = f.validateSection();
            nextButton.setEnabled(isValid);
        } else
            f.setOnViewCreatedListener(new OnViewCreatedListener() {
                @Override
                public void onViewCreated() {
                    boolean isValid = f.validateSection();
                    nextButton.setEnabled(isValid);
                }
            });
    }

    private SectionFragment getFragment(PageState state) {
        SectionFragment f = (SectionFragment) getChildFragmentManager().findFragmentByTag(String.valueOf(state.ordinal()));

        if (f != null)
            return f;

        switch (state) {
            default:
            case BASIC:
                f = new BasicSectionFragment();
                break;
            case SERVICES:
                f = new ServicesSectionFragment();
                break;
            case SKILLS:
                f = new SkillsSectionFragment();
                break;
        }

        return f;
    }

    public PageState getCurrentState() {
        return currentState;
    }

    @OnClick(R.id.back_button)
    public void onBackButtonClicked() {
        int stateOrdinal = currentState.ordinal();
        if (stateOrdinal == PageState.BASIC.ordinal()) {
//            throw new RuntimeException("This should never happen!");
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
            replaceFragment(getFragment(currentState), currentState);
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

    private void replaceFragment(SectionFragment f, PageState state) {
        replaceFragment(f, state, true);
    }

    private void replaceFragment(SectionFragment f, PageState state, boolean addToBackStack) {
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, R.anim.slide_in_right, R.anim.slide_out_left);
        FrameLayout container = (FrameLayout) getView().findViewById(R.id.container);

        if (container.getChildCount() == 0)
            ft.add(R.id.container, f, String.valueOf(state.ordinal()));
        else
            ft.replace(R.id.container, f, String.valueOf(state.ordinal()));
        if (addToBackStack)
            ft.addToBackStack(null);
        ft.commit();
    }

}
