package dummydata.android.spinWheel;

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

import dummydata.android.R;

import java.util.List;

public class SpinWheelView extends RelativeLayout implements PielView.PieRotateListener {
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

    private SpinRoundItemSelectedListener mSpinRoundItemSelectedListener;

    @Override
    public void rotateDone(int index) {
        if (mSpinRoundItemSelectedListener != null) {
            mSpinRoundItemSelectedListener.LuckyRoundItemSelected(index);
        }
    }

    public interface SpinRoundItemSelectedListener {
        void LuckyRoundItemSelected(int index);
    }

    public void setSpinRoundItemSelectedListener(SpinRoundItemSelectedListener listener) {
        this.mSpinRoundItemSelectedListener = listener;
    }

    public SpinWheelView(Context context) {
        super(context);
        init(context, null);
    }

    public SpinWheelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context ctx, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = ctx.obtainStyledAttributes(attrs, R.styleable.SpinWheelView);
            mBackgroundColor = typedArray.getColor(R.styleable.SpinWheelView_swBackgroundColor, 0xffcc0000);
            mTopTextSize = typedArray.getDimensionPixelSize(R.styleable.SpinWheelView_swTopTextSize, (int) SpinWheelUtils.convertDpToPixel(25f, getContext()));
            mSecondaryTextSize = typedArray.getDimensionPixelSize(R.styleable.SpinWheelView_swSecondaryTextSize, (int) SpinWheelUtils.convertDpToPixel(10f, getContext()));
            mTextColor = typedArray.getColor(R.styleable.SpinWheelView_swTopTextColor, 0);
            mTopTextPadding = typedArray.getDimensionPixelSize(R.styleable.SpinWheelView_swTopTextPadding, (int) SpinWheelUtils.convertDpToPixel(35f, getContext())) + (int) SpinWheelUtils.convertDpToPixel(20f, getContext());
            mCursorImage = typedArray.getDrawable(R.styleable.SpinWheelView_swCursor);
            mCenterImage = typedArray.getDrawable(R.styleable.SpinWheelView_swCenterImage);
            mEdgeWidth = typedArray.getInt(R.styleable.SpinWheelView_swEdgeWidth, 10);
            mBorderColor = typedArray.getColor(R.styleable.SpinWheelView_swEdgeColor, 0);
            typedArray.recycle();
        }

        LayoutInflater inflater = LayoutInflater.from(getContext());
        FrameLayout frameLayout = (FrameLayout) inflater.inflate(R.layout.layout_spin_wheel, this, false);

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
        //This is to control that the touch events triggered are only going to the PieView
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

    public void setLuckyWheelBackgrouldColor(int color) {
        pielView.setPieBackgroundColor(color);
    }

    public void setLuckyWheelCursorImage(int drawable) {
        ivCursorView.setBackgroundResource(drawable);
    }

    public void setLuckyWheelCenterImage(Drawable drawable) {
        pielView.setPieCenterImage(drawable);
    }

    public void setBorderColor(int color) {
        pielView.setBorderColor(color);
    }

    public void setLuckyWheelTextColor(int color) {
        pielView.setPieTextColor(color);
    }

    public void setData(List<WheelItem> data) {
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
}
