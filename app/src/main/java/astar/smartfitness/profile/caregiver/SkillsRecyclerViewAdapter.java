package astar.smartfitness.profile.caregiver;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import astar.smartfitness.R;
import astar.smartfitness.model.Skill;
import butterknife.Bind;
import butterknife.ButterKnife;

public class SkillsRecyclerViewAdapter extends RecyclerView.Adapter<SkillsRecyclerViewAdapter.ViewHolder> {
    private ArrayList<Skill> skillList;
    private Context context;
    private RecyclerView recyclerView;

    public SkillsRecyclerViewAdapter() {
        this.skillList = new ArrayList<>();
    }

    public SkillsRecyclerViewAdapter(ArrayList<Skill> skillList) {
        this.skillList = skillList;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.item_caregiver_profile_skills, parent, false);
        // Return a new holder instance
        return new SkillsRecyclerViewAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Skill skill = skillList.get(position);

        holder.titleTextView.setText(skill.getTitle());
        holder.descriptionTextView.setText(skill.getDescription());
    }

    @Override
    public int getItemCount() {
        return skillList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        @Bind(R.id.title)
        public TextView titleTextView;
        @Bind(R.id.description)
        public TextView descriptionTextView;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void addSkill(String title, String description) {
        Skill skill = new Skill();
        skill.setTitle(title);
        skill.setDescription(description);

        addSkill(skill);
    }

    public void addSkill(Skill skill) {
        skillList.add(skill);

        notifyItemInserted(getItemCount());

        if (recyclerView != null)
            recyclerView.smoothScrollToPosition(getItemCount());
    }

    public void deleteSkill(int position) {
        skillList.remove(position);

        notifyItemRemoved(position);
    }

    public Skill getSkill(int position) {
        return skillList.get(position);
    }

    public void setSkill(int position, Skill skill) {
        skillList.set(position, skill);

        notifyItemChanged(position);
    }

    public ArrayList<Skill> getSkillList() {
        return skillList;
    }
}
