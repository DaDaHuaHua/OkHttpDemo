package com.gaiay.base.common;

/**
 * 用于存放公用标识码,如连网失败标识码
 * 
 * @author iMuto
 */
public class CommonCode {
	private static final int BASE_INT = 0XFF223E;
	/**
	 * 连接超时
	 */
	public static final int ERROR_TIME_OUT = 0 + BASE_INT;
	/**
	 * 连接超时信息
	 */
	public static final String ERROR_TIME_OUT_MSG = "网络连接超时";
	/**
	 * 参数错误
	 */
	public static final int ERROR_PARAMETER = 1 + BASE_INT;
	/**
	 * 参数错误信息
	 */
	public static final String ERROR_PARAMETER_MSG = "参数错误";
	/**
	 * SDCard空间已满
	 */
	public static final int ERROR_HARD_FULL = 2 + BASE_INT;
	/**
	 * SDCard空间已满信息
	 */
	public static final String ERROR_HARD_FULL_MSG = "SDCard空间已满";
	/**
	 * 保存文件失败
	 */
	public static final int ERROR_SAVE_FAILD = 3 + BASE_INT;
	/**
	 * 保存文件失败信息
	 */
	public static final String ERROR_SAVE_FAILD_MSG = "保存文件失败";
	/**
	 * 无可用网络
	 */
	public static final int ERROR_NO_NETWORK = 4 + BASE_INT;
	/**
	 * 无可用网络信息
	 */
	public static final String ERROR_NO_NETWORK_MSG = "无可用网络,请检查网络后重试.";
	/**
	 * 其他错误
	 */
	public static final int ERROR_OTHER = 5 + BASE_INT;
	/**
	 * 其他错误信息
	 */
	public static final String ERROR_OTHER_MSG = "发生了未知错误";
	/**
	 * 成功标识
	 */
	public static final int SUCCESS = 6 + BASE_INT;
	/**
	 * 成功标识信息
	 */
	public static final String SUCCESS_MSG = "成功";
	/**
	 * 图片下载成功
	 */
	public static final int SUCCESS_BITMAP = 7 + BASE_INT;
	/**
	 * 图片下载失败
	 */
	public static final int ERROR_BITMAP_FAILD = 8 + BASE_INT;
	/**
	 * 图片下载进度更新失败
	 */
	public static final int ERROR_BITMAP_PROGRESS_UPDATE = 9 + BASE_INT;
	/**
	 * 图片处理消息
	 */
	public static final int BITMAP_HOLDE_MSG = 10 + BASE_INT;
	/**
	 * ARequest参数错误
	 */
	public static final int ERROR_AREQUEST = 11 + BASE_INT;
	/**
	 * URL参数错误
	 */
	public static final int ERROR_URL = 12 + BASE_INT;
	/**
	 * URL参数错误
	 */
	public static final int ERROR_PARSE_DATA = 13 + BASE_INT;
	/**
	 * 连接失败
	 */
	public static final int ERROR_LINK_FAILD = 14 + BASE_INT;
	/**
	 * 线程停止
	 */
	public static final int ERROR_TASK_CANCEL = 15 + BASE_INT;
}
