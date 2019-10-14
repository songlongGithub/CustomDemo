package com.sl.customdemo.dial;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import androidx.annotation.Nullable;

import java.math.BigDecimal;


/**
 * Created by sl on 2018/3/5.
 */

public class WeightView extends View {

    private static final String TAG = "WeightView";
    private Paint mPaint;
    private int mWidth;
    private int mHeight;
    private float outerArcWidth;
    private RectF rectF;
    private int mRingWidth;
    private int blueStart;
    private int blueEnd;
    private int greenStart;
    private int greenEnd;
    private int redStart;
    private int redEnd;
    private float radius;
    private float[] circlePos;
    private float[] tan;
    private RectF innerArea;
    private RectF outerArea;
    private int innerRadius;
    private int space;
    //    private int[] blueSweep;
//    private int[] greenSweep;
//    private int[] redSweep;
    /**
     * 底部空白角度
     */
    private float mBottomBlankAngle = 90f;
    /**
     *
     */
    private float mOtherBlankAngle = 12f;
    private float sweepAngle;

    private float everyAngle;
    private float startAngle;
    private Path orbit;
    private int defaultDuration = 2000;
    private ValueAnimator mValueAnimator;
    private float endAngle = 358;
    private int mBitmapHeight;
    private int defalutColor;
    private int alpha = 1;
    private Paint mPathPaint;
    private int linearGradientUtilColor;
    private float value = -1f;
    private float mBenchmarkL;
    private float mBenchmarkH;
    private Paint bitmapPaint;
    private float mCalculateResultAngle;
    private int redEnd00;
    private int blueEnd0;
    private int greenEnd0;
    private Typeface impact;
    private TextPaint centerPaint;
    private int centerTvColor;
    private int centerLeftHeight;
    private TextPaint centerLablePaint;
    private int centerLableHeight;
    private int centerMargin;
    private TextPaint textPaint;
    private float topTextSize;
    private float bottomTextSize;
    private String weightStr;
    private String mBottomText;
    private int weightStateCode = 0;
    private int stateHightColor;
    private int stateNormalColor;
    private int stateLowColor;
    private boolean mDrawBottom;
    private float defalutweight;
    private float maxAngle;
    private float maxWeight;
    private String unit = "kg";
    private float[] positions;
    private SweepGradient sweepGradient;
    private float mOutsideMargin;

    private int[] colors;
    private SweepGradient mBgSweepGradient;
    private SweepGradient mBgSweepGradient1;
    private SweepGradient mBgSweepGradient2;
    private SweepGradient mBgSweepGradient3;
    private PathMeasure mPathMeasure;
    private float[] linePos;
    private Path outerOrbit;
    private SweepGradient mOrbitSweepGradient;
    private int baseCenterY;
    private String weightStrValue;
    private int baseTopY;
    private int baseBottomY;

    public WeightView(Context context) {
        this(context, null);
    }

    public WeightView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WeightView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void setWeight(float value, float benchmarkL, float benchmarkH, String bottomText) {
        this.mBottomText = bottomText;
        this.value = defalutweight = value;
        this.mBenchmarkL = benchmarkL;
        this.mBenchmarkH = benchmarkH;
        mDrawBottom = false;
        maxWeight = 150;
        mCalculateResultAngle = calculateAngle(value, benchmarkL, benchmarkH);
        if (mCalculateResultAngle <= everyAngle) {
            linearGradientUtilColor = blueEnd;
        } else if (mCalculateResultAngle <= everyAngle * 2 + mOtherBlankAngle) {
            linearGradientUtilColor = greenEnd;
        } else {
            linearGradientUtilColor = redEnd;
        }
        if (mValueAnimator == null) {
            initAnimator();
        }
        if (mValueAnimator.isRunning()) {
            mValueAnimator.cancel();
        }
        mValueAnimator.start();


    }


    public void cancle() {
        if (mValueAnimator != null && mValueAnimator.isRunning()) {
            mValueAnimator.cancel();
        }
    }

    private float calculateAngle(float value, float low, float high) {
        float endAngle;
        if (value < low) {
            weightStateCode = 2;
            endAngle = everyAngle * ((value / low) > 1 ? 1 : (value / low));
        } else if (value >= high) {
            weightStateCode = 1;
            float benchmark = ((value - high) / (maxWeight - high)) * everyAngle;
            endAngle = everyAngle * 2 + mOtherBlankAngle * 2 + benchmark;
        } else {
            weightStateCode = 0;
            float benchmark = (value - low) / (high - low);
            endAngle = everyAngle + mOtherBlankAngle * 1 + everyAngle * (benchmark > 1 ? 1 : benchmark);

        }
        if (endAngle > maxAngle) {
            endAngle = maxAngle;
        }
        return endAngle;
    }

