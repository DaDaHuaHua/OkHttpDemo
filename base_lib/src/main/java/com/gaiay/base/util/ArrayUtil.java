package com.gaiay.base.util;

/**
 * 操作数组的工具类
 * @author RenTao
 * @time Aug 14, 2015 1:46:08 PM
 */
public class ArrayUtil {

    private ArrayUtil() {}

    /**
     * 获取数组长度，数组为null则返回0
     * @param sourceArray
     * @return
     */
    public static <T> int length(T[] sourceArray) {
        return sourceArray == null ? 0 : sourceArray.length;
    }

    public static <T> boolean isEmpty(T[] sourceArray) {
        return (sourceArray == null || sourceArray.length == 0);
    }

    public static <T> T get(T[] sourceArray, int index) {
        return sourceArray != null && index >= 0 && index < sourceArray.length ? sourceArray[index] : null;
    }

}
