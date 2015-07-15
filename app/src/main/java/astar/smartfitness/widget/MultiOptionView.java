package astar.smartfitness.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.GridLayout;
import android.util.AttributeSet;
import android.view.Gravity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import astar.smartfitness.R;
import astar.smartfitness.util.Utils;

public class MultiOptionView extends GridLayout {
    private ArrayList<String> items;

    private Set<Integer> selectedItems = new HashSet<>();

    public interface OnItemSelectedListener {
        void onItemSelected(boolean selected, int position, String item);
    }

    private OnItemSelectedListener internalListener = new OnItemSelectedListener() {
        @Override
        public void onItemSelected(boolean selected, int position, String item) {
            if (selected) {
                selectedItems.add(position);
            } else {
                selectedItems.remove(position);
            }

            if (externalListener != null) {
                externalListener.onItemSelected(selected, position, item);
            }
        }
    };

    private OnItemSelectedListener externalListener = null;

    public void setOnItemSelectedListener(OnItemSelectedListener listener) {
        this.externalListener = listener;
    }

    public MultiOptionView(Context context) {
        super(context);
        initView(context);
    }

    public MultiOptionView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initAttrs(context, attributeSet);
        initView(context);
    }

    public MultiOptionView(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);
        initAttrs(context, attributeSet);
        initView(context);
    }

    private void initView(Context context) {
        if (this.items == null) {
            this.items = new ArrayList<>();
        }

//        setWeightDefault(1f);
//        setGravity(Gravity.FILL);

        int columnCount = 3;
        setColumnCount(columnCount);


        int l = items.size();

        for (int i = 0; i < l; i++) {
            ChipView chipView = new ChipView(context, items.get(i));

            final int position = i;
            final String item = items.get(i);
            chipView.setOnSelectedListener(new ChipView.OnSelectedListener() {
                @Override
                public void onSelected(ChipView chipView, boolean isSelected) {
                    internalListener.onItemSelected(isSelected, position, item);
                }
            });

            LayoutParams layoutParams = new LayoutParams(GridLayout.spec(i / columnCount, 1), GridLayout.spec(i % columnCount, 1, 1f));
            int margin = Utils.dpToPx(context, 2f);

            layoutParams.setGravity(Gravity.FILL_HORIZONTAL);
            layoutParams.width = 0;
            layoutParams.setMargins(margin, margin, margin, margin);
            chipView.setLayoutParams(layoutParams);
            addView(chipView);
        }
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        if (attrs == null)
            return;

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.MultiOptionView,
                0, 0);

        try {
            ArrayList<CharSequence> list = new ArrayList<>(Arrays.asList(a.getTextArray(R.styleable.MultiOptionView_array)));

            int size = list.size();
            items = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                items.add(list.get(i).toString());
            }

        } finally {
            a.recycle();
        }
    }

    public ArrayList<Integer> getSelectedIndices() {
        Integer[] array = new Integer[selectedItems.size()];
        selectedItems.toArray(array);

        return new ArrayList<>(Arrays.asList(array));
    }

    public void clearSelection() {
        this.items.clear();

        ArrayList<Integer> selectedIndices = getSelectedIndices();

        int l = selectedIndices.size();

        int index;
        for (int i = 0; i < l; i++) {
            index = selectedIndices.get(i);
            setSelection(index, false);
        }
    }

    public void setSelection(int index, boolean state) {
        int total = getChildCount();
        if (index >= 0 && index < total) {
            ((ChipView) getChildAt(index)).setState(state);
        }
    }

    public void setSelection(ArrayList<Integer> items, boolean state) {
        int l = items.size();

        for (int i = 0; i < l; i++) {
            int index = items.get(i);
            setSelection(index, state);
        }
    }

}
