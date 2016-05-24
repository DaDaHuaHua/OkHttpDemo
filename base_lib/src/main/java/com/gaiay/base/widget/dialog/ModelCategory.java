package com.gaiay.base.widget.dialog;

public class ModelCategory {
	public String id;
	public String name;
	public boolean isCheck = false;
	public String parentId;
	public String typeId;

	@Override
	public String toString() {
		String str = "CategoryModel: id(" + id + ") name(" + name + ") type("
				+ ") parentId(" + parentId + parentId + ") typeId("
				+ typeId + ")";
		return str;
	}
}
