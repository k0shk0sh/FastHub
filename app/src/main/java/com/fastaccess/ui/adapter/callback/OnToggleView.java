package com.fastaccess.ui.adapter.callback;

public interface OnToggleView {
    void onToggle(int position, boolean isCollapsed);

    boolean isCollapsed(int position);
}