package astar.smartfitness.profile.caregiver;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import astar.smartfitness.R;
import butterknife.Bind;
import butterknife.ButterKnife;

public class ServicesSectionFragment extends Fragment {

    @Bind(R.id.list_view)
    ListView listView;

    private ArrayAdapter<String> listAdapter;

    public ServicesSectionFragment() {
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

        String[] serviceList = getResources().getStringArray(R.array.service_list);
        listAdapter = new ArrayAdapter<>(getActivity(), R.layout.item_caregiver_profile_services, serviceList);
        listView.setAdapter(listAdapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
    }
}
