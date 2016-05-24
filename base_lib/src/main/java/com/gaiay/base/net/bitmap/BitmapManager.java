package com.gaiay.base.net.bitmap;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;

import com.gaiay.base.common.CommonCode;
import com.gaiay.base.util.BitmapUtil;
import com.gaiay.base.util.Log;
import com.gaiay.base.util.StringUtil;

/**
 * Bitmap的管理类,理想情况应为所有的连网获取图片全部使用本类的getBitmap方法获取
 * <br>此类维护系统中最大5M的图片内存占用,此方法可按照组Id进行图片回收</br>
 * <br>当退回一个Activity时将回收此Activity的图片资源</br>
 * <br>此类缓存所有经由此类生成的bitmap到本地,以便更有效的重用资源</br>
 * @author iMuto
 */
public class BitmapManager {
	
	private static final String TAG = "Gaiay_marriage_BitmapManager";
	
	private static final int TOTLE_SIZE = 5 * 1024 * 1024;
	private static int currSize = 0;
	private static final LinkedList<BitmapModel> bitmaps = new LinkedList<BitmapModel>();
	private static final List<String> bmpList = new ArrayList<String>();
	@SuppressWarnings("unused")
	private static final Map<String, BitmapAsynTask> task = new HashMap<String, BitmapAsynTask>();
	public static String cacheFolder;
	
	static {
		isFirst = true;
	}
	
	private BitmapManager() {}
	
	/**
	 * 获取bitmap,<br>图片处理完成后使用handler参数发送默认what为指定what值的消息,
	 * 此消息的arg1为{@link com.gaiay.base.common.CommonCode#BITMAP_FAILD}(失败)<br>
	 * 或者{@link com.gaiay.base.common.CommonCode#BITMAP_SUCCESS}(成功).
	 * <br>当成功的时候arg2为输入参数id,用于标识图片.obj为图片的bitmap
	 * <br>当失败的时候arg2为0,obj为提示字符串
	 * @param url 图片的url地址
	 * @param cxt 上下文菜单
	 * @param handler 装载完成后接收更新消息的Handler
	 * @param what 指定handler处理图片消息的what标识
	 * @param setId 图片加载完成后的组Id,请不要让此参数为空,否者可能在回收图片的时候产生错误的回收结果
	 * @param id 图片的id,如list中的position.用于标识图片
	 */
	public static BitmapAsynTask getBitmap(String url, Context cxt, Handler handler, int what, String setId, int id, String cacheFolder) {
		Log.e("id:" + id);
		if (handler == null) {
			return null;
		}
		if (StringUtil.isBlank(cacheFolder)) {
			Log.e("设置的默认图片缓存路径（cacheFolder）为空！！");
			return null;
		}
		if (StringUtil.isBlank(url) || cxt == null) {
			Log.e("图片url错误或上下文参数错误");
			handler.sendMessage(handler.obtainMessage(what, CommonCode.ERROR_BITMAP_FAILD, 0, "图片url错误或上下文参数错误"));
			return null;
		}
		BitmapManager.cacheFolder = cacheFolder;
		initFirstBitmapList();
		Bitmap bmp = isInLoc(url, setId);
		if (bmp != null) {
			handler.sendMessage(handler.obtainMessage(what, CommonCode.SUCCESS_BITMAP, id, bmp));
			return null;
		}
		Log.e(TAG, "FROM INTENET");
		BitmapAsynTask t = new BitmapAsynTask(url, cxt, handler, what, setId, id, cacheFolder);
		checkTask();
//		task.put(url, t);
		t.execute();
		return t;
	}
	public static BitmapAsynTask getBitmap(Context cxt, Handler handler, String setId, String cacheFolder, BitmapReqModel model) {
		if (handler == null || model == null) {
			return null;
		}
		if (StringUtil.isBlank(cacheFolder)) {
			Log.e("设置的默认图片缓存路径（cacheFolder）为空！！");
			return null;
		}
		if (StringUtil.isBlank(model.url) || cxt == null) {
			Log.e("图片url错误或上下文参数错误");
			model.resultCode = CommonCode.ERROR_BITMAP_FAILD;
			model.msg = "图片url错误或上下文参数错误";
			handler.sendMessage(handler.obtainMessage(model.what, model));
			return null;
		}
		BitmapManager.cacheFolder = cacheFolder;
		initFirstBitmapList();
		Bitmap bmp = isInLoc(model.url, setId);
		if (bmp != null) {
			model.resultCode = CommonCode.SUCCESS_BITMAP;
			model.bmp = bmp;
			handler.sendMessage(handler.obtainMessage(model.what, model));
			return null;
		}
		Log.e(TAG, "FROM INTENET");
		BitmapAsynTask t = new BitmapAsynTask(cxt, handler, setId, cacheFolder, model);
		checkTask();
		t.execute();
		return t;
	}
	
