package astar.smartfitness.screen.profile.caregiver;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.parse.ParseFile;
import com.parse.ParseObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import astar.smartfitness.R;
import astar.smartfitness.model.CaregiverProfile;
import astar.smartfitness.model.Skill;
import astar.smartfitness.model.User;
import astar.smartfitness.screen.MainActivity;
import astar.smartfitness.util.OnBackPressedListener;
import astar.smartfitness.util.Utils;
import bolts.Continuation;
import bolts.Task;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

public class EditProfileFragment extends Fragment implements OnSectionChangedListener, OnBackPressedListener {
    @Override
    public void onSectionChanged() {
        notifyToValidate();
    }

    @Override
    public boolean onBackPressed() {
        onBackButtonClicked();

        if (getCurrentState() == PageState.BASIC)
            return false;
        else
            return true;
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
    private MaterialDialog dialog = null;

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
            boolean leftToRight = false;
            replaceFragment(getFragment(currentState), currentState, leftToRight);
//            getChildFragmentManager().popBackStack();
        }

        checkButtonStates();
    }

    @OnClick(R.id.next_button)
    public void onNextButtonClicked() {
        saveData();

        int stateOrdinal = currentState.ordinal();
        if (stateOrdinal == PageState.SERVICES.ordinal()) {
            //TODO: Change this dummy progress dialog
            dialog = new MaterialDialog.Builder(getActivity())
                    .title("Submitting...")
                    .content("Please wait")
                    .progress(true, 0)
                    .showListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(final DialogInterface dialog) {
                            submitData();
                        }
                    })
                    .cancelable(false)
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
        Bundle basicData = dataMap.get(PageState.BASIC);
        Bundle skillsData = dataMap.get(PageState.SKILLS);
        Bundle servicesData = dataMap.get(PageState.SERVICES);

        String profileImagePath = basicData.getString(BasicSectionFragment.ARG_PROFILE_IMAGE);
        int yearOfExp = basicData.getInt(BasicSectionFragment.ARG_YEAR_OF_EXP);
        int[] wageRange = basicData.getIntArray(BasicSectionFragment.ARG_WAGE_RANGE);
        ArrayList<Integer> languages = basicData.getIntegerArrayList(BasicSectionFragment.ARG_LANGUAGES);
        final ArrayList<Skill> skills = skillsData.getParcelableArrayList(SkillsSectionFragment.ARG_SKILL_LIST);
        ArrayList<Integer> services = servicesData.getIntegerArrayList(ServicesSectionFragment.ARG_SERVICE_LIST);

        User user = Utils.getCurrentUser();


        final CaregiverProfile profile = new CaregiverProfile();
        profile.setUserId(user);
        profile.setYearOfExp(yearOfExp);
        profile.setWageRangeMin(wageRange[0]);
        profile.setWageRangeMax(wageRange[1]);
        profile.setLanguages(languages);
        profile.setServices(services);

        int l = skills.size();
        for (int i = 0; i < l; i++) {
            skills.get(i).setUserId(user);
        }

        final ArrayList<Task<Void>> tasks = new ArrayList<>();

        final ParseFile imageFile = convertToParseFile(profileImagePath);

        if (imageFile == null) {
            handleError(new Exception("Cannot convert path to ParseFile"));
            return;
        }

        imageFile
                .saveInBackground()
                .continueWithTask(new Continuation<Void, Task<Void>>() {
                    @Override
                    public Task<Void> then(Task<Void> task) throws Exception {
                        if (task.isCancelled()) {
                            Timber.d("Task is cancelled");
                            handleError(new Exception("Task is cancelled"));
                        } else if (task.isFaulted()) {
                            handleError(task.getError());
                        } else {
                            profile.setProfileImage(imageFile.getUrl());

                            tasks.add(profile.saveInBackground());
                            tasks.add(ParseObject.saveAllInBackground(skills));

                            return Task.whenAll(tasks);
                        }

                        return Task.cancelled();
                    }
                }).continueWith(new Continuation<Void, Object>() {
            @Override
            public Object then(Task<Void> task) throws Exception {
                if (task.isCancelled()) {
                    Timber.d("Task is cancelled");
                    handleError(new Exception("Task is cancelled"));
                } else if (task.isFaulted()) {
                    handleError(task.getError());
                } else {
                    onSubmitSuccess();
                }
                return null;
            }
        });

    }

    private ParseFile convertToParseFile(String image) {
        Bitmap bitmap = BitmapFactory.decodeFile(image);

        if (bitmap != null) {
            // Convert it to byte
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            // Compress image to lower quality scale 1 - 100
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
            byte[] imageBytes = stream.toByteArray();

            Long tsLong = System.currentTimeMillis() / 1000;
            String timestamp = tsLong.toString();
            String fileName = Utils.getCurrentUser().getObjectId() + "_" + timestamp + ".jpg";
            // Create the ParseFile
            ParseFile file = new ParseFile(fileName, imageBytes);

            return file;
        }

        return null;
    }

    private void onSubmitSuccess() {
        Timber.d("Submission success!");
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (dialog != null) {
                    dialog.dismiss();
                }

                Snackbar.make(getActivity().getWindow().getDecorView().findViewById(android.R.id.content),
                        "Profile creation success!", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private void handleError(final Exception e) {
        if (dialog != null)
            dialog.dismiss();

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Snackbar.make(getView(), "An error occured. Try again later.", Snackbar.LENGTH_SHORT).show();
            }
        });

        Timber.e("Submission error: %s", Log.getStackTraceString(e));

//        if (e instanceof ParseException) {
//            final ParseException error = (ParseException) e;
//            int errorCode = error.getCode();
//
//            Timber.e(e.getMessage());
//
//            switch (errorCode) {
//                case ParseException.USERNAME_TAKEN:
//                    getActivity().runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                        }
//                    });
//                    break;
//                case ParseException.DUPLICATE_VALUE:
//                    getActivity().runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                        }
//                    });
//                    break;
//                default:
//
//            }
//        } else {
//            e.printStackTrace();
//        }
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

    private void replaceFragment(SectionFragment f, PageState state, boolean leftToRight) {
        replaceFragment(f, state, true, leftToRight);
    }

    private void replaceFragment(SectionFragment f, PageState state, boolean addToBackStack, boolean leftToRight) {
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        if (leftToRight)
            ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, R.anim.slide_in_right, R.anim.slide_out_left);
        else
            ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);

        FrameLayout container = (FrameLayout) getView().findViewById(R.id.container);

        if (container.getChildCount() == 0)
            ft.add(R.id.container, f, String.valueOf(state.ordinal()));
        else
            ft.replace(R.id.container, f, String.valueOf(state.ordinal()));
        if (addToBackStack)
            ft.addToBackStack(null);
        ft.commit();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        SectionFragment f = getFragment(currentState);
        if (f != null) {
            f.onActivityResult(requestCode, resultCode, data);
        }
    }

}
