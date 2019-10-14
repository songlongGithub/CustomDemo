package com.sl.customdemo.dial

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PathMeasure
import android.graphics.RectF
import android.graphics.SweepGradient
import android.text.TextPaint
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.animation.OvershootInterpolator
import java.lang.Math.min

import java.math.BigDecimal


/**
 * Created by sl on 2018/3/5.
 */

class DialView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    /**
     * 顶部文字大小
     */
    private var mTopTextSize: Float = 0.toFloat()
    /**
     * 顶部文字的下间距
     */
    private var mTopTextBottomMargin: Float = 0.toFloat()
    /**
     * 中间文字大小
     */
    private var mCenterTextSize: Float = 0.toFloat()
    /**
     * 中间文字颜色
     */
    private var mCenterTvColor: Int = Color.parseColor("#333333")
    /**
     * 中间文字高度
     */
    private var mCenterValueHeight: Int = 0
    /**
     * 中间文字单位大小
     */
    private var mCenterUnitTextSize: Float = 0.toFloat()
    /**
     * 底部文字大小
     */
    private var mBottomTextSize: Float = 0.toFloat()
    /**
     * 底部文字颜色
     */
    private var mBottomTextColor: Int = 0
    /**
     * 外圆环宽度
     */
    private var mOuterArcWidth: Float = 0.toFloat()
    /**
     * 内环宽度
     */
    private var mInnerArcWidth: Float = 0.toFloat()
    /**
     * 结束点圆半径
     */
    private var mEndCircleRadius: Float = 0.toFloat()
    /**
     * 结束点上的直线宽度
     */
    private var mEndLineWidth: Float = 0.toFloat()

    private var mPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var mPathPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var mTextPaint: TextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
    /**
     * 中间值
     */
    private var mCenterValue: Float = 0.toFloat()
    /**
     * 底部文字
     */
    private var mBottomText: String? = null
    /**
     * 动画时展示的值
     */
    private var mShowValue = -1f

    /**
     * 底部空白角度
     */
    private val mBottomBlankAngle = 90f
    /**
     * 中间空白角度
     */
    private val mOtherBlankAngle = 12f
    /**
     * 三端圆弧每个的角度
     */
    private var mEveryAngle: Float = 0.toFloat()
    /**
     * 开始的角度
     */
    private var mStartAngle: Float = 0.toFloat()
    /**
     * 结束的最大角度
     */
    private var mMaxEndAngle: Float = 0.toFloat()
    /**
     * 结束的角度
     */
    private var mResultAngle: Float = 0.toFloat()

    /**
     * 内环动画时候的角度
     */
    private var mEndAngle: Float = 360f
    /**
     * 内环动画时候的透明度
     */
    private var mInnerArcAlpha = 1

    private var mDrawBottom: Boolean = false

    private var maxWeight: Float = 150f

    /**
     * 内环Path
     */
    private var mInnerArcPath: Path = Path()
    /**
     * 结束点 直线的顶端Path
     */
    private var mOuterOrbitPath: Path = Path()
    private var mInnerArcColor: Int = 0
    /**
     * 内环结束点的坐标
     */
    private var mInnerPos: FloatArray? = null

    /**
     * 内环结束点圆上的线的另一个点的坐标
     */
    private var mOuterPos: FloatArray? = null

    private var mTan: FloatArray? = null
    /**
     * 外间距为外圆环宽度的一半
     */
    private var mOutsideMargin: Float = 0.toFloat()
    private var mWidth: Int = 0
    private var mHeight: Int = 0

    private var mRectF: RectF = RectF()
    /**
     * 内环的范围
     */
    private var mInnerArea: RectF = RectF()
    /**
     * 结束点圆环范围
     */
    private var mOuterLineArea: RectF = RectF()

    private var mValueAnimator: ValueAnimator? = null

    private val mTopStr = "体重"

    private val mUnit = "kg"

    private var mValueStr: String? = null

    private var mBgSweepGradient1: SweepGradient? = null
    private var mBgSweepGradient2: SweepGradient? = null
    private var mBgSweepGradient3: SweepGradient? = null
    private var mPathMeasure: PathMeasure = PathMeasure()

    private var mCenterBaseY: Int = 0
    private var mTopBaseY: Int = 0
    private var mBottomBaseY: Int = 0

    private val mBlueStart = Color.parseColor("#C2E9FB")
    private val mBlueEnd = Color.parseColor("#A1C4FD")
    private val mGreenStart = Color.parseColor("#58DBAE")
    private val mGreenEnd = Color.parseColor("#63D798")
    private val mRedStart = Color.parseColor("#F9C46F")
    private val mRedEnd = Color.parseColor("#FA8677")
    private val mBottomHighColor = Color.parseColor("#FA8677")
    private val mBottomNormalColor = Color.parseColor("#63D798")
    private val mBottomLowColor = Color.parseColor("#A1C4FD")

    init {
        mTopTextSize = spToPx(15f)
        mTopTextBottomMargin = dpToPx(3f)
        mCenterTextSize = spToPx(33f)
        mCenterUnitTextSize = spToPx(10f)
        mBottomTextSize = spToPx(16f)
        mOuterArcWidth = dpToPx(12f)
        mInnerArcWidth = dpToPx(3f)
        mEndCircleRadius = dpToPx(6f)
        mEndLineWidth = dpToPx(5f)

        mTextPaint.color = mCenterTvColor
        mTextPaint.textAlign = Paint.Align.CENTER

        mTextPaint.textSize = mCenterTextSize
        mCenterValueHeight = (mTextPaint.descent() - mTextPaint.ascent()).toInt()

        val otherAngle = 360 - mBottomBlankAngle
        mEveryAngle = (otherAngle - mOtherBlankAngle * 2) / 3f
        mStartAngle = 90 + mBottomBlankAngle / 2f
        mMaxEndAngle = 360 - mBottomBlankAngle

        mInnerPos = FloatArray(2)
        mOuterPos = FloatArray(2)
        mTan = FloatArray(2)
        mValueStr = getBigDecimalValue(mShowValue)

        mInnerArcColor = mBlueEnd
    }

    fun setWeight(value: Float, benchmarkL: Float, benchmarkH: Float) {
        if (value < 0) {
            mCenterValue = 0f
        } else if (value > 150) {
            mCenterValue = maxWeight
        } else {
            mCenterValue = value
        }
        this.mShowValue = mCenterValue
        mDrawBottom = false
        mResultAngle = calculateAngle(mCenterValue, benchmarkL, benchmarkH)
        when {
            mResultAngle <= mEveryAngle -> {
                this.mBottomText = "偏低" + (benchmarkL - mCenterValue)
                mInnerArcColor = mBlueEnd
                mBottomTextColor = mBottomLowColor
            }
            mResultAngle <= mEveryAngle * 2 + mOtherBlankAngle -> {
                this.mBottomText = "正常"
                mInnerArcColor = mGreenEnd
                mBottomTextColor = mBottomNormalColor
            }
            else -> {
                mInnerArcColor = mRedEnd
                mBottomTextColor = mBottomHighColor
                this.mBottomText = "偏高" + (mCenterValue - benchmarkH)

            }
        }
        if (mValueAnimator == null) {
            initAnimator()
        }
        if (mValueAnimator!!.isRunning) {
            mValueAnimator!!.cancel()
        }
        mValueAnimator!!.start()


    }

    /**
     * 计算结束的角度
     *
     * @param value
     * @param low
     * @param high
     * @return
     */
    private fun calculateAngle(value: Float, low: Float, high: Float): Float {
        var endAngle: Float
        endAngle = when {
            value < low -> mEveryAngle * if (value / low > 1) 1f else value / low
            value > high -> {
                val benchmark = (value - high) / (maxWeight - high) * mEveryAngle
                mEveryAngle * 2 + mOtherBlankAngle * 2 + benchmark
            }
            else -> {
                val benchmark = (value - low) / (high - low)
                mEveryAngle + mOtherBlankAngle * 1 + mEveryAngle * if (benchmark > 1) 1f else benchmark

            }
        }
        if (endAngle > mMaxEndAngle) {
            endAngle = mMaxEndAngle
        }
        return endAngle
    }

    private fun initAnimator() {
        mValueAnimator = ValueAnimator.ofFloat(0f, 1f)
        mValueAnimator!!.interpolator = OvershootInterpolator()
        mValueAnimator!!.duration = 2000

        mValueAnimator!!.addUpdateListener { animation ->
            val animatedValue = animation.animatedValue as Float
            mInnerArcAlpha = (animatedValue * 255f).toInt()
            mEndAngle = animatedValue * mResultAngle
            mShowValue = mCenterValue * animatedValue
            mValueStr = getBigDecimalValue(mShowValue)
            invalidate()
        }

        mValueAnimator!!.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                mDrawBottom = true
                invalidate()
            }
        })

    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mHeight = kotlin.math.min(w, h)
        mWidth = mHeight
        mOutsideMargin = mOuterArcWidth / 2f

        //SweepGradient的 postions beginning with 0 and ending with 1.0.而第三段明显超过一圈，所以绘制的时候画布旋转，旋转到startAngle为开始点
        val pos1 = floatArrayOf(0f, mEveryAngle / 360f)
        val pos2 = floatArrayOf(
            (mEveryAngle + mOtherBlankAngle) / 360f,
            (mEveryAngle * 2f + mOtherBlankAngle) / 360f
        )
        val pos3 = floatArrayOf(
            (mEveryAngle * 2f + mOtherBlankAngle * 2f) / 360f,
            (mEveryAngle * 3f + mOtherBlankAngle * 2f) / 360f
        )
        val colors1 = intArrayOf(mBlueStart, mBlueEnd)
        val colors2 = intArrayOf(mGreenStart, mGreenEnd)
        val colors3 = intArrayOf(mRedStart, mRedEnd)

        mBgSweepGradient1 = SweepGradient(w / 2f, h / 2f, colors1, pos1)
        mBgSweepGradient2 = SweepGradient(w / 2f, h / 2f, colors2, pos2)
        mBgSweepGradient3 = SweepGradient(w / 2f, h / 2f, colors3, pos3)

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (width > height) {
            canvas.translate((width - height) / 2f, 0f)
        } else if (height > width) {
            canvas.translate(0f, (height - width) / 2f)
        }
        drawBgArc(canvas)
        drawCenterText(canvas)
        drawInnerAcr(canvas)
    }

    /**
     * 画圆弧
     *
     * @param canvas
     */
    private fun drawBgArc(canvas: Canvas) {
        mRectF.set(
            mOutsideMargin,
            mOutsideMargin,
            mWidth - mOutsideMargin,
            mHeight - mOutsideMargin
        )
        mPaint.color = Color.RED
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeWidth = mOuterArcWidth
        mPaint.strokeCap = Paint.Cap.ROUND

        canvas.save()
        //正常绘制，最后一段会超过一圈，但是SweepGradient的positions的范围是【0，1】，所以旋转绘制
        //旋转开始位置，会使开始点的圆帽 渐变色异常，所以少旋转mOtherBlankAngle
        canvas.rotate(mStartAngle - mOtherBlankAngle, mWidth / 2f, mHeight / 2f)
        mPaint.shader = mBgSweepGradient1
        canvas.drawArc(mRectF, mOtherBlankAngle, mEveryAngle, false, mPaint)
        mPaint.shader = mBgSweepGradient2
        canvas.drawArc(
            mRectF,
            mEveryAngle + mOtherBlankAngle + mOtherBlankAngle,
            mEveryAngle,
            false,
            mPaint
        )
        mPaint.shader = mBgSweepGradient3
        canvas.drawArc(
            mRectF,
            mEveryAngle * 2f + mOtherBlankAngle * 2f + mOtherBlankAngle,
            mEveryAngle,
            false,
            mPaint
        )
        canvas.restore()

    }

    /**
     * 中间文字
     *
     * @param canvas
     */
    private fun drawCenterText(canvas: Canvas) {
        mTextPaint.textSize = mCenterTextSize
        mTextPaint.color = mCenterTvColor
        val measureCenterText = mTextPaint.measureText(mValueStr)
        if (mCenterBaseY == 0) {
            //计算中间文字的矩形
            mRectF.set(
                mWidth / 2f - measureCenterText / 2,
                (mHeight / 2 - mCenterValueHeight / 2).toFloat(),
                mWidth / 2f + measureCenterText / 2,
                (mHeight / 2 + mCenterValueHeight / 2).toFloat()
            )
            val fontMetrics = mTextPaint.fontMetricsInt
            mCenterBaseY =
                ((mRectF.bottom + mRectF.top - fontMetrics.bottom.toFloat() - fontMetrics.top.toFloat()) / 2).toInt()
        }

        canvas.drawText(mValueStr!!, mWidth / 2f, mCenterBaseY.toFloat(), mTextPaint)
        //计算单位的区域
        mTextPaint.textSize = mCenterUnitTextSize
        canvas.drawText(
            mUnit,
            mWidth / 2f + measureCenterText / 2f + mTextPaint.measureText(mUnit) / 2f,
            mCenterBaseY.toFloat(),
            mTextPaint
        )

        //画顶部的文字
        mTextPaint.textSize = mTopTextSize
        if (mTopBaseY == 0) {
            val topWidth = mTextPaint.measureText(mTopStr)
            val bottom = (mHeight / 2 - mCenterValueHeight / 2).toFloat()
            val top = bottom - (mOuterArcWidth + mTopTextBottomMargin + mEndCircleRadius * 2)
            mRectF.set(mWidth / 2f - topWidth / 2, top, mWidth / 2f + topWidth / 2f, bottom)
            val topFontMetrics = mTextPaint.fontMetricsInt
            mTopBaseY =
                ((mRectF.bottom + mRectF.top - topFontMetrics.bottom.toFloat() - topFontMetrics.top.toFloat()) / 2).toInt()
        }
        canvas.drawText(mTopStr, mWidth / 2f, mTopBaseY.toFloat(), mTextPaint)
        //画底部的值
        if (mShowValue > 0 && mDrawBottom) {
            mTextPaint.textSize = mBottomTextSize
            mTextPaint.color = mBottomTextColor
            if (mBottomBaseY == 0) {
                val arcRadius = mHeight / 2f
                mBottomBaseY =
                    (mHeight / 2f + Math.cos(Math.toRadians((mBottomBlankAngle / 2f).toDouble())) * arcRadius).toInt()
            }

            canvas.drawText(mBottomText!!, mWidth / 2f, mBottomBaseY.toFloat(), mTextPaint)

        }

    }


    private fun drawInnerAcr(canvas: Canvas) {
        val radius =
            mWidth / 2f - mOuterArcWidth - mEndCircleRadius - mTopTextBottomMargin
        mInnerArea.set(
            mWidth / 2f - radius,
            mHeight / 2f - radius,
            mWidth / 2f + radius,
            mHeight / 2f + radius
        )
        //通过Path类画一个内切圆弧路径
        mInnerArcPath.reset()

        mInnerArcPath.addArc(
            mInnerArea, mStartAngle, if (mCenterValue == 0f) {
                360f
            } else {
                mEndAngle
            }
        )
        mPathMeasure.setPath(mInnerArcPath, false)
        //得出切点
        mPathMeasure.getPosTan(mPathMeasure.length, mInnerPos, mTan)

        mOuterLineArea.set(
            mOutsideMargin,
            mOutsideMargin,
            mWidth - mOutsideMargin,
            mHeight - mOutsideMargin
        )
        mOuterOrbitPath.reset()
        mOuterOrbitPath.addArc(
            mOuterLineArea, mStartAngle, if (mCenterValue == 0f) {
                360f
            } else {
                mEndAngle
            }
        )
        // 创建 PathMeasure
        mPathMeasure.setPath(mOuterOrbitPath, false)
        //得出直线的顶点
        mPathMeasure.getPosTan(mPathMeasure.length, mOuterPos, mTan)

        //绘制实心小圆圈
        mPathPaint.color = mInnerArcColor
        mPathPaint.style = Paint.Style.FILL
        mPathPaint.shader = null
        mPathPaint.alpha = 255
        canvas.drawCircle(mInnerPos!![0], mInnerPos!![1], mEndCircleRadius, mPathPaint)
        //圆中心点和线的顶端连接起来
        mPathPaint.strokeWidth = mEndLineWidth
        mPathPaint.style = Paint.Style.STROKE
        canvas.drawLine(
            mInnerPos!![0],
            mInnerPos!![1],
            mOuterPos!![0],
            mOuterPos!![1],
            mPathPaint
        )
        //有数据的时候 绘制内环
        if (mShowValue >= 0) {
            canvas.save()
            mPathPaint.alpha = mInnerArcAlpha
            mPathPaint.strokeWidth = mInnerArcWidth
            mPathPaint.style = Paint.Style.STROKE
            mInnerArcPath.reset()
            mInnerArcPath.addArc(mInnerArea, 0f, mEndAngle)
//            mOuterOrbitPath.reset()
//            mOuterOrbitPath.addArc(mOuterLineArea,0f,mEndAngle)
            canvas.rotate(mStartAngle, (mWidth / 2).toFloat(), (mHeight / 2).toFloat())
            canvas.drawPath(mInnerArcPath, mPathPaint)
//            canvas.drawPath(mOuterOrbitPath, mPathPaint)
            canvas.restore()
        }

    }

    fun cancel() {
        if (mValueAnimator != null && mValueAnimator!!.isRunning) {
            mValueAnimator!!.cancel()
        }
    }


    private fun getBigDecimalValue(value: Float): String {
        if (value <= 0) {
            return "--"
        }
        val bigDecimal = BigDecimal(value.toDouble())
        return bigDecimal.setScale(1, BigDecimal.ROUND_HALF_UP).toString()
    }

    private fun dpToPx(dp: Float): Float {
        return (dp * context.resources.displayMetrics.density)
    }

    private fun spToPx(sp: Float): Float {
        return (TypedValue.applyDimension(2, sp, context.resources.displayMetrics) + 0.5f)
    }

    companion object {

        private val TAG = "DialView"
    }
}
