package com.fastaccess.data.dao;

import android.os.Parcel;
import android.os.Parcelable;

import com.annimon.stream.Stream;
import com.fastaccess.helper.InputHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Serves a model for the filter in Repositories fragment
 */
public class FilterOptionsModel implements Parcelable {

    private static final String TYPE = "type";
    private static final String SORT = "sort";
    private static final String AFFILIATION = "affiliation";
    private static final String DIRECTION = "direction";

    private String type;
    private String sort = "Pushed";
    private String sortDirection = "descending";
    private Map<String, String> queryMap;
    private boolean isPersonalProfile;

    private List<String> typesListForPersonalProfile = Stream.of("Select", "All", "Owner", "Public", "Private", "Member").toList();
    private List<String> typesListForExternalProfile = Stream.of("Select", "All", "Owner", "Member").toList();
    private List<String> typesListForOrganizationProfile = Stream.of("Select", "All", "Public", "Private", "Forks", "Sources", "Member").toList();
    private List<String> sortOptionsList = Stream.of("Pushed", "Created", "Updated", "Full Name").toList();
    private List<String> sortDirectionList = Stream.of("Descending", "Ascending").toList();
    private boolean isOrg;

    public FilterOptionsModel() {
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public void setSortDirection(String sortDirection) {
        this.sortDirection = sortDirection;
    }

    public Map<String, String> getQueryMap() {
        if (queryMap == null) {
            queryMap = new HashMap<>();
        } else {
            queryMap.clear();
        }
        if (InputHelper.isEmpty(type) || "Select".equalsIgnoreCase(type)) {
            queryMap.remove(TYPE);
            queryMap.put(AFFILIATION, "owner,collaborator");
        } else {
            queryMap.remove(AFFILIATION);
            queryMap.put(TYPE, type.toLowerCase());
        }
        //Not supported for organization repo
        if (!isOrg) {
            if (sort.contains(" ")) {
                //full name should be full_name
                queryMap.put(SORT, sort.replace(" ", "_").toLowerCase());
            } else {
                queryMap.put(SORT, sort.toLowerCase());
            }
            if (sortDirection.equals(sortDirectionList.get(0))) {
                //Descending should be desc
                queryMap.put(DIRECTION, sortDirection.toLowerCase().substring(0, 4));
            } else {
                //Ascending should be asc
                queryMap.put(DIRECTION, sortDirection.toLowerCase().substring(0, 3));
            }
        }
        return queryMap;
    }

    public int getSelectedTypeIndex() {
        if (isPersonalProfile) {
            return typesListForPersonalProfile.indexOf(type);
        } else {
            return typesListForExternalProfile.indexOf(type);
        }
    }

    public int getSelectedSortOptionIndex() {
        return sortOptionsList.indexOf(sort);
    }

    public int getSelectedSortDirectionIndex() {
        return sortDirectionList.indexOf(sortDirection);
    }

    public List<String> getTypesList() {
        if (isPersonalProfile) {
            return typesListForPersonalProfile;
        } else if (isOrg) {
            return typesListForOrganizationProfile;
        } else {
            return typesListForExternalProfile;
        }
    }

    public List<String> getSortOptionList() {
        return sortOptionsList;
    }

    public List<String> getSortDirectionList() {
        return sortDirectionList;
    }

    public void setIsPersonalProfile(boolean isPersonalProfile) {
        this.isPersonalProfile = isPersonalProfile;
    }

    public void setOrg(boolean org) {
        this.isOrg = org;
    }

    public boolean isOrg() {
        return isOrg;
    }

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.type);
        dest.writeString(this.sort);
        dest.writeString(this.sortDirection);
        dest.writeInt(this.queryMap.size());
        for (Map.Entry<String, String> entry : this.queryMap.entrySet()) {
            dest.writeString(entry.getKey());
            dest.writeString(entry.getValue());
        }
        dest.writeByte(this.isPersonalProfile ? (byte) 1 : (byte) 0);
        dest.writeStringList(this.typesListForPersonalProfile);
        dest.writeStringList(this.typesListForExternalProfile);
        dest.writeStringList(this.typesListForOrganizationProfile);
        dest.writeStringList(this.sortOptionsList);
        dest.writeStringList(this.sortDirectionList);
        dest.writeByte(this.isOrg ? (byte) 1 : (byte) 0);
    }

    private FilterOptionsModel(Parcel in) {
        this.type = in.readString();
        this.sort = in.readString();
        this.sortDirection = in.readString();
        int queryMapSize = in.readInt();
        this.queryMap = new HashMap<String, String>(queryMapSize);
        for (int i = 0; i < queryMapSize; i++) {
            String key = in.readString();
            String value = in.readString();
            this.queryMap.put(key, value);
        }
        this.isPersonalProfile = in.readByte() != 0;
        this.typesListForPersonalProfile = in.createStringArrayList();
        this.typesListForExternalProfile = in.createStringArrayList();
        this.typesListForOrganizationProfile = in.createStringArrayList();
        this.sortOptionsList = in.createStringArrayList();
        this.sortDirectionList = in.createStringArrayList();
        this.isOrg = in.readByte() != 0;
    }

    public static final Creator<FilterOptionsModel> CREATOR = new Creator<FilterOptionsModel>() {
        @Override public FilterOptionsModel createFromParcel(Parcel source) {return new FilterOptionsModel(source);}

        @Override public FilterOptionsModel[] newArray(int size) {return new FilterOptionsModel[size];}
    };
}
