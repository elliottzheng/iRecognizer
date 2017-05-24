package com.elliott.a18350.mylearning;

/**
 * Created by 18350 on 2017/5/24 0024.
 */

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * 添加文字限制的输入控件
 */
public class InputText extends android.support.v7.widget.AppCompatEditText{
    /**
     * 输入模式
     */
    public enum InputMode {
        NONE(0),
        INTEGER(1),
        ASCII(2),
        BMP0(3);
        int value;

        InputMode(int value) {
            this.value = value;
        }

        static public InputMode getInputMode(int value) {
            if (value == INTEGER.getValue()) {
                return INTEGER;
            }
            if (value == ASCII.getValue()) {
                return ASCII;
            }
            if (value == BMP0.getValue()) {
                return BMP0;
            }
            return NONE;
        }

        public int getValue() {
            return value;
        }
    }

    //文本变化通知，给自动清空按钮使用
    TextWatcher textWatcherForClear = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            refreshAutoClearImg();
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
    //获取是否当前组件具有焦点
    OnFocusChangeListener onFocusChangeListenerForClear = new OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            refreshAutoClearImg();
        }
    };

    //当前输入状态
    InputMode inputMode;
    //当前状态是否允许输入空字符串
    boolean inputEmpty;
    //当前最大输入字符数量, 如果为-1，则表示不需要进行限制
    int maxLength;
    //自动清除文本输入的ICON RES ID, 如果为0，则表示不显示清空ICON
    int autoClearImg;

    public InputText(Context context) {
        this(context, null);
    }

    public InputText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    /**
     * 初始化属性
     */
    private void init(AttributeSet attrs) {
        //初始化基本过滤链
        {
            setFilters(new InputFilter[]{new InputFilter() {
                @Override
                public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                    //过滤字符集类型
                    {
                        switch (inputMode) {
                            case NONE: {
                            }
                            break;
                            case INTEGER: {
                                if (!source.toString().matches("^\\d*$")) {
                                    return "";
                                }
                            }
                            break;
                            case ASCII: {
                                for (int i = start; i < end; ++i) {
                                    if (source.charAt(i) < 0 || source.charAt(i) > 128) {
                                        return "";
                                    }
                                }
                            }
                            break;
                            case BMP0: {
                                for (int i = start; i < end; ++i) {
                                    if (Character.isHighSurrogate(source.charAt(i)))
                                        return "";
                                }
                            }
                            break;
                        }
                    }
                    //是否允许空字符
                    {
                        if (!inputEmpty) {
                            if (!source.toString().matches("^\\S*$")) {
                                return "";
                            }
                        }
                    }
                    //最大长度检测
                    {
                        if (maxLength != -1 && maxLength > 0) {
                            int destLength = 0;
                            for (int i = 0; i < dest.length(); ++i) {
                                if (dest.charAt(i) < 128)
                                    destLength += 1;
                                else
                                    destLength += 2;
                            }
                            int srcLength = 0;
                            for (int i = 0; i < source.length(); ++i) {
                                if (source.charAt(i) < 128)
                                    srcLength += 1;
                                else
                                    srcLength += 2;
                            }
                            int keep = maxLength - (destLength - (dend - dstart));
                            if (keep <= 0) {
                                return "";
                            } else if (keep < srcLength) {
                                int pos = start;
                                for (; pos < end; ++pos) {
                                    if (source.charAt(pos) < 128) {
                                        if (keep - 1 >= 0) {
                                            keep -= 1;
                                        } else {
                                            break;
                                        }
                                    } else {
                                        if (keep - 2 >= 0) {
                                            keep -= 2;
                                        } else {
                                            break;
                                        }
                                    }
                                }
                                return source.subSequence(start, pos);
                            }
                        }
                    }
                    return null;
                }
            }});
        }
        //注册监听器
        {
            //焦点监听器
            setOnFocusChangeListener(onFocusChangeListenerForClear);
            //文本变化监听器
            addTextChangedListener(textWatcherForClear);
        }
        //读取配置文件
        InputMode inputMode = InputMode.NONE;
        boolean inputEmpty = true;
        int maxLength = -1;
        int autoClearImgRedId = 0;

        if (attrs != null) {
            //读取自定义属性
            TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.input_text);
            //输入模式
            inputMode = InputMode.getInputMode(ta.getInt(R.styleable.input_text_input_mode, InputMode.NONE.getValue()));
            //是否允许输入空格
            inputEmpty = ta.getBoolean(R.styleable.input_text_input_empty, true);
            //最大输入长度
            maxLength = ta.getInt(R.styleable.input_text_max_length, -1);
            //自动清除内容ICON
            autoClearImgRedId = ta.getResourceId(R.styleable.input_text_auto_clear_img, 0);
            ta.recycle();
        }
        //设置属性
        setInputMode(inputMode);
        setInputEmpty(inputEmpty);
        setMaxLength(maxLength);
        setAutoClearImgRedId(autoClearImgRedId);
    }

    /**
     * 设置输入文字的模式
     *
     * @param inputMode 输入的模式
     */
    public void setInputMode(InputMode inputMode) {
        this.inputMode = inputMode;
    }

    /**
     * 设置是否允许输入空格之类的符号
     *
     * @param inputEmpty 是否允许输入空格
     */
    public void setInputEmpty(boolean inputEmpty) {
        this.inputEmpty = inputEmpty;
    }

    /**
     * 设置最大输入字符数量
     *
     * @param maxLength 最大字符数量，如果为-1，则表示不限制
     */
    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    /**
     * 设置自动清除图片资源
     *
     * @param autoClearImg 如果为0，则表示不现实该ICON
     */
    public void setAutoClearImgRedId(int autoClearImg) {
        this.autoClearImg = autoClearImg > 0 ? autoClearImg : 0;
        refreshAutoClearImg();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            //具有图片的时候，才能进行清空
            if (getCompoundDrawables()[2] != null) {
                int x = (int) event.getX();
                int y = (int) event.getY();
                boolean isInnerWidth = x > (getWidth() - getTotalPaddingRight() - 10) && x < (getWidth() - getPaddingRight() + 10);
                boolean isInnerHeight = y > 0 && y < getHeight();
                if (isInnerWidth && isInnerHeight) {
                    this.setText("");
                }
            }
        }
        return super.onTouchEvent(event);
    }

    /**
     * 发生事件后，自动检测是否显示 自动清空ICON
     */
    private void refreshAutoClearImg() {
        if (autoClearImg != 0 && getText().length() > 0 && isFocused()) {
            //打开 ICON
            Drawable drawable = getContext().getResources().getDrawable(autoClearImg);
            // 调用setCompoundDrawables时，必须调用Drawable.setBounds()方法,否则图片不显示
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            setCompoundDrawables(getCompoundDrawables()[0], getCompoundDrawables()[1], drawable, getCompoundDrawables()[3]);
        } else {
            //清空 ICON
            setCompoundDrawables(getCompoundDrawables()[0], getCompoundDrawables()[1], null, getCompoundDrawables()[3]);
        }
    }
}