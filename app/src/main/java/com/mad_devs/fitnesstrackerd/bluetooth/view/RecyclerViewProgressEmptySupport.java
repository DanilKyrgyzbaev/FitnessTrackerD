package com.mad_devs.fitnesstrackerd.bluetooth.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ProgressBar;

import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewProgressEmptySupport extends RecyclerView  {
    /**
     * Представление, чтобы показать, если список пуст.
     */
    private View emptyView;

    /**
     * Наблюдатель для списка данных. Устанавливает пустое представление, если список пуст.
     */
    private RecyclerView.AdapterDataObserver emptyObserver = new RecyclerView.AdapterDataObserver () {

        @Override
        public void onChanged() {
            RecyclerView.Adapter<?> adapter = getAdapter();
            if (adapter != null && emptyView != null) {
                if (adapter.getItemCount() == 0) {
                    emptyView.setVisibility( VISIBLE);
                    RecyclerViewProgressEmptySupport.this.setVisibility( GONE);
                } else {
                    emptyView.setVisibility( GONE);
                    RecyclerViewProgressEmptySupport.this.setVisibility( VISIBLE);
                }
            }

        }
    };

    /**
     * Вид отображается во время загрузки.
     */
    private ProgressBar progressView;

    public RecyclerViewProgressEmptySupport(Context context) {
        super(context);
    }

    public RecyclerViewProgressEmptySupport(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public RecyclerViewProgressEmptySupport(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setAdapter(RecyclerView.Adapter adapter) {
        super.setAdapter(adapter);
        if (adapter != null) {
            adapter.registerAdapterDataObserver(emptyObserver);
        }

        emptyObserver.onChanged();
    }

    /**
     * Устанавливает пустой вид.
     */
    public void setEmptyView(View emptyView) {
        this.emptyView = emptyView;
    }

    /**
     * Устанавливает вид прогресса.
     */
    public void setProgressView(ProgressBar progressView) {
        this.progressView = progressView;
    }

    /**
     * Показывает вид прогресса.
     */
    public void startLoading() {
        // Hides the empty view.
        if (this.emptyView != null) {
            this.emptyView.setVisibility(GONE);
        }
        // Shows the progress bar.
        if (this.progressView != null) {
            this.progressView.setVisibility(VISIBLE);
        }
    }

    /**
     * Скрывает вид прогресса.
     */
    public void endLoading() {
        // Hides the progress bar.
        if (this.progressView != null) {
            this.progressView.setVisibility(GONE);
        }

        // Forces the view refresh.
        emptyObserver.onChanged();
    }
}
