package astar.smartfitness.screen.search;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appyvet.rangebar.RangeBar;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.flipboard.bottomsheet.BottomSheetLayout;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import astar.smartfitness.R;
import astar.smartfitness.model.CaregiverProfile;
import astar.smartfitness.model.User;
import astar.smartfitness.util.InsetViewTransformer;
import astar.smartfitness.util.MarginDecoration;
import astar.smartfitness.util.Utils;
import astar.smartfitness.widget.EmptyRecyclerView;
import astar.smartfitness.widget.MultiOptionView;
import astar.smartfitness.widget.SingleOptionView;
import bolts.Continuation;
import bolts.Task;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

public class SearchResultsFragment extends BaseSearchFragment {
    enum SortType {RATING, YEARS_OF_EXP, PRICE}

    enum FilterCategory {RATING, YEARS_OF_EXP, PRICE}

    public static final String ARG_SEARCH_DATA = "search_data";
    private Bundle searchBundle = null;

    private ArrayList<Integer> languageResult = new ArrayList<>();
    private ArrayList<Integer> locationResult = new ArrayList<>();

    private int[] budgetResult = new int[]{0, 1000};
    private SortType sortType = SortType.RATING;

    @Bind(R.id.bottomsheet)
    BottomSheetLayout bottomSheet;

