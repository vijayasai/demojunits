package com.externalize.mock.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Pattern;

import org.apache.commons.lang3.time.FastDateFormat;

public class TimestampUtils {
	
	
	public static final String TIMEZONE_UTC = "UTC";
	public static final String TIMEZONE_EDT = "EDT";
	public static final String TIMEZONE_GMT = "GMT";
	
	public static final String ISO_8601_DATE_TIME_LONG_FORMAT =  "yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX";
	public static final String ISO_8601_DATE_TIME_ZONE_FORMAT =  "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
	public static final String ISO_8601_DATE_TIME_ZONE_FORMAT_MPAY =  "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
	public static final String ISO_8601_DATE_TIME_NO_ZONE_FORMAT =  "yyyy-MM-dd'T'HH:mm:ss";
	public static final String ISO_8601_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
	public static final String ISO_8601_DATE_FORMAT = "yyyy-MM-dd";
	public static final String ISO_8601_YYYY_MM_FORMAT = "yyyy-MM";
	//ISO 8601 time format uses the 24-hour clock system
	public static final String ISO_8601_TIME_FORMAT_MAX = "T23:59:59.999";
	public static final String ISO_8601_TIME_FORMAT = "HH:mm:ss"; 
	public static final String INVALID_DATE_STR="9999-99-99";
	public static final String DUMMY_DATE_STR="0000-00-00";
	
	private static final long DAY_IN_MILLIS = 1000 * 60 * 60 * 24;
	private final static Pattern IsoDateRegExPattern = Pattern.compile("^\\d{4}-[01]\\d-[0-3]\\d$");
	private final static SimpleDateFormat IsoDateFormat = new SimpleDateFormat(ISO_8601_DATE_FORMAT);
	private final static FastDateFormat FastIsoDateFormat = FastDateFormat.getInstance(ISO_8601_DATE_FORMAT);
	private final static FastDateFormat FastIsoYYYYDashMMFormat = FastDateFormat.getInstance(ISO_8601_YYYY_MM_FORMAT);

	static {
		// FastDateFormat parsing is not detecting invalid date (e.g. Feb 30) as errors. So use SimpleDateFormat if date parsing robustness is required.
		IsoDateFormat.setLenient(false); // make the parsing more strict
	}
	
	private TimestampUtils() {
	}
	
	public static boolean isValidIsoDateTodayOrBefore(String iso8601DateStr) {
		try {
			return isIsoDateTodayOrBefore(iso8601DateStr);
		}
		catch (ParseException e) {
			return false;
		}
	}
	
	public static boolean isIsoDateTodayOrBefore(String iso8601DateStr) throws ParseException {	
		if (!IsoDateRegExPattern.matcher(iso8601DateStr).matches()) { // make sure the simple yyyy-mm-dd pattern is strictly followed.  SimpleDateFormat parsing tolerates yyyy-2-28 (month missing lead zero) but MidTier shouldn't
			return false;
		}
		Date date;
		synchronized (IsoDateFormat) { // need synchronzed block since SimpleDateFormat object is not thread-safe.  Also performance test shows that a single synchronized formatter is faster than using a new one every time.
			date = IsoDateFormat.parse(iso8601DateStr);	// SimpleDateFormat used to get ParseException for impossible dates like yyyy-02-31; FastDateFormat does not check for impossible date
		}
		Date today = Calendar.getInstance().getTime();
		return isSameDay(date, today) || isBeforeDay(date, today);
	}
	
	public static boolean isIsoDateBeforeToday(String iso8601DateStr) throws ParseException {	
		Date date;
		synchronized (IsoDateFormat) { // need synchronzed block since SimpleDateFormat object is not thread-safe.  Also performance test shows that a single synchronized formatter is faster than using a new one every time.
			date = IsoDateFormat.parse(iso8601DateStr);	// SimpleDateFormat used to get ParseException for impossible dates like yyyy-02-31; FastDateFormat does not check for impossible date
		}
		Date today = Calendar.getInstance().getTime();
		return isBeforeDay(date, today);
	}
	
