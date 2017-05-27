package com.fastaccess.ui.adapter.callback;

public interface OnToggleView {
    void onToggle(long id, boolean isCollapsed);

    boolean isCollapsed(long id);
}