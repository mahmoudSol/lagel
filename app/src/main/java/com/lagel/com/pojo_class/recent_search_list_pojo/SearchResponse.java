package com.lagel.com.pojo_class.recent_search_list_pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SearchResponse implements Parcelable {

    @SerializedName("searchHistoryData")
    @Expose
    private List<String> searchHistory = null;
    public final static Parcelable.Creator<SearchResponse> CREATOR = new Creator<SearchResponse>() {


        @SuppressWarnings({
                "unchecked"
        })
        public SearchResponse createFromParcel(Parcel in) {
            return new SearchResponse(in);
        }

        public SearchResponse[] newArray(int size) {
            return (new SearchResponse[size]);
        }

    };

    protected SearchResponse(Parcel in) {
        in.readList(this.searchHistory, (java.lang.String.class.getClassLoader()));
    }

    public SearchResponse() {
    }

    public List<String> getSearchHistory() {
        return searchHistory;
    }

    public void setSearchHistory(List<String> searchHistory) {
        this.searchHistory = searchHistory;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(searchHistory);
    }

    public int describeContents() {
        return 0;
    }


}