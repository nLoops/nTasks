package com.nloops.ntasks.UI;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import java.util.Calendar;

/**
 * This Fragment Will use as a picker to get Task DueDate.
 */
public class DatePickerFragment extends DialogFragment {

    DatePickerDialog.OnDateSetListener mDateListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(),
                mDateListener, year, month, day);
    }


    public void setDateListener(DatePickerDialog.OnDateSetListener dateListener) {
        this.mDateListener = dateListener;
    }
}
