package com.gaiay.support.update;

import org.json.JSONException;
import org.json.JSONObject;

public class ReqUpdate {

	ModelUpdate model;

	public boolean isSuccess() {
		return model.hasNewVersion;
	}

	public boolean hasData() {
		return model != null && model.hasNewVersion;
	}

	public void parseJSON(String paramString) throws JSONException {
		model = new ModelUpdate();
		if (paramString == null || "".equals(paramString)) {
			model.hasNewVersion = false;
			return;
		}
		JSONObject jo = new JSONObject(paramString);
		model.name = jo.optString("name", "");
		if (jo.isNull("version") && jo.isNull("message")) {
			model.code = jo.optString("code", "");
		} else {
			model.code = jo.optString("version", "");
		}
		if (jo.isNull("desc")) {
			model.desc = jo.optString("message", "");
		} else {
			model.desc = jo.optString("desc", "");
		}
		model.date = jo.optString("date", "");
		model.url = jo.optString("url", "");
		model.rc = jo.optString("rc", "");
		model.hasNewVersion = model.rc.equals("1");
	}
	
}
