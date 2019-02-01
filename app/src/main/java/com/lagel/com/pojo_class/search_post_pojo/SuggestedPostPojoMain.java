package com.lagel.com.pojo_class.search_post_pojo;



import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.lagel.com.pojo_class.recent_search_list_pojo.SearchResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by embed on 20/1/18.
 */

public class SuggestedPostPojoMain {

    private String code="",message="";
    private ArrayList<SuggestedResponse> data;
    private ArrayList<String> searchResponses;
    @SerializedName("searchHistoryData")
    @Expose
    private List<String> searchHistory = null;

    public ArrayList<String> getSearchResponses() {
        return searchResponses;
    }

    public void setSearchResponses(ArrayList<String> searchResponses) {
        this.searchResponses = searchResponses;
    }



    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ArrayList<SuggestedResponse> getData() {
        return data;
    }

    public void setData(ArrayList<SuggestedResponse> data) {
        this.data = data;
    }

    public List<String> getSearchHistory() {
        return searchHistory;
    }

    public void setSearchHistory(List<String> searchHistory) {
        this.searchHistory = searchHistory;
    }
}