	public static String getIsoTimestampStringZone(Date date) {
		if (date == null) return null;
		DateFormat dateFormat = new SimpleDateFormat(ISO_8601_DATE_TIME_ZONE_FORMAT_MPAY);
		return dateFormat.format(date);
	}
	
	// Return 2 ISO date strings, one is today, one is X days ago (so the date range span X+1 days).  E.g. today is Dec 11, 2000, response would be 2000-12-01 and 2000-12-11
	public static String[] getIsoDateRangeLastXDaysAndToday(int x) {
		Calendar cal = Calendar.getInstance();
		String today = FastIsoDateFormat.format(cal);
		String xMinusOneDaysAgo = getIsoDateXdaysBefore(cal, x);
		return new String[] {xMinusOneDaysAgo,today};
	}
	
	public static String getIsoDateXdaysBefore(String isoDateStr, int x) throws ParseException {
		Date date = FastIsoDateFormat.parse(isoDateStr);
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return getIsoDateXdaysBefore(cal, x);
	}
	
	public static String getIsoDateXdaysBefore(Calendar cal, int x) {
		cal.add(Calendar.DATE, -x);
		String rc = FastIsoDateFormat.format(cal);
		return rc;
	}
	
	 // start of code copied from http://www.java2s.com/Code/Java/Data-Type/Checksifacalendardateistoday.htm
	/**
     * <p>Checks if two dates are on the same day ignoring time.</p>
     * @param date1  the first date, not altered, not null
     * @param date2  the second date, not altered, not null
     * @return true if they represent the same day
     * @throws IllegalArgumentException if either date is <code>null</code>
     */
    public static boolean isSameDay(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            throw new IllegalArgumentException("The dates must not be null");
        }
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        return isSameDay(cal1, cal2);
    }
    
    /**
     * <p>Checks if two calendars represent the same day ignoring time.</p>
     * @param cal1  the first calendar, not altered, not null
     * @param cal2  the second calendar, not altered, not null
     * @return true if they represent the same day
     * @throws IllegalArgumentException if either calendar is <code>null</code>
     */
    public static boolean isSameDay(Calendar cal1, Calendar cal2) {
        if (cal1 == null || cal2 == null) {
            throw new IllegalArgumentException("The dates must not be null");
        }
        return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR));
    }
    
    /**
     * <p>Checks if a date is today.</p>
     * @param date the date, not altered, not null.
     * @return true if the date is today.
     * @throws IllegalArgumentException if the date is <code>null</code>
     */
    public static boolean isToday(Date date) {
        return isSameDay(date, Calendar.getInstance().getTime());
    }
    
    /**
     * <p>Checks if a calendar date is today.</p>
     * @param cal  the calendar, not altered, not null
     * @return true if cal date is today
     * @throws IllegalArgumentException if the calendar is <code>null</code>
     */
    public static boolean isToday(Calendar cal) {
        return isSameDay(cal, Calendar.getInstance());
    }

	/**
	 * <p>Checks if a date is tomorrow.</p>
	 * @param date the date, not altered, not null.
	 * @return true if the date is today.
	 * @throws IllegalArgumentException if the date is <code>null</code>
	 */
	public static boolean isTomorrow(Date date) {
		return isSameDay(date, getFutureDate(1));
	}
	
	 /**
     * <p>Checks if two dates are on the same day ignoring time.</p>
     * @param date1  the first date, not altered, not null
     * @param date2  the second date, not altered, not null
     * @return true if they represent the same day
     * @throws IllegalArgumentException if either date is <code>null</code>
     */
	   /**
     * <p>Checks if the first date is before the second date ignoring time.</p>
     * @param date1 the first date, not altered, not null
     * @param date2 the second date, not altered, not null
     * @return true if the first date day is before the second date day.
     * @throws IllegalArgumentException if the date is <code>null</code>
     */
    public static boolean isBeforeDay(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            throw new IllegalArgumentException("The dates must not be null");
        }
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        return isBeforeDay(cal1, cal2);
    }
  
    public static boolean isBeforeDay(String date1, String date2) {
    	return isBeforeDay(toDate(date1), toDate(date2));
    }
    
    /**
     * <p>Checks if the first calendar date is before the second calendar date ignoring time.</p>
     * @param cal1 the first calendar, not altered, not null.
     * @param cal2 the second calendar, not altered, not null.
     * @return true if cal1 date is before cal2 date ignoring time.
     * @throws IllegalArgumentException if either of the calendars are <code>null</code>
     */
    public static boolean isBeforeDay(Calendar cal1, Calendar cal2) {
        if (cal1 == null || cal2 == null) {
            throw new IllegalArgumentException("The dates must not be null");
        }
        if (cal1.get(Calendar.ERA) < cal2.get(Calendar.ERA)) return true;
        if (cal1.get(Calendar.ERA) > cal2.get(Calendar.ERA)) return false;
        if (cal1.get(Calendar.YEAR) < cal2.get(Calendar.YEAR)) return true;
        if (cal1.get(Calendar.YEAR) > cal2.get(Calendar.YEAR)) return false;
        return cal1.get(Calendar.DAY_OF_YEAR) < cal2.get(Calendar.DAY_OF_YEAR);
    }
   
 // end of code copied from http://www.java2s.com/Code/Java/Data-Type/Checksifacalendardateistoday.htm
    
 	/**
 	 * This method takes a Date object and returns the ISO 8601 string format representation.
 	 * 
 	 * @param date
 	 * @return ISO 8601 string format for the date
 	 */
 	
 	public static String getIsoTimestampString(Date date) {
 		if (date == null) return null;
 		DateFormat dateFormat = new SimpleDateFormat(ISO_8601_DATE_TIME_FORMAT, Locale.US);
 		dateFormat.setTimeZone(TimeZone.getTimeZone(TIMEZONE_UTC));
 		return dateFormat.format(date);
 	}
 	
 	public static String getIsoTimestampStringNoLocale(Date date) {
 		if (date == null) return null;
 		DateFormat dateFormat = new SimpleDateFormat(ISO_8601_DATE_TIME_FORMAT);
 		return dateFormat.format(date);
 	}
 	
 	public static String getLongIsoTimestampString() {
 		DateFormat dateFormat = new SimpleDateFormat(ISO_8601_DATE_TIME_LONG_FORMAT);
 		// somehow if I set the timezone, the XXX part of the date format pattern does not work (the XXX becomes the letter Z)
 		return dateFormat.format(new Date());
 	}
 	
 	/**
 	 * This method takes a Date object and returns the yyyy-MM-dd string format representation.
 	 * 
 	 * @param date
 	 * @return the yyyy-MM-dd string format for the date
 	 */
 	public static String getIsoDateString(Date date) {
 		DateFormat dateFormat = new SimpleDateFormat(ISO_8601_DATE_FORMAT, Locale.US);
 		return dateFormat.format(date);
 	}
 	
 	/**
 	 * This method takes a Date object and returns yyyy-MM-dd string format representation.
 	 * 
 	 * @param date
 	 * @return ISO 8601 string format for the date
 	 */
 	private static String getIsoDateString(Date date, String timeZone) {
 		DateFormat dateFormat = new SimpleDateFormat(ISO_8601_DATE_FORMAT, Locale.US);
 		dateFormat.setTimeZone(TimeZone.getTimeZone(timeZone));
 		return dateFormat.format(date);
 	}
 	
 	/**
 	 * This method takes a Date object and returns yyyy-MM-dd string format representation.
 	 * The date string will be null if the date is before 1900-01-01
 	 * 
 	 * @param date
 	 * @param timeZone
 	 * @return ISO 8601 string format for the date
 	 */
 	public static String getValidIsoDateString(Date date, String timeZone) {
         Calendar calendar = new GregorianCalendar(1900, 0, 1);
         Date beforeDate = calendar.getTime();
         if((date!=null) && beforeDate.before(date)){
         	return getIsoDateString(date, timeZone);
         }else {
         	return null;
         }
 	}

 	public static boolean isValidIsoDateString(String dateString) {
 		if(dateString == null || dateString.isEmpty() || INVALID_DATE_STR.equalsIgnoreCase(dateString)
         		|| DUMMY_DATE_STR.equalsIgnoreCase(dateString)){
         	return false;
         }else{
         	return true;
         }
        
 	}
 	
 	public static boolean isValidIsoDateFormat(String dateString) {
         Date date = null;
         try {
         	if(isValidIsoDateString(dateString)){
 	            SimpleDateFormat dateFormat = new SimpleDateFormat(ISO_8601_DATE_FORMAT);
 	            date = dateFormat.parse(dateString);
 	            if (!dateString.equals(dateFormat.format(date))) {
 	                date = null;
 	            }
         	}
         } catch (ParseException ex) {
             // Parsing error or wrong date format!
         }
         return date != null;
     }
 	
 	/**
 	 * This method takes a Date object and returns yyyy-MM-dd string format representation.
 	 * The date string will be null if the date is before 1900-01-01
 	 * 
 	 * @param date
 	 * @return ISO 8601 string format for the date
 	 */
 	public static String getValidIsoDateString(Date date) {
         Calendar calendar = new GregorianCalendar(1900, 0, 1);
         Date beforeDate = calendar.getTime();
         if((date!=null) && beforeDate.before(date)){
         	return getIsoDateString(date);
         }else {
         	return null;
         }
 	}
 	
 	/**
 	 * This method takes a Date String in yyyy-MM-dd format and returns a Date object
 	 *
 	 * @param dateString
 	 * @param timeZone
 	 */
 	public static Date toDate(String dateString, String timeZone) {
 		DateFormat dateFormat = new SimpleDateFormat(ISO_8601_DATE_FORMAT, Locale.US);
 		if(TIMEZONE_EDT.equalsIgnoreCase(timeZone)){
 			dateFormat.setTimeZone(TimeZone.getTimeZone("GMT-04:00"));
 		}else{
 			dateFormat.setTimeZone(TimeZone.getTimeZone(timeZone));
 		}
 		try {
 			if(dateString!=null){        	
 				Date date = dateFormat.parse(dateString);
 				return date;
 	        }else {
 	        	return null;
 	        }
 		} catch (ParseException e) {
 			return null;
 		}
 	}
 	
 	
 	/**
 	 * This method takes a Date String in yyyy-MM-dd format and returns a Date object
 	 * 
 	 * @param dateString String in ISO 8601 format
 	 * @return java date object
 	 */
 	public static Date toDate(String dateString) {
 		DateFormat dateFormat = new SimpleDateFormat(ISO_8601_DATE_FORMAT, Locale.US);
 		try {
 			if(dateString!=null){        	
 				Date date = dateFormat.parse(dateString);
 				return date;
 	        }else {
 	        	return null;
 	        }
 		} catch (ParseException e) {
 			return null;
 		}
 	}
 	
 	/**
 	 * This method adds or subtracts the specific number of days to the current date
 	 * 
 	 * @param offset number of days to add or subtract
 	 * @return java date object
 	 */
 	public static Date getFutureDate(int offset){	
 		Calendar c = Calendar.getInstance(); 
 		Date date = new Date();
 		c.setTime(date); 
 		c.add(Calendar.DATE, offset);
 		return c.getTime();
 	}
 	
 	/**
 	 * This method adds or subtracts the specific number of days to the given date
 	 * @param date the target date to add to or subtract from
 	 * @param offset number of days to add or subtract
 	 * @return java date object
 	 */
 	public static Date getFutureDate(Date date, int offset){	
 		Calendar c = Calendar.getInstance();
 		c.setTime(date); 
 		c.add(Calendar.DATE, offset);
 		return c.getTime();
 	}
 	
 	/**
 	 * This method adds or subtracts the specific number of days to the given date
 	 * @param date the target date to add to or subtract from
 	 * @param offsetString number of days to add or subtract
 	 * @return java date object
 	 */
 	public static Date getFutureDate(Date date, String offsetString){	
 		int offset = Integer.parseInt(offsetString);
 		return getFutureDate(date, offset);
 	}
 	
 	/**
 	 * This method adds or subtracts the specific number of date or time to the given date
 	 * @param date the target date to add to or subtract from
 	 * @param field calendar field
 	 * @param offset number of date or time to add or subtract
 	 * @return java date object
 	 */
 	public static Date getFutureDate(Date date, int field, int offset){	
 		Calendar c = Calendar.getInstance(); 
 		c.setTime(date); 
 		c.add(field, offset);
 		return c.getTime();
 	}
 	
 	/**
 	 * This method calculate number of days between two dates
 	 * @param date1
 	 * @param date2
 	 * @return number of days
 	 */
 	public static int getDaysBetween(Date date1, Date date2)
 	{
 		long difference = date2.getTime() - date1.getTime();
 	    return (int)(difference / DAY_IN_MILLIS);
 	}
 	
 	/**
 	 * This method parses the string in HH:mm:ss to java date object
 	 * @param timeStr the string of time to parse (in HH:mm:ss format)
 	 * @return java date object
 	 * @throws ParseException
 	 */
 	public static Date parseDate(String timeStr) throws ParseException{
 		SimpleDateFormat timeFormat = new SimpleDateFormat(ISO_8601_TIME_FORMAT, Locale.US);
 		//timeFormat.setTimeZone(TimeZone.getTimeZone(TIMEZONE_UTC));
 		Date time = timeFormat.parse(timeStr);
 		return time;
 	}

 	/**
 	 * This method parses the string in HH:mm:ss to java date object
 	 * @param dateTimeStr the string of time to parse (in yyyy-MM-dd'T'HH:mm:ssZ format)
 	 * @return java date object
 	 * @throws ParseException
 	 */
 	public static Date parseDateTime(String dateTimeStr) throws ParseException{
 		SimpleDateFormat timeFormat = new SimpleDateFormat(ISO_8601_DATE_TIME_FORMAT, Locale.US);
 		timeFormat.setTimeZone(TimeZone.getTimeZone(TIMEZONE_UTC));
 		Date time = timeFormat.parse(dateTimeStr);
 		return time;
 	}
 	
 	/**
 	 * This method parses the string in HH:mm:ss to java date object
 	 * @param dateTimeStr the string of time to parse (in yyyy-MM-dd'T'HH:mm:ssZ GMT format)
 	 * @return java date object
 	 * @throws ParseException
 	 */
 	public static Date parseDateTimeUTC(String dateTimeStr) throws ParseException{
 		SimpleDateFormat timeFormat = new SimpleDateFormat(ISO_8601_DATE_TIME_NO_ZONE_FORMAT);
 		Date time = timeFormat.parse(dateTimeStr);
 		return time;
 	}
 	
 	public static Date parseDate(String dateTimeStr, String format) throws ParseException{
 		SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.US);
 		Date date = dateFormat.parse(dateTimeStr);
 		return date;
 	}
 	
 	/**
	 * 
	 * @param date the date used to set the year, month and day of the calendar
	 * @param time the time used to set the time of the calendar
	 * @return Calendar object
	 */
	public static Calendar getDateTime(Calendar date, Date time){
		Calendar dateTime = new GregorianCalendar();
		//set the time
		dateTime.setTime(time);
		//change the year, month and day of the calendar to use the fields from the given date
		dateTime.set(date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH));
		dateTime.set(Calendar.MILLISECOND, 0);
		return dateTime;
		
	}
	
	/**
	 * 
	 * @param date the date used to set the year, month and day of the calendar
	 * @param time the time used to set the time of the calendar
	 * @return Calendar object
	 */
	public static Calendar getDateTime(Date date, Date time){
		Calendar dateTime = new GregorianCalendar();
		Calendar calendar = getCalendar(date);
		//set the time
		dateTime.setTime(time);
		//change the year, month and day of the calendar to use the fields from the given date
		dateTime.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
		dateTime.set(Calendar.MILLISECOND, 0);
		return dateTime;	
	}
	
	
	/**
	 * 
	 * @param date the date used to set the YEAR, MONTH and DAY_OF_MONTH calendar fields
	 * @param hourOfDay the value used to set the HOUR_OF_DAY calendar field.
     * @param minute the value used to set the MINUTE calendar field.
	 * @return Calendar object
	 */
	public static Calendar getDateTime(Calendar date, int hourOfDay, int minute){
		Calendar dateTime = new GregorianCalendar();
		//set the year, month, day of month, hour of day and minute for the calendar
		dateTime.set(date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH), hourOfDay, minute);
		dateTime.set(Calendar.MILLISECOND, 0);
		return dateTime;		
	}
	
	/**
	 * Sets this Calendar's current time from the given date
	 * @param date the date used to set the Calendar's time
	 * @return Calendar object
	 */
	public static Calendar getCalendar(Date date){
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime(date);
		return calendar;	
	}
	/**
	 * Gets the date (today or the next day) based on the cut-off time in configuration
	 * @param cutoffTime time string configured in the properties (in HH:mm:ss format)
	 * @return java date object
	 * @throws ParseException
	 */
	
	public static Date getDateByCutOffTime(String cutoffTime) throws ParseException{
		Date currentDate = new Date();
		Date cutoffDate = parseDate(cutoffTime);
		return getDateByCutOffTime(currentDate, cutoffDate);
	}
	
	/**
	 * helping method to get today or the next day based on the cut-off date
	 * @param currentDate current date
	 * @param cutoffDate the cut-off date
	 * @return today or or the next day based on the cut-off date
	 */
	private static Date getDateByCutOffTime(Date currentDate, Date cutoffDate){
		Calendar current_dateTime = getCalendar(currentDate);
		Calendar current_date = removeTime(currentDate);
		Calendar cutoff_dateTime = getDateTime(current_date, cutoffDate);	
		if(current_dateTime.before(cutoff_dateTime)){
			return current_date.getTime();
		}else{
			//return next day
			current_date.add(Calendar.DATE, 1);
			return current_date.getTime();
		}
	}
	
	private static Calendar removeTime(Date date) {
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
	}
	
	public static boolean isDateInWeekend(Date date){
		Calendar cal = GregorianCalendar.getInstance();
		cal.setTime(date);
		if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY){
			return true;
		}
		else{
			return false;
		}
	}
	
	public static Date removeTimeFromDate(Date dt) {
		if (dt != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(dt);
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND,  0);
			cal.set(Calendar.MILLISECOND,  0);
			return cal.getTime();
		}
		return dt;
	}

	/**
	 * insert char at given position in given str string
	 * @param str
	 * @param ch
	 * @param position
	 * @return
	 */
	public static String addChar(String str, char ch, int position) {
		StringBuilder sb = new StringBuilder(str);
		sb.insert(position, ch);
		return sb.toString();
	}

	/**
	 * get the year month string (YYYY-MM) with given number of months ago
	 * @param xMonth
	 * @return
	 */
	public static String getXMonthFromNow(int xMonth){
		Calendar xMonthsAgoCal = Calendar.getInstance();
		if(xMonth!=0) {
			xMonthsAgoCal.add(Calendar.MONTH, xMonth);
		}
		int yyyy = xMonthsAgoCal.get(Calendar.YEAR);
		int mm = xMonthsAgoCal.get(Calendar.MONTH)+1;
		int yyyymm = yyyy * 100 + mm;
		String yyyy_mmStr = addChar(String.valueOf(yyyymm), '-', 4);
		return yyyy_mmStr;
	}

	/**
	 * get date string in YYYY-MM format
	 * @param inDate
	 * @return
	 */
	public static String getYYYYDashMMString(Date inDate){
		return FastIsoYYYYDashMMFormat.format(inDate);
	}
}