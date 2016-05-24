package com.gaiay.support.update;

import android.os.Binder;

public class UpdateBinder extends Binder {
	UpdateService getService() {
        return UpdateService.instance;
    }
}
