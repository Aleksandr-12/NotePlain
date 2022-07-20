package com.writesimple.simplenote.model;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.writesimple.simplenote.R;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.ViewModel;

public class WorkFontManager extends ViewModel {

    @SuppressLint("StaticFieldLeak")
    private final Context context;
    private Boolean isIt;
    private Boolean isBold;
    private Boolean isUnd;
    private int mBold;
    private int mIsItalic;
    private int mUnderline;
    @SuppressLint("StaticFieldLeak")
    private ImageButton bold;
    @SuppressLint("StaticFieldLeak")
    private ImageButton italic;
    @SuppressLint("StaticFieldLeak")
    private ImageButton underline;
    private int ColorBakcgroundForButtonActive;
    @SuppressLint("StaticFieldLeak")
    private EditText title;
    @SuppressLint("StaticFieldLeak")
    private EditText content;
    @SuppressLint("StaticFieldLeak")
    private TextView titleIdTextView;
    @SuppressLint("StaticFieldLeak")
    private TextView contentIdTextView;
    private String fontFamily;
    private int colorBackground;
    private String stringBold;

    private String contentText;
    @SuppressLint("StaticFieldLeak")
    private EditText contentEditText;


    public int getmFontSizeTitle() {
        return mFontSizeTitle;
    }

    public void setmFontSizeTitle(int mFontSizeTitle) {
        this.mFontSizeTitle = mFontSizeTitle;
    }

    public int getmFontSizeContent() {
        return mFontSizeContent;
    }

    public void setContentText(String contentText) {
        this.contentText = contentText;
    }

    public void setmFontSizeContent(int mFontSizeContent) {
        this.mFontSizeContent = mFontSizeContent;
    }

    private int mFontSizeTitle;
    private int mFontSizeContent;

    public void setFontFamily(String fontFamily) {
        this.fontFamily = fontFamily;
    }

    public WorkFontManager(Context context, ImageButton[] idFont,EditText editText){
        this.context = context;
        bold = idFont[0];
        italic = idFont[1];
        underline = idFont[2];
        contentEditText = editText;
   }

    public WorkFontManager(Context context){
        this.context = context;
    }

    public int getmBold() {
        return mBold;
    }

    public void setmBold(int mBold) {
        this.mBold = mBold;
    }

    public int getmIsItalic() {
        return mIsItalic;
    }

    public void setmIsItalic(int mIsItalic) {
        this.mIsItalic = mIsItalic;
    }

    public int getmUnderline() {
        return mUnderline;
    }

    public void setmUnderline(int mUnderline) {
        this.mUnderline = mUnderline;
    }

