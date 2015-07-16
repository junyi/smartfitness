package astar.smartfitness.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

public class EmptyRecyclerView extends RecyclerView {

    private boolean isEnabled = true;

    @Nullable
    private View emptyView;

    @Nullable
    private View loadingView;

    public EmptyRecyclerView(Context context) {
        super(context);
    }

    public EmptyRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EmptyRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private void checkIfEmpty() {
        if (emptyView != null && getAdapter() != null) {
            if (isEnabled) {
                emptyView.setVisibility(getAdapter().getItemCount() > 0 ? GONE : VISIBLE);
            } else {
                emptyView.setVisibility(GONE);
            }
        }

        if (loadingView != null) {
            if (isEnabled || getAdapter() != null && getAdapter().getItemCount() > 0) {
                loadingView.setVisibility(GONE);
            } else {
                loadingView.setVisibility(VISIBLE);
                isEnabled = true;
            }
        }
    }

    private final AdapterDataObserver observer = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            checkIfEmpty();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            checkIfEmpty();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            checkIfEmpty();
        }
    };

    @Override
    public void setAdapter(@Nullable Adapter adapter) {
        final Adapter oldAdapter = getAdapter();
        if (oldAdapter != null) {
            oldAdapter.unregisterAdapterDataObserver(observer);
        }
        super.setAdapter(adapter);
        if (adapter != null) {
            adapter.registerAdapterDataObserver(observer);
        }
        checkIfEmpty();
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (null != emptyView && (visibility == GONE || visibility == INVISIBLE)) {
            emptyView.setVisibility(GONE);
        } else {
            checkIfEmpty();
        }
    }

    public void setEmptyView(@Nullable View emptyView) {
        this.emptyView = emptyView;
        checkIfEmpty();
    }

    public void setLoadingView(@Nullable View loadingView) {
        this.loadingView = loadingView;
        checkIfEmpty();
    }

    public void setEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }
}