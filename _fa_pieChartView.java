package com.fairytale110.mycustomviews.Widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import com.fairytale110.mycustomviews.Utils.ColorUtil;

import java.text.DecimalFormat;

/**
 * Created by   fairytale110
 * Creat date   2016/9/14 15:57
 * Copy Right   www.xkyiliao.com
 * function  可旋转 饼状图
 */
@SuppressWarnings("all")
public class _fa_pieChartView extends View implements ValueAnimator.AnimatorUpdateListener {

    private int ACTION_ROTATION = 1;//旋转Action
    private int ACTION_ENLARGE = 2;//放大半径action
    private int ACTION_ = ACTION_ROTATION;//当前Action
    private int width, height;//画布宽高
    private String TAG = getClass().getSimpleName();
    /**
     * 圆弧颜色
     * 注意，目前的问题是，不能有重复的颜色，不然无法确定唯一index
     */
    private String[] arcColors = new String[]{"#FD9033", "#F98AEC", "#FF3564", "#92BEFF", "#ABEA3D"};
    /**
     * 默认圆弧颜色
     */
    private String defaultColor = "#A9A9A9";
    private String tittleStr = "总支出";
    private String totalPrice = "";
    /**
     * 像素密度、圆弧厚度，要旋转的角度、放大系数(最大0.5F)、数据和、字体大小
     */
    private float density, charRi, degree, gain, dataSum, tittleSize, priceSize;
    private long DURATION = 1000;//动画时长 毫秒
    private long DURATION_GAIN = 2000;//动画时长 毫秒
    private Float[] datas = {};

    private Bitmap bitmap;
    RectF rectF, rectFSelected;
    private Paint txtPaint;//文本画笔
    private Paint paintArc;//圆弧画笔
    private ValueAnimator valueAnimator;
    private OnItemChangedListener mOnItemChangedListener;

    public _fa_pieChartView(Context context) {
        super(context);
        init();
    }

    public _fa_pieChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public _fa_pieChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        this.density = getResources().getDisplayMetrics().density;

        this.paintArc = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.paintArc.setStyle(Paint.Style.STROKE);
        this.paintArc.setAntiAlias(true);
        this.paintArc.setColor(Color.RED);

        this.txtPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.txtPaint.setAntiAlias(true);
        this.txtPaint.setTextSize(12 * this.density);
        this.txtPaint.setTextAlign(Paint.Align.CENTER);
        this.txtPaint.setColor(Color.GRAY);

        DecimalFormat decimalFomat = new DecimalFormat("##0.00");
        this.totalPrice = decimalFomat.format(this.dataSum);

        if (arcColors.length < datas.length) {
            throw new IllegalArgumentException("颜色数组长度 不能小于 数据数组长度");
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        width = MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft() - getPaddingRight();
        height = MeasureSpec.getSize(heightMeasureSpec) - getPaddingTop() - getPaddingBottom();
        if (height > width) {
            height = width;
        } else {
            width = height;
        }
        this.charRi = width / 6;
        this.paintArc.setStrokeWidth(charRi);
        rectF = new RectF();
        rectF.left = -width / 2 + charRi / 1.5f;
        rectF.right = width / 2 - charRi / 1.5f;
        rectF.top = -width / 2 + charRi / 1.5f;
        rectF.bottom = width / 2 - charRi / 1.5f;

        // 半径变小 位置下移，角度不变
        rectFSelected = new RectF();
        rectFSelected.left = -width / 2 + charRi / 1.5f;
        rectFSelected.right = width / 2 - charRi / 1.5f;
        rectFSelected.top = -width / 2 + charRi / 1.5f;
        rectFSelected.bottom = width / 2 - charRi / 1.5f;

        this.tittleSize = 1.5F * (width - charRi * 2) / (6 * tittleStr.length());
        this.priceSize = tittleSize*1.5F;
        setMeasuredDimension(width, height);
    }

    private float startAngleTemp;

