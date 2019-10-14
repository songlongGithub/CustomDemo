package com.sl.customdemo.chart;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by sl on 2018/3/12.
 */

public class TrendDataBean implements Parcelable {

    private String recordDate;
    private double value;
    private int year;
    private boolean isShowYear;
    private int month;
    private int day;

    public TrendDataBean() {
    }

    public TrendDataBean(String recordDate, double value) {
        this.recordDate = recordDate;
        this.value = value;
    }

    public String getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(String recordDate) {
        this.recordDate = recordDate;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public boolean isShowYear() {
        return isShowYear;
    }

    public void setShowYear(boolean showYear) {
        isShowYear = showYear;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.recordDate);
        dest.writeDouble(this.value);
        dest.writeInt(this.year);
        dest.writeByte(this.isShowYear ? (byte) 1 : (byte) 0);
        dest.writeInt(this.month);
        dest.writeInt(this.day);
    }

    protected TrendDataBean(Parcel in) {
        this.recordDate = in.readString();
        this.value = in.readDouble();
        this.year = in.readInt();
        this.isShowYear = in.readByte() != 0;
        this.month = in.readInt();
        this.day = in.readInt();
    }

    public static final Parcelable.Creator<TrendDataBean> CREATOR = new Parcelable.Creator<TrendDataBean>() {
        @Override
        public TrendDataBean createFromParcel(Parcel source) {
            return new TrendDataBean(source);
        }

        @Override
        public TrendDataBean[] newArray(int size) {
            return new TrendDataBean[size];
        }
    };
}