    private void init(Context context) {

        outerArcWidth = dpToPx(3);
        space = dpToPx(3);
        mBitmapHeight = dpToPx(5);
        rectF = new RectF();
        mRingWidth = dpToPx(12);
        innerRadius = dpToPx(6);
        topTextSize = spToPx(12);
        bottomTextSize = spToPx(16);
        centerMargin = dpToPx(6);
        blueStart = Color.parseColor("#C2E9FB");
        blueEnd = linearGradientUtilColor = Color.parseColor("#A1C4FD");
        blueEnd0 = linearGradientUtilColor = Color.parseColor("#00A1C4FD");
        greenStart = Color.parseColor("#58DBAE");
        greenEnd = Color.parseColor("#63D798");
        greenEnd0 = Color.parseColor("#0063D798");
        redStart = Color.parseColor("#F9C46F");
        redEnd = Color.parseColor("#FA8677");
        redEnd00 = Color.parseColor("#00FA8677");
        defalutColor = Color.parseColor("#6C99F4");
        centerTvColor = Color.parseColor("#333333");
        stateHightColor = Color.parseColor("#FA8677");
        stateNormalColor = Color.parseColor("#63D798");
        stateLowColor = Color.parseColor("#A1C4FD");
        positions = new float[]{18 * 7.5f / 360, 0};


        colors = new int[]{blueStart, blueEnd, greenStart, greenEnd, redStart, redEnd};

        weightStr = "体重";
        mPaint = new Paint();
        mPaint.setAntiAlias(true);

        mPathPaint = new Paint();
        mPathPaint.setAntiAlias(true);
        mPathPaint.setColor(defalutColor);


        bitmapPaint = new Paint();
        bitmapPaint.setAntiAlias(true);
        bitmapPaint.setStyle(Paint.Style.FILL);
        bitmapPaint.setStrokeWidth(mBitmapHeight);
        bitmapPaint.setStrokeCap(Paint.Cap.ROUND);//设置为圆角

        centerPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        centerPaint.setColor(centerTvColor);
        float centerTextSize = spToPx(33);
        centerPaint.setTextSize(centerTextSize);
        centerPaint.setTextAlign(Paint.Align.CENTER);
//        impact = Typeface.createFromAsset(context.getAssets(),
//                "fonts/impact.ttf");
//        centerPaint.setTypeface(impact);


        centerLeftHeight = (int) (centerPaint.descent() - centerPaint.ascent());

        centerLablePaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        centerLablePaint.setColor(centerTvColor);
        float centerLableTextSize = spToPx(10);
        centerLablePaint.setTextSize(centerLableTextSize);
        centerLablePaint.setTextAlign(Paint.Align.CENTER);
        centerLablePaint.setTypeface(impact);
        centerLableHeight = (int) (centerLablePaint.descent() - centerLablePaint.ascent());

        textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(centerTvColor);
        textPaint.setTextAlign(Paint.Align.CENTER);

        sweepAngle = 360 - mBottomBlankAngle;
        everyAngle = (sweepAngle - mOtherBlankAngle * 2) / 3f;
        startAngle = 90 + mBottomBlankAngle / 2f;
        maxAngle = 360 - mBottomBlankAngle / 2f;
        innerArea = new RectF();
        outerArea = new RectF();
        circlePos = new float[2];
        linePos = new float[2];
        tan = new float[2];

        orbit = new Path();
        outerOrbit = new Path();

        linearGradientUtilColor = blueEnd;
        mPathMeasure = new PathMeasure();
        weightStrValue = getBigDecimalValue(value);

    }

    private String getBigDecimalValue(float value) {
        if (value <= 0) {
            return "--";
        }
        BigDecimal bigDecimal = new BigDecimal(value);
        return bigDecimal.setScale(1, BigDecimal.ROUND_HALF_UP).toString();
    }

    private int dpToPx(float dp) {
        return (int) (dp * getContext().getResources().getDisplayMetrics().density);
    }

