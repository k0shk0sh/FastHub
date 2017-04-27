package com.fastaccess.data.dao;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Kosh on 27 Apr 2017, 6:10 PM
 */

@Getter @Setter public class TabsCountStateModel {
    private int count;
    private int tabIndex;

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TabsCountStateModel that = (TabsCountStateModel) o;
        return count == that.count && tabIndex == that.tabIndex;
    }

    @Override public int hashCode() {
        int result = count;
        result = 31 * result + tabIndex;
        return result;
    }
}