    private void setColorBakcgroundForButtonActive(ImageButton event, int color){
        GradientDrawable gdDefault = new GradientDrawable();
        gdDefault.setColor(color);
        gdDefault.setCornerRadius(4);
        gdDefault.setStroke(3, Color.BLACK);
        event.setBackgroundDrawable(gdDefault);

    }
    public void setBorderRadiusForEditText(EditText event){
        GradientDrawable gdDefault = new GradientDrawable();
        gdDefault.setColor(Color.WHITE);
        gdDefault.setCornerRadius(10);
        gdDefault.setStroke(1, Color.WHITE);
        event.setBackgroundDrawable(gdDefault);

    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void setColorBackroundForNote(View id){
        int[] bgColor = context.getResources().getIntArray(R.array.dataColor);

        View view = new View(context.getApplicationContext());
        view.setBackgroundColor(bgColor[colorBackground]);

        id.setBackground(view.getBackground());
    }

    private void setColorBakcgroundForButtonNoActive(ImageButton event){
        event.setBackgroundColor(Color.TRANSPARENT);
    }

    public void setColorBakcgroundForButtonActive(int colorBakcgroundForButtonActive) {
        ColorBakcgroundForButtonActive = colorBakcgroundForButtonActive;
    }

    public void exangeStyleFont(String text){
        contentEditText.setText(exangeParamsStyleFont(text,true));
    }

    public void exangeStyleFontForTextView(String text, TextView value){
        value.setText(exangeParamsStyleFont(text,false));
    }

    private SpannableString exangeParamsStyleFont(String text, Boolean background) {
        SpannableString spanString = new SpannableString(text);
        if(mBold==1 && mIsItalic== 1 && mUnderline==1){
            spanString.setSpan(new UnderlineSpan(), 0, spanString.length(), 0);
            spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString.length(), 0);
            spanString.setSpan(new StyleSpan(Typeface.ITALIC), 0, spanString.length(), 0);
            if(background){
                setColorBakcgroundForButtonActive(bold,ColorBakcgroundForButtonActive);
                setColorBakcgroundForButtonActive(italic,ColorBakcgroundForButtonActive);
                setColorBakcgroundForButtonActive(underline,ColorBakcgroundForButtonActive);
            }
        }
        if(mBold==1 && mIsItalic== 0 && mUnderline==0){
            spanString.setSpan(new UnderlineSpan(), 0, 0, 0);
            spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString.length(), 0);
            spanString.setSpan(new StyleSpan(Typeface.NORMAL), 0, spanString.length(), 0);
            if(background){
                setColorBakcgroundForButtonActive(bold,ColorBakcgroundForButtonActive);
                setColorBakcgroundForButtonNoActive(italic);
                setColorBakcgroundForButtonNoActive(underline);
            }
         }
        if(mBold==1 && mIsItalic== 0 && mUnderline==1){
            spanString.setSpan(new UnderlineSpan(), 0, spanString.length(), 0);
            spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString.length(), 0);
            spanString.setSpan(new StyleSpan(Typeface.NORMAL), 0, spanString.length(), 0);
            if(background){
                setColorBakcgroundForButtonActive(bold,ColorBakcgroundForButtonActive);
                setColorBakcgroundForButtonNoActive(italic);
                setColorBakcgroundForButtonActive(underline,ColorBakcgroundForButtonActive);
            }
         }
        if(mBold==0 && mIsItalic== 1 && mUnderline==0){
            spanString.setSpan(new UnderlineSpan(), 0, 0, 0);
            spanString.setSpan(new StyleSpan(Typeface.NORMAL), 0, spanString.length(), 0);
            spanString.setSpan(new StyleSpan(Typeface.ITALIC), 0, spanString.length(), 0);
            if(background){
                setColorBakcgroundForButtonNoActive(bold);
                setColorBakcgroundForButtonActive(italic,ColorBakcgroundForButtonActive);
                setColorBakcgroundForButtonNoActive(underline);
            }
        }
        if(mBold==0 && mIsItalic== 1 && mUnderline==1){
            spanString.setSpan(new UnderlineSpan(), 0, spanString.length(), 0);
            spanString.setSpan(new StyleSpan(Typeface.NORMAL), 0, spanString.length(), 0);
            spanString.setSpan(new StyleSpan(Typeface.ITALIC), 0, spanString.length(), 0);
            if(background){
                setColorBakcgroundForButtonNoActive(bold);
                setColorBakcgroundForButtonActive(italic,ColorBakcgroundForButtonActive);
                setColorBakcgroundForButtonActive(underline,ColorBakcgroundForButtonActive);
            }
        }
        if(mBold==0 && mIsItalic== 0 && mUnderline==1){
            spanString.setSpan(new UnderlineSpan(), 0, spanString.length(), 0);
            spanString.setSpan(new StyleSpan(Typeface.NORMAL), 0, spanString.length(), 0);
            spanString.setSpan(new StyleSpan(Typeface.NORMAL), 0, spanString.length(), 0);
            if(background){
                setColorBakcgroundForButtonNoActive(bold);
                setColorBakcgroundForButtonNoActive(italic);
                setColorBakcgroundForButtonActive(underline,ColorBakcgroundForButtonActive);
            }
        }
        if(mBold==1 && mIsItalic== 1 && mUnderline==0){
            spanString.setSpan(new UnderlineSpan(), 0, 0, 0);
            spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString.length(), 0);
            spanString.setSpan(new StyleSpan(Typeface.ITALIC), 0, spanString.length(), 0);
            if(background){
                setColorBakcgroundForButtonActive(bold,ColorBakcgroundForButtonActive);
                setColorBakcgroundForButtonActive(italic,ColorBakcgroundForButtonActive);
                setColorBakcgroundForButtonNoActive(underline);
            }
        }
        if(mBold==0 && mIsItalic== 0 && mUnderline==0){
             spanString.setSpan(new UnderlineSpan(), 0, 0, 0);
            spanString.setSpan(new StyleSpan(Typeface.NORMAL), 0, spanString.length(), 0);
            spanString.setSpan(new StyleSpan(Typeface.NORMAL), 0, spanString.length(), 0);
            if(background){
                setColorBakcgroundForButtonNoActive(bold);
                setColorBakcgroundForButtonNoActive(italic);
                setColorBakcgroundForButtonNoActive(underline);
            }
        }
        return spanString;
    }

    public TextWatcher onTextChangedListener(EditText value) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s) {
                   int selection = value.getSelectionStart();
                    value.removeTextChangedListener(this);
                    String string = s.toString();
                    exangeStyleFont(string);
                   // value.setText(spanString);
                    value.addTextChangedListener(this);
                    value.setSelection(selection);
            }
        };
    }

    public void setTitleIdTextView(TextView titleIdTextView) {
        this.titleIdTextView = titleIdTextView;
    }

    public void setContentIdTextView(TextView contentIdTextView) {
        this.contentIdTextView = contentIdTextView;
    }
    public void setText(String text) {
        this.contentText = text;
    }

    public void initVarFormateFont(String value, String text) {
         if ("italic".equals(value)) {
            if(mIsItalic == 1){
                mIsItalic = 0;
            }else{
                mIsItalic = 1;

            }
        }
        if ("bold".equals(value)) {
            if(mBold == 1){
                mBold = 0;
             }else{
                mBold = 1;
            }
        }
        if ("underline".equals(value)) {
            if(mUnderline==1){
                mUnderline = 0;
              }else{
                mUnderline = 1;
           }
        }
        exangeStyleFont(text);
    }

    public EditText getIdTitle() {
        return title;
    }

    public void setIdTitle(EditText title) {
        this.title = title;
    }

    public void setIdContent(EditText content) {
        this.content = content;
    }

    public String getFontFamily() {
        return fontFamily;
    }

    public void setTextSizeFont(int mSizeTitle, int mSixeContent) {
        if(mSizeTitle==0){
            if(titleIdTextView!=null){
                titleIdTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
            }
            if(title!=null){
                title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
            }
        }else{
            if(titleIdTextView!=null){
                titleIdTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, mSizeTitle);
            }
            if(title!=null){
                title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, mSizeTitle);
            }
        }
        if(mSixeContent==0){
            if(content!=null){
                content.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
            }
            if(contentIdTextView!=null){
                contentIdTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
            }
        }else{
            if(content!=null){
                content.setTextSize(TypedValue.COMPLEX_UNIT_DIP, mSixeContent);
            }
            if(contentIdTextView!=null){
                contentIdTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, mSixeContent);
            }
        }
    }

    public void setNoteFontFamily() {
       // System.out.println(mFont);
        Typeface font;
        Typeface font1;
        if(fontFamily == null){
            font = Typeface.createFromAsset(context.getAssets(), "fonts/open_sans.ttf");
            if(titleIdTextView!=null){
                titleIdTextView.setTypeface(font);
            }
            if(contentIdTextView!=null){
                contentIdTextView.setTypeface(font);
            }
            if(title!=null){
                title.setTypeface(font);
            }
            if(content!=null){
                content.setTypeface(font);
            }
        }else{
            font = Typeface.createFromAsset(context.getAssets(), "fonts/"+fontFamily+".ttf");
            if(titleIdTextView!=null){
                titleIdTextView.setTypeface(font);
            }
            if(contentIdTextView!=null){
                contentIdTextView.setTypeface(font);
            }
            if(title!=null){
                title.setTypeface(font);
            }
            if(content!=null){
                content.setTypeface(font);
            }
        }

    }

    private void addWordToStyle(int selection, String[] aLlWords) {
        int start=0;
        int end=0;
        for(String b: aLlWords) {
            for (int i = -1; (i = contentEditText.getText().toString().indexOf( " "+b+" ", i + 1)) != -1; i++) {
                start=i+1;
                end = start+b.length()+1;
                if ((start <= selection) && (end >= selection)) {
                    stringBold = stringBold + ","+start+"-"+end;
                }
            }

        }
    }
}


