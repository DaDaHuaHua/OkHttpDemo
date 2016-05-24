package com.gaiay.base;

import android.os.Environment;

public class BaseConstants {

	/**
	 * 该亚的文件夹名称
	 */
	private static String path_name_folder_root = "gaiay";
	/**
	 * 项目的文件夹名称
	 */
	private static String path_name_folder_project = "all";

	/**
	 * 项目在SDcard上的绝对路径
	 */
	public static String path_sd = Environment.getExternalStorageDirectory().getAbsolutePath() + "/"
			+ path_name_folder_root + "/" + path_name_folder_project + "/";

	public static String path_cache = path_sd + "cache/";

	public static String USER_AGENT;

	/** 微信AppKey */
	public static String WEIXIN_APP_KEY;
	/** 微信AppSecret */
	public static String WEIXIN_APP_SECRET;
	/** QQ AppKey */
	public static String QQ_APP_ID;
	/** 新浪AppKey */
	public static String SINA_APP_KEY;
	/** im AppKey */
	public static String IM_APP_KEY;

}
