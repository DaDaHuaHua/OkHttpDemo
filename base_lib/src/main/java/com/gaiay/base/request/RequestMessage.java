package com.gaiay.base.request;

import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;

import com.gaiay.base.common.CommonCode;
import com.gaiay.base.model.BaseModel;

public class RequestMessage extends ARequest {
	private BaseModel data = null;
	
	@Override
	protected int parseSelfInfo(InputStream result, int what) {
		if (result == null) {
			return CommonCode.ERROR_PARSE_DATA;
		}
		try {
			data = parseResult(result);
			if (data == null) {
				return CommonCode.ERROR_PARSE_DATA;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return CommonCode.ERROR_PARSE_DATA;
		}
		return CommonCode.SUCCESS;
	}

	private BaseModel parseResult(InputStream result) {
		BaseModel data = new BaseModel();
		try {
			parser.setInput(result, "utf-8");
			int event = parser.getEventType(); 
			while (event != XmlPullParser.END_DOCUMENT) {
				String name = parser.getName();
				switch (event) {
				case XmlPullParser.START_TAG:
					if ("rc".equals(name)) {
						data.rc = parser.nextText();
					} else if ("rm".equals(name)) {
						if (data.rc == null || data.rc.equals("") || !data.rc.equals("0")) {
							data.rm = parser.nextText();
						}
					}
					break;
				}
				event = parser.next();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}
	
	public BaseModel getData() {
		return data;
	}

	@Override
	public boolean hasData() {
		return data != null;
	}

}
