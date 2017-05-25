package com.elliott.a18350.irecognizer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by 18350 on 2017/5/24 0024.
 */

public class MyButton extends android.support.v7.widget.AppCompatButton {

//This constructormust be

        public MyButton(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public MyButton(Context context) {
            super(context);
        }

        private Paint mPaint = null;
        private String mText;
        private int mX, mY;

        public void onSetText(String text, int nLeft, int nBottom, int nTextSize,
                              int nTextColor) {
            mPaint = new Paint();
            mPaint.setTextSize(nTextSize);
            mPaint.setColor(nTextColor);
            this.mText = text;
            this.mX = nLeft;
            this.mY = nBottom;
        }

        private int mDownBmpId, mUpBmpId;

        public void onSetBmp(int nDownID, int nUpID) {
            this.mDownBmpId = nDownID;
            this.mUpBmpId = nDownID;
        }

        @Override
        public void onDraw(Canvas canvas) {

            if (mPaint != null)
                canvas.drawText(mText, mX, mY, mPaint);
            super.onDraw(canvas);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
                super.setBackgroundResource(mDownBmpId);
            return super.onTouchEvent(event);
            //修改了这个东西，让他按下去不会变
        }
}

