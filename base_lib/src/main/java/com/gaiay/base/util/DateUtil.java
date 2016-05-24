package com.gaiay.base.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtil {
	/**
	 * 一分钟的毫秒数
	 */
	public final static long MINUTE_IN_MILLIS = 60 * 1000;
	/**
	 * 一小时的毫秒数
	 */
	public final static long HOUR_IN_MILLIS = 60 * MINUTE_IN_MILLIS;
	/**
	 * 一天的毫秒数
	 */
	public final static long DAY_IN_MILLIS = 24 * HOUR_IN_MILLIS;

	/**
	 * 将时间毫秒数转化为时分
	 *
	 * @param time 时间毫秒数
	 * @return 长度为2的int数组：0..时；1.分
	 */
	public static int[] parseMillis(long time) {
		int hour = (int) (time / DateUtil.HOUR_IN_MILLIS);
		int minute = (int) ((time - hour * DateUtil.HOUR_IN_MILLIS) / DateUtil.MINUTE_IN_MILLIS);
		return new int[]{hour, minute};
	}

	/**
	 * 对yyyy-MM-dd格式的日期,计算两个日期之间的天数
	 * 
	 * @param fromDay
	 *            开始日期
	 * @param toDay
	 *            结束日期
	 * @return 两个日期之间的天数
	 */
	public static int returnCalculateDays(String fromDay, String toDay) {
		if (StringUtil.isBlank(fromDay) || StringUtil.isBlank(toDay)) {
			return 0;
		}
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date date1 = sdf.parse(fromDay);
			Date date2 = sdf.parse(toDay);
			return (int) ((date2.getTime() - date1.getTime()) / 1000 / 60 / 60 / 24);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 对yyyy-MM-dd格式的日期,查看当前在此天的前（true）或者后（false）
	 * 
	 * @param fromDay
	 *            指定的日期
	 * @return true 在之前，反之在之后
	 */
	public static boolean isBeforeCurr(String fromDay) {
		if (StringUtil.isBlank(fromDay)) {
			return false;
		}
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date date1 = sdf.parse(fromDay);
			Calendar cal1 = Calendar.getInstance();
			return (cal1.getTimeInMillis() - date1.getTime()) > 0;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 对yyyy-MM-dd格式的日期,查看给定日期到现在的天数，如果在之后，为负数
	 * 
	 * @param fromDay
	 *            指定的日期
	 * @return 天数
	 */
	public static int getSelectDataToNow(String fromDay) {
		if (StringUtil.isBlank(fromDay)) {
			return 0;
		}
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date date1 = sdf.parse(fromDay);
			Date date2 = Calendar.getInstance().getTime();
			return (int) ((date2.getTime() - date1.getTime()) / 1000 / 60 / 60 / 24);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public static int getSelectDataToNow(int year, int month, int day) {
		int res;
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		month--;
		cal2.set(year, month, day);
		res = (int) ((cal2.getTimeInMillis() - cal1.getTimeInMillis()) / 1000 / 60 / 60 / 24);
		return res;
	}
	
	/**
	 * 根据毫秒值得到时间
	 */
	public static String parseTime(int time) {
		time = time/ 1000;
		int minute = time / 60;// 共多少 分
		int hour = minute / 60;// 时
		int second = time % 60;// 秒
		minute = minute % 60;// 分
		return String.format("%02d:%02d:%02d", hour, minute, second);
	}
	
	/**
	 * 把时间毫秒值转化成日期格式
	 */
	public static String format(long milliseconds){
		if (milliseconds <= 0) {
			return null;
		}
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return dateFormat.format(new Date(milliseconds));
	}
	/**
	 * 把时间毫秒值转化成日期格式
	 */
	public static String formatLongToDate(long milliseconds){
		if (milliseconds <= 0) {
			return null;
		}
		SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_yyyyMMddHHmm);
		return dateFormat.format(new Date(milliseconds));
	}
	//直播小列表的显示时间
	public static String formatLongToDates(long milliseconds){
		if (milliseconds <= 0) {
			return null;
		}
		SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_MMddHHmm);
		return dateFormat.format(new Date(milliseconds));
	}

	
	public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
	public static final String DATE_yyyyMMddHHmm = "yyyy-MM-dd HH:mm";
	public static final String DATE_MMddHHmm = "MM-dd HH:mm";
	public static final String DATE_FORMAT = "yyyy-MM-dd";
	public static final DateFormat SNS_DATE_FORMAT = new SimpleDateFormat("M月d日HH:mm");
	public static final DateFormat SNS_TODAY_FORMAT = new SimpleDateFormat("HH:mm");
	public static final DateFormat SNS_YESTERDAY_FORMAT = new SimpleDateFormat("昨天 HH:mm");
	public static final DateFormat SNS_BEFORE_YESTERDAY_FORMAT = new SimpleDateFormat("前天 HH:mm");
	public static final DateFormat SNS_THIS_YEAR_FORMAT = new SimpleDateFormat("MM-dd HH:mm");
	public static final DateFormat SNS_BEFORE_THIS_YEAR_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	public static final DateFormat SNS_SIMPLE_FORMAT = new SimpleDateFormat("yyMMdd");
	public static final DateFormat DATE_FORMAT_YYYYMM = new SimpleDateFormat("yyMM");
	
	
	
	public static DateFormat DATEFORMAT = new SimpleDateFormat("yyyy-MM-dd");

	/**
	 * 在当前时间基础上加分钟
	 * 
	 * @param date 基础时间
	 * @param days 所要加的天数
	 * @param hour 所要加的小时数
	 * @param minute 所要加的分钟数
	 */
	public static Date addTime(Date date, int days, int hour, int minute) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DATE, days);
		calendar.add(Calendar.HOUR, hour);
		calendar.add(Calendar.MINUTE, minute);
		return calendar.getTime();
	}

	public static String addMonth(Long date, int months) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date(date));
		calendar.add(Calendar.MONTH, months);
		return dateToString(calendar.getTime().getTime(), "yyyy年MM月dd日");
	}

	/**
	 * 获得某一天的最小间
	 * 
	 * @param date 时间
	 * @return 当天最时间
	 */
	public static Date getMinOfDate(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}

	/**
	 * 获得某一天的最大时间
	 * 
	 * @param date 时间
	 * @return 当天最时间
	 */
	public static Date getMaxOfDate(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		calendar.set(Calendar.MILLISECOND, 999);
		return calendar.getTime();
	}

	/**
	 * @param date 日期或时间
	 * @param pattern 格式
	 */
	public static Date stringToDate(String date, String pattern) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, Locale.CHINESE);
		Date d = null;
		try {
			d = simpleDateFormat.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return d;
	}

	public static Date LongToDate(Long date, String pattern) {
		Date date1 = new Date();
		if (date != null && date != 0) {
			date1.setTime(date);
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("", Locale.CHINESE);
			simpleDateFormat.applyPattern(pattern);
		}
		return date1;
	}

	/**
	 * 日期转换成毫秒
	 * 
	 * @param date 日期或时间
	 * @param pattern 格式
	 */
	public static Long stringToLong(String date, String pattern) {

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, Locale.CHINESE);
		Date date1 = null;
		try {
			if (StringUtil.isNotBlank(date)) {
				date1 = simpleDateFormat.parse(date);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return (date1 == null ? 0L : date1.getTime());
	}

	/**
	 * 获取当前时间并转换成毫秒数
	 */
	public static Long sysTimeToLong() {
		Date date = new Date();
		return date.getTime();
	}

	public static String dateToString(Long date, String pattern) {
		Date date1 = new Date();
		String timeStr = "";
		if (date != null && date != 0) {
			date1.setTime(date);
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("", Locale.CHINESE);
			simpleDateFormat.applyPattern(pattern);
			timeStr = simpleDateFormat.format(date1);
		}
		return timeStr;
	}

	/**
	 * 计算倒计时
	 * 
	 * @param strDate 计算日期
	 * @param pattern 日期格式 (比如 yyyy/MM/dd)
	 * @return 倒计时天数
	 */
	public static Long countDown(String strDate, String pattern) {
		Long longDate = stringToLong(strDate, pattern);
		Long longNow = stringToLong(dateToString(sysTimeToLong(), pattern), pattern);
		Long countDown = (longDate - longNow) / 1000 / 3600 / 24;
		return countDown;
	}

	/**
	 * 获取“今天”零点的毫秒数
	 */
	public static Long getBeginOfToday() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTimeInMillis();
	}

	/**
	 * 获得某天的开始时间
	 */
	public static Date getBeginOfDate(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}
	/**
     * 获得某天的开始时间
     */
    public static Date getBeginOfYear(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_YEAR, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

	/**
	 * 返回两者之间的分钟差
	 */
	public static int subMinute(Date date1, Date date2) {
		Long subTime = date1.getTime() - date2.getTime();
		Long subMinute = subTime / 60000;
		return subMinute.intValue();
	}

	public static boolean isMonthFirst() {
		Calendar localTime = Calendar.getInstance();
		int x = localTime.get(Calendar.DAY_OF_MONTH);
		return x == 1;
	}

	/**
	 * 取本月最后一天
	 */
	public static int getLastDayOfMonth(Long date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(LongToDate(date, "yyyy-MM-dd"));
		// 某年某月的最后一天
		return cal.getActualMaximum(Calendar.DATE);
	}

	/***
	 * 获取当前日期偏移后的时间的毫秒数
	 */
	public static long getCurrDateOffsetTime(int day) {
		Calendar cal = Calendar.getInstance();// 使用默认时区和语言环境获得一个日历。
		cal.add(Calendar.DAY_OF_MONTH, day);
		return cal.getTimeInMillis();
	}

	/**
	 * 社交格式日期为yyMMdd的形式
	 */
	public static String formatSimpleDate(Date date) {
		return SNS_SIMPLE_FORMAT.format(date);
	}

	/**
	 * 格式化日期类型
	 */
	public static String formatDate(Date date, String format) {
		if (date == null)
			return null;
		SimpleDateFormat f = new SimpleDateFormat(format);
		return f.format(date);
	}

	/**
	 * 格式化字符串 formatDate
	 * 
	 * @param date 日期
	 * @return 返回格式化后的日期字符串
	 */
	public static String formatDateTime(Date date) {
		DateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if (date == null) {
			return null;
		} else {
			return DATE_TIME_FORMAT.format(date);
		}
	}

	/**
	 * 抽取社区计算时间的方法
	 */
	public static String formatDateToHours(Date date, String format) {
		if (date != null) {
			Date crrDate = new Date();
			if (date.after(DateUtil.addTime(crrDate, 0, 0, -1))) {
				return "刚刚";
			} else if (date.after(DateUtil.addTime(crrDate, 0, -1, 0))) {
				return DateUtil.subMinute(crrDate, date) + "分钟前";
			} else if (date.after(DateUtil.getBeginOfDate(crrDate))) {
				return DateUtil.SNS_TODAY_FORMAT.format(date);
			} else {
				if (StringUtil.isNotBlank(format)) {
					return formatDate(date, format);
				} else {
					return DateUtil.SNS_DATE_FORMAT.format(date);
				}
			}
		}
		return null;
	}

	/**
	 * 抽取社区计算时间的方法
	 */
	public static String formatDateToHours(Long time, String format) {
		if (null == time) {
			return "";
		}
		Date date = new Date(time);
		Date crrDate = new Date();
		if (date.after(DateUtil.addTime(crrDate, 0, 0, -1))) {
			return "刚刚";
		} else if (date.after(DateUtil.addTime(crrDate, 0, -1, 0))) {
			return DateUtil.subMinute(crrDate, date) + "分钟前";
		} else if (date.after(DateUtil.getBeginOfDate(crrDate))) {
			return DateUtil.SNS_TODAY_FORMAT.format(date);
		} else {
			if (StringUtil.isNotBlank(format)) {
				return formatDate(date, format);
			} else {
				return DateUtil.SNS_DATE_FORMAT.format(date);
			}
		}
	}
	
	/**
	 * 格式化yyyy-MM-dd的日期
	 */
	public static Date parseDate(String date) {
		try {
			return DATEFORMAT.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 计算两个日期相差多少天
	 * 
	 * @param begin
	 *            起始日期
	 * @param end
	 *            结束日期
	 * @return 返回相差的天数
	 */
	public static int countDays(String begin, String end) {
		int days = 0;
		Calendar c_b = Calendar.getInstance();
		Calendar c_e = Calendar.getInstance();
		c_b.setTime(parseDate(begin));
		c_e.setTime(parseDate(end));

		if (c_b.after(c_e)) {
			return -1;
		} else {
			while (c_b.before(c_e)) {
				days++;
				c_b.add(Calendar.DAY_OF_YEAR, 1);
			}
		}
		return days;
	}
	
	
	public static  String parseDate(Date time) {
		if (time != null) {
			Date date = new Date();
			if (time.after(DateUtil.addTime(date, 0, 0, -1))) {
				return "刚刚";
			} else if (time.after(DateUtil.addTime(date, 0, -1, 0))){
				return DateUtil.subMinute(date, time) + "分钟前";
			} else if (time.after(DateUtil.getBeginOfDate(date))) {
				return DateUtil.SNS_TODAY_FORMAT.format(time);
			} else {
				return DateUtil.SNS_DATE_FORMAT.format(time);
			}
		}
		return "";
	}

	/**
	 * 返回带有星期的 String  如：1988-01-12  星期五  09:00
	 */
	public static String formatDateToWeek(Date time) {
		String ss;
		DateFormat df = new java.text.SimpleDateFormat("yyyy-M-dd ", Locale.CHINA);
		DateFormat df2 = new java.text.SimpleDateFormat("HH:mm", Locale.CHINA);
		String[] weekDaysName = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五","星期六" };
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(time);
		int intWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
		ss = df.format(time) + weekDaysName[intWeek] + " " +df2.format(time);
		return ss;
	}
	
   /* 获取现在时间
   * 
   * @return返回字符串格式  yyyy年MM月dd日 HH时mm分
   */
	public static String getStringDate(Long time) {
	   Date date = new Date(time);
	   SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日   HH时mm分");
		return formatter.format(date);
	}
	/* 获取现在时间
	 * 
	 * @return返回字符串格式  yyyy-MM-dd   HH:mm
	 */
	public static String getStringDate1(Long time) {
		Date date = new Date(time);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd   HH:mm");
		return formatter.format(date);
	}
	/* 获取现在时间
	 * 
	 * @return返回字符串格式  yyyy-MM-dd
	 */
	public static String getStringDate3(Long time) {
		Date date = new Date(time);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		return formatter.format(date);
	}
	
	/**
	 * 获取免打扰的 状态
	 * @return true 代表在规定时间内
	 */
	public static boolean getNotifictionState(){
		Calendar cal = Calendar.getInstance();// 当前日期
		int hour = cal.get(Calendar.HOUR_OF_DAY);// 获取小时
		int minute = cal.get(Calendar.MINUTE);// 获取分钟
		int minuteOfDay = hour * 60 + minute;// 从0:00分开是到目前为止的分钟数
		
		final int start = 22 * 60 ;// 起始时间 22:00的分钟数
		final int end = 8 * 60;// 结束时间 8:00的分钟数
		if ((minuteOfDay >= start && minuteOfDay <= 23*60+59)   ||(minuteOfDay >= 0 && minuteOfDay <= end) ) {
		    return false;
		} else {
		    return true;
		}
	}
	
}
