package astar.smartfitness.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

import astar.smartfitness.R;

public class SingleOptionView extends LinearLayout {
    private ArrayList<CharSequence> items;

    private int currentSelectedPosition = -1;
    private int previousSelectedPosition = -1;

    public interface OnItemSelectedListener {
        void onItemSelected(int position, CharSequence item);
    }

    private OnItemSelectedListener internalListener = new OnItemSelectedListener() {
        @Override
        public void onItemSelected(int position, CharSequence item) {
            setCurrentSelectedPosition(position);

            if (externalListener != null) {
                externalListener.onItemSelected(position, items.get(position));
            }
        }
    };

    private OnItemSelectedListener externalListener = null;

    public void setOnItemSelectedListener(OnItemSelectedListener listener) {
        this.externalListener = listener;
    }

    public SingleOptionView(Context context) {
        super(context);
        initView(context);
    }

    public SingleOptionView(Context context, ArrayList<CharSequence> items) {
        super(context);
        this.items = items;
        initView(context);
    }

    public SingleOptionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
        initView(context);
    }

    public SingleOptionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        if (this.items == null) {
            this.items = new ArrayList<>();
        }

        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);

        int l = items.size();

        for (int i = 0; i < l; i++) {
            View childView = View.inflate(context, R.layout.single_option_view_item, null);
            LinearLayout.LayoutParams params = new LayoutParams(0, LayoutParams.MATCH_PARENT, 1f);
            childView.setLayoutParams(params);

            TextView textView = (TextView) childView.findViewById(R.id.text);
            textView.setText(items.get(i));

            final int position = i;
            childView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    internalListener.onItemSelected(position, items.get(position));
                }
            });

            childView.setSelected(false);

            addView(childView);
        }
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        if (attrs == null)
            return;

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.SingleOptionView,
                0, 0);

        try {
            items = new ArrayList<>(Arrays.asList(a.getTextArray(R.styleable.SingleOptionView_array)));
        } finally {
            a.recycle();
        }
    }

    public void setCurrentSelectedPosition(int position) {
        previousSelectedPosition = currentSelectedPosition;
        currentSelectedPosition = position;

        if (position >= 0 && position < getChildCount()) {
            checkSelectedStates();
        }
    }

    private void setTextStyleAtPosition(int position, int style) {
        TextView textView = (TextView) getChildAt(position).findViewById(R.id.text);
        if (style == Typeface.NORMAL) {
            textView.setTypeface(Typeface.create(textView.getTypeface(), Typeface.NORMAL), Typeface.NORMAL);
        } else {
            textView.setTypeface(textView.getTypeface(), style);
        }
    }

    private void checkSelectedStates() {
        if (currentSelectedPosition != -1) {
            if (previousSelectedPosition == -1) {
                getChildAt(currentSelectedPosition).setSelected(true);
                setTextStyleAtPosition(currentSelectedPosition, Typeface.BOLD);
            } else {
                getChildAt(previousSelectedPosition).setSelected(false);
                setTextStyleAtPosition(previousSelectedPosition, Typeface.NORMAL);

                getChildAt(currentSelectedPosition).setSelected(true);
                setTextStyleAtPosition(currentSelectedPosition, Typeface.BOLD);
            }
        }
    }
}
