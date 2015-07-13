package astar.smartfitness.screen.profile.caregiver;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import java.util.ArrayList;
import java.util.List;

import astar.smartfitness.util.DividerItemDecoration;
import astar.smartfitness.R;
import astar.smartfitness.util.RecyclerItemClickListener;
import astar.smartfitness.model.Skill;
import astar.smartfitness.widget.EmptyRecyclerView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SkillsSectionFragment extends SectionFragment implements Validator.ValidationListener {
    public static final String ARG_SKILL_LIST = "skill_list";

    @Bind(R.id.recycler_view)
    EmptyRecyclerView recyclerView;

    @Bind(R.id.empty_view)
    View emptyView;

    private ArrayList<Skill> skillList = new ArrayList<>();
    private MaterialDialog dialogInstance = null;
    final DialogViewHolder dialogViewHolder = new DialogViewHolder();

    private SkillsRecyclerViewAdapter recyclerViewAdapter;
    private Validator validator;

    @Override
    public boolean validateSection() {
        if (skillList.size() > 0)
            return true;

        return false;
    }

    @Override
    public void saveSection(Bundle data) {
        skillList = recyclerViewAdapter.getSkillList();
        data.putParcelableArrayList(ARG_SKILL_LIST, skillList);
    }

    @Override
    public void restoreSection(Bundle data) {
        skillList = data.getParcelableArrayList(ARG_SKILL_LIST);
    }

    static class DialogViewHolder {
        @NotEmpty
        @Nullable
        @Bind(R.id.title_edit_text)
        EditText titleEditText;

        @NotEmpty
        @Nullable
        @Bind(R.id.description_edit_text)
        EditText descriptionEditText;

        @Nullable
        @Bind(R.id.title_text_input_layout)
        TextInputLayout titleTextInputLayout;

        @Nullable
        @Bind(R.id.description_text_input_layout)
        TextInputLayout descriptionTextInputLayout;
    }


    public SkillsSectionFragment() {
    }

    public static SkillsSectionFragment newInstance(Bundle data) {
        SkillsSectionFragment fragment = new SkillsSectionFragment();
        fragment.setArguments(data);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_caregiver_profile_skills, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            restoreSection(getArguments());
        }

        recyclerViewAdapter = new SkillsRecyclerViewAdapter(skillList);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST);
        recyclerView.addItemDecoration(itemDecoration);

        recyclerView.setEmptyView(emptyView);

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                showEditSkillDialog(position);
            }
        }));

        recyclerViewAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                super.onItemRangeChanged(positionStart, itemCount);
                skillList = recyclerViewAdapter.getSkillList();
                // Notify changed section
                notifySectionChanged();
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                skillList = recyclerViewAdapter.getSkillList();
                // Notify changed section
                notifySectionChanged();
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                super.onItemRangeRemoved(positionStart, itemCount);
                skillList = recyclerViewAdapter.getSkillList();
                // Notify changed section
                notifySectionChanged();
            }

            @Override
            public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
                super.onItemRangeMoved(fromPosition, toPosition, itemCount);
            }
        });

        notifyToValidate();
    }

    @OnClick(R.id.add_button)
    public void showAddSkillDialog() {
        if (dialogInstance != null)
            dialogInstance.cancel();

        MaterialDialog.Builder dialogBuilder = getDialogBuilder();

        dialogInstance = dialogBuilder.build();
        bindDialogViews(dialogInstance, dialogViewHolder);

        dialogInstance.show();
    }

    private void showEditSkillDialog(final int position) {
        if (dialogInstance != null)
            dialogInstance.cancel();

        MaterialDialog.Builder dialogBuilder = getDialogBuilder();

        dialogInstance = dialogBuilder
                .title("Edit Skill")
                .neutralText("Delete")
                .positiveText("Save")
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        String title = dialogViewHolder.titleEditText.getText().toString();
                        String description = dialogViewHolder.descriptionEditText.getText().toString();

                        Skill skill = new Skill();
                        skill.setTitle(title);
                        skill.setDescription(description);

                        recyclerViewAdapter.setSkill(position, skill);
                    }

                    @Override
                    public void onNeutral(MaterialDialog dialog) {
                        recyclerViewAdapter.deleteSkill(position);
                    }
                })
                .build();

        bindDialogViews(dialogInstance, dialogViewHolder);

        Skill skill = recyclerViewAdapter.getSkill(position);
        dialogViewHolder.titleEditText.setText(skill.getTitle());
        dialogViewHolder.descriptionEditText.setText(skill.getDescription());

        dialogInstance.show();
    }

    private MaterialDialog.Builder getDialogBuilder() {

        boolean wrapInScrollView = true;
        MaterialDialog.Builder dialogBuilder = new MaterialDialog.Builder(getActivity())
                .backgroundColorRes(R.color.bg_dark_blue)
                .title("Add Skill")
                .customView(R.layout.dialog_caregiver_profile_skills, wrapInScrollView)
                .positiveText("Add")
                .negativeText("Cancel")
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        String title = dialogViewHolder.titleEditText.getText().toString();
                        String description = dialogViewHolder.descriptionEditText.getText().toString();

                        recyclerViewAdapter.addSkill(title, description);
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        super.onNegative(dialog);
                    }
                })
                .cancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        dialogInstance = null;
                    }
                })
                .dismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        dialogInstance = null;
                    }
                });

        return dialogBuilder;
    }

    private void bindDialogViews(MaterialDialog dialog, final DialogViewHolder dialogViewHolder) {
        dialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);

        ButterKnife.bind(dialogViewHolder, dialog);

        validator = new Validator(dialogViewHolder);
        validator.setValidationListener(this);

        if (dialogViewHolder.titleEditText == null || dialogViewHolder.descriptionEditText == null) {
            return;
        }

        dialogViewHolder.titleEditText.setTag(dialogViewHolder.titleTextInputLayout);
        dialogViewHolder.descriptionEditText.setTag(dialogViewHolder.descriptionTextInputLayout);

        dialogViewHolder.titleEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                dialogViewHolder.titleTextInputLayout.setErrorEnabled(false);
                dialogViewHolder.descriptionTextInputLayout.setErrorEnabled(false);
                validator.validate();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        dialogViewHolder.descriptionEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                dialogViewHolder.titleTextInputLayout.setErrorEnabled(false);
                dialogViewHolder.descriptionTextInputLayout.setErrorEnabled(false);
                validator.validate();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void populateWithMockSkills() {
        Skill skill = new Skill();
        skill.setTitle("Expert cooking skills");
        skill.setDescription("Achieved high level in cooking meals of variety ranging from Thai to Korean.");

        for (int i = 0; i < 10; i++) {
            recyclerViewAdapter.addSkill(skill);
        }
    }

    @Override
    public void onValidationSucceeded() {
        if (dialogInstance != null) {
            View postiveButton = dialogInstance.getActionButton(DialogAction.POSITIVE);
            if (postiveButton != null) {
                postiveButton.setEnabled(true);
            }
        }
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        if (dialogInstance != null) {
            View postiveButton = dialogInstance.getActionButton(DialogAction.POSITIVE);
            if (postiveButton != null) {
                postiveButton.setEnabled(false);
            }
        }
//        int l = errors.size();

//        for (int i = 0; i < l; i++) {
//            ValidationError error = errors.get(i);
//            TextInputLayout textInputLayout = (TextInputLayout) error.getView().getTag();
//            textInputLayout.setErrorEnabled(true);
//            textInputLayout.setError(error.getCollatedErrorMessage(getActivity()));
//        }
    }
}
