package com.douzi.gamesc.user.utils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateExtendUtil {

    public static final long SECOND = 1000L;
    public static final long MINUTE = 60000L;
    public static final long HOUR = 3600000L;
    public static final long DAY = 86400000L;
    public static final String YEAR_PART = "yyyy";
    public static final String MONTH_PART = "MM";
    public static final String DATE_PART = "dd";
    public static final String HOUR_PART = "HH";
    public static final String MINUTE_PART = "mm";
    public static final String SECOND_PART = "ss";
    public static final String MILlISECOND_PART = "SSS";
    public static final String SMALL_DATE_FORMAT = "yyyy-MM-dd";
    public static final String FULL_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String MILLIS_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String UTC_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static final String FULL_DATE_FORMAT_TWO = "yyyyMMddHHmmssSSS";

    public static Date parseString2Date(String s) throws Exception {
        Date date = null;
        try {
            date = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")).parse(s);
        } catch (ParseException parseexception) {
            try {
                date = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).parse(s);
            } catch (ParseException parseexception1) {
                try {
                    date = (new SimpleDateFormat("yyyy-MM-dd")).parse(s);
                } catch (ParseException parseexception2) {
                    throw new Exception(parseexception2);
                }
            }
        }
        return date;
    }

    public static String getNowFullDate() {
        return formatDate2FullDateString(new Date());
    }

    public static String getNowSmallDate() {
        return formatDate2SmallDateString(new Date());
    }

    public static String formatDate2FullDateString(Date date) {
        return formatDate2String(date, "yyyy-MM-dd HH:mm:ss");
    }

    public static String formatDate2SmallDateString(Date date) {
        return formatDate2String(date, "yyyy-MM-dd");
    }

    public static String formatDate2String(Date date, String s) {
        return (new SimpleDateFormat(s)).format(date);
    }

    public static Date dateAdd(String s, int i, Date date) {
        Date date1 = null;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        GregorianCalendar gregoriancalendar = new GregorianCalendar(
                calendar.get(1), calendar.get(2), calendar.get(5),
                calendar.get(11), calendar.get(12), calendar.get(13));
        s = s.toLowerCase();
        if ("day".equals(s)) {
            gregoriancalendar.add(5, i);
            date1 = gregoriancalendar.getTime();
        } else if ("month".equals(s)) {
            gregoriancalendar.add(2, i);
            date1 = gregoriancalendar.getTime();
        } else if ("year".equals(s)) {
            gregoriancalendar.add(1, i);
            date1 = gregoriancalendar.getTime();
        } else if ("hour".equals(s)) {
            gregoriancalendar.add(10, i);
            date1 = gregoriancalendar.getTime();
        } else if ("minute".equals(s)) {
            gregoriancalendar.add(12, i);
            date1 = gregoriancalendar.getTime();
        } else if ("second".equals(s)) {
            gregoriancalendar.add(13, i);
            date1 = gregoriancalendar.getTime();
        }
        return date1;
    }

    public static String getFirstDayOfMonth() {
        String s = "";
        Calendar calendar = Calendar.getInstance();
        calendar.set(5, 1);
        s = formatDate2SmallDateString(calendar.getTime());
        return s;
    }

    public static String getNextDay() {
        String s = "";
        Calendar calendar = Calendar.getInstance();
        calendar.add(5, 1);
        s = formatDate2SmallDateString(calendar.getTime());
        return s;
    }

    public static Timestamp dateAddTimestamp(String s, int i,
            Timestamp timestamp) {
        Timestamp timestamp1 = null;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp.getTime());
        GregorianCalendar gregoriancalendar = new GregorianCalendar(
                calendar.get(1), calendar.get(2), calendar.get(5),
                calendar.get(11), calendar.get(12), calendar.get(13));
        s = s.toLowerCase();
        if ("day".equals(s)) {
            gregoriancalendar.add(5, i);
            timestamp1 = new Timestamp(gregoriancalendar.getTimeInMillis());
        } else if ("month".equals(s)) {
            gregoriancalendar.add(2, i);
            timestamp1 = new Timestamp(gregoriancalendar.getTimeInMillis());
        } else if ("year".equals(s)) {
            gregoriancalendar.add(1, i);
            timestamp1 = new Timestamp(gregoriancalendar.getTimeInMillis());
        } else if ("hour".equals(s)) {
            gregoriancalendar.add(10, i);
            timestamp1 = new Timestamp(gregoriancalendar.getTimeInMillis());
        } else if ("minute".equals(s)) {
            gregoriancalendar.add(12, i);
            timestamp1 = new Timestamp(gregoriancalendar.getTimeInMillis());
        } else if ("second".equals(s)) {
            gregoriancalendar.add(13, i);
            timestamp1 = new Timestamp(gregoriancalendar.getTimeInMillis());
        }
        return timestamp1;
    }

    public static Timestamp dateAddTimestampFromNow(String s, int i,
            Timestamp timestamp) {
        Timestamp timestamp1 = new Timestamp((new Date()).getTime());
        if (timestamp.before(timestamp1))
            timestamp = timestamp1;
        return dateAddTimestamp(s, i, timestamp);
    }

    public static int getMonthDiff(Calendar calendar, Calendar calendar1) {
        int i = calendar.get(2) - calendar1.get(2);
        int j = calendar.get(1) - calendar1.get(1);
        return j * 12 + i;
    }

    public static int daysOfTwo(Date date, Date date1) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int i = calendar.get(6);
        calendar.setTime(date1);
        int j = calendar.get(6);
        return j - i;
    }

    public static long dateDiff(String s, Date date, Date date1) {
        long l = 0L;
        if ("year".equalsIgnoreCase(s) || "y".equalsIgnoreCase(s)) {
            l = (date1.getTime() - date.getTime()) / 1471228928L;
            return l;
        }
        if ("month".equalsIgnoreCase(s) || "m".equalsIgnoreCase(s)) {
            l = (date1.getTime() - date.getTime()) / -1702967296L;
            return l;
        }
        if ("day".equalsIgnoreCase(s) || "d".equalsIgnoreCase(s)) {
            l = (date1.getTime() - date.getTime()) / 86400000L;
            return l;
        }
        if ("hour".equalsIgnoreCase(s) || "h".equalsIgnoreCase(s)) {
            l = (date1.getTime() - date.getTime()) / 3600000L;
            return l;
        }
        if ("minute".equalsIgnoreCase(s) || "min".equalsIgnoreCase(s)) {
            l = (date1.getTime() - date.getTime()) / 60000L;
            return l;
        }
        if ("second".equalsIgnoreCase(s) || "s".equalsIgnoreCase(s)) {
            l = (date1.getTime() - date.getTime()) / 1000L;
            return l;
        } else {
            return l;
        }
    }

    public static long dateDiff(String s, String s1, String s2)
            throws Exception {
        return dateDiff(s, parseString2Date(s1), parseString2Date(s2));
    }

    public static long dateDayDiff(String s, String s1) throws Exception {
        return dateDiff("day", parseString2Date(s), parseString2Date(s1));
    }

    public static long dateMonthDiff(String s, String s1)
            throws Exception {
        return dateDiff("month", parseString2Date(s), parseString2Date(s1));
    }

    public static long dateYearDiff(String s, String s1)
            throws Exception {
        return dateDiff("year", parseString2Date(s), parseString2Date(s1));
    }

    public static Timestamp getNowTimestamp() {
        return new Timestamp((new Date()).getTime());
    }

    public static String getPassByDate(int i, String s) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(5, i);
        return (new SimpleDateFormat(s)).format(calendar.getTime());
    }

    public static Date getUTCDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int i = calendar.get(15);
        int j = calendar.get(16);
        calendar.add(14, -(i + j));
        return new Date(calendar.getTimeInMillis());
    }

    public static Timestamp getTimestampForString(String s) {
        return Timestamp.valueOf(s);
    }
}
