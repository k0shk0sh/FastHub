package com.fastaccess.data.dao;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by Kosh on 05 Mar 2017, 2:30 AM
 */

@Getter @Setter @NoArgsConstructor
public class CreateMilestoneModel {
    private String title;
    private String description;
    @SerializedName("due_one") private String dueOn;
}
