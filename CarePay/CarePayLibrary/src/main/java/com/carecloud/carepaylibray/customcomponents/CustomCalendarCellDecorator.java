package com.carecloud.carepaylibray.customcomponents;

import com.squareup.timessquare.CalendarCellDecorator;
import com.squareup.timessquare.CalendarCellView;

import java.util.Date;

/**
 * Created by jorge on 21/01/17.
 */

public class CustomCalendarCellDecorator implements CalendarCellDecorator {
    private int colorDayCurrentMonth;
    private int colorDayNotCurrentMonth;

    public CustomCalendarCellDecorator(int colorDayCurrentMonth, int colorDayNotCurrentMonth) {
        this.colorDayNotCurrentMonth = colorDayNotCurrentMonth;
        this.colorDayCurrentMonth = colorDayCurrentMonth;
    }

    /**
     * Method to decorate each cell in the calendar
     * @param cellView the cellView
     * @param date the date of cell
     */
    public void decorate(CalendarCellView cellView, Date date) {
        if (cellView.isCurrentMonth()){
            cellView.setTextColor(colorDayCurrentMonth);
        }else{
            cellView.setTextColor(colorDayNotCurrentMonth);
        }
    }
}
