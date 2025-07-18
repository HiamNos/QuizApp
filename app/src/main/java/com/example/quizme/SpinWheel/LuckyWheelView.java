package com.example.quizme.SpinWheel;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.quizme.R;
import com.example.quizme.SpinWheel.model.LuckyItem;

import java.util.List;

public class LuckyWheelView extends RelativeLayout implements PielView.PieRotateListener {
    private int mBackgroundColor;
    private int mTextColor;
    private int mTopTextSize;
    private int mSecondaryTextSize;
    private int mBorderColor;
    private int mTopTextPadding;
    private int mEdgeWidth;
    private Drawable mCenterImage;
    private Drawable mCursorImage;

    public PielView pielView;
    private ImageView ivCursorView;

    private LuckyRoundItemSelectedListener mLuckyRoundItemSelectedListener;

    public interface LuckyRoundItemSelectedListener {
        void LuckyRoundItemSelected(int index);
    }

    public void setLuckyRoundItemSelectedListener(LuckyRoundItemSelectedListener listener) {
        this.mLuckyRoundItemSelectedListener = listener;
    }

    public LuckyWheelView(Context context) {
        super(context);
        init(context, null);
    }

    public LuckyWheelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context ctx, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = ctx.obtainStyledAttributes(attrs, R.styleable.LuckyWheelView);
            mBackgroundColor = typedArray.getColor(R.styleable.LuckyWheelView_lkwBackgroundColor, 0xffcc0000);
            mTopTextSize = typedArray.getDimensionPixelSize(R.styleable.LuckyWheelView_lkwTopTextSize, (int) LuckyWheelUtils.convertDpToPixel(25f, getContext()));
            mSecondaryTextSize = typedArray.getDimensionPixelSize(R.styleable.LuckyWheelView_lkwSecondaryTextSize, (int) LuckyWheelUtils.convertDpToPixel(10f, getContext()));
            mTextColor = typedArray.getColor(R.styleable.LuckyWheelView_lkwTopTextColor, 0);
            mTopTextPadding = typedArray.getDimensionPixelSize(R.styleable.LuckyWheelView_lkwTopTextPadding, (int) LuckyWheelUtils.convertDpToPixel(22f, getContext())) + (int) LuckyWheelUtils.convertDpToPixel(15f, getContext());
            mCursorImage = typedArray.getDrawable(R.styleable.LuckyWheelView_lkwCursor);
            mCenterImage = typedArray.getDrawable(R.styleable.LuckyWheelView_lkwCenterImage);
            mEdgeWidth = typedArray.getInt(R.styleable.LuckyWheelView_lkwEdgeWidth, 10);
            mBorderColor = typedArray.getColor(R.styleable.LuckyWheelView_lkwEdgeColor, 0);
            typedArray.recycle();
        }

        LayoutInflater inflater = LayoutInflater.from(getContext());
        FrameLayout frameLayout = (FrameLayout) inflater.inflate(R.layout.lucky_wheel_layout, this, false);

        pielView = frameLayout.findViewById(R.id.pieView);
        ivCursorView = frameLayout.findViewById(R.id.cursorView);

        pielView.setPieRotateListener(this);
        pielView.setPieBackgroundColor(mBackgroundColor);
        pielView.setTopTextPadding(mTopTextPadding);
        pielView.setTopTextSize(mTopTextSize);
        pielView.setSecondaryTextSizeSize(mSecondaryTextSize);
        pielView.setPieCenterImage(mCenterImage);
        pielView.setBorderColor(mBorderColor);
        pielView.setBorderWidth(mEdgeWidth);

        if (mTextColor != 0)
            pielView.setPieTextColor(mTextColor);

        ivCursorView.setImageDrawable(mCursorImage);
        addView(frameLayout);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        for (int i = 0; i < getChildCount(); i++) {
            if (isPielView(getChildAt(i))) {
                return super.dispatchTouchEvent(ev);
            }
        }
        return false;
    }

    private boolean isPielView(View view) {
        if (view instanceof ViewGroup) {
            for (int i = 0; i < getChildCount(); i++) {
                if (isPielView(((ViewGroup) view).getChildAt(i))) {
                    return true;
                }
            }
        }
        return view instanceof PielView;
    }

    public void setData(List<LuckyItem> data) {
        pielView.setData(data);
    }

    public void setRound(int numberOfRound) {
        pielView.setRound(numberOfRound);
    }

    public void setPredeterminedNumber(int fixedNumber) {
        pielView.setPredeterminedNumber(fixedNumber);
    }

    public void startLuckyWheelWithTargetIndex(int index) {
        pielView.rotateTo(index);
    }

    @Override
    public void rotateDone(int index) {
        if (mLuckyRoundItemSelectedListener != null) {
            mLuckyRoundItemSelectedListener.LuckyRoundItemSelected(index);
        }
    }
}