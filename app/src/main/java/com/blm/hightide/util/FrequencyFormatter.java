package com.blm.hightide.util;


import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;

public class FrequencyFormatter implements ValueFormatter {

    private static final String EMPTY_STR = "";

    private static final int ZERO = 0;

    private int frequency;

    private DecimalFormat decimalFormat = new DecimalFormat("###,###,##0.0");

    public FrequencyFormatter() {
        this(5);
    }

    public FrequencyFormatter(int frequency) {
        this.frequency = frequency;
    }

    @Override
    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
        return entry.getXIndex() % frequency == ZERO ?
                decimalFormat.format(value) :
                EMPTY_STR;
    }
}