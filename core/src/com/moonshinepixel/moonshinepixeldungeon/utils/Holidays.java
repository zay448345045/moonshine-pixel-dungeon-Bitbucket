package com.moonshinepixel.moonshinepixeldungeon.utils;

import com.moonshinepixel.moonshinepixeldungeon.items.food.Pasty;

import java.util.Calendar;

public enum Holidays {
    NONE,
    EASTER, //TBD
    HWEEN,  //2nd week of october though first day of november
    XMAS;   //3rd week of december through first week of january

    private static Holidays curHoliday;

    static {
        curHoliday = Holidays.NONE;

        final Calendar calendar = Calendar.getInstance();
        switch (calendar.get(Calendar.MONTH)) {
            case Calendar.JANUARY:
                if (calendar.get(Calendar.WEEK_OF_MONTH) == 1)
                    curHoliday = Holidays.XMAS;
                break;
            case Calendar.OCTOBER:
                if (calendar.get(Calendar.WEEK_OF_MONTH) >= 2)
                    curHoliday = Holidays.HWEEN;
                break;
            case Calendar.NOVEMBER:
                if (calendar.get(Calendar.DAY_OF_MONTH) == 1)
                    curHoliday = Holidays.HWEEN;
                break;
            case Calendar.DECEMBER:
                if (calendar.get(Calendar.WEEK_OF_MONTH) >= 3)
                    curHoliday = Holidays.XMAS;
                break;
        }
    }

    public static Holidays getHoliday(){
        //System.out.println(curHoliday);
        return curHoliday;
    }
}
