package com.gaiay.base.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class BaseModel implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 相应代码。0代表成功；1代表失败
	 */
	public String rc;
	/**
	 * 错误信息。当rc为1时才会显示
	 */
	public String rm;
	public String id;
	public String name;
	public boolean isCheck;
	
	public String getRc() {
		return rc;
	}

	public void setRc(String rc) {
		this.rc = rc;
	}

	public String getRm() {
		return rm;
	}

	public void setRm(String rm) {
		this.rm = rm;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isCheck() {
		return isCheck;
	}

	public void setCheck(boolean isCheck) {
		this.isCheck = isCheck;
	}

	public Object clone() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();  
        ObjectOutputStream oos;
        ByteArrayInputStream bis;
        ObjectInputStream ois = null;
        Object obj = null;
		try {
			oos = new ObjectOutputStream(bos);
			/* 写入当前对象的二进制流 */
			oos.writeObject(this);  
			bis = new ByteArrayInputStream(bos.toByteArray());  
			ois = new ObjectInputStream(bis);
			/* 读出二进制流产生的新对象 */
			obj = ois.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return obj;
	}
	
}