    @Bind(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    @Bind(R.id.recycler_view)
    EmptyRecyclerView recyclerView;

    @Bind(R.id.empty_view)
    View emptyView;


    static class BottomSheetViewHolder {
        @Bind(R.id.budget_text_view)
        TextView budgetTextView;

        @Bind(R.id.rangebar)
        RangeBar rangeBar;

        @Bind(R.id.location_multi_view)
        MultiOptionView locationMultiOptionView;

        @Bind(R.id.language_multi_view)
        MultiOptionView languageMultiOptionView;

        @Bind(R.id.sort_view)
        SingleOptionView sortView;

        @Bind(R.id.shimmer_view_container)
        ShimmerFrameLayout shimmer;

        @Bind(R.id.filter_container)
        LinearLayout filterContainer;

        @Bind(R.id.apply_button)
        View applyButton;

        @Bind(R.id.reset_button)
        View resetButton;

        @OnClick(R.id.language_clear_filter_button)
        public void clearFilterForLanguage() {
            languageMultiOptionView.clearSelection();
        }

        @OnClick(R.id.location_clear_filter_button)
        public void clearFilterForLocation() {
            locationMultiOptionView.clearSelection();
        }
    }

    final BottomSheetViewHolder bottomSheetViewHolder = new BottomSheetViewHolder();

    private SearchResultsRecyclerViewAdapter adapter;

    public static SearchResultsFragment newInstance(Bundle data) {
        SearchResultsFragment fragment = new SearchResultsFragment();
        fragment.setArguments(data);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_results, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (searchBundle != null) {
            executeSearch(searchBundle);
        } else if (getArguments() != null) {
            searchBundle = getArguments();
            executeSearch(getArguments());
        }

        adapter = new SearchResultsRecyclerViewAdapter(getActivity());
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new MarginDecoration(getActivity()));
        recyclerView.setEmptyView(emptyView);
        recyclerView.setAdapter(adapter);

        swipeRefreshLayout.setColorSchemeResources(R.color.login_blue);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (searchBundle != null) {
                    executeSearch(searchBundle);
                } else {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });

    }

    @Override
    public void saveSection(Bundle data) {
        data.putIntArray(FilterFragment.ARG_BUDGET, budgetResult);
    }

    @Override
    public void restoreSection(Bundle data) {
        if (data != null) {
            searchBundle = data;

            budgetResult = searchBundle.getIntArray(FilterFragment.ARG_BUDGET);
        }
    }

    private void executeSearch(Bundle data) {
        ArrayList<Integer> servicesResult = data.getIntegerArrayList(FilterFragment.ARG_SERVICES);
        int[] budgetResult = data.getIntArray(FilterFragment.ARG_BUDGET);

        ParseQuery<CaregiverProfile> query = ParseQuery.getQuery(CaregiverProfile.class);
        query.include(CaregiverProfile.KEY_USER_ID);

        ParseQuery<User> innerQuery = ParseQuery.getQuery(User.class);
        innerQuery.whereEqualTo(User.KEY_ROLES, User.ROLE_CAREGIVER);

        switch (sortType) {
            case RATING:
                query.addDescendingOrder(CaregiverProfile.KEY_RATING);
                break;
            case YEARS_OF_EXP:
                query.addDescendingOrder(CaregiverProfile.KEY_YEAR_OF_EXP);
                break;
            case PRICE:
                query.addAscendingOrder(CaregiverProfile.KEY_WAGE_RANGE_MIN);
                break;
        }

//        if (genderResult != null) {
//            ParseQuery<User> genderQuery = ParseQuery.getQuery(User.class);
//            genderQuery.whereEqualTo(User.KEY_GENDER, genderResult);
//            query.whereMatchesQuery(CaregiverProfile.KEY_USER_ID, genderQuery);
//        }
//
        if (budgetResult != null) {
            query.whereGreaterThanOrEqualTo(CaregiverProfile.KEY_WAGE_RANGE_MIN, budgetResult[0]);
            query.whereLessThanOrEqualTo(CaregiverProfile.KEY_WAGE_RANGE_MAX, budgetResult[1]);
        }
//
//        if (yearOfExpResult != -1) {
//            query.whereGreaterThanOrEqualTo(CaregiverProfile.KEY_YEAR_OF_EXP, yearOfExpResult);
//        }
//
        if (servicesResult != null && servicesResult.size() > 0) {
            query.whereContainedIn(CaregiverProfile.KEY_SERVICES, servicesResult);
        }
//
//        if (languagesResult != null && languagesResult.size() > 0) {
//            query.whereContainedIn(CaregiverProfile.KEY_LANGUAGES, languagesResult);
//        }

        query.findInBackground().continueWithTask(new Continuation<List<CaregiverProfile>, Task<Void>>() {
            @Override
            public Task<Void> then(Task<List<CaregiverProfile>> task) throws Exception {
                if (task.isCancelled()) {
                    Timber.d("Task is cancelled");
                    handleError(new Exception("Task is cancelled"));
                } else if (task.isFaulted()) {
                    handleError(task.getError());
                } else {
                    onSearchSuccess(task.getResult());
                }

                return Task.cancelled();
            }
        });

    }

    private void onSearchSuccess(final List<CaregiverProfile> result) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (swipeRefreshLayout.isRefreshing())
                    swipeRefreshLayout.setRefreshing(false);

                if (bottomSheet.isSheetShowing())
                    bottomSheet.dismissSheet();

                adapter.setCaregiverList(result);

                recyclerView.scrollToPosition(0);
            }
        });
    }

    private void handleError(final Exception e) {
        Timber.e("Error: " + Log.getStackTraceString(e));

        if (e instanceof ParseException) {
            ParseException error = (ParseException) e;
            int errorCode = error.getCode();

            Timber.e(errorCode + ": " + e.getMessage());

        } else {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.filter_button)
    public void onFilterButtonClicked() {
        expandBottomSheet();
    }

    private void expandBottomSheet() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.bottom_sheet_search_filter, bottomSheet, false);

        InsetViewTransformer transformer = new InsetViewTransformer();
        transformer.setViewTransformedListener(new InsetViewTransformer.ViewTransformedListener() {
            float height = 0f;
            float y = Utils.dpToPx(getActivity(), 24);

            @Override
            public void viewTransformed(float translation, float maxTranslation, float peekedTranslation, BottomSheetLayout parent, View view) {
                if (height == 0f) {
                    height = bottomSheetViewHolder.shimmer.getMeasuredHeight() + Utils.dpToPx(getActivity(), 16);
                }

                double shimmerClampedTranslation = Utils.clamp(translation, peekedTranslation, maxTranslation);
                float shimmerTranslationAmount = (float) Utils.mapValueFromRangeToRange(shimmerClampedTranslation, peekedTranslation, maxTranslation, 0, -height);
                bottomSheetViewHolder.shimmer.setTranslationY(shimmerTranslationAmount);

                float filterTranslationAmount = (float) Utils.mapValueFromRangeToRange(shimmerClampedTranslation, peekedTranslation, maxTranslation, y, 0);
                bottomSheetViewHolder.filterContainer.setTranslationY(height + shimmerTranslationAmount + filterTranslationAmount);

                double fromRangeSize = maxTranslation - peekedTranslation;
                double valueScale = (shimmerClampedTranslation - peekedTranslation) / fromRangeSize;
                bottomSheetViewHolder.shimmer.setAlpha(1 - (float) valueScale);
                bottomSheetViewHolder.filterContainer.setAlpha((float) valueScale);
            }
        });
        bottomSheet.setDefaultViewTransformer(transformer);
        bottomSheet.showWithSheetView(view);
        bottomSheet.setOnSheetStateChangeListener(new BottomSheetLayout.OnSheetStateChangeListener() {
            @Override
            public void onSheetStateChanged(BottomSheetLayout.State state) {
                switch (state) {
                    case PEEKED:
                        bottomSheetViewHolder.filterContainer.setVisibility(View.VISIBLE);
                        bottomSheetViewHolder.shimmer.setVisibility(View.VISIBLE);
                        bottomSheetViewHolder.shimmer.startShimmerAnimation();
                        break;
                    case EXPANDED:
                        bottomSheetViewHolder.shimmer.stopShimmerAnimation();
//                        bottomSheetViewHolder.shimmer.setVisibility(View.GONE);
                        bottomSheetViewHolder.filterContainer.setVisibility(View.VISIBLE);
                        break;
                    case HIDDEN:
                        bottomSheetViewHolder.shimmer.stopShimmerAnimation();
                }
            }
        });

        ButterKnife.bind(bottomSheetViewHolder, view);

        setupSortView();
        setupBudget();
        setupLanguages();
        setupShimmer();
        setupApplyButton();
        setupResetButton();
    }

    private void setupShimmer() {
        bottomSheetViewHolder.shimmer.setRepeatDelay(1000);
        bottomSheetViewHolder.shimmer.setBaseAlpha(0.7f);
        bottomSheetViewHolder.shimmer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheet.expandSheet();
            }
        });
    }

    private void setupSortView() {
        bottomSheetViewHolder.sortView.setCurrentSelectedPosition(sortType.ordinal());
        bottomSheetViewHolder.sortView.setOnItemSelectedListener(new SingleOptionView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int position, CharSequence item) {
                sortType = SortType.values()[position];
            }
        });
    }

    private void setupBudget() {
        bottomSheetViewHolder.rangeBar.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onRangeChangeListener(RangeBar rangeBar, int startIndex, int endIndex, String start, String end) {
                bottomSheetViewHolder.budgetTextView.setText(String.format(getResources().getString(R.string.wage_range_formatted_text), start, end));

                int budgetStart = Integer.parseInt(start);
                int budgetEnd = Integer.parseInt(end);

                // Save wage range result
                budgetResult[0] = budgetStart;
                budgetResult[1] = budgetEnd;
            }
        });

        if (budgetResult[0] == 0 && budgetResult[0] == budgetResult[1]) {
            budgetResult[0] = (int) bottomSheetViewHolder.rangeBar.getTickStart();
            budgetResult[1] = (int) bottomSheetViewHolder.rangeBar.getTickEnd();
        } else {
            bottomSheetViewHolder.rangeBar.setRangePinsByValue(budgetResult[0], budgetResult[1]);
        }

        bottomSheetViewHolder.budgetTextView.setText(String.format(getResources().getString(R.string.wage_range_formatted_text), budgetResult[0], budgetResult[1]));
    }

    private void setupApplyButton() {
        bottomSheetViewHolder.applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swipeRefreshLayout.setRefreshing(true);

                searchBundle.putIntArray(FilterFragment.ARG_BUDGET, budgetResult);

                executeSearch(searchBundle);
            }
        });
    }

    private void setupResetButton() {
        bottomSheetViewHolder.resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (bottomSheet.getState()) {
                    case PEEKED:

                        break;
                    case EXPANDED:
                        break;
                    default:
                }
            }
        });
    }

    private void setupLanguages() {
        bottomSheetViewHolder.languageMultiOptionView.setSelection(languageResult, true);
        bottomSheetViewHolder.languageMultiOptionView.setOnItemSelectedListener(new MultiOptionView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(boolean selected, int position, String item) {
                languageResult.clear();
                languageResult.addAll(bottomSheetViewHolder.languageMultiOptionView.getSelectedIndices());
            }
        });
    }
}
