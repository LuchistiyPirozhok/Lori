package ru.ssau.rest_app.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Spinner;

/**
 * Created by Дмитрий on 23.12.2016.
 */
public class MySpinner extends Spinner {
    OnItemSelectedListener listener;

    public MySpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setSelection(int position) {
        super.setSelection(position);
        if (listener != null)
            listener.onItemSelected(null, null, position, 0);
    }

    public void setOnItemSelectedEvenIfUnchangedListener(
            OnItemSelectedListener listener) {
        this.listener = listener;
    }
}