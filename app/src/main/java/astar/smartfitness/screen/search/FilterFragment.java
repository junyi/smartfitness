package astar.smartfitness.screen.search;

import android.os.Bundle;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import org.apmem.tools.layouts.FlowLayout;

import java.util.ArrayList;

import astar.smartfitness.R;
import astar.smartfitness.widget.ChipView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FilterFragment extends BaseSearchFragment {
    public static final String ARG_GENDER = "gender";
    public static final String ARG_SERVICES = "services";
    public static final String ARG_BUDGET = "budget";
    public static final String ARG_YEAR_OF_EXP = "year_of_exp";
    public static final String ARG_LANGUAGES = "languages";

    private String genderResult;
    private ArrayList<Integer> servicesResult = new ArrayList<>();
    private int yearOfExpResult = -1;
    private int[] budgetResult = new int[]{0, 1000};
    private ArrayList<Integer> languagesResult = new ArrayList<>();
    private SparseArray<String> tempLanguageResult = new SparseArray<>();

    private Bundle result = new Bundle();
    @Bind(R.id.gender)
    TextView genderTextView;

    @Bind(R.id.services)
    TextView servicesTextView;

    @Bind(R.id.language_layout)
    FlowLayout languageLayout;

    public static FilterFragment newInstance(Bundle data) {
        FilterFragment fragment = new FilterFragment();
        fragment.setArguments(data);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupLanguages();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_filter, container, false);
        ButterKnife.bind(this, view);

        return view;
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

    @OnClick(R.id.gender)
    public void showGenderDialog() {
        new MaterialDialog.Builder(getActivity())
                .backgroundColorRes(R.color.bg_dark_blue)
                .title("Select gender")
                .items(R.array.gender_list)
                .itemsCallbackSingleChoice(0, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {
                        return false;
                    }
                })
                .show();
    }

    @OnClick(R.id.services)
    public void showServicesDialog() {
        new MaterialDialog.Builder(getActivity())
                .backgroundColorRes(R.color.bg_dark_blue)
                .title("Select services")
                .items(R.array.service_list)
                .itemsCallbackMultiChoice(null, new MaterialDialog.ListCallbackMultiChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog materialDialog, Integer[] integers, CharSequence[] charSequences) {
                        return false;
                    }
                })
                .show();
    }

    private void processTempLanguageResult() {
        languagesResult = new ArrayList<>();
        int l = tempLanguageResult.size();
        for (int i = 0; i < l; i++) {
            int position = tempLanguageResult.keyAt(i);
            languagesResult.add(position);
        }
    }


    @OnClick(R.id.search_button)
    public void onSearchButtonClicked() {
        getParentContainer().passSearchResults();
    }

    @Override
    public void saveSection(Bundle data) {
        data.putIntegerArrayList(ARG_SERVICES, servicesResult);
        data.putString(ARG_GENDER, genderResult);
        data.putInt(ARG_YEAR_OF_EXP, yearOfExpResult);
        data.putIntArray(ARG_BUDGET, budgetResult);

        processTempLanguageResult();
        data.putIntegerArrayList(ARG_LANGUAGES, languagesResult);
    }

    @Override
    public void restoreSection(Bundle data) {

    }

    private void notifySectionChanged() {

    }
}
