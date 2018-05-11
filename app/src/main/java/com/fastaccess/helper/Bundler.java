package com.fastaccess.helper;

import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.SparseArray;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Kosh on 23 May 2016, 3:37 PM
 */

public class Bundler {

    private final Bundle bundle;

    private Bundler() {
        bundle = new Bundle();
    }

    public static Bundler start() {
        return new Bundler();
    }

    public Bundler put(@NonNull String key, boolean value) {
        bundle.putBoolean(key, value);
        return this;
    }

    public Bundler put(@NonNull String key, boolean[] value) {
        bundle.putBooleanArray(key, value);
        return this;
    }

    public Bundler put(@NonNull String key, IBinder value) {
        // Uncommment this line if your minimum sdk version is API level 18
        //start.putBinder(key, value);
        return this;
    }

    public Bundler put(@NonNull String key, int value) {
        bundle.putInt(key, value);
        return this;
    }

    public Bundler put(@NonNull String key, int[] value) {
        bundle.putIntArray(key, value);
        return this;
    }

    public Bundler putIntegerArrayList(@NonNull String key, ArrayList<Integer> value) {
        bundle.putIntegerArrayList(key, value);
        return this;
    }

    public Bundler put(@NonNull String key, Bundle value) {
        bundle.putBundle(key, value);
        return this;
    }

    public Bundler put(@NonNull String key, byte value) {
        bundle.putByte(key, value);
        return this;
    }

    public Bundler put(@NonNull String key, byte[] value) {
        bundle.putByteArray(key, value);
        return this;
    }

    public Bundler put(@NonNull String key, String value) {
        bundle.putString(key, value);
        return this;
    }

    public Bundler put(@NonNull String key, String[] value) {
        bundle.putStringArray(key, value);
        return this;
    }

    public Bundler putStringArrayList(@NonNull String key, ArrayList<String> value) {
        bundle.putStringArrayList(key, value);
        return this;
    }

    public Bundler put(@NonNull String key, long value) {
        bundle.putLong(key, value);
        return this;
    }

    public Bundler put(@NonNull String key, long[] value) {
        bundle.putLongArray(key, value);
        return this;
    }

    public Bundler put(@NonNull String key, float value) {
        bundle.putFloat(key, value);
        return this;
    }

    public Bundler put(@NonNull String key, float[] value) {
        bundle.putFloatArray(key, value);
        return this;
    }

    public Bundler put(@NonNull String key, char value) {
        bundle.putChar(key, value);
        return this;
    }

    public Bundler put(@NonNull String key, char[] value) {
        bundle.putCharArray(key, value);
        return this;
    }

    public Bundler put(@NonNull String key, CharSequence value) {
        bundle.putCharSequence(key, value);
        return this;
    }

    public Bundler put(@NonNull String key, CharSequence[] value) {
        bundle.putCharSequenceArray(key, value);
        return this;
    }

    public Bundler putCharSequenceArrayList(@NonNull String key, ArrayList<CharSequence> value) {
        bundle.putCharSequenceArrayList(key, value);
        return this;
    }

    public Bundler put(@NonNull String key, double value) {
        bundle.putDouble(key, value);
        return this;
    }

    public Bundler put(@NonNull String key, double[] value) {
        bundle.putDoubleArray(key, value);
        return this;
    }

    public Bundler put(@NonNull String key, Parcelable value) {
        Bundle safeBundle = new Bundle();
        safeBundle.putParcelable(key, value);
        if (isValidBundleSize(safeBundle)) {
            bundle.putParcelable(key, value);
        }
        clearBundle(safeBundle);
        return this;
    }

    public Bundler put(@NonNull String key, Parcelable[] value) {
        Bundle safeBundle = new Bundle();
        safeBundle.putParcelableArray(key, value);
        if (isValidBundleSize(safeBundle)) {
            bundle.putParcelableArray(key, value);
        }
        clearBundle(safeBundle);
        return this;
    }

    public Bundler putParcelableArrayList(@NonNull String key, ArrayList<? extends Parcelable> value) {
        Bundle safeBundle = new Bundle();
        safeBundle.putParcelableArrayList(key, value);
        if (isValidBundleSize(safeBundle)) {
            bundle.putParcelableArrayList(key, value);
        }
        clearBundle(safeBundle);
        return this;
    }

    public Bundler putSparseParcelableArray(@NonNull String key, SparseArray<? extends Parcelable> value) {
        Bundle safeBundle = new Bundle();
        safeBundle.putSparseParcelableArray(key, value);
        if (isValidBundleSize(safeBundle)) {
            bundle.putSparseParcelableArray(key, value);
        }
        clearBundle(safeBundle);
        return this;
    }

    public Bundler put(@NonNull String key, short value) {
        bundle.putShort(key, value);
        return this;
    }

    public Bundler put(@NonNull String key, short[] value) {
        bundle.putShortArray(key, value);
        return this;
    }

    public Bundler put(@NonNull String key, Serializable value) {
        Bundle safeBundle = new Bundle();
        safeBundle.putSerializable(key, value);
        if (isValidBundleSize(safeBundle)) {
            bundle.putSerializable(key, value);
        }
        clearBundle(safeBundle);
        return this;
    }

    public Bundler putAll(Bundle map) {
        bundle.putAll(map);
        return this;
    }

    /**
     * Get the underlying start.
     */
    private Bundle get() {
        return bundle;
    }

    @NonNull public Bundle end() {
        Parcel parcel = Parcel.obtain();
        bundle.writeToParcel(parcel, 0);
        int size = parcel.dataSize();
        Logger.e(size);
        if (size > 500000) {
            bundle.clear();
        }
        return get();
    }

    public static boolean isValidBundleSize(@NonNull Bundle bundle) {
        Parcel parcel = Parcel.obtain();
        bundle.writeToParcel(parcel, 0);
        return parcel.dataSize() < 500000;
    }

    private void clearBundle(Bundle safeBundle) {
        safeBundle.clear();
        safeBundle = null;
    }

}
