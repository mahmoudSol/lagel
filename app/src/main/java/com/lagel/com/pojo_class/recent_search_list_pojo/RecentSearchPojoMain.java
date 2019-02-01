package com.lagel.com.pojo_class.recent_search_list_pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class RecentSearchPojoMain implements Parcelable {

    @SerializedName("code")
    @Expose
    private Integer code;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private ArrayList<SearchResponse> data = null;
    public final static Parcelable.Creator<RecentSearchPojoMain> CREATOR = new Creator<RecentSearchPojoMain>() {


        @SuppressWarnings({
                "unchecked"
        })
        public RecentSearchPojoMain createFromParcel(Parcel in) {
            return new RecentSearchPojoMain(in);
        }

        public RecentSearchPojoMain[] newArray(int size) {
            return (new RecentSearchPojoMain[size]);
        }

    };

    protected RecentSearchPojoMain(Parcel in) {
        this.code = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.message = ((String) in.readValue((String.class.getClassLoader())));
        in.readList(this.data, (SearchResponse.class.getClassLoader()));
    }

    public RecentSearchPojoMain() {
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ArrayList<SearchResponse> getData() {
        return data;
    }

    public void setData(ArrayList<SearchResponse> data) {
        this.data = data;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(code);
        dest.writeValue(message);
        dest.writeList(data);
    }

    public int describeContents() {
        return 0;
    }

}