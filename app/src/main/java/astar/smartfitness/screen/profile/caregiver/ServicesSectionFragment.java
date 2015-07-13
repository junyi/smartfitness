package astar.smartfitness.screen.profile.caregiver;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Checkable;
import android.widget.ListView;

import java.util.ArrayList;

import astar.smartfitness.R;
import butterknife.Bind;
import butterknife.ButterKnife;

public class ServicesSectionFragment extends SectionFragment {
    public static final String ARG_SERVICE_LIST = "service_list";

    @Bind(R.id.list_view)
    ListView listView;

    private ArrayList<Integer> servicesResult = new ArrayList<>();

    private ArrayAdapter<String> listAdapter;

    public ServicesSectionFragment() {
    }

    public static ServicesSectionFragment newInstance(Bundle data) {
        ServicesSectionFragment fragment = new ServicesSectionFragment();
        fragment.setArguments(data);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_caregiver_profile_services, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            restoreSection(getArguments());
        }

        final String[] serviceList = getResources().getStringArray(R.array.service_list);
        listAdapter = new ArrayAdapter<>(getActivity(), R.layout.item_caregiver_profile_services, serviceList);
        listView.setAdapter(listAdapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (((Checkable) view).isChecked()) {
                    servicesResult.add((Integer) position);
                } else {
                    servicesResult.remove((Integer) position);
                }

                // Notify section changed
                notifySectionChanged();
            }
        });

        int l = servicesResult.size();
        for (int i = 0; i < l; i++) {
            listView.setItemChecked(servicesResult.get(i), true);
        }

        notifyToValidate();
    }

    @Override
    public boolean validateSection() {
        return servicesResult.size() > 0;
    }

    @Override
    public void saveSection(Bundle data) {
        data.putIntegerArrayList(ARG_SERVICE_LIST, servicesResult);
    }

    @Override
    public void restoreSection(Bundle data) {
        servicesResult = data.getIntegerArrayList(ARG_SERVICE_LIST);
    }
}
