package com.nloops.ntasks.UI;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import java.util.Calendar;

public class TimePickerFragment extends DialogFragment {

    TimePickerDialog.OnTimeSetListener mOnTimeSetListenr;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int hours = c.get(Calendar.HOUR_OF_DAY);
        int minutes = c.get(Calendar.MINUTE);

        return new TimePickerDialog(
                getActivity(),
                mOnTimeSetListenr,
                hours,
                minutes,
                false
        );
    }

    public void setOnTimeSetListenr(TimePickerDialog.OnTimeSetListener timeSetListenr) {
        this.mOnTimeSetListenr = timeSetListenr;
    }

}
