package com.gaiay.base.widget.horizontalscrollview;

import net.tsz.afinal.annotation.sqlite.Id;


public class HorizontalScrollViewModel {
	
	@Id
	public String id;
	public String name;
	public boolean isCheck = false;
	public String parentId;
	public String typeId;

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

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getTypeId() {
		return typeId;
	}

	public void setTypeId(String typeId) {
		this.typeId = typeId;
	}

	@Override
	public String toString() {
		String str = "CategoryModel: id(" + id + ") name(" + name + ") parentId(" + parentId + parentId + ") typeId("
				+ typeId + ")";
		return str;
	}
}
