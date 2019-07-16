package com.affirm.android;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.util.TypedValue;

import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;

import static com.affirm.android.AffirmConstants.LOGO_PLACEHOLDER;
import static com.affirm.android.AffirmLogoType.AFFIRM_DISPLAY_TYPE_TEXT;

class PromotionButton extends AppCompatButton {

    private Paint paint;
    private AffirmLogoType affirmLogoType;
    private AffirmColor affirmColor;

    public void setAffirmLogoType(AffirmLogoType affirmLogoType) {
        this.affirmLogoType = affirmLogoType;
    }

    public void setAffirmColor(AffirmColor affirmColor) {
        this.affirmColor = affirmColor;
    }

    public void setAffirmTextSize(float affirmTextSize) {
        setTextSize(TypedValue.COMPLEX_UNIT_PX, affirmTextSize);
    }

    public PromotionButton(@NonNull Context context) {
        this(context, null);
    }

    public PromotionButton(@NonNull Context context,
                           @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PromotionButton(@NonNull Context context,
                           @Nullable AttributeSet attrs,
                           int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
    }

    SpannableString updateSpan(@NonNull String template) {
        float textSize = getTextSize();

        paint.setTextSize(textSize);
        paint.setTypeface(getTypeface());
        paint.getTextBounds(template.toUpperCase(Locale.getDefault()), 0, template.length(), new Rect());

        return AffirmUtils.spannableFromEditText(template, textSize, affirmLogoType, affirmColor, getContext());
    }
}
