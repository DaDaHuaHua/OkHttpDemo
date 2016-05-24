package com.gaiay.base.common;

/**
 * Error信息包裹,用于自定义错误信息
 * 
 * @author iMuto
 */
public class ErrorMsg extends Throwable {

    private static final long serialVersionUID = 1L;

    private int errorCode;
    private String errorMsg;

    /**
     * 错误整体信息
     */
    @Override
    public String toString() {
    	return "错误代号:" + errorCode + " 错误信息:" + errorMsg;
    }
    /**
     * 获取错误码
     * @return 错误码
     */
    public int getCode() {
    	return errorCode;
    }
	/**
	 * 获取错误信息
	 * @return 错误信息
	 */
    public String getMsg() {
    	return errorMsg;
    }
    
    public ErrorMsg(int errorCode, String errorMsg) {
		this.errorCode = errorCode;
		this.errorMsg = errorMsg;
    }

}