    private void drawBackg(Canvas canvas) {

        if (this.datas == null || this.dataSum == 0F) {
            paintArc.setColor(Color.parseColor(defaultColor));
            canvas.drawArc(rectF, 0F, 360F, false, paintArc);
            return;
        }

        for (int i = 0; i < datas.length; i++) {
            canvas.save();
            paintArc.setColor(Color.parseColor(arcColors[i]));
            startAngleTemp = 90F - 180F * datas[0] / dataSum;

            if (i != 0) {
                for (int j = 0; j < i; j++) {
                    startAngleTemp += 360F * datas[j] / dataSum;
                }
            }
            if (this.ACTION_ == this.ACTION_ROTATION) {

                if (curColorIndex == i && degree == degreeT && datas.length > 1) {
                    if (mOnItemChangedListener != null)
                        this.mOnItemChangedListener.onItemChanged(i, datas[i]);
                    try {
                        Thread.sleep(15);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    gain(0.5f);
                }
                canvas.drawArc(rectF, startAngleTemp - 1.5f, 360F * datas[i] / dataSum + 1.5f, false, paintArc);
            } else if (this.ACTION_ == this.ACTION_ENLARGE) {
                if (curColorIndex == i && datas.length > 1) {
                    setRectFSelected();
                    canvas.drawArc(rectFSelected, startAngleTemp + 2f, 360F * datas[i] / dataSum - 5.5f, false, paintArc);
                } else {
                    canvas.drawArc(rectF, startAngleTemp - 1.5f, 360F * datas[i] / dataSum + 1.5f, false, paintArc);
                }
            }
            canvas.restore();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawText(tittleStr, width / 2 - tittleStr.length() / 2, width / 2 - tittleSize/1.5F , txtPaint);
        txtPaint.setTextSize(priceSize);
        txtPaint.setFakeBoldText(true);
        txtPaint.setColor(Color.BLACK);
        canvas.drawText(totalPrice, width / 2 - tittleStr.length() / 2, width / 2 + priceSize/1.5F, txtPaint);
        txtPaint.setTextSize(tittleSize);
        txtPaint.setFakeBoldText(false);
        txtPaint.setColor(Color.GRAY);

        canvas.translate(width / 2, width / 2);
        canvas.rotate(degree);

        drawBackg(canvas);
    }

    private float degreeT = 0;
    private float curtX, curtY;
    private float offsetDegree = 90f;           //初始偏移量
    private float curIndexdegree = 0f;          //点击的色块角度
    private float curIndexOffsetdegree = 0f;    //点击的色块中线 偏移角度
    private int curColorIndex = 0;              //点击的色块 index

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                curtX = event.getX();
                curtY = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                if (this.datas == null || this.dataSum == 0F) {
                    break;
                }
                setDrawingCacheEnabled(true);
                bitmap = getDrawingCache();
                if (bitmap == null) {
                    Log.w(TAG, "bitmap == null");
                    break;
                }
                int pixel = bitmap.getPixel((int) curtX, (int) curtY);
                setDrawingCacheEnabled(false);
                //获取颜色RGB
                int redValue = Color.red(pixel);
                int blueValue = Color.blue(pixel);
                int greenValue = Color.green(pixel);
                int tarIndex = 0;
                String curColorHex = ColorUtil.toHex(redValue, greenValue, blueValue);

                for (int i = 0; i < arcColors.length; i++) {
                    if (curColorHex.equals(arcColors[i])) {
                        tarIndex = i;
                        break;
                    }
                }
                if (curColorIndex == tarIndex) {
                    break;
                }
                curColorIndex = tarIndex;
                curIndexdegree = 360 * datas[curColorIndex] / dataSum;

                if (curColorIndex == 0) {
                    curIndexOffsetdegree = 90;
                } else {
                    curIndexOffsetdegree = 90;
                    for (int j = 0; j < curColorIndex; j++) {
                        if (j == 0) {
                            curIndexOffsetdegree += 180 * datas[j] / dataSum;
                        } else {
                            curIndexOffsetdegree += 360 * datas[j] / dataSum;
                        }
                    }
                    curIndexOffsetdegree += curIndexdegree / 2;
                }
                degreeT += offsetDegree - curIndexOffsetdegree;
                offsetDegree = curIndexOffsetdegree;
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                rotation(degreeT);
                break;
        }
        return true;
    }

    @Override
    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        if (this.ACTION_ == this.ACTION_ROTATION) {
            degree = Float.valueOf(valueAnimator.getAnimatedValue().toString());
        } else if (this.ACTION_ == this.ACTION_ENLARGE) {
            gain = Float.valueOf(valueAnimator.getAnimatedValue().toString());
        }
        invalidate();
    }

    private void animateToValue(float value) {
        if (this.ACTION_ == this.ACTION_ROTATION) {
            if (valueAnimator == null) {
                valueAnimator = createAnimator(value);
            }
            valueAnimator.setFloatValues(this.degree, value);
            valueAnimator.setDuration(DURATION);
        } else if (this.ACTION_ == this.ACTION_ENLARGE) {
            valueAnimator = createAnimator(value);
            valueAnimator.setFloatValues(this.gain, value);
            valueAnimator.setDuration(DURATION_GAIN);
        }
        if (valueAnimator.isRunning()) {
            valueAnimator.cancel();
        }
        valueAnimator.start();
    }

    private ValueAnimator createAnimator(float value) {
        ValueAnimator valueAnimator = null;
        if (this.ACTION_ == this.ACTION_ROTATION) {
            valueAnimator = ValueAnimator.ofFloat(this.degree, value);
            valueAnimator.setDuration(DURATION);
        } else if (this.ACTION_ == this.ACTION_ENLARGE) {
            valueAnimator = ValueAnimator.ofFloat(this.gain, value);
            valueAnimator.setDuration(DURATION_GAIN);
        }
        valueAnimator.setInterpolator(new OvershootInterpolator());
        valueAnimator.addUpdateListener(this);
        return valueAnimator;
    }


    private void rotation(float degree) {
        this.ACTION_ = this.ACTION_ROTATION;
        if (this.degree != degree) {
            animateToValue(degree);
        }
    }

    private void gain(float gain) {
        this.ACTION_ = this.ACTION_ENLARGE;
        animateToValue(gain);
    }

    private void setRectFSelected() {
        rectFSelected.left = -width / 2 + charRi / (1.5F + gain);
        rectFSelected.right = width / 2 - charRi / (1.5F + gain);
        rectFSelected.top = -width / 2 + charRi / (1.5F + gain);
        rectFSelected.bottom = width / 2 - charRi / (1.5F + gain);
    }

    public void setDatas(Float[] dataFlo) {
        if (dataFlo == null)
            return;
        if (dataFlo != null && dataFlo.length < 1)
            return;
        this.datas = dataFlo;

        if (arcColors.length < datas.length) {
            throw new IllegalArgumentException("颜色数组长度 不能小于 数据数组长度");
        }

        this.dataSum = 0f;
        for (float dataitem :
                dataFlo) {
            this.dataSum += dataitem;
        }

        DecimalFormat decimalFomat = new DecimalFormat(".00");
        this.totalPrice = decimalFomat.format(this.dataSum);
        invalidate();
    }


    public void setOnItemChangedListener(OnItemChangedListener listener) {
        this.mOnItemChangedListener = listener;
    }

    public interface OnItemChangedListener {
        void onItemChanged(int index, float value);
    }
}
