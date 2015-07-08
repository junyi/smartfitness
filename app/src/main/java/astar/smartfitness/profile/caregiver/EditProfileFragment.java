package astar.smartfitness.profile.caregiver;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.HashMap;

import astar.smartfitness.MainActivity;
import astar.smartfitness.R;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EditProfileFragment extends Fragment implements OnSectionChangedListener {
    @Override
    public void onSectionChanged() {
        notifyToValidate();
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

    protected void checkButtonStates() {
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
    }

    protected void notifyToValidate() {
        checkButtonStates();

        final SectionFragment f = getFragment(currentState);

        boolean isValid = f.validateSection();
        nextButton.setEnabled(isValid);
    }

    private SectionFragment getFragment(PageState state) {
        SectionFragment f = (SectionFragment) getChildFragmentManager().findFragmentByTag(String.valueOf(state.ordinal()));

        if (f != null)
            return f;

        Bundle data = dataMap.get(state);

        switch (state) {
            default:
            case BASIC:
                f = BasicSectionFragment.newInstance(data);
                break;
            case SERVICES:
                f = ServicesSectionFragment.newInstance(data);
                break;
            case SKILLS:
                f = SkillsSectionFragment.newInstance(data);
                break;
        }


        return f;
    }

    public PageState getCurrentState() {
        return currentState;
    }

    @OnClick(R.id.back_button)
    public void onBackButtonClicked() {
        saveData();

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
        saveData();

        int stateOrdinal = currentState.ordinal();
        if (stateOrdinal == PageState.SERVICES.ordinal()) {
            //TODO: Change this dummy progress dialog
            new MaterialDialog.Builder(getActivity())
                    .title("Submitting...")
                    .content("Please wait")
                    .progress(true, 0)
                    .showListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(final DialogInterface dialog) {
                            Runnable runnable = new Runnable() {
                                @Override
                                public void run() {
                                    dialog.dismiss();
                                }
                            };
                            Handler handler = new Handler();
                            handler.postDelayed(runnable, 3000);
                        }
                    })
                    .show();
        } else if (stateOrdinal < PageState.SERVICES.ordinal()) {
            currentState = PageState.values()[stateOrdinal + 1];
            replaceFragment(getFragment(currentState), currentState);
        }

        checkButtonStates();
    }

    private void saveData() {
        SectionFragment f = getFragment(currentState);
        Bundle data = new Bundle();
        f.saveSection(data);
        dataMap.put(currentState, data);
    }

    private void submitData() {

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
