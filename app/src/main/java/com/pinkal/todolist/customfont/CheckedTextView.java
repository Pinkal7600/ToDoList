package com.pinkal.todolist.customfont;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

/**
 * Created by Pinkal Daliya on 21-Oct-16.
 */

public class CheckedTextView extends android.widget.CheckedTextView {
    public CheckedTextView(Context context) {
        super(context);
        applyCustomFont(context);
    }

    public CheckedTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyCustomFont(context);
    }

    public CheckedTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        applyCustomFont(context);
    }

    private void applyCustomFont(Context context) {
        Typeface customFont = FontCache.getTypeface("fonts/avenir.ttf", context);
        setTypeface(customFont);
    }
}