    private int spToPx(float sp) {
        return (int) (TypedValue.applyDimension(2, sp, getContext().getResources().getDisplayMetrics()) + 0.5f);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int min = Math.min(widthSize, heightSize);
        setMeasuredDimension(min, min);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = mHeight = Math.min(w, h);
        mOutsideMargin = mRingWidth / 2f;
        float[] positions = new float[]{0, (everyAngle) / 360f, (everyAngle + mOtherBlankAngle) / 360f,
                (everyAngle * 2 + mOtherBlankAngle) / 360f, (everyAngle * 2 + mOtherBlankAngle * 2f) / 360f, (everyAngle * 3 + mOtherBlankAngle * 2f) / 360f};

        int[] colors = new int[]{blueStart, blueEnd, greenStart, greenEnd, redStart, redEnd};
        mOrbitSweepGradient = new SweepGradient(w / 2f, h / 2f, colors, positions);

        float[] pos1 = new float[]{0, everyAngle / 360f};
        float[] pos2 = new float[]{(everyAngle + mBottomBlankAngle) / 360f, (everyAngle + mBottomBlankAngle + everyAngle) / 360f};
        float[] pos3 = new float[]{(everyAngle + mBottomBlankAngle + everyAngle + mOtherBlankAngle) / 360f,
                (everyAngle * 2f + mBottomBlankAngle + everyAngle + mOtherBlankAngle) / 360f};
        int[] colors1 = new int[]{redStart, redEnd};
        int[] colors2 = new int[]{blueStart, blueEnd};
        int[] colors3 = new int[]{greenStart, greenEnd};

        mBgSweepGradient1 = new SweepGradient(w / 2f, h / 2f, colors1, pos1);
        mBgSweepGradient2 = new SweepGradient(w / 2f, h / 2f, colors2, pos2);
        mBgSweepGradient3 = new SweepGradient(w / 2f, h / 2f, colors3, pos3);

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (getWidth() > getHeight()) {
            canvas.translate((getWidth() - getHeight()) / 2f, 0);
        } else if (getHeight() > getWidth()) {
            canvas.translate(0, (getHeight() - getWidth()) / 2f);
        }
        drawBgArc(canvas);
//        getShader();
        drawInnerAcr(endAngle, canvas);
        drawCenterText(canvas);
    }

    /**
     * 中间文字
     *
     * @param canvas
     */
    private void drawCenterText(Canvas canvas) {

        float measureCenterText = centerPaint.measureText(weightStrValue);
        if (baseCenterY == 0) {
            //计算中间文字的矩形
            rectF.set(mWidth / 2f - measureCenterText / 2, mHeight / 2 - centerLeftHeight / 2,
                    mWidth / 2f + measureCenterText / 2, mHeight / 2 + centerLeftHeight / 2);
            Paint.FontMetricsInt fontMetrics = centerPaint.getFontMetricsInt();
            baseCenterY = (int) ((rectF.bottom + rectF.top - fontMetrics.bottom - fontMetrics.top) / 2);
        }

        canvas.drawText(weightStrValue, mWidth / 2f, baseCenterY, centerPaint);
        //计算单位的区域
        canvas.drawText(unit, mWidth / 2f + measureCenterText / 2f + centerLablePaint.measureText(unit) / 2f, baseCenterY, centerLablePaint);

        //画头部的名称
        textPaint.setTextSize(topTextSize);
        textPaint.setColor(centerTvColor);
        if (baseTopY == 0) {
            float topWidth = textPaint.measureText(weightStr);
            float bottom = mHeight / 2 - centerLeftHeight / 2;
            float top = bottom - (mRingWidth + space + innerRadius * 2);
            rectF.set(mWidth / 2f - topWidth / 2, top, mWidth / 2f + topWidth / 2f, bottom);
            Paint.FontMetricsInt topFontMetrics = textPaint.getFontMetricsInt();
            baseTopY = (int) ((rectF.bottom + rectF.top - topFontMetrics.bottom - topFontMetrics.top) / 2);
        }

        canvas.drawText(weightStr, mWidth / 2f, baseTopY, textPaint);

        //画底部的值
        if (value > 0 && mDrawBottom) {
            textPaint.setTextSize(bottomTextSize);

            if (baseBottomY == 0) {
                float arcRadius = mHeight / 2f;
                baseBottomY = (int) (mHeight / 2f + Math.cos(Math.toRadians(mBottomBlankAngle / 2f)) * arcRadius);
            }
            if (weightStateCode == 0) {
                textPaint.setColor(stateNormalColor);
            } else if (weightStateCode == 1) {
                textPaint.setColor(stateHightColor);
            } else if (weightStateCode == 2) {
                textPaint.setColor(stateLowColor);
            }
            canvas.drawText(mBottomText, mWidth / 2f, baseBottomY, textPaint);

        }


    }


