package com.example.cataloguemoviefinal.util;

import android.os.Parcel;
import android.os.Parcelable;

// Kelas ini berguna untuk mengconvert Parcelable object menjadi byte[] maupun ke arah sebaliknya
public class ParcelableUtil {
    // Method ini berguna untuk convert Parcelable ke byte[]
    public static byte[] marshall(Parcelable parcelable){
        Parcel parcel = Parcel.obtain(); // obtain parcel object
        parcelable.writeToParcel(parcel, 0);
        byte[] bytes = parcel.marshall(); // memanggil marshall method dari object Parcel
        parcel.recycle();
        return bytes;
    }
    // Method ini berguna untuk convert byte[] ke Parcelable tanpa menggunakan Creator di Parcelable
    public static Parcel unmarshall(byte[] bytes){
        Parcel parcel = Parcel.obtain(); // obtain parcel object
        parcel.unmarshall(bytes, 0, bytes.length); // memanggil unmarshall method dari object Parcel
        parcel.setDataPosition(0);
        return parcel;
    }

    // Method ini berguna untuk convert byte[] ke Parcelable dengan menggunakan Creator di Parcelable
    public static <T> T unmarshall(byte[] bytes, Parcelable.Creator<T> creator){
        Parcel parcel = unmarshall(bytes);
        T result = creator.createFromParcel(parcel);
        parcel.recycle();
        return result;
    }
}
