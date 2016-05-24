package com.gaiay.support.update;

import java.io.Serializable;

public class ModelUpdate implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public String id;
	public String desc;
	public String name;
	public String code;
	public String url;
	public String date;
	public String rc;
	public boolean hasNewVersion = false;
	public boolean isForce = false;
	
	@Override
	public String toString() {
		return "name:" + name + "  desc:" + desc + "  url:" + url + "  date:" + date + " hasNewVersion:" + hasNewVersion;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getRc() {
		return rc;
	}
	public void setRc(String rc) {
		this.rc = rc;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getVersionName() {
		return name;
	}
	public void setVersionName(String versionName) {
		this.name = versionName;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public boolean isHasNewVersion() {
		return hasNewVersion;
	}
	public void setHasNewVersion(boolean hasNewVersion) {
		this.hasNewVersion = hasNewVersion;
	}
	
}
