package com.fastaccess.data.dao;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fastaccess.helper.InputHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import static com.fastaccess.ui.widgets.DiffLineSpan.HUNK_TITLE;

/**
 * Created by Kosh on 20 Jun 2017, 7:32 PM
 */

@Getter @Setter @AllArgsConstructor public class CommitLinesModel implements Parcelable {

    public static final int TRANSPARENT = 0;
    public static final int ADDITION = 1;
    public static final int DELETION = 2;
    public static final int PATCH = 3;


    public String text;
    public int color;
    public int leftLineNo;
    public int rightLineNo;
    public boolean noNewLine;
    public int position;

    @NonNull public static List<CommitLinesModel> getLines(@Nullable String text) {
        ArrayList<CommitLinesModel> models = new ArrayList<>();
        if (!InputHelper.isEmpty(text)) {
            String[] split = text.split("\\r?\\n|\\r");
            if (split.length > 1) {
                int leftLineNo = -1;
                int rightLineNo = -1;
                int position = 0;
                for (String token : split) {
                    char firstChar = token.charAt(0);
                    boolean addLeft = false;
                    boolean addRight = false;
                    int color = TRANSPARENT;
                    position++;
                    if (token.startsWith("@@")) {
                        color = PATCH;
                        Matcher matcher = HUNK_TITLE.matcher(token.trim());
                        if (matcher.matches()) {
                            try {
                                leftLineNo = Math.abs(Integer.parseInt(matcher.group(1))) - 1;
                                rightLineNo = Integer.parseInt(matcher.group(3)) - 1;
                            } catch (NumberFormatException e) {e.printStackTrace();}
                        }
                    } else if (firstChar == '+') {
                        color = ADDITION;
                        ++rightLineNo;
                        addRight = true;
                        addLeft = false;
                    } else if (firstChar == '-') {
                        color = DELETION;
                        ++leftLineNo;
                        addRight = false;
                        addLeft = true;
                    } else {
                        addLeft = true;
                        addRight = true;
                        ++rightLineNo;
                        ++leftLineNo;
                    }
                    int index = token.indexOf("\\ No newline at end of file");
                    if (index != -1) {
                        token = token.replace("\\ No newline at end of file", "");
                    }
                    models.add(new CommitLinesModel(token, color, token.startsWith("@@") || !addLeft ? -1 : leftLineNo,
                            token.startsWith("@@") || !addRight ? -1 : rightLineNo, index != -1, position));
                }
            }
        }
        return models;
    }

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.text);
        dest.writeInt(this.color);
        dest.writeInt(this.leftLineNo);
        dest.writeInt(this.rightLineNo);
        dest.writeByte(this.noNewLine ? (byte) 1 : (byte) 0);
        dest.writeInt(this.position);
    }

    protected CommitLinesModel(Parcel in) {
        this.text = in.readString();
        this.color = in.readInt();
        this.leftLineNo = in.readInt();
        this.rightLineNo = in.readInt();
        this.noNewLine = in.readByte() != 0;
        this.position = in.readInt();
    }

    public static final Creator<CommitLinesModel> CREATOR = new Creator<CommitLinesModel>() {
        @Override public CommitLinesModel createFromParcel(Parcel source) {return new CommitLinesModel(source);}

        @Override public CommitLinesModel[] newArray(int size) {return new CommitLinesModel[size];}
    };
}
