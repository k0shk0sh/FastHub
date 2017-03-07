package in.uncod.android.bypass;

import android.text.style.ImageSpan;

/**
 * Callback when the image span is clicked
 */
public interface ImageSpanClickListener {

    /**
     * Indicates that an image span has been clicked
     *
     * @param imageSpan the imageSpan
     * @param span      the string
     */
    void onImageClicked(ImageSpan imageSpan, String span);
}