	/**
	 * 获取bitmap,
	 * <br>图片处理完成后使用handler参数发送默认what为{@link com.gaiay.base.common.CommonCode#BITMAP_HOLDE_MSG}的消息,
	 * 此消息的arg1为{@link com.gaiay.base.common.CommonCode#BITMAP_FAILD}(失败)<br>
	 * 或者{@link com.gaiay.base.common.CommonCode#BITMAP_SUCCESS}(成功).
	 * <br>当成功的时候arg2为输入参数id,用于标识图片.obj为图片的bitmap
	 * <br>当失败的时候arg2为0,obj为提示字符串
	 * @param url 图片的url地址
	 * @param cxt 上下文菜单
	 * @param handler 装载完成后接收更新消息的Handler
	 * @param setId 图片加载完成后的组Id,请不要让此参数为空,否者可能在回收图片的时候产生错误的回收结果
	 * @param id 图片的id,如list中的position.用于标识图片
	 */
	public static BitmapAsynTask getBitmap(String url, Context cxt, Handler handler, String setId, int id, String cacheFolder) {
		return getBitmap(url, cxt, handler, CommonCode.BITMAP_HOLDE_MSG, setId, id, cacheFolder);
	}
	
	public static int count = 0;
	
	private static void checkTask() {
		count ++;
		Log.e("count:" + count);
//		if (task.size() >= 10) {
//			for (Map.Entry<String, BitmapAsynTask> t : task.entrySet()) {
//				t.getValue().cancel(true);
//			}
//			task.clear();
//		}
	}

	/**
	 * 批量获取图片(暂时功能未实现)
	 * @param urls 图片的url地址以及图片下载成功后要存放的位置
	 * @param cxt 上下文菜单
	 * @param callback 下载成功后的回调函数
	 */
//	public void getBitmap(Map<String, Bitmap> urls, Context cxt, BaseActivity.Callback callback) {
//		
//	}
	
	/**
	 * 检查图片是否缓存在本地(本地内存>本地sdcard)
	 * @param url 图片的url地址
	 * @param bitmap 图片的bitmap
	 * @return true在本地,并加载完成,false本地没有,或者加载失败
	 */
	private static Bitmap isInLoc(String url, String setId) {
		Bitmap bmp = null;
		url = BitmapUtil.getImgLocName(url);
		if (bmpList.contains(url)) {
			bmp = isInCache(url, setId);
			if(bmp != null && !bmp.isRecycled()) {
				Log.e(TAG, "FROM CACHE");
				return bmp;
			}
			Log.e(TAG, "FROM LOCAL");
			bmp = createBitmapFromFile(url);
			if (cacheBitmap(bmp, url, setId)) {
				return bmp;
			}
		}
		return null;
	}
	/**
	 * 指定url地址的图片是否在管理队列中有缓存对象
	 * @param urlMD5 指定的图片url地址经过md5后的参数
	 * @param bitmap 指定的图片存放位置
	 * @return true指定的url在管理队列中并加载成功,false指定的url不在管理队列中或者加载失败
	 */
	private static Bitmap isInCache(String urlMD5, String setId) {
		for (int i = 0; i < bitmaps.size(); i++) {
			if (urlMD5.equals(bitmaps.get(i).getUrl())) {
				BitmapModel md = bitmaps.remove(i);
				if (md.getBitmap() != null && !md.getBitmap().isRecycled()) {
					bitmaps.offer(md);
					md.setSetId(setId);
					return md.getBitmap();
				} else {
					md = null;
				}
				break;
			}
		}
		return null;
	}
	
	/**
	 * 将Bitmap对象加入管理队列中
	 * @param bitmap 要加入队列的Bitmap
	 * @param urlMD5 Bitmap的url地址经过md5后的string,作为记录此Bitmap的唯一标识
	 * @return true加入成功,false加入失败
	 */
	public static boolean cacheBitmap(Bitmap bitmap, String urlMD5, String setId) {
		if (bitmap != null && !StringUtil.isBlank(urlMD5)) {
			BitmapModel model = getBitmapModel(urlMD5, bitmap, setId);
			if (model != null) {
				bitmaps.offer(model);
				return true;
			}
			return false;
		}
		return false;
	}
	
