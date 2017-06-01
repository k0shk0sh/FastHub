package com.fastaccess.data.dao.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by adibk on 5/19/17.
 */

public class FilterOptionsModel implements Parcelable {

    private static final String TYPE = "type";
    private static final String SORT = "sort";
    private static final String DIRECTION = "direction";

    private String type = "All";
    private String sort = "Pushed";
    private String sortDirection = "descending";
    private Map<String, String> queryMap;

    private List<String> typesList =  new ArrayList<>(Arrays.asList("All", "Owner", "Public", "Private", "Member"));
    private List<String> sortOptionsList = new ArrayList<>(Arrays.asList("Pushed", "Created", "Updated", "Full Name"));
    private List<String> sortDirectionList = new ArrayList<>(Arrays.asList("Descending", "Ascending"));

    public FilterOptionsModel() {
    }

    protected FilterOptionsModel(Parcel in) {
        type = in.readString();
        sort = in.readString();
        sortDirection = in.readString();
        typesList = in.createStringArrayList();
        sortOptionsList = in.createStringArrayList();
        sortDirectionList = in.createStringArrayList();
    }

    public static final Creator<FilterOptionsModel> CREATOR = new Creator<FilterOptionsModel>() {
        @Override
        public FilterOptionsModel createFromParcel(Parcel in) {
            return new FilterOptionsModel(in);
        }

        @Override
        public FilterOptionsModel[] newArray(int size) {
            return new FilterOptionsModel[size];
        }
    };

    public void setType(String type) {
        this.type = type;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public void setsortDirection(String sortDirection) {
        this.sortDirection = sortDirection;
    }

    public Map<String, String> getQueryMap() {
        if (queryMap == null) {
            queryMap = new HashMap<>();
        } else {
            queryMap.clear();
        }
        queryMap.put(TYPE, type.toLowerCase());
        if (sort.contains(" ")) {
            //full name should be full_name
            queryMap.put(SORT, sort.replace(" ", "_").toLowerCase());
        } else {
            queryMap.put(SORT, sort.toLowerCase());
        }
        if (sortDirection.equals(sortDirectionList.get(0))) {
            queryMap.put(DIRECTION, sortDirection.toLowerCase().substring(0, 4));
        } else {
            queryMap.put(DIRECTION, sortDirection.toLowerCase().substring(0, 3));
        }

        return queryMap;
    }

    public int getSelectedTypeIndex() {
        return typesList.indexOf(type);
    }

    public int getSelectedSortOptionIndex() {
        return sortOptionsList.indexOf(sort);
    }

    public int getSelectedSortDirectionIndex() {
        return sortDirectionList.indexOf(sortDirection);
    }

    public List<String> getTypesList() {
        return typesList;
    }

    public List<String> getSortOptionList() {
        return sortOptionsList;
    }

    public List<String> getSortDirectionList() {
        return sortDirectionList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(type);
        dest.writeString(sort);
        dest.writeString(sortDirection);
        dest.writeStringList(typesList);
        dest.writeStringList(sortOptionsList);
        dest.writeStringList(sortDirectionList);
    }
}
