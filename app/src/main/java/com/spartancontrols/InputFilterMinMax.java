package com.spartancontrols;

import android.text.InputFilter;
import android.text.Spanned;

/**
 * Sets the minimum and maximum number for a number input field
 */
public class InputFilterMinMax implements InputFilter {

    private int min, max;

    /**
     *
     * @param min - int the smallest number that can be used for the input field
     * @param max - int the largest number that can be used for input field
     */
    public InputFilterMinMax(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public InputFilterMinMax(String min, String max) {
        this.min = Integer.parseInt(min);
        this.max = Integer.parseInt(max);
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        try {
            int input = Integer.parseInt(dest.toString() + source.toString());
            if (isInRange(min, max, input))
                return null;
        } catch (NumberFormatException nfe) { }
        return "";
    }

    private boolean isInRange(int a, int b, int c) {
        return b > a ? c >= a && c <= b : c >= b && c <= a;
    }
}