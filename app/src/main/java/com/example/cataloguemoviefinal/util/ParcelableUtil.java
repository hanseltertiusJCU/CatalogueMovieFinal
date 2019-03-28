package com.example.cataloguemoviefinal.util;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Kelas ini berguna untuk mengconvert Parcelable object menjadi byte[] maupun ke arah sebaliknya
 */
public class ParcelableUtil {

    /**
     * Method ini berguna untuk convert {@link Parcelable} ke {@link byte[]} object
     *
     * @param parcelable Parcelable object
     * @return byte[] object
     */
    public static byte[] marshall(Parcelable parcelable) {
        Parcel parcel = Parcel.obtain(); // obtain parcel object
        parcelable.writeToParcel(parcel, 0);
        byte[] bytes = parcel.marshall(); // memanggil marshall method dari object Parcel
        parcel.recycle();
        return bytes;
    }

    /**
     * Method ini berguna untuk convert {@link byte[]} ke {@link Parcel} object tanpa
     * {@link Parcelable.Creator} object
     *
     * @param bytes byte[] object (input)
     * @return Parcel object (output)
     */
    public static Parcel unmarshall(byte[] bytes) {
        Parcel parcel = Parcel.obtain(); // obtain parcel object
        parcel.unmarshall(bytes, 0, bytes.length); // memanggil unmarshall method dari object Parcel
        parcel.setDataPosition(0);
        return parcel;
    }

    /**
     * Method ini berguna untuk convert {@link byte[]} ke {@link Parcel} object dengan
     * {@link Parcelable.Creator} object
     *
     * @param bytes   byte[] object
     * @param creator Parcelable creator
     * @param <T>     Class yg mengimplement Parcelable
     * @return <T> yaitu class yg implement Parcelable juga
     */
    public static <T> T unmarshall(byte[] bytes, Parcelable.Creator<T> creator) {
        Parcel parcel = unmarshall(bytes);
        T result = creator.createFromParcel(parcel);
        parcel.recycle();
        return result;
    }
}