	/**
	 * 将所有写入sdcard的图片文件全部以图片url为key,本地实际绝对路径为value的形式保存到管理Map中
	 * @param url 图片的url地址经md5后的参数
	 */
	public static void saveLocImgMsg(String url) {
		if (!StringUtil.isBlank(url)) {
			bmpList.add(url);
		}
	}
	
	/**
	 * 将本地图片缓存到内存中,并警醒管理与对管理队列的大小进行检查
	 * @param name 图片的名称，url经md5后的图片名称
	 * @return 加载完成的图片
	 */
	public static Bitmap createBitmapFromFile(String name) {
		Log.e("createBitmapFromFile：" + (cacheFolder + name));
		if (StringUtil.isBlank(name)) {
			return null;
		}
		Bitmap bmp = null;
		if (bmpList.contains(name)) {
			bmp = BitmapFactory.decodeFile(cacheFolder + name);
		}
		return bmp;
	}
	
	public static String getLocPath(String url) {
		if (!StringUtil.isBlank(url)) {
			if (bmpList.contains(url)) {
				return cacheFolder + url;
			}
		}
		return null;
	}
	
	/**
	 * 检查管理队列中的图片中大小.
	 * 如果总大小大于默认设定的总大小,则将最早放入管理队列的图片进行释放内存操作
	 */
	@SuppressWarnings("unused")
	private static void removeSpareBitmap() {
		if (currSize >= TOTLE_SIZE) {
			int spare = currSize - TOTLE_SIZE;
			int totle = 0;
			while(totle <= spare && bitmaps.size() > 0) {
				BitmapModel m = bitmaps.remove(0);
				m.getBitmap().recycle();
				m.setBitmap(null);
				totle += m.getSize();
				m = null;
			}
			currSize = currSize - totle;
		}
	}
	
	/**
	 * 根据参数获取BitmapModel对象
	 * @param url 图片的url地址
	 * @param bitmap 图片的Bitmap对象
	 * @return 生成的BitmapModel对象
	 */
	private static BitmapModel getBitmapModel(String url, Bitmap bitmap, String setId) {
		if (StringUtil.isBlank(url) || bitmap == null) {
			return null;
		}
		BitmapModel md = new BitmapModel();
		md.setUrl(url);
		md.setBitmap(bitmap);
		md.setSize(BitmapUtil.getBitmapSize(bitmap));
		md.setSetId(setId);
		return md;
	}
	
	/**
	 * 清除缓存中特定URL的bitmap
	 * @param url url
	 * @param setId 组ID
	 */
	public static void recycleSpecil(String url, String setId) {
		List<Integer> data = null;
		for (int i = 0; i < bitmaps.size(); i++) {
			BitmapModel md = bitmaps.get(i);
			if (md.getUrl() != null && md.getUrl().equals(BitmapUtil.getImgLocName(url)) && md.recycle(setId)) {
				if (data == null) {
					data = new ArrayList<Integer>();
				}
				data.add(i);
			}
		}
		if (data != null) {
			for (int i = 0; i < data.size(); i++) {
				bitmaps.remove(data.get(i));
			}
		}
	}
	
	/**
	 * 清除缓存中固定组Id的图片
	 * @param id 组Id
	 */
	public static void clearSelfBmp(String id) {
		List<Integer> data = null;
		for (int i = 0; i < bitmaps.size(); i++) {
			BitmapModel md = bitmaps.get(i);
			if (md.recycle(id)) {
				if (data == null) {
					data = new ArrayList<Integer>();
				}
				data.add(i);
			}
		}
		if (data != null) {
			for (int i = 0; i < data.size(); i++) {
				bitmaps.remove(data.get(i));
			}
		}
	}
	
	/**
	 * 释放所有缓存的Bitmap内存及信息
	 */
	public static void recycleSelf() {
		if (bitmaps != null) {
			for (int i = 0; i < bitmaps.size(); i++) {
				if (bitmaps.get(i) != null && bitmaps.get(i).getBitmap() != null) {
					bitmaps.get(i).recycle(null);
				}
			}
			bitmaps.clear();
		}
	}
	
	public static void initLocBmpList() {
		if (bmpList != null) {
			bmpList.clear();
		}
		String[] paths = new File(cacheFolder).list();
		if (paths != null) {
			for (int i = 0; i < paths.length; i++) {
				bmpList.add(paths[i]);
			}
		}
	}
	
	private static boolean isFirst;
	
	public static void initFirstBitmapList() {
		if (isFirst) {
			if (bmpList != null) {
				bmpList.clear();
			}
			String[] paths = new File(cacheFolder).list();
			if (paths != null) {
				for (int i = 0; i < paths.length; i++) {
					bmpList.add(paths[i]);
				}
			}
		}
		isFirst = false;
	}
}
