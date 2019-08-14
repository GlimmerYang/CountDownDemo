package com.dm.yzy.countdowndemo.widget

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import com.dm.yzy.countdowndemo.R
import com.dm.yzy.countdowndemo.dpToPx
import com.dm.yzy.countdowndemo.spToPx

/**
 * Function:
 *
 * @author yangzhiying
 * @date  2019/8/14
 *
 */
class CountDownCircleView
@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(
    context,
    attrs,
    defStyleAttr
) {

    //圆轮颜色
    private var mRingColor = 0
    //圆轮宽度
    private var mRingWidth = 0f
    //圆轮进度值文本大小
    private var mRingProgressTextSize = 0
    //宽度
    private var mWidth = 0
    //高度
    private var mHeight = 0
    private var mPaint: Paint? = null
    //圆环的矩形区域
    private var mRectF: RectF? = null

    private var mProgressTextColor = 0
    private var mCountdownTime = 0
    private var mCurrentProgress = 0f
    private var mListener: () -> Unit = {}


    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        mWidth = measuredWidth
        mHeight = measuredHeight
        val rLeft = mRingWidth / 2 + 1f.dpToPx(context)
        val rRight = mWidth - mRingWidth / 2 - 1f.dpToPx(context)
        val rTop = mRingWidth / 2 + 1f.dpToPx(context)
        val rBottom = mHeight - mRingWidth / 2 - 1f.dpToPx(context)
        mRectF = RectF(rLeft, rTop, rRight, rBottom)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        /**
         * 绘制圆形,此处绘制的是实心的部分
         */
        val circlePaint = Paint()
        circlePaint.isAntiAlias = true
        //此处可以修改圆形的颜色
        circlePaint.color = resources.getColor(R.color.colorAccent)
        //此处控制圆心的坐标，以及半径的大小，这里是根据控件的宽设置的最大的圆形（因为需求要求控件设置的是正方形，所以这里没有去判断宽高谁大谁小的问题，大家可以根据实际情况去设定）
        canvas.drawCircle(mWidth / 2.toFloat(), mWidth / 2.toFloat(), mWidth / 2.toFloat(), circlePaint)


        /**
         * 绘制圆环，此处是绘制底部的圆环，和倒计时的圆环是一样的大小，只不过它不随着倒计时缩小
         */
        val ringPaint = Paint()
        //颜色
        ringPaint.color = context.resources.getColor(R.color.colorPrimaryDark)
        //空心
        ringPaint.style = Paint.Style.STROKE
        //宽度
        ringPaint.strokeWidth = mRingWidth
        canvas.drawArc(mRectF, -90f, -360f, false, ringPaint)

        /**
         * 绘制圆环，此处是绘制倒计时的圆环
         */
        //颜色
        mPaint?.color = mRingColor
        //空心
        mPaint?.style = Paint.Style.STROKE
        //宽度
        mPaint?.strokeWidth = mRingWidth
        canvas.drawArc(mRectF, -90f, mCurrentProgress - 360, false, mPaint)


        /**
         * 绘制文本，可以根据需求进行更改，例如倒计时几秒
         */
        val textPaint = Paint()
        textPaint.isAntiAlias = true
        textPaint.textAlign = Paint.Align.CENTER
        val text = "跳过"
        textPaint.textSize = mRingProgressTextSize.toFloat()
        textPaint.color = mProgressTextColor

        //文字居中显示
        val fontMetrics = textPaint.fontMetricsInt
        mRectF?.let {
            val baseline = ((it.bottom + it.top - fontMetrics.bottom - fontMetrics.top) / 2).toInt()
            canvas.drawText(text, it.centerX(), baseline.toFloat(), textPaint)
        }


    }

    private fun getValA(countdownTime: Long): ValueAnimator {
        val valueAnimator = ValueAnimator.ofFloat(0f, 100f)
        valueAnimator.duration = countdownTime
        valueAnimator.interpolator = LinearInterpolator()
        valueAnimator.repeatCount = 0
        return valueAnimator
    }

    /**
     * 开始倒计时
     */
    fun startCountDown() {
        val valueAnimator = getValA(mCountdownTime * 1000.toLong())
        valueAnimator.addUpdateListener { animation ->
            val i = java.lang.Float.valueOf(animation.animatedValue.toString())
            mCurrentProgress = (360 * (i / 100f))
            invalidate()
        }
        valueAnimator.start()
        valueAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                //倒计时结束回调
                mListener()
            }

        })
    }

    /**
     * 倒计时监听，可在countDownFinished（）方法中进行倒计时结束后的逻辑
     */
    fun setAddCountDownListener(mListener: () -> Unit) {
        this.mListener = mListener
    }

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.CountDownCircleView)
        mRingColor =
            a.getColor(R.styleable.CountDownCircleView_ringColor, context.resources.getColor(R.color.colorWhite))
        mRingWidth =
            a.getFloat(R.styleable.CountDownCircleView_ringWidth, 1f).dpToPx(context).toFloat()
        mRingProgressTextSize = a.getDimensionPixelSize(
            R.styleable.CountDownCircleView_progressTextSize,
            10.0f.spToPx(context)
        )
        mProgressTextColor = a.getColor(
            R.styleable.CountDownCircleView_progressTextColor,
            context.resources.getColor(R.color.colorPrimary)
        )
        mCountdownTime = a.getInteger(R.styleable.CountDownCircleView_countdownTime, 3)
        a.recycle()
        mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mPaint?.isAntiAlias = true
        this.setWillNotDraw(false)
    }


}