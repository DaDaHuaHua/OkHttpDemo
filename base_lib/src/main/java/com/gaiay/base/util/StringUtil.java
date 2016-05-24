package com.gaiay.base.util;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

	private final static ThreadLocal<SimpleDateFormat> dateFormater = new ThreadLocal<SimpleDateFormat>() {
		@Override
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		}
	};

	private final static ThreadLocal<SimpleDateFormat> dateFormater2 = new ThreadLocal<SimpleDateFormat>() {
		@Override
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat("yyyy-MM-dd");
		}
	};

	/**
	 * 将字符串转位日期类型
	 * 
	 * @param sdate
	 * @return
	 */
	public static Date toDate(String sdate) {
		try {
			return dateFormater.get().parse(sdate);
		} catch (ParseException e) {
			return null;
		}
	}

	/**
	 * 以友好的方式显示时间
	 * 
	 * @param sdate
	 * @return
	 */
	public static String friendly_time(String sdate) {
		Date time = toDate(sdate);
		if (time == null) {
			return "Unknown";
		}
		String ftime = "";
		Calendar cal = Calendar.getInstance();

		// 判断是否是同一天
		String curDate = dateFormater2.get().format(cal.getTime());
		String paramDate = dateFormater2.get().format(time);
		if (curDate.equals(paramDate)) {
			int hour = (int) ((cal.getTimeInMillis() - time.getTime()) / 3600000);
			if (hour == 0)
				ftime = Math.max((cal.getTimeInMillis() - time.getTime()) / 60000, 1) + "分钟前";
			else
				ftime = hour + "小时前";
			return ftime;
		}

		long lt = time.getTime() / 86400000;
		long ct = cal.getTimeInMillis() / 86400000;
		int days = (int) (ct - lt);
		if (days == 0) {
			int hour = (int) ((cal.getTimeInMillis() - time.getTime()) / 3600000);
			if (hour == 0)
				ftime = Math.max((cal.getTimeInMillis() - time.getTime()) / 60000, 1) + "分钟前";
			else
				ftime = hour + "小时前";
		} else if (days == 1) {
			ftime = "昨天";
		} else if (days == 2) {
			ftime = "前天";
		} else if (days > 2 && days <= 10) {
			ftime = days + "天前";
		} else if (days > 10) {
			ftime = dateFormater2.get().format(time);
		}
		return ftime;
	}

	/**
	 * 判断是否为空串（会对字符串进行trim()）
	 * 
	 * @param str
	 *            需要判断的 字符串
	 * @return true为""或者null
	 */
	public static boolean isBlank(String str) {
		return str == null || "".equals(str.trim());
	}

	/**
	 * 判断是否为空串（会对字符串进行trim()）
	 * 
	 * @param str
	 *            需要判断的 字符串
	 * @return true不为空；false为""或者null
	 */
	public static boolean isNotBlank(String str) {
		return str != null && !"".equals(str.trim());
	}

	/**
	 * 判断是否为空串（不会对字符串进行trim()）
	 * 
	 * @param str
	 *            需要判断的 字符串
	 * @return true为""或者null
	 */
	public static boolean isEmpty(String str) {
		return str == null || "".equals(str);
	}

	/**
	 * 判断是否为空串（不会对字符串进行trim()）
	 * 
	 * @param str
	 *            需要判断的 字符串
	 * @return true不为空；false为""或者null
	 */
	public static boolean isNotEmpty(String str) {
		return str != null && !"".equals(str);
	}

	public static boolean startWith(String str, String prefix) {
		return (str == null || prefix == null) ? false : str.startsWith(prefix);
	}

	public static boolean isHttp(String url) {
		return startWith(url, "http");
	}

	/**
	 * 获取文件/路径的后缀名
	 */
	public static String getSuffix(String str) {
		if (isBlank(str)) {
			return null;
		}
		return str.substring(str.lastIndexOf("."), str.length() - 1);
	}

	/**
	 * 判断一个String是否为数字
	 * 
	 * @param str
	 *            要判断的String串
	 * @return 是数字返回true，否则返回false;
	 */
	public static boolean isNum(String str) {
		if (isBlank(str)) {
			return false;
		}
		try {
			Pattern p = Pattern.compile("[0-9]+(\\.[0-9]+)?");
			Matcher m = p.matcher(str);
			if (m.matches()) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 判断一个String是否为int数据
	 * 
	 * @param str
	 *            要判断的String串
	 * @return 是数字返回true，否则返回false;
	 */
	public static boolean isInteger(String str) {
		if (isBlank(str)) {
			return false;
		}
		try {
			Integer.parseInt(str);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 返回一个小数点后p位的string
	 */
	public static String getNum(double d, int p) {
		if (p < 0) {
			p = 0;
		}
		return String.format("%." + p + "f", d);
	}

	/**
	 * 判断一个String是否为小数
	 * 
	 * @param str
	 *            要判断的String串
	 * @return 是小数返回true，否则返回false;
	 */
	public static boolean isDecimal(String str) {
		if (isBlank(str)) {
			return false;
		}
		try {
			Pattern p = Pattern.compile("[0-9]+\\.{1}[0-9]+");
			Matcher m = p.matcher(str);
			if (m.matches()) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 去除字符串中的空格、回车、换行符、制表符
	 * 
	 * @param src
	 *            String
	 * @return
	 */
	public static String getStringWithoutEnter(String str) {
		String dest = "";
		if (str != null) {
			Pattern p = Pattern.compile("\\s*|\t|\r|\n");
			Matcher m = p.matcher(str);
			dest = m.replaceAll("");
		}
		return dest;
	}

	/**
	 * 获取指定长度的随机数
	 * 
	 * @param length
	 *            要获取的随机数长度
	 * @return 获取的结果
	 */
	public static String getRandom(int length) {
		int tmp = 1;
		if (length <= 0) {
			return "";
		}
		for (int i = 0; i < length; i++) {
			tmp = tmp * 10;
		}
		int temp = (int) (tmp * Math.random());
		while (temp == 0 || temp == 100) {
			temp = (int) (tmp * Math.random());
		}
		return temp + "";
	}

	/**
	 * 将数字保留两位小数
	 * 
	 * @param number
	 * @return
	 */
	public static String parseNumber(double number) {
		DecimalFormat df = new DecimalFormat("#0.00");
		return df.format(number);
	}

	/**
	 * 将数组用","拼接起来
	 */
	public static String join(Object[] arr) {
		return join(arr, ",");
	}

	/**
	 * 将数组转换成字符串，并将在中间插入分隔符
	 * 
	 * @param arr
	 *            数组
	 * @param separator
	 *            分隔符
	 * @return
	 */
	public static String join(Object[] arr, String separator) {
		if (arr != null && isNotBlank(separator)) {
			StringBuilder sb = new StringBuilder();
            for (Object obj : arr) {
                if (obj != null) {
                    sb.append(separator);
                    sb.append(obj);
                }
            }
            if (sb.length() > 0) {
                return sb.substring(separator.length());
            }
        }
        return "";
    }

    public static String join(List<?> list, String separator) {
        if (ListUtil.isNotEmpty(list) && isNotBlank(separator)) {
            StringBuilder sb = new StringBuilder();
            for (Object obj : list) {
                if (obj != null) {
                    sb.append(separator);
                    sb.append(obj);
                }
			}
			if (sb.length() > 0) {
				return sb.substring(separator.length());
			}
		}
		return "";
	}

	public static String ToDBC(String input) {
		char[] c = input.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] == 12288) {
				c[i] = (char) 32;
				continue;
			}
			if (c[i] > 65280 && c[i] < 65375)
				c[i] = (char) (c[i] - 65248);
		}
		return new String(c);
	}

	/**
	 * 1.str1 == null || str2 == null，retrun false<br>
	 * 2.str1 == str2，return true<br>
	 * else.return str1.equals(str2)
	 */
	public static boolean equals(String str1, String str2) {
		if (str1 == null || str2 == null) {
			return false;
		}
		if (str1 == str2) {
			return true;
		}
		return str1.equals(str2);
	}

	/**
	 * 统计字符串长度，可以统计中文 中文为2个字符，英文为1个字符
	 * 
	 * @param str
	 * @return
	 */
	public static int countUTF8StringLength(String str) {
		int m = 0;
		char arr[] = str.toCharArray();
		for (int i = 0; i < arr.length; i++) {
			char c = arr[i];
			if ((c >= 0x0391 && c <= 0xFFE5)) // 中文字符
			{
				m = m + 2;
			} else if ((c >= 0x0000 && c <= 0x00FF)) // 英文字符
			{
				m = m + 1;
			}
		}
		return m;
	}

	/**
	 * 判断str是否为空，为空则返回""；否则返回str
	 */
	public static String getString(String str) {
		return getString(str, null);
	}

	/**
	 * 判断str是否为空，为空则返回def；否则返回str
	 */
	public static String getString(String str, String def) {
		if (isNotBlank(str)) {
			return str;
		}
		if (isBlank(def)) {
			def = "";
		}
		return def;
	}

	/**
	 * 截取" 字节"的方法,注意 不是字符截取
	 * 
	 * @param str
	 * @param pstart
	 * @param pend
	 * @return
	 */
	public static String getSubString(String str, int pstart, int pend) {
		String resu = "";
		int beg = 0;
		int end = 0;
		int count1 = 0;
		char[] temp = new char[str.length()];
		str.getChars(0, str.length(), temp, 0);
		boolean[] bol = new boolean[str.length()];
		for (int i = 0; i < temp.length; i++) {
			bol[i] = false;
			if ((int) temp[i] > 255) {// 说明是中文
				count1++;
				bol[i] = true;
			}
		}
		if (pstart > str.length() + count1) {
			resu = null;
		}
		if (pstart > pend) {
			resu = null;
		}
		if (pstart < 1) {
			beg = 0;
		} else {
			beg = pstart - 1;
		}
		if (pend > str.length() + count1) {
			end = str.length() + count1;
		} else {
			end = pend;// 在substring的末尾一样
		}
		// 下面开始求应该返回的字符串
		if (resu != null) {
			if (beg == end) {
				int count = 0;
				if (beg == 0) {
					if (bol[0] == true)
						resu = null;
					else
						resu = new String(temp, 0, 1);
				} else {
					int len = beg;// zheli
					for (int y = 0; y < len; y++) {// 表示他前面是否有中文,不管自己
						if (bol[y] == true)
							count++;
						len--;// 想明白为什么len--
					}
					// for循环运行完毕后，len的值就代表在正常字符串中，目标beg的上一字符的索引值
					if (count == 0) {// 说明前面没有中文
						if ((int) temp[beg] > 255) // 说明自己是中文
							resu = null;// 返回空
						else
							resu = new String(temp, beg, 1);
					} else {// 前面有中文，那么一个中文应与2个字符相对
						if ((int) temp[len + 1] > 255) // 说明自己是中文
							resu = null;// 返回空
						else
							resu = new String(temp, len + 1, 1);
					}
				}
			} else {// 下面是正常情况下的比较
				int temSt = beg;
				int temEd = end - 1;// 这里减掉一
				for (int i = 0; i < temSt; i++) {
					if (bol[i] == true)
						temSt--;
				} // 循环完毕后temSt表示前字符的正常索引
				for (int j = 0; j < temEd; j++) {
					if (bol[j] == true)
						temEd--;
				} // 循环完毕后temEd-1表示最后字符的正常索引
				if (bol[temSt] == true) // 说明是字符，说明索引本身是汉字的后半部分，那么应该是不能取的
				{
					int cont = 0;
					for (int i = 0; i <= temSt; i++) {
						cont++;
						if (bol[i] == true)
							cont++;
					}
					if (pstart == cont) // 是偶数不应包含,如果pstart<cont则要包含
						temSt++;// 从下一位开始
				}
				if (bol[temEd] == true) {// 因为temEd表示substring
											// 的最面参数，此处是一个汉字，下面要确定是否应该含这个汉字
					int cont = 0;
					for (int i = 0; i <= temEd; i++) {
						cont++;
						if (bol[i] == true)
							cont++;
					}
					if (pend < cont) // 是汉字的前半部分不应包含
						temEd--;// 所以只取到前一个
				}
				if (temSt == temEd) {
					resu = new String(temp, temSt, 1);
				} else if (temSt > temEd) {
					resu = null;
				} else {
					resu = str.substring(temSt, temEd + 1);
				}
			}
		}
		return resu;// 返回结果
	}

	/**
	 * 去除特殊字符，首行缩进 ："\\s*|\t|\r|\n" 有问题
	 * 
	 * @param str
	 * @return
	 */
	public static String replaceBlank(String str) {
		String dest = "";
		if (str != null) {
			Pattern p = Pattern.compile("\\s*|\t|\r|\n");
			Matcher m = p.matcher(str);
			dest = m.replaceAll("");
		}
		return dest;
	}

	/**
	 * 将两个字符串拼成一个字符串（会将null换成""）
	 */
	public static String append(String str1, String str2) {
		return (str1 == null ? "" : str1) + (str2 == null ? "" : str2);
	}

	/**
	 * 
	 * @param str
	 * @param maxLength
	 *            要显示的字的个数
	 * @return
	 */
	public static String ellipsizeEnd(String str, int maxLength) {
		if (isBlank(str)) {
			return str;
		}
		if (str.length() <= maxLength) {
			return str;
		} else {
			return str.substring(0, maxLength) + "...";
		}
	}

	/**
	 * 讲大于等于0的double转成带有两位小数的string
	 */
	public static String doubleToString(double d) {
		String s = "0.00";
		if (d >= 0) {
			DecimalFormat df = new DecimalFormat("######0.00");
			s = df.format(d);
		}
		return s;
	}
}
