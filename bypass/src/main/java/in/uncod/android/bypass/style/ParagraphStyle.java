package in.uncod.android.bypass.style;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.style.LineBackgroundSpan;

public class ParagraphStyle implements LineBackgroundSpan {
    private Paint paint = new Paint();
    private float radius;

    public ParagraphStyle(int color, float radius) {
        this.paint.setAlpha(Color.alpha(color));
        this.paint.setColor(color);
        this.paint.setAntiAlias(true);
        this.radius = radius;
    }

    @Override public void drawBackground(Canvas canvas, Paint paint, int i, int i2, int i3, int i4, int i5,
                                         CharSequence charSequence, int i6, int i7, int i8) {
        canvas.drawRoundRect(new RectF((float) i, ((float) i3) - (this.radius * 4.0f), (float) i2, ((float) i5) + (this.radius * 4.0f)), this
                .radius, this.radius, this.paint);
    }
}