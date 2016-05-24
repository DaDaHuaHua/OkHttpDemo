package com.gaiay.base.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.text.InputFilter;
import android.text.Spanned;

/**
 * 文字过滤器
 * @author iMuto
 */
public class TextFilter implements InputFilter{

	/**
	 * 指定Fileter过滤后保留的类型
	 */
	public enum FM {
		/**
		 * 中文
		 */
		CN,
		/**
		 * 英文
		 */
		EN,
		/**
		 * 数字
		 */
		NUM,
		/**
		 * 构成单词的内容a-zA-Z_0-9
		 */
		WORD,
		/**
		 * 标点包括：。，：！‘’“”？；.,:!'"?;
		 */
		PUNC,
		/**
		 * 所有的
		 */
		ALL
	}
	
	/**
	 * 监听输入框内容长度及匹配度
	 * @author imuto
	 */
	public interface TextInvalidateListener{
		/**
		 * 当输入长度大于设定长度时调用
		 */
		void onLenInvalidate();
		/**
		 * 当输入字符与字符规则不匹配时调用
		 */
		void onConInvalidate();
	}
	/**
	 * 监听输入框内容长度改变
	 * @author imuto
	 */
	public interface OnLengthChangeListener{
		/**
		 * 当文本框内容长度改变时触发
		 * len:输入框的文本长度，res输入框中的内容；
		 */
		void onLenChange(int len, String res);
	}
	
	Pattern p;
	TextInvalidateListener mListener;
	OnLengthChangeListener lenListener;
	
	int mMax;
	String reg;
	
	public void setOnLengthChangeListener(OnLengthChangeListener len) {
		lenListener = len;
	}
	
	/**
	 * 用法：
	 * EditText.setFilters(new InputFilter[]{new GaiayFilter(this, 10, new FM[]{FM.CN, FM.NUM, FM.EN, FM.PUNC}, "abcd")});
	 * @param cxt 上下文变量
	 * @param max 输入框最大文本长度
	 * @param fms 输入模式，具体参见GaiayFilter.FM枚举
	 * @param specil 希望通过过滤的字符
	 */
	public TextFilter(int max, FM[] fms, TextInvalidateListener listener) {
		this(max, fms, null, listener);
	}
	/**
	 * 用法：
	 * EditText.setFilters(new InputFilter[]{new GaiayFilter(this, 10, new FM[]{FM.CN, FM.NUM, FM.EN, FM.PUNC}, "abcd")});
	 * @param cxt 上下文变量
	 * @param max 输入框最大文本长度
	 * @param fms 输入模式，具体参见GaiayFilter.FM枚举
	 * @param specil 希望通过过滤的字符
	 */
	public TextFilter(int max, FM[] fms, String specil, TextInvalidateListener listener) {
		mMax = max;
		mListener = listener;
		
		if (fms != null) {
			StringBuffer sb = new StringBuffer();
			sb.append("[");
			for (int i = 0; i < fms.length; i++) {
				if (fms[i].equals(FM.ALL)) {
					reg = ".";
					break;
				}
				switch (fms[i]) {
				case CN:
					sb.append("\u4e00-\u9fa5");
					break;
				case EN:
					sb.append("a-zA-Z");
					break;
				case NUM:
					sb.append("0-9");
					break;
				case WORD:
					sb.append("\\w");
					break;
				case PUNC:
					sb.append("。，：！‘’、“”？；.,:!'\"?;……—_");
					break;
				}
				if (i == (fms.length - 1)) {
					if (specil != null) {
						sb.append(specil);
					}
					sb.append("]");
					reg = sb.toString();
				}
			}
		} else {
			if (specil != null) {
				reg = "[" + specil + "]";
			} else {
				reg = ".";
			}
		}
		p = Pattern.compile(reg);
	}
	
	/**
	 * source为你要输入到文本框的内容，start为source的开始，一般为0，end为source的结束，一般为source的长度。
	 * dest为文本框原有的内容，dstart为source要输入的位置的开始，一般为dest的长度，dend为source要输入的位置结束，一般为dest的长度
	 */
	@Override
	public CharSequence filter(CharSequence source, int start, int end,
			Spanned dest, int dstart, int dend) {
//		Log.e("start:" + start + " end: " + end + " dstart:" + dstart + " dend:" + dend);
//		Log.e("source:" + source);
//		Log.e("dest:" + dest);
//		Log.e("dest.length:" + dest.length());
//		Log.e("source.length:" + source.length());
		StringBuffer sb = new StringBuffer();
		for (int i = start; i < end; i++) {
			String str = (String) source.subSequence(i, i + 1).toString();
			if (isMatch(str)) {
				sb.append(str);
			}
		}
		
		int keep = mMax - (dest.length() - (dend - dstart));
		
        if (keep <= 0) {
        	telListener(false);
        	onLenChange(dest.length(), dest.toString());
            return "";
        } else if (keep >= end - start) {
        	String res = dest.toString();
        	String s1;
        	String s2;
			s1 = res.substring(0, dstart);
			s2 = res.substring(dend, res.length());
        	res = s1 + source + s2;
//        	Log.e(res);
        	onLenChange(dest.length() + source.length() - (dend - dstart), res);
            return sb;
        } else {
        	telListener(false);
        	String res = dest.toString();
        	String s1;
        	String s2;
			s1 = res.substring(0, dstart);
			s2 = res.substring(dend, res.length());
        	res = s1 + sb.subSequence(start, start + keep) + s2;
//        	Log.e(res);
        	onLenChange(dest.length() + start + keep - (dend - dstart), res);
            return sb.subSequence(start, start + keep);
        }
	}

	private boolean isMatch(String str) {
		Matcher m = p.matcher(str);
		if (m.matches()) {
			return true;
		}
		telListener(true);
		return false;
	}
	
	private void onLenChange(int len, String res) {
		if (lenListener != null) {
			lenListener.onLenChange(len, res);
		}
	}
	
	private void telListener(boolean isCon) {
		if (mListener != null) {
			if (isCon) {
				mListener.onConInvalidate();
			} else {
				mListener.onLenInvalidate();
			}
		}
	}
	
}