    /**
     * 画圆弧
     *
     * @param canvas
     */
    private void drawBgArc(Canvas canvas) {
        rectF.set(mOutsideMargin, mOutsideMargin, mWidth - mOutsideMargin, mHeight - mOutsideMargin);
        radius = rectF.width();
        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mRingWidth);
        mPaint.setStrokeCap(Paint.Cap.ROUND);

        canvas.save();
        canvas.rotate(-(everyAngle - (180f - mBottomBlankAngle) / 2f) - mOtherBlankAngle, mWidth / 2f, mHeight / 2f);
        mPaint.setShader(mBgSweepGradient1);
        canvas.drawArc(rectF, mOtherBlankAngle, everyAngle, false, mPaint);
        mPaint.setShader(mBgSweepGradient2);
        canvas.drawArc(rectF, everyAngle + mBottomBlankAngle + mOtherBlankAngle, everyAngle, false, mPaint);
        mPaint.setShader(mBgSweepGradient3);
        canvas.drawArc(rectF, everyAngle * 2f + mBottomBlankAngle + mOtherBlankAngle + mOtherBlankAngle, everyAngle, false, mPaint);
        canvas.restore();

    }


    private void drawInnerAcr(float toDegree, Canvas canvas) {
        canvas.save();
        float x = mWidth / 2f - mRingWidth - innerRadius - space;
        innerArea.set(mWidth / 2f - x, mHeight / 2f - x, mWidth / 2f + x, mHeight / 2f + x);
        //通过Path类画一个内切圆弧路径
        orbit.reset();
        orbit.addArc(innerArea, startAngle, toDegree);
        mPathMeasure.setPath(orbit, false);
        //得出圆点
        mPathMeasure.getPosTan(mPathMeasure.getLength(), circlePos, tan);

        rectF.set(mOutsideMargin, mOutsideMargin, mWidth - mOutsideMargin, mHeight - mOutsideMargin);
        outerOrbit.reset();
        outerOrbit.addArc(rectF, startAngle, toDegree);

        // 创建 PathMeasure
        mPathMeasure.setPath(outerOrbit, false);
        //得出直线的顶点
        mPathMeasure.getPosTan(mPathMeasure.getLength(), linePos, tan);

        //绘制实心小圆圈
        mPathPaint.setColor(linearGradientUtilColor);
        mPathPaint.setStyle(Paint.Style.FILL);
        mPathPaint.setShader(null);
        mPathPaint.setAlpha(255);
        canvas.drawCircle(circlePos[0], circlePos[1], innerRadius, mPathPaint);

        mPathPaint.setStrokeWidth(outerArcWidth * 1.5f);
        mPathPaint.setStyle(Paint.Style.STROKE);
        //圆中心点和线的顶端连接起来
        canvas.drawLine(circlePos[0], circlePos[1], linePos[0], linePos[1], mPathPaint);

        canvas.restore();

        if (value >= 0) {
            canvas.save();
            mPathPaint.setAlpha(alpha);
            mPathPaint.setStrokeWidth(outerArcWidth);
            mPathPaint.setStyle(Paint.Style.STROKE);
            orbit.reset();
            orbit.addArc(innerArea, 0, endAngle);
            canvas.rotate(startAngle, mWidth / 2, mHeight / 2);
            canvas.drawPath(orbit, mPathPaint);
            canvas.restore();
        }


    }


    private void initAnimator() {
        mValueAnimator = ValueAnimator.ofFloat(0, 1);
        mValueAnimator.setInterpolator(new OvershootInterpolator());
        mValueAnimator.setDuration(defaultDuration);

        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                alpha = (int) (value * 255f);
                endAngle = (value * (mCalculateResultAngle));
                WeightView.this.value = defalutweight * value;
                weightStrValue = getBigDecimalValue(WeightView.this.value);
                invalidate();
            }
        });

        mValueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mDrawBottom = true;
                invalidate();
            }
        });

    }

    public float getValue() {
        return value;
    }
}
