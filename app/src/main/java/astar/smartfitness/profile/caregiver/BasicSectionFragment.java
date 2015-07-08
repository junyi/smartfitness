package astar.smartfitness.profile.caregiver;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.appyvet.rangebar.RangeBar;

import org.apmem.tools.layouts.FlowLayout;

import java.util.ArrayList;
import java.util.Arrays;

import astar.smartfitness.R;
import astar.smartfitness.Utils;
import astar.smartfitness.widget.ChipView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import commons.validator.routines.IntegerValidator;

public class BasicSectionFragment extends SectionFragment {
    public static final String ARG_LOCATION = "location";
    public static final String ARG_YEAR_OF_EXP = "year_of_exp";
    public static final String ARG_WAGE_RANGE = "wage_range";
    public static final String ARG_LANGUAGES = "languages";

    private static final int MIN_YEAR_OF_EXP = 0;
    private static final int MAX_YEAR_OF_EXP = 99;

    private Bundle result = new Bundle();

    private ArrayList<Integer> locationResult = new ArrayList<>();
    private int yearOfExpResult = -1;
    private int[] wageRangeResult = new int[2];
    private ArrayList<Integer> languagesResult = new ArrayList<>();
    private SparseArray<String> tempLanguageResult = new SparseArray<>();

//    @Bind(R.id.select_location_button)
//    Button selectLocationButton;
//
//    @Bind(R.id.selected_locations)
//    TextView selectedLocationsTextView;

    @Bind(R.id.experience_edit_text)
    EditText yearOfExpEditText;

    @Bind(R.id.language_layout)
    FlowLayout languageLayout;

    @Bind(R.id.rangebar)
    RangeBar rangeBar;

    @Bind(R.id.wage_range_text_view)
    TextView wageRangeTextView;

    @Bind(R.id.scroll_view)
    ScrollView scrollView;

    public BasicSectionFragment() {
    }

    public static BasicSectionFragment newInstance(Bundle data) {
        BasicSectionFragment fragment = new BasicSectionFragment();
        fragment.setArguments(data);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_caregiver_profile_basic, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            restoreSection(getArguments());
        }

//        setupLocation();
        setupYearOfExp();
        setupWageRange();
        setupLanguages();

        scrollView.setOnTouchListener(new View.OnTouchListener() {
            float downX = 0;
            float downY = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        downX = event.getX();
                        downY = event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (event.getX() - downX > 0 || event.getY() - downY > 0) {
                            Utils.hideKeyboard(getActivity());
                        }
                }
                return false;
            }
        });
    }

//    @OnClick({R.id.select_location_button, R.id.selected_locations})
//    public void onSelectLocation() {
//        Integer[] selectedIndices = new Integer[locationResult.size()];
//        locationResult.toArray(selectedIndices);
//
//        new MaterialDialog.Builder(getActivity())
//                .backgroundColorRes(R.color.bg_dark_blue)
//                .title("Select location")
//                .items(R.array.location_list)
//                .itemsCallbackMultiChoice(selectedIndices, new MaterialDialog.ListCallbackMultiChoice() {
//                    @Override
//                    public boolean onSelection(MaterialDialog materialDialog, Integer[] integers, CharSequence[] charSequences) {
//
//                        renderLocationView(integers);
//
//                        // Save location result
//                        locationResult = new ArrayList<>(Arrays.asList(integers));
//
//                        // Notify change in section
//                        notifySectionChanged();
//
//                        return false;
//                    }
//                })
//                .positiveText("Confirm")
//                .showListener(new DialogInterface.OnShowListener() {
//                    @Override
//                    public void onShow(DialogInterface dialog) {
//                        Utils.hideKeyboard(getActivity());
//                    }
//                })
//                .show();
//    }

//    private void renderLocationView(Object[] locationIndexList) {
//        String[] locations = getResources().getStringArray(R.array.location_list);
//
//        int length = locationIndexList.length;
//
//        if (length == 0) {
//            selectedLocationsTextView.setVisibility(View.GONE);
//            selectLocationButton.setVisibility(View.VISIBLE);
//        } else {
//            StringBuilder sb = new StringBuilder();
//            for (int i = 0; i < length; i++) {
//                sb.append(locations[((int) locationIndexList[i])]);
//                if (length > 1 && i < length - 1)
//                    sb.append(", ");
//            }
//
//            selectedLocationsTextView.setText(sb.toString());
//            selectedLocationsTextView.setVisibility(View.VISIBLE);
//            selectLocationButton.setVisibility(View.GONE);
//        }
//    }

