package com.sl.customdemo.chart

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Shader
import android.text.TextPaint
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.ViewConfiguration
import android.widget.Scroller
import androidx.core.view.marginTop


import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Calendar
import java.util.Locale
import kotlin.math.abs
import kotlin.math.ceil


/**
 * Created by taimeng on 2017/10/31.
 */

class TrendCurveView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    /**
     * 源数据
     */
    private val mSourceData = ArrayList<DataBean>()
    /**
     * 总条目数
     */
    private var mTotalSize: Int = 0
    /**
     * 绘制的条目
     */
    private val mShowList = ArrayList<TextBean>()
    /**
     * 计算后的总条目
     */
    private val mCacheList = ArrayList<TextBean>()
    private val mMinimumFlingVelocity: Int
    private val mMaximumFlingVelocity: Float
    /**
     * 平滑度
     */
    private val lineSmoothness = 0.2f
    private var mVelocityTracker: VelocityTracker? = null

    private var mPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    /**
     * 文字画笔
     */
    private var mTextPaint: TextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
    /**
     * 中间文字画笔
     */
    private var mTopTextPaint: TextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)

    /**
     * 曲线画笔
     */
    private var mCurvePaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    /**
     * 填充path画笔
     */
    private var mPathPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    /**
     * 背景横线paint
     */
    private var mHorizontalLinePaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    /**
     * 内圆画笔
     */
    private var mInnerCirclePaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    /**
     * 外圆画笔
     */
    private var mOuterCirclePaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    /**
     * 单位画笔
     */
    private var mUnitPaint: TextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
    /**
     * 单位描述 单位：kg
     */
    private var mUnitDesPaint: TextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
    private var mRectF: RectF = RectF()
    private var mPath: Path = Path()
    private var mTopPath: Path = Path()

    private var mMarginRight: Float = 0.toFloat()
    private var mMarginTop: Float = 0.toFloat()

    /**
     * 数据展示区域的高度
     */
    private var mAvailableAreaHeight: Float = 0.toFloat()
    /**
     * 数据展示顶部 = 顶部文字高度+文字上下Margin +上margin+箭头高度+箭头底部间距
     */
    private var mAvailableAreaTop: Float = 0.toFloat()

    /**
     * 扩折线右边距
     */
    private var mBrokenLineRightMargin: Float = 0.toFloat()
    /**
     * 展示年的文字大小
     */
    private var mYearTextSize: Float = 0.toFloat()

    /**
     * 顶部文字高度
     */
    private var mTopTextHeight: Float = 0.toFloat()
    private var mTopTvHorizontalMargin: Float = 0.toFloat()
    private var mTopTvVerticalMargin: Float = 0.toFloat()

    /**
     * 顶部文字矩形的圆角
     */
    private var mTopRectRadius: Float = 0.toFloat()
    /**
     * 每个值底部的间距
     */
    private var mItemTvBottomMargin: Float = 0.toFloat()

    private var mBottomTextPadding: Float = 0.toFloat()
    private var mBottomTvHeight: Int = 0
    private var mBottomTextSize: Float = 0.toFloat()
    private var mBottomTextMargin: Float = 0.toFloat()
    private var mBottomHeight: Float = 0.toFloat()

    private var mBottomLineColor: Int = 0
    /**
     * 值的单位
     */
    private var mUnit: String? = null
    /**
     * 右边显示的文字
     */
    private var mUnitDes: String? = null
    /**
     * 右边单位描述的宽高
     */
    private var mUnitDesTextWidth: Float = 0.toFloat()
    private var mUnitDesTextHeight: Float = 0.toFloat()

    /**
     * 渐变色的开始颜色和结束颜色
     */
    private var mStartGradientColor: Int = 0
    private var mEndGradientColor: Int = 0
    /**
     * 曲线颜色
     */
    private var mCurveLineColor: Int = 0
    /**
     * 内圆半径
     */
    private var mInnerRadius: Float = 0.toFloat()
    /**
     * 外圆StrokeWidth
     */
    private var mOuterRadiusWidth: Float = 0.toFloat()
    /**
     * 当前viewWidth
     */
    private var mViewWidth: Float = 0.toFloat()
    /**
     * 总宽
     */
    private var mTotalWidth: Int = 0
    /**
     * 总高
     */
    private var mTotalHeight: Int = 0

    /**
     * 宽的中点
     */
    private var mCenterX: Int = 0

    /**
     * 每个矩形的宽度
     */
    private var mEveryRectWidth: Int = 0

    /**
     * 顶部矩形 顶部箭头的宽度 箭头高度=宽度的一半
     */
    private var mArrowWidth: Float = 0.toFloat()
    /**
     * 箭头底部的间距
     */
    private var mArrowBottomMargin: Float = 0.toFloat()

    private var mBottomLineHeight: Float = 0.toFloat()
    /**
     * 顶部文字baseLine 计算一次就好了
     */
    private var mTopBaseLineY: Int = 0
    /**
     * 最大的滑动距离
     */
    private var mMaxMove: Int = 0
    /**
     * 居中的条目数
     */
    private var mSelectPosition: Int = 0

    /**
     * mMove为偏移量
     */
    private var mMove = 0
    private var mLastFocusX: Float = 0.toFloat()
    private var mLastFocusY: Float = 0.toFloat()
    private var mDownFocusX: Float = 0.toFloat()
    private var mDownFocusY: Float = 0.toFloat()

    private val mScroller: Scroller?

    private var mListener: OnTableSelectListener? = null


    init {
        initSize()
        initPaint()
        mScroller = Scroller(getContext())
        val configuration = ViewConfiguration.get(context)
        mMinimumFlingVelocity = configuration.scaledMinimumFlingVelocity
        mMaximumFlingVelocity = configuration.scaledMaximumFlingVelocity.toFloat()

    }

    private fun initSize() {
        mMarginRight = dpToPx(19f)
        mMarginTop = dpToPx(11f)

        mYearTextSize = spToPx(12f)
        mBrokenLineRightMargin = dpToPx(5f)
        mTopTvHorizontalMargin = dpToPx(10f)
        mTopTvVerticalMargin = dpToPx(5f)
        mTopRectRadius = dpToPx(5f)

        mArrowWidth = dpToPx(9f)
        mArrowBottomMargin = dpToPx(12f)

        mBottomLineHeight = dpToPx(1f)
        mBottomTextMargin = dpToPx(9f)
        mBottomTextPadding = dpToPx(9f)
        mBottomTextSize = spToPx(12f)
        mInnerRadius = dpToPx(3f)
        mOuterRadiusWidth = dpToPx(2f)
        mItemTvBottomMargin = dpToPx(4f)

        mStartGradientColor = Color.parseColor("#7047DE69")
        mEndGradientColor = Color.parseColor("#0679D58E")
        mCurveLineColor = Color.parseColor("#63D798")
        mBottomLineColor = Color.parseColor("#EEEEEE")


    }

    private fun initPaint() {
        mTextPaint.textAlign = Paint.Align.CENTER
        mTextPaint.color = Color.parseColor("#999999")

        mTopTextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
        mTopTextPaint.textSize = spToPx(16f)
        mTopTextPaint.color = Color.WHITE
        mTopTextPaint.textAlign = Paint.Align.CENTER
        mTopTextPaint.isFakeBoldText = true
        mTopTextHeight = getTextHeight(mTopTextPaint)

        mCurvePaint.style = Paint.Style.STROKE
        mCurvePaint.strokeJoin = Paint.Join.ROUND
        mCurvePaint.strokeWidth = dpToPx(3f)
        mCurvePaint.color = mCurveLineColor


        mHorizontalLinePaint.style = Paint.Style.STROKE
        mHorizontalLinePaint.strokeWidth = dpToPx(1f)
        mHorizontalLinePaint.color = Color.parseColor("#EEEEEE")

        mPathPaint
        mPathPaint.style = Paint.Style.FILL


        mInnerCirclePaint.style = Paint.Style.FILL
        mInnerCirclePaint.color = Color.WHITE


        mOuterCirclePaint.style = Paint.Style.STROKE
        mOuterCirclePaint.color = mCurveLineColor
        mOuterCirclePaint.strokeWidth = mOuterRadiusWidth

        mUnitPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
        mUnitPaint.textSize = spToPx(10f)
        mUnitPaint.color = Color.WHITE
        mUnitPaint.textAlign = Paint.Align.CENTER


        mUnitDesPaint.textSize = spToPx(12f)
        mUnitDesPaint.color = Color.parseColor("#63D798")
        mUnitDesPaint.textAlign = Paint.Align.CENTER


        mTextPaint.textSize = mBottomTextSize
        mBottomTvHeight = getTextHeight(mTextPaint).toInt()
        mBottomHeight = mBottomTvHeight + mBottomTextMargin * 2
    }

    private fun getTextHeight(paint: Paint): Float {
        val fm = paint.fontMetrics
        return ceil((fm.descent - fm.ascent).toDouble()).toFloat()
    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mTotalWidth = w
        mTotalHeight = h
        mViewWidth = w.toFloat()
        mEveryRectWidth = mTotalWidth / NUB
        mCenterX = (w / 2f).toInt()
        mTopBaseLineY = 0
        initData(mSourceData)
    }


    /**
     * 唯一公开方法，用于设置元数据
     *
     * @param data
     */
    fun setData(data: List<DataBean>?, unit: String) {
        if (data == null || data.isEmpty()) {
            return
        }
        initUnit(unit)
        mSourceData.clear()
        mSourceData.addAll(data)
        mTotalSize = data.size
        //默认选中最后一条
        mSelectPosition = data.size - 1

        mMove = 0
        if (mScroller != null) {
            mScroller.finalX = 0
        }
        initData(data)

    }

    private fun initUnit(unit: String) {
        if (TextUtils.isEmpty(unit)) {
            return
        }
        this.mUnit = unit
        mUnitDes = "单位：$unit"
        mUnitDesTextWidth = measureText(mUnitDesPaint, mUnitDes!!)[0]
        mUnitDesTextHeight = measureText(mUnitDesPaint, mUnitDes!!)[1]
    }

    private fun initData(data: List<DataBean>) {
        if (mTotalWidth == 0 || mTotalHeight == 0) {
            return
        }
        if (data.size > 1) {
            mTotalWidth += mEveryRectWidth * (data.size - 1)
        }

        mAvailableAreaTop =
            mTopTextHeight + mTopTvVerticalMargin * 2f + mArrowWidth / 2f + mArrowBottomMargin + mMarginTop
        mAvailableAreaHeight = (mTotalHeight.toFloat() - mAvailableAreaTop
                - mBottomTextPadding * 2 - mBottomLineHeight - mBottomTvHeight.toFloat())

        val lineGra = LinearGradient(
            0f,
            mAvailableAreaTop - 50,
            0f,
            mAvailableAreaTop + mAvailableAreaHeight,
            mStartGradientColor,
            mEndGradientColor,
            Shader.TileMode.REPEAT
        )
        mPathPaint.shader = lineGra

        mMaxMove = (data.size - 1) * mEveryRectWidth
        var max = data[0].value
        var min = max
        for (dataBean in data) {
            val value = dataBean.value
            if (value > max) {
                max = value
            }
            if (value < min) {
                min = value
            }
        }

        val diff = max - min
        val scale = if (diff == 0.0) 0.6f else mAvailableAreaHeight * 0.8f / diff.toFloat()

        val pm = mTextPaint.fontMetricsInt
        mCacheList.clear()
        val calendar = Calendar.getInstance()
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)

        for (i in data.indices) {
            //计算所有点坐标
            val trendDataBean = data[i]
            //从右向左绘制的，偏移viewWidth的一半
            val x = (mCenterX - (data.size - 1 - i) * mEveryRectWidth).toFloat()
            val y = (mAvailableAreaTop + (max - trendDataBean.value) * scale).toFloat()
            val pointF = PointF(x, y)
            val recordDate = trendDataBean.recordDate
            try {
                val parse = simpleDateFormat.parse(recordDate)
                calendar.time = parse
                //计算所有文字的坐标
                val textBean = getTextBean(pm, trendDataBean.value.toString(), calendar, pointF)
                textBean.pointF = pointF
                mCacheList.add(textBean)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
        }
        invalidate()
    }


    private fun getTextBean(
        pm: Paint.FontMetricsInt,
        value: String,
        calendar: Calendar,
        pointF: PointF
    ): TextBean {
        //计算字体区域
        val bottomTvTop = mAvailableAreaTop + mAvailableAreaHeight
        //内圆半径+外圆的宽+文字间距
        val marginTop = mInnerRadius + mOuterRadiusWidth + mItemTvBottomMargin
        val textBean = TextBean()
        val x = pointF.x
        val y = pointF.y

//        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val topTextWidth = mTextPaint.measureText(value)
        //字体居中区域
        mRectF.set(
            x - topTextWidth / 2f, y - mTopTextHeight - marginTop,
            x + topTextWidth / 2f, y - marginTop
        )
        val baseY =
            ((mRectF.bottom + mRectF.top - pm.bottom.toFloat() - pm.top.toFloat()) / 2f).toInt()
        textBean.centerStr = (value)
        textBean.centerX = (mRectF.centerX())
        textBean.centerY = (baseY.toFloat())
        //底部文字
        mTextPaint.textSize = mBottomTextSize
        val stringBuilder = StringBuilder()
        stringBuilder.append(if (month < 10) "0$month" else month)
        stringBuilder.append("-")
        stringBuilder.append(if (day < 10) "0$day" else day)
        val bottomTvWidth = mTextPaint.measureText(stringBuilder.toString())
        mRectF.set(
            x - bottomTvWidth / 2f, bottomTvTop + mBottomTextPadding,
            x + bottomTvWidth / 2f, bottomTvTop + mBottomTextPadding + mBottomTvHeight.toFloat()
        )

        val baseBottomY =
            ((mRectF.bottom + mRectF.top - pm.bottom.toFloat() - pm.top.toFloat()) / 2f).toInt()
        textBean.bottomStr = (stringBuilder.toString())
        textBean.bottomX = (mRectF.centerX())
        textBean.bottomY = (baseBottomY.toFloat())

        textBean.circleX = (x)
        textBean.circleY = (pointF.y)

        return textBean
    }

    /**
     *
     * 保证每次绘制做多nub + 3+3  三阶贝塞尔 三个控制点 左右各三个
     * 根据滑动距离计算展示的条目
     *
     * @param move
     */
    private fun calculateShowList(move: Int) {
        if (mCacheList.isEmpty()) {
            return
        }
        val absMove = abs(move)
        var start: Int
        var end: Int
        if (absMove < mCenterX) {
            end = mTotalSize
            start = mTotalSize - ((absMove + mCenterX) / mEveryRectWidth + 3)
        } else {
            val exceedStart = (absMove - mCenterX) / mEveryRectWidth
            end = mTotalSize - (exceedStart - 3)
            start = mTotalSize - (exceedStart + NUB + 3)
        }
        //越界处理
        end = if (mTotalSize > end) end else mTotalSize
        start = if (start > 0) start else 0
        mShowList.clear()
        //        mShowList.addAll(mCacheList.subList(start,end));
        for (i in start until end) {
            mShowList.add(mCacheList[i])
        }
    }

    /**
     * 根据要展示的条目 计算出需要绘制path
     *
     * @param pointFList
     */
    private fun measurePath(pointFList: List<TextBean>) {
        mPath.reset()
        var prePreviousPointX = java.lang.Float.NaN
        var prePreviousPointY = java.lang.Float.NaN
        var previousPointX = java.lang.Float.NaN
        var previousPointY = java.lang.Float.NaN
        var currentPointX = java.lang.Float.NaN
        var currentPointY = java.lang.Float.NaN
        var nextPointX: Float
        var nextPointY: Float

        val lineSize = pointFList.size
        for (i in 0 until lineSize) {
            if (java.lang.Float.isNaN(currentPointX)) {
                val point = pointFList[i].pointF
                currentPointX = point!!.x + mMove
                currentPointY = point.y
            }
            if (java.lang.Float.isNaN(previousPointX)) {
                //是否是第一个点
                if (i > 0) {
                    val point = pointFList[i - 1].pointF
                    previousPointX = point!!.x + mMove
                    previousPointY = point.y
                } else {
                    //是的话就用当前点表示上一个点
                    previousPointX = currentPointX
                    previousPointY = currentPointY
                }
            }

            if (java.lang.Float.isNaN(prePreviousPointX)) {
                //是否是前两个点
                if (i > 1) {
                    val point = pointFList[i - 2].pointF
                    prePreviousPointX = point!!.x + mMove
                    prePreviousPointY = point.y
                } else {
                    //是的话就用当前点表示上上个点
                    prePreviousPointX = previousPointX
                    prePreviousPointY = previousPointY
                }
            }

            // 判断是不是最后一个点了
            if (i < lineSize - 1) {
                val point = pointFList[i + 1].pointF
                nextPointX = point!!.x + mMove
                nextPointY = point.y
            } else {
                //是的话就用当前点表示下一个点
                nextPointX = currentPointX
                nextPointY = currentPointY
            }

            if (i == 0) {
                // 将Path移动到开始点
                mPath.moveTo(currentPointX, currentPointY)
            } else {
                // 求出控制点坐标
                val firstDiffX = currentPointX - prePreviousPointX
                val firstDiffY = currentPointY - prePreviousPointY
                val secondDiffX = nextPointX - previousPointX
                val secondDiffY = nextPointY - previousPointY
                val firstControlPointX = previousPointX + lineSmoothness * firstDiffX
                val firstControlPointY = previousPointY + lineSmoothness * firstDiffY
                val secondControlPointX = currentPointX - lineSmoothness * secondDiffX
                val secondControlPointY = currentPointY - lineSmoothness * secondDiffY
                //画出曲线
                mPath.cubicTo(
                    firstControlPointX,
                    firstControlPointY,
                    secondControlPointX,
                    secondControlPointY,
                    currentPointX,
                    currentPointY
                )
            }
            // 更新值,
            prePreviousPointX = previousPointX
            prePreviousPointY = previousPointY
            previousPointX = currentPointX
            previousPointY = currentPointY
            currentPointX = nextPointX
            currentPointY = nextPointY
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawHorizontalLine(canvas)
        if (mCacheList.isEmpty()) {
            return
        }
        calculateShowList(mMove)
        measurePath(mShowList)
        drawUnitDes(canvas)
        drawCurveLineAndBgPath(canvas)
        drawTopAndVerticalLineView(canvas)
        drawValueAndPoint(canvas)
    }

    /**
     * 绘制背景线
     *
     * @param canvas
     */
    private fun drawHorizontalLine(canvas: Canvas) {
        val baseHeight = mAvailableAreaHeight / 5
        for (i in 0 until 6) {
            val startY = baseHeight * i + mAvailableAreaTop
            canvas.drawLine(0f, startY, mViewWidth, startY, mHorizontalLinePaint)
        }
        //画底部line
        mPaint.shader = null
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeWidth = mBottomLineHeight
        mPaint.color = mBottomLineColor
        canvas.drawLine(
            0f,
            mTotalHeight - mBottomLineHeight,
            mViewWidth,
            mTotalHeight - mBottomLineHeight,
            mPaint
        )
    }

    /**
     * 绘制右边的 单位：kg
     *
     * @param canvas
     */
    private fun drawUnitDes(canvas: Canvas) {
        if (!TextUtils.isEmpty(mUnitDes)) {
            canvas.drawText(
                mUnitDes!!,
                width.toFloat() - mMarginRight - mUnitDesTextWidth / 2,
                mMarginTop + mUnitDesTextHeight / 2,
                mUnitDesPaint
            )
        }
    }

    /**
     * 绘制曲线和背景填充
     *
     * @param canvas
     */
    private fun drawCurveLineAndBgPath(canvas: Canvas) {
        if (mShowList.size > 0) {
            val firstX = mShowList[0].pointF!!.x + mMove
            val lastX = mShowList[mShowList.size - 1].pointF!!.x + mMove
            //先画曲线
            canvas.drawPath(mPath, mCurvePaint)
            //再填充背景
            mPath.lineTo(lastX, mAvailableAreaTop + mAvailableAreaHeight)
            mPath.lineTo(firstX, mAvailableAreaTop + mAvailableAreaHeight)
            mPath.close()
            canvas.drawPath(mPath, mPathPaint)
        }

    }

    /**
     * 绘制顶部矩形和文字 以及垂直线
     *
     * @param canvas
     */
    private fun drawTopAndVerticalLineView(canvas: Canvas) {
        val scrollX = abs(mMove)
        val baseWidth = mEveryRectWidth / 2f
        //因为是从右向左滑动 最右边最大，计算的时候要反过来
        var nub = mTotalSize - 1 - ((scrollX + baseWidth) / mEveryRectWidth).toInt()
        if (nub > mTotalSize - 1) {
            nub = mTotalSize - 1
        }
        if (nub < 0) {
            nub = 0
        }
        val centerValue = mCacheList[nub].centerStr
        val valueWidth = mTopTextPaint.measureText(centerValue)
        val unitWidth = if (TextUtils.isEmpty(mUnit)) 0f else mUnitPaint.measureText(mUnit)

        val centerTvWidth = valueWidth + unitWidth + 1f

        val topRectPath = getTopRectPath(centerTvWidth)
        mPaint.style = Paint.Style.FILL
        mPaint.color = mCurveLineColor
        canvas.drawPath(topRectPath, mPaint)
        //画居中线
        canvas.drawLine(
            mCenterX.toFloat(),
            mAvailableAreaTop - mArrowBottomMargin,
            mCenterX.toFloat(),
            mTotalHeight.toFloat() - mBottomHeight - mBottomLineHeight,
            mPaint
        )

        //计算text Y坐标
        mRectF.set(
            mCenterX - centerTvWidth / 2f,
            mMarginTop,
            mCenterX + centerTvWidth / 2,
            mMarginTop + mTopTvVerticalMargin * 2 + mTopTextHeight
        )
        if (mTopBaseLineY == 0) {
            val pm = mTextPaint.fontMetricsInt
            mTopBaseLineY =
                ((mRectF.bottom + mRectF.top - pm.bottom.toFloat() - pm.top.toFloat()) / 2f).toInt()
        }
        //画居中的值
        canvas.drawText(
            centerValue!!,
            mRectF.centerX() - centerTvWidth / 2 + valueWidth / 2,
            mTopBaseLineY.toFloat(),
            mTopTextPaint
        )
        if (!TextUtils.isEmpty(mUnit)) {
            //单位
            canvas.drawText(
                mUnit!!,
                mRectF.centerX() + centerTvWidth / 2 - unitWidth / 2,
                mTopBaseLineY.toFloat(),
                mUnitPaint
            )
        }


    }

    /**
     * 顶部矩形+三角
     *
     * @param rectWidth
     */
    private fun getTopRectPath(rectWidth: Float): Path {
        mRectF.set(
            mCenterX.toFloat() - rectWidth / 2f - mTopTvHorizontalMargin,
            mMarginTop,
            mCenterX.toFloat() + rectWidth / 2f + mTopTvHorizontalMargin,
            mMarginTop + mTopTvVerticalMargin * 2 + mTopTextHeight
        )
        mTopPath.reset()
        //圆角矩形
        mTopPath.addRoundRect(mRectF, mTopRectRadius, mTopRectRadius, Path.Direction.CCW)
        //画三角
        mTopPath.moveTo(mRectF.centerX() - mArrowWidth / 2f, mMarginTop + mRectF.height())
        mTopPath.lineTo(mRectF.centerX(), mMarginTop + mRectF.height() + mArrowWidth / 2f)
        mTopPath.lineTo(mRectF.centerX() + mArrowWidth / 2f, mMarginTop + mRectF.height())
        mTopPath.close()
        return mTopPath
    }


    /**
     * 绘制每个点的值和圆
     *
     * @param canvas
     */
    private fun drawValueAndPoint(canvas: Canvas) {
        for (i in mShowList.indices) {
            val textBean = mShowList[i]
            val centerX = textBean.centerX + mMove
            //绘制值
            canvas.drawText(textBean.centerStr!!, centerX, textBean.centerY, mTextPaint)
            //绘制底部日期
            mTextPaint.textSize = mBottomTextSize
            canvas.drawText(textBean.bottomStr!!, centerX, textBean.bottomY, mTextPaint)

            canvas.drawCircle(centerX, textBean.circleY, mInnerRadius, mInnerCirclePaint)
            canvas.drawCircle(
                centerX,
                textBean.circleY,
                mInnerRadius + mOuterRadiusWidth / 2,
                mOuterCirclePaint
            )
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain()
        }
        mVelocityTracker!!.addMovement(event)
        val action = event.action

        val pointerUp = action and MotionEvent.ACTION_MASK == MotionEvent.ACTION_POINTER_UP
        val skipIndex = if (pointerUp) event.actionIndex else -1
        // Determine focal point
        var sumX = 0f
        var sumY = 0f
        val count = event.pointerCount
        for (i in 0 until count) {
            if (skipIndex == i) continue
            sumX += event.getX(i)
            sumY += event.getY(i)
        }
        val div = if (pointerUp) count - 1 else count
        val focusX = sumX / div
        val focusY = sumY / div

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mLastFocusX = focusX
                mDownFocusX = mLastFocusX
                mLastFocusY = focusY
                mDownFocusY = mLastFocusY
                return true
            }
            MotionEvent.ACTION_MOVE ->

                if (abs(mMove) <= mMaxMove) {
                    val scrollX = (mLastFocusX - focusX).toInt()
                    smoothScrollBy(-scrollX, 0)
                    mLastFocusX = focusX
                    mLastFocusY = focusY
                }
            MotionEvent.ACTION_UP -> {
                mVelocityTracker!!.computeCurrentVelocity(1000, mMaximumFlingVelocity)
                val velocityX = mVelocityTracker!!.xVelocity
                //
                if (abs(velocityX) > mMinimumFlingVelocity) {
                    mScroller!!.fling(
                        mMove,
                        0,
                        velocityX.toInt(),
                        mVelocityTracker!!.yVelocity.toInt(),
                        0,
                        mMaxMove,
                        0,
                        0
                    )
                    var finalX = mScroller.finalX
                    val distance = abs(finalX % mEveryRectWidth)
                    if (distance < mEveryRectWidth / 2) {
                        finalX -= distance
                    } else {
                        finalX += (mEveryRectWidth - distance)
                    }
                    mScroller.finalX = finalX

                } else {
                    setClick(event.x.toInt(), mDownFocusX)
                }
                getCurrentIndex()

                if (mVelocityTracker != null) {
                    // This may have been cleared when we called out to the
                    // application above.
                    mVelocityTracker!!.recycle()
                    mVelocityTracker = null
                }
            }
            else -> {
            }
        }//                invalidate();
        return super.onTouchEvent(event)
    }


    private fun setClick(upX: Int, downX: Float) {
        var finalX = mScroller!!.finalX
        val distance: Int
        if (abs(downX - upX) > 10) {
            distance = abs(finalX % mEveryRectWidth)
            if (distance < mEveryRectWidth / 2) {
                finalX -= distance
            } else {
                finalX += (mEveryRectWidth - distance)
            }

        } else {
            val space = (mCenterX - upX).toFloat()
            distance = abs(space % mEveryRectWidth).toInt()
            val nub = (space / mEveryRectWidth).toInt()
            if (distance < mEveryRectWidth / 2) {
                if (nub != 0) {
                    finalX = if (space > 0) {
                        (finalX + (space - distance)).toInt()
                    } else {
                        (finalX + (space + distance)).toInt()
                    }
                }
            } else {
                if (space > 0) {
                    finalX += (nub + 1) * mEveryRectWidth
                } else {
                    finalX = (finalX + space - (mEveryRectWidth - distance)).toInt()

                }

            }
        }
        if (finalX < 0) {
            finalX = 0
        } else if (finalX > mMaxMove) {
            finalX = mMaxMove
        }
        smoothScrollTo(finalX, 0)
    }

    /**
     * 当前居中点
     */
    private fun getCurrentIndex() {
        mSelectPosition = abs(mScroller!!.finalX / mEveryRectWidth)
        mSelectPosition = mCacheList.size - 1 - mSelectPosition
        if (mSelectPosition < 0) {
            mSelectPosition = 0
        }
        if (mSelectPosition > mCacheList.size - 1) {
            mSelectPosition = mCacheList.size - 1
        }
        mListener?.onTableItemSelect(mSelectPosition, mScroller.duration)
    }


    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        parent.requestDisallowInterceptTouchEvent(true)
        return super.dispatchTouchEvent(ev)
    }

    override fun computeScroll() {
        if (mScroller!!.computeScrollOffset()) {
            //判断左右边界
            mMove = mScroller.currX
            if (mMove > mMaxMove) {
                mMove = mMaxMove
            } else if (mMove < 0) {
                mMove = 0
            }
            invalidate()
        }
    }

    /**
     * 调用此方法设置滚动的相对偏移
     */
    private fun smoothScrollBy(dx: Int, dy: Int) {
        //设置mScroller的滚动偏移量
        mScroller!!.startScroll(mScroller.finalX, mScroller.finalY, dx, dy)
        invalidate()//这里必须调用invalidate()才能保证computeScroll()会被调用，否则不一定会刷新界面，看不到滚动效果
    }

    /**
     * 调用此方法滚动到目标位置
     *
     * @param fx
     * @param fy
     */
    private fun smoothScrollTo(fx: Int, fy: Int) {

        val dx = fx - mScroller!!.finalX
        val dy = fy - mScroller.finalY
        smoothScrollBy(dx, dy)
    }

    fun clearData() {
        mCacheList.clear()
        mSourceData.clear()
        mUnit = ""
        mUnitDes = ""
        invalidate()
    }

    private fun dpToPx(dp: Float): Float {
        return dp * context.resources.displayMetrics.density
    }

    private fun spToPx(sp: Float): Float {
        return TypedValue.applyDimension(2, sp, context.resources.displayMetrics) + 0.5f
    }

    private fun measureText(paint: Paint, text: String): FloatArray {
        val size = FloatArray(2)
        if (TextUtils.isEmpty(text)) {
            return size
        }
        val width = paint.measureText(text, 0, text.length)
        val bounds = Rect()
        paint.getTextBounds(text, 0, text.length, bounds)
        size[0] = width
        size[1] = bounds.height().toFloat()
        return size
    }


    interface OnTableSelectListener {
        fun onTableItemSelect(position: Int, durationTime: Int)
    }

    fun setOnTableSelectListener(listener: OnTableSelectListener) {
        this.mListener = listener
    }


    private inner class TextBean internal constructor() {
        //数据文字坐标
        var centerX: Float = 0.toFloat()
        var centerY: Float = 0.toFloat()
        //数据文字
        var centerStr: String? = null
        //底部日期坐标
        var bottomX: Float = 0.toFloat()
        var bottomY: Float = 0.toFloat()
        //底部日期
        var bottomStr: String? = null
        //数据圆点坐标
        var circleX: Float = 0.toFloat()
        var circleY: Float = 0.toFloat()
        //坐标点
        var pointF: PointF? = null
    }

    companion object {
        /**
         * 一屏幕展示几条
         */
        private const val NUB = 7
        private const val TAG = "TrendCurveView"
    }


}
