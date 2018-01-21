package com.fastaccess.helper;

import android.os.Build;

import com.fastaccess.App;
import com.jaredrummler.android.device.DeviceName;

import io.reactivex.Observable;

/**
 * Created by Front Desk on 8/7/2017.
 */

public class DeviceNameGetter {
    private static final DeviceNameGetter ourInstance = new DeviceNameGetter();
    private String deviceName;

    public static DeviceNameGetter getInstance() {
        return ourInstance;
    }

    private DeviceNameGetter() {}

    public void loadDevice() {
        DeviceName.with(App.getInstance())
                .request((info, error) -> {
                    if (error == null && null != info) {
                        deviceName = info.marketName;
                    }
                });
    }

    String getDeviceName() {
        if (deviceName == null) {
            deviceName = blockingDeviceName();
        }
        return deviceName;
    }

    private String blockingDeviceName() {
        return (String) Observable.fromPublisher(s -> {
            DeviceName.with(App.getInstance())
                    .request((info, error) -> {
                        if (error == null && info != null) s.onNext(info.marketName);
                        else s.onError(error);
                    });
            s.onComplete();
        }).blockingFirst(Build.MODEL);
    }
}
