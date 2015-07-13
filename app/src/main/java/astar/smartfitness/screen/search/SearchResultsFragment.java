package astar.smartfitness.screen.search;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import astar.smartfitness.R;
import astar.smartfitness.model.CaregiverProfile;
import astar.smartfitness.model.User;
import astar.smartfitness.util.MarginDecoration;
import bolts.Continuation;
import bolts.Task;
import butterknife.Bind;
import butterknife.ButterKnife;
import timber.log.Timber;

public class SearchResultsFragment extends BaseSearchFragment {

    @Bind(R.id.recycler_view)
    RecyclerView recyclerView;

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

        if (getArguments() != null) {
            executeSearch(getArguments());
        }

        adapter = new SearchResultsRecyclerViewAdapter(getActivity());
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new MarginDecoration(getActivity()));
        recyclerView.setAdapter(adapter);

    }

    @Override
    public void saveSection(Bundle data) {

    }

    @Override
    public void restoreSection(Bundle data) {

    }

    private void executeSearch(Bundle data) {
        ArrayList<Integer> servicesResult = data.getIntegerArrayList(FilterFragment.ARG_SERVICES);
        String genderResult = data.getString(FilterFragment.ARG_GENDER);
        int yearOfExpResult = data.getInt(FilterFragment.ARG_YEAR_OF_EXP);
        int[] budgetResult = data.getIntArray(FilterFragment.ARG_BUDGET);
        ArrayList<Integer> languagesResult = data.getIntegerArrayList(FilterFragment.ARG_LANGUAGES);

        String[] ratings = getResources().getStringArray(R.array.rating_list);
        String[] genders = getResources().getStringArray(R.array.gender_list);
        String[] languages = getResources().getStringArray(R.array.language_list);
        String[] services = getResources().getStringArray(R.array.service_list);

        ParseQuery<CaregiverProfile> query = ParseQuery.getQuery(CaregiverProfile.class);
        query.include(CaregiverProfile.KEY_USER_ID);

        ParseQuery<User> innerQuery = ParseQuery.getQuery(User.class);
        innerQuery.whereEqualTo(User.KEY_ROLES, User.ROLE_CAREGIVER);

        query.addDescendingOrder(CaregiverProfile.KEY_RATING);

//        if (genderResult != null) {
//            ParseQuery<User> genderQuery = ParseQuery.getQuery(User.class);
//            genderQuery.whereEqualTo(User.KEY_GENDER, genderResult);
//            query.whereMatchesQuery(CaregiverProfile.KEY_USER_ID, genderQuery);
//        }
//
//        if (budgetResult != null) {
//            query.whereGreaterThanOrEqualTo(CaregiverProfile.KEY_WAGE_RANGE_MIN, budgetResult[0]);
//            query.whereLessThanOrEqualTo(CaregiverProfile.KEY_WAGE_RANGE_MAX, budgetResult[1]);
//        }
//
//        if (yearOfExpResult != -1) {
//            query.whereGreaterThanOrEqualTo(CaregiverProfile.KEY_YEAR_OF_EXP, yearOfExpResult);
//        }
//
//        if (servicesResult != null) {
//            query.whereContainedIn(CaregiverProfile.KEY_SERVICES, servicesResult);
//        }
//
//        if (languagesResult != null) {
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

    private void onSearchSuccess(List<CaregiverProfile> result) {
        adapter.setCaregiverList(result);
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
}
