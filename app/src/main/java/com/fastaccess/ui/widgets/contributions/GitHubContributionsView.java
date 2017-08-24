package com.fastaccess.ui.widgets.contributions;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.fastaccess.R;
import com.fastaccess.ui.widgets.contributions.utils.ColorsUtils;
import com.fastaccess.ui.widgets.contributions.utils.DatesUtils;

import java.util.List;

/**
 * Copyright 2016 Javier González
 * All right reserved.
 */

public class GitHubContributionsView extends View {
    private static final String BASE_COLOR = "#D6E685"; // default of Github

    private int baseColor = Color.parseColor(BASE_COLOR);
    private int baseEmptyColor = Color.rgb(238, 238, 238);
    private int backgroundBaseColor = Color.TRANSPARENT;
    private int textColor = Color.BLACK;
    private boolean displayMonth = false;
    private int lastWeeks = 53;
    private String username;
    private Rect rect;
    private Paint monthTextPaint;
    private Matrix matrix = new Matrix();
    private Paint paint = new Paint();
    private Paint blockPaint;
    private Bitmap bitmap = null;
    private int height;
    private Point point = new Point();

    public GitHubContributionsView(Context context) {
        super(context);
        init(context, null, 0, 0);
    }

    public GitHubContributionsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0, 0);
    }

    public GitHubContributionsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    public GitHubContributionsView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getSize(point);
        final TypedArray attributes = context.getTheme().obtainStyledAttributes(
                attrs, R.styleable.GitHubContributionsView, defStyleAttr, defStyleRes);
        initAttributes(attributes);

        rect = new Rect();
        monthTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        blockPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        blockPaint.setStyle(Paint.Style.FILL);
    }

    private void initAttributes(TypedArray attributes) {
        baseColor = attributes.getColor(R.styleable.GitHubContributionsView_baseColor, baseColor);
        baseEmptyColor = attributes.getColor(R.styleable.GitHubContributionsView_baseEmptyColor, baseEmptyColor);
        backgroundBaseColor = attributes.getColor(R.styleable.GitHubContributionsView_backgroundBaseColor, backgroundBaseColor);
        textColor = attributes.getColor(R.styleable.GitHubContributionsView_textColor, textColor);
        displayMonth = attributes.getBoolean(R.styleable.GitHubContributionsView_displayMonth, displayMonth);
        lastWeeks = attributes.getInt(R.styleable.GitHubContributionsView_lastWeeks, lastWeeks);
        if (attributes.getString(R.styleable.GitHubContributionsView_username) != null) {
            username = attributes.getString(R.styleable.GitHubContributionsView_username);
            if (!isInEditMode()) {
                loadUserName(username);
            }
        }
    }

    /**
     * Set a base color for blocks.
     * The tone depends on the number of contributions for a day.
     * Supported formats See {@link Color#parseColor(String)}
     *
     * @param baseColor
     *         base color supported formats
     */
    public void setBaseColor(String baseColor) {
        try {
            this.baseColor = Color.parseColor(baseColor);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        invalidate();
    }

    /**
     * Set a base color for blocks.
     * The tone depends on the number of contributions for a day.
     *
     * @param color
     *         resource color
     */
    public void setBaseColor(int color) {
        this.baseColor = color;
        invalidate();
    }

    /**
     * Set a base empty color for blocks without contributions.
     * Supported formats See {@link Color#parseColor(String)}
     *
     * @param baseColor
     *         base color supported formats
     */
    public void setBaseEmptyColor(String baseColor) {
        try {
            this.baseEmptyColor = Color.parseColor(baseColor);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        invalidate();
    }

    /**
     * Set a base empty color for blocks without contributions.
     *
     * @param color
     *         resource color
     */
    public void setBaseEmptyColor(int color) {
        this.baseEmptyColor = color;
        invalidate();
    }

    /**
     * Sets the background color for this contributions view.
     *
     * @param backgroundBaseColor
     *         the color of the background
     */
    public void setBackgroundBaseColor(String backgroundBaseColor) {
        try {
            this.backgroundBaseColor = Color.parseColor(backgroundBaseColor);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        invalidate();
    }

    /**
     * Sets the background color for this contributions view.
     *
     * @param backgroundBaseColor
     *         the color of the background
     */
    public void setBackgroundBaseColor(int backgroundBaseColor) {
        this.backgroundBaseColor = backgroundBaseColor;
        invalidate();
    }

    /**
     * Set a text color for month names.
     * Supported formats See {@link Color#parseColor(String)}
     *
     * @param textColor
     *         text color supported formats
     */
    public void setTextColor(String textColor) {
        try {
            this.textColor = Color.parseColor(textColor);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        invalidate();
    }

    /**
     * Set a text color for month names.
     *
     * @param textColor
     *         resource color
     */
    public void setTextColor(int textColor) {
        this.textColor = textColor;
        invalidate();
    }

    /**
     * Set the number of weeks that you want to display.
     * You can set minimum 2 weeks but is not recommended. 1 week is impossible.
     * You can set maximum 53 weeks (1 year = 52.14 weeks).
     * By default is 53 (52 weeks and the current week).
     *
     * @param lastWeeks
     *         number of week (2..53)
     */
    public void setLastWeeks(int lastWeeks) {
        if (lastWeeks >= 2 && lastWeeks <= 53) {
            this.lastWeeks = lastWeeks;
            invalidate();
        } else {
            throw new RuntimeException("The last weeks should be a number between 2 and 53");
        }
    }

    /**
     * Set if you want to see the name of the months
     * If you send true, the component height increase
     *
     * @param displayMonth
     *         true or false
     */
    public void displayMonth(boolean displayMonth) {
        this.displayMonth = displayMonth;
        invalidate();
    }

    /**
     * Load and show contributions information for a user / organization
     *
     * @param username
     *         also, can be an organization
     */
    private void loadUserName(String username) {
        this.username = username;
        clearContribution();
    }

    /**
     * Clean de component.
     */
    private void clearContribution() {
        bitmap = null;
        invalidate();
    }

    public void onResponse() {
        adjustHeight(height);
        invalidate();
    }

    @Override protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (bitmap != null) {
            canvas.drawBitmap(bitmap, matrix, paint);
        } else {
            drawPlaceholder(canvas);
        }
    }

    public Bitmap drawOnCanvas(List<ContributionsDay> contributionsFilter, List<ContributionsDay> contributions) {
        if ((contributionsFilter == null || contributions == null) || (contributionsFilter.isEmpty() || contributions.isEmpty())) {
            return null;
        }
        if (bitmap == null) {
            int padding = getResources().getDimensionPixelSize(R.dimen.spacing_large);
            int width = point.x - padding;
            int verticalBlockNumber = 7;
            int horizontalBlockNumber = getHorizontalBlockNumber(contributionsFilter.size(), verticalBlockNumber);
            float marginBlock = (1.0F - 0.1F);
            float blockWidth = width / (float) horizontalBlockNumber * marginBlock;
            float spaceWidth = width / (float) horizontalBlockNumber - blockWidth;
            float topMargin = (displayMonth) ? 7f : 0;
            float monthTextHeight = (displayMonth) ? blockWidth * 1.5F : 0;
            int height = (int) ((blockWidth + spaceWidth) * 7 + topMargin + monthTextHeight);
            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas1 = new Canvas(bitmap);
            // Background
            blockPaint.setColor(backgroundBaseColor);
            canvas1.drawRect(0, (topMargin + monthTextHeight), width, height + monthTextHeight, blockPaint);
            monthTextPaint.setColor(textColor);
            monthTextPaint.setTextSize(monthTextHeight);
            // draw the blocks
            int currentWeekDay = DatesUtils.getWeekDayFromDate(
                    contributions.get(0).year,
                    contributions.get(0).month,
                    contributions.get(0).day);
            float x = 0;
            float y = (currentWeekDay - 7) % 7 * (blockWidth + spaceWidth) + (topMargin + monthTextHeight);
            for (ContributionsDay day : contributionsFilter) {
                blockPaint.setColor(ColorsUtils.calculateLevelColor(baseColor, baseEmptyColor, day.level));
                canvas1.drawRect(x, y, x + blockWidth, y + blockWidth, blockPaint);
                if (DatesUtils.isFirstDayOfWeek(day.year, day.month, day.day + 1)) {
                    // another column
                    x += blockWidth + spaceWidth;
                    y = topMargin + monthTextHeight;
                    if (DatesUtils.isFirstWeekOfMount(day.year, day.month, day.day + 1)) {
                        canvas1.drawText(DatesUtils.getShortMonthName(day.year, day.month, day.day + 1), x, monthTextHeight,
                                monthTextPaint);
                    }

                } else {
                    y += blockWidth + spaceWidth;
                }
            }
            this.height = height;
        }
        return bitmap;
    }

    private void adjustHeight(int height) {
        ViewGroup.LayoutParams ll = getLayoutParams();
        if (height != ll.height) {
            ll.height = height;
            setLayoutParams(ll);
        }
    }

    private void drawPlaceholder(Canvas canvas) {
        if (!isInEditMode()) return;
        canvas.getClipBounds(rect);
        int width = rect.width();

        int verticalBlockNumber = 7;
        int horizontalBlockNumber = getHorizontalBlockNumber(lastWeeks * 7, verticalBlockNumber);

        float marginBlock = (1.0F - 0.1F);
        float blockWidth = width / (float) horizontalBlockNumber * marginBlock;
        float spaceWidth = width / (float) horizontalBlockNumber - blockWidth;

        float monthTextHeight = (displayMonth) ? blockWidth * 1.5F : 0;
        float topMargin = (displayMonth) ? 7f : 0;

        monthTextPaint.setTextSize(monthTextHeight);

        int height = (int) ((blockWidth + spaceWidth) * 7 + topMargin + monthTextHeight);

        // Background
        blockPaint.setColor(backgroundBaseColor);
        canvas.drawRect(0, (topMargin + monthTextHeight), width, height + monthTextHeight, blockPaint);


        float x = 0;
        float y = 0
                * (blockWidth + spaceWidth)
                + (topMargin + monthTextHeight);

        for (int i = 1; i < (lastWeeks * 7) + 1; i++) {

            blockPaint.setColor(ColorsUtils.calculateLevelColor(baseColor, baseEmptyColor, 0));
            canvas.drawRect(x, y, x + blockWidth, y + blockWidth, blockPaint);

            if (i % 7 == 0) {
                // another column
                x += blockWidth + spaceWidth;
                y = topMargin + monthTextHeight;

            } else {
                y += blockWidth + spaceWidth;
            }
        }

        // Resize component
        ViewGroup.LayoutParams ll = getLayoutParams();
        ll.height = height;
        setLayoutParams(ll);
    }

    private int getHorizontalBlockNumber(int total, int divider) {
        boolean isInteger = total % divider == 0;
        int result = total / divider;
        return (isInteger) ? result : result + 1;
    }

    public List<ContributionsDay> getLastContributions(List<ContributionsDay> contributions) {
        int lastWeekDays = contributions.size() % 7;
        int lastDays = (lastWeekDays > 0) ? lastWeekDays + (lastWeeks - 1) * 7 : lastWeeks * 7;
        return contributions.subList(contributions.size() - lastDays, contributions.size());
    }
}