//    private void setupLocation() {
//        if (locationResult.size() > 0) {
//            renderLocationView(locationResult.toArray());
//        }
//    }

    private void setupYearOfExp() {
        yearOfExpEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateYearOfExp();

                // Notify change in section
                notifySectionChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        yearOfExpEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    Utils.hideKeyboard(getActivity(), v);
                }
            }
        });

        if (yearOfExpResult != -1)
            yearOfExpEditText.setText(String.valueOf(yearOfExpResult));
    }

    private void setupLanguages() {
        if (languageLayout.getChildCount() > 0) {
            languageLayout.removeAllViews();
        }

        String[] languageList = getResources().getStringArray(R.array.language_list);

        int l = languageList.length;
        for (int i = 0; i < l; i++) {
            String language = languageList[i];

            ChipView chipView = new ChipView(getActivity(), null, R.style.ChipViewStyle, language);
            chipView.setTag(i);

            if (tempLanguageResult.get(i) != null) {
                chipView.setSelected(true);
            } else {
                chipView.setSelected(false);
            }

            chipView.setOnSelectedListener(new ChipView.OnSelectedListener() {
                @Override
                public void onSelected(ChipView chipView, boolean isSelected) {
                    int position = (int) chipView.getTag();
                    if (isSelected)
                        tempLanguageResult.put(position, chipView.getText());
                    else
                        tempLanguageResult.remove(position);

                    // Notify change in section
                    notifySectionChanged();
                }
            });

            FlowLayout.LayoutParams layoutParams = new FlowLayout.LayoutParams(FlowLayout.LayoutParams.WRAP_CONTENT,
                    FlowLayout.LayoutParams.WRAP_CONTENT);
            int twoDp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics());
            layoutParams.setMargins(twoDp, twoDp, twoDp, twoDp);
            languageLayout.addView(chipView, layoutParams);
        }

        languageLayout.invalidate();
    }

    private void setupWageRange() {
        rangeBar.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onRangeChangeListener(RangeBar rangeBar, int startIndex, int endIndex, String start, String end) {
                wageRangeTextView.setText(String.format(getResources().getString(R.string.wage_range_formatted_text), start, end));

                int wageStart = Integer.parseInt(start);
                int wageEnd = Integer.parseInt(end);

                // Save wage range result
                wageRangeResult = new int[]{wageStart, wageEnd};

                // Notify section change
                notifySectionChanged();
            }
        });

        if (wageRangeResult[0] == 0 && wageRangeResult[0] == wageRangeResult[1]) {
            wageRangeResult[0] = (int) rangeBar.getTickStart();
            wageRangeResult[1] = (int) rangeBar.getTickEnd();
        } else {
            rangeBar.setRangePinsByValue(wageRangeResult[0], wageRangeResult[1]);
        }

        wageRangeTextView.setText(String.format(getResources().getString(R.string.wage_range_formatted_text), wageRangeResult[0], wageRangeResult[1]));
    }

    private void processTempLanguageResult() {
        languagesResult = new ArrayList<>();
        int l = tempLanguageResult.size();
        for (int i = 0; i < l; i++) {
            int position = tempLanguageResult.keyAt(i);
            languagesResult.add(position);
        }
    }

    private boolean validateYearOfExp() {
        IntegerValidator validator = IntegerValidator.getInstance();
        Integer yearOfExp = validator.validate(yearOfExpEditText.getText().toString());

        if (yearOfExp == null)
            return false;

        boolean isValid = validator.isInRange(yearOfExp, MIN_YEAR_OF_EXP, MAX_YEAR_OF_EXP);
        if (isValid) {
            // Save result for year of exp
            yearOfExpResult = yearOfExp;
        }

        return isValid;
    }

    @Override
    public boolean validateSection() {
//        if (locationResult.size() == 0)
//            return false;

        if (TextUtils.isEmpty(yearOfExpEditText.getText())) {
            return false;
        }

        if (!validateYearOfExp()) {
            return false;
        }

        if (tempLanguageResult.size() == 0)
            return false;

        return true;
    }

    @Override
    public void saveSection(Bundle data) {
//        data.putIntegerArrayList(ARG_LOCATION, locationResult);
        data.putInt(ARG_YEAR_OF_EXP, yearOfExpResult);
        data.putIntArray(ARG_WAGE_RANGE, wageRangeResult);

        processTempLanguageResult();
        data.putIntegerArrayList(ARG_LANGUAGES, languagesResult);
    }

    @Override
    public void restoreSection(Bundle data) {
//        locationResult = data.getIntegerArrayList(ARG_LOCATION);
        yearOfExpResult = data.getInt(ARG_YEAR_OF_EXP);
        wageRangeResult = data.getIntArray(ARG_WAGE_RANGE);
        languagesResult = data.getIntegerArrayList(ARG_LANGUAGES);

        String[] languageList = getResources().getStringArray(R.array.language_list);

        int size = languagesResult.size();
        if (size > 0) {
            tempLanguageResult.clear();
            for (int i = 0; i < size; i++) {
                tempLanguageResult.put(languagesResult.get(i), languageList[i]);
            }
        }
    }
}
