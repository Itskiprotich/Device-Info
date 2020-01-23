package com.imeja.carpooling.styles;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;

import androidx.appcompat.widget.AppCompatTextView;

import android.util.AttributeSet;

import com.imeja.carpooling.R;
import com.imeja.carpooling.styles.TypefaceCache;

public class CustomTextView extends AppCompatTextView {

    public static final String ANDROID_SCHEMA = "http://schemas.android.com/apk/res/android";

    public CustomTextView(Context context) {
        super(context);
        if (!isInEditMode()) {
            Typeface customFont = null;
            try {
                customFont = selectTypeface(context, "Roboto", Typeface.NORMAL);
                setTypeface(customFont);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyCustomFont(context, attrs);
    }

    public CustomTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        applyCustomFont(context, attrs);
    }

    private void applyCustomFont(Context context, AttributeSet attrs) {
        TypedArray attributeArray = context.obtainStyledAttributes(
                attrs,
                R.styleable.CustomTextView);

        String fontName = getFont(attributeArray);
//        int textStyle = attrs.getAttributeIntValue(ANDROID_SCHEMA, "textStyle", Typeface.NORMAL);

        int textStyle = attributeArray.getInt(R.styleable.CustomTextView_textStyle, 0);
        // if nothing extra was used, fall back to regular android:textStyle parameter
        if (textStyle == 0) {
            textStyle = getTextStyle(attrs);
        }

        Typeface customFont = null;
        try {
            customFont = selectTypeface(context, fontName, textStyle);
            setTypeface(customFont);
        } catch (Exception e) {
            e.printStackTrace();
        }

        attributeArray.recycle();
    }

    private String getFont(TypedArray attributeArray) {
        String font = attributeArray.getString(R.styleable.CustomTextView_customFont);

        if (font != null && !font.equalsIgnoreCase("")) {
            switch (font) {
                case "Lato":
                    font = "Lato";
                    break;
                case "Roboto":
                    font = "Roboto";
                    break;
                default:
                    font = "Roboto";
                    break;
            }
        } else {
            font = "Roboto";
        }

        return font;
    }

    private Typeface selectTypeface(Context context, String fontName, int textStyle) throws Exception {
        if (fontName.contentEquals(context.getString(R.string.font_roboto))) {
            switch (textStyle) {
                case Typeface.BOLD: // bold
                    return TypefaceCache.getInstance().getTypeface(context.getApplicationContext(), "Roboto-Bold.ttf");
                case Typeface.ITALIC: // italic
                    return TypefaceCache.getInstance().getTypeface(context.getApplicationContext(), "Roboto-Italic.ttf");
                case Typeface.BOLD_ITALIC: // bold italic
                    return TypefaceCache.getInstance().getTypeface(context.getApplicationContext(), "Roboto-BoldItalic.ttf");
                case Typeface.NORMAL: // regular
                    return TypefaceCache.getInstance().getTypeface(context.getApplicationContext(), "Roboto-Regular.ttf");
                default:
                    return TypefaceCache.getInstance().getTypeface(context.getApplicationContext(), "Roboto-Regular.ttf");
            }
        } else {
            return TypefaceCache.getInstance().getTypeface(context.getApplicationContext(), "Roboto-Regular.ttf");
        }
    }

    private int getTextStyle(AttributeSet attrs) {
        String style = "0x0";
        try {
            style = attrs.getAttributeValue(ANDROID_SCHEMA, "textStyle");
        } catch (Exception e) {
            e.printStackTrace();
        }

        int textStyle = Typeface.NORMAL;
        if (style != null) {
            switch (style) {
                case "0x0":
                case "normal":
                    textStyle = Typeface.NORMAL;
                    break;
                case "0x1":
                case "bold":
                    textStyle = Typeface.BOLD;
                    break;
                case "0x2":
                case "italic":
                    textStyle = Typeface.ITALIC;
                    break;
                case "0x3":
                case "bold|italic":
                    textStyle = Typeface.BOLD_ITALIC;
                    break;
                default:
                    textStyle = Typeface.NORMAL;
                    break;

            }
        }
        return textStyle;

    }

}