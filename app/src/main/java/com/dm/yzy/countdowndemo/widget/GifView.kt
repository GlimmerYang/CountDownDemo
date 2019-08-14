package com.dm.yzy.countdowndemo.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Movie
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.dm.yzy.countdowndemo.R

import java.io.InputStream

/**
 * Function:
 *
 * @author yangzhiying
 * @date  2019/8/14
 *
 */
class GifView
@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = R.styleable.CustomTheme_gifViewStyle
) : View(context, attrs, defStyle) {

    private var mMovieResourceId = 0
    private var mMovie: Movie? = null

    private var mMovieStart = 0L
    private var mCurrentAnimationTime = 0

    /**
     * Position for drawing animation frames in the center of the view.
     */
    private var mLeft = 0f
    private var mTop = 0f

    /**
     * Scaling factor to fit the animation within view bounds.
     */
    private var mScaleX = 0f
    private var mScaleY = 0f

    /**
     * Scaled movie frames width and height.
     */
    private var mMeasuredMovieWidth = 0
    private var mMeasuredMovieHeight = 0

    @Volatile
    private var mPaused = false
    private var mVisible = true

    var movie: Movie?
        get() = mMovie
        set(movie) {
            this.mMovie = movie
            requestLayout()
        }

    /**
     * Calculate new movie start time, so that it resumes from the same frame.
     */
    var isPaused: Boolean
        get() = this.mPaused
        set(paused) {
            this.mPaused = paused
            if (!paused) {
                mMovieStart = android.os.SystemClock.uptimeMillis() - mCurrentAnimationTime
            }

            invalidate()
        }

    init {

        setViewAttributes(context, attrs, defStyle)
    }

    @SuppressLint("NewApi")
    private fun setViewAttributes(context: Context, attrs: AttributeSet?, defStyle: Int) {

        /**
         * Starting from HONEYCOMB(Api Level:11) have to turn off HW acceleration to draw
         * Movie on Canvas.
         */
        setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        //获取到属性以及属性值，以Resource的形式存到TypedArray的对象中，TypeArray使用结束后必须要回收
        val array = context.obtainStyledAttributes(
            attrs, R.styleable.GifView,
            defStyle, R.style.GifView
        )

        //-1 is default value
        mMovieResourceId = array.getResourceId(R.styleable.GifView_gif, -1)
        mPaused = array.getBoolean(R.styleable.GifView_paused, false)

        //手动回收TypedArray对象  内存泄漏 内存溢出
        array.recycle()

        if (mMovieResourceId != -1) {
            //解析出gif的每一帧
            mMovie = Movie.decodeStream(resources.openRawResource(mMovieResourceId))
        }
    }

    fun setMovieResource(movieResId: Int) {
        this.mMovieResourceId = movieResId
        mMovie = Movie.decodeStream(resources.openRawResource(mMovieResourceId))
        requestLayout()
    }

    fun setMovieResource(inputStream: InputStream) {
        mMovie = Movie.decodeStream(inputStream)
        requestLayout()
    }

    fun setMovieTime(time: Int) {
        mCurrentAnimationTime = time
        invalidate()
    }

    @SuppressLint("NewApi")
    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)
        mVisible = visibility == View.VISIBLE
        invalidateView()
    }

    override fun onWindowVisibilityChanged(visibility: Int) {
        super.onWindowVisibilityChanged(visibility)
        mVisible = visibility == View.VISIBLE
        invalidateView()
    }

    override fun onDraw(canvas: Canvas) {
        mMovie?.let {
            if (!mPaused) {
                updateAnimationTime()
                drawMovieFrame(canvas)
                invalidateView()
            } else {
                drawMovieFrame(canvas)
            }
        }
    }

    /**
     * Calculate current animation time
     */
    private fun updateAnimationTime() {
        val now = android.os.SystemClock.uptimeMillis()

        if (mMovieStart == 0L) {
            mMovieStart = now
        }

        var dur = mMovie?.duration() ?: 0

        if (dur == 0) {
            dur = DEFAULT_MOVIE_DURATION
        }

        mCurrentAnimationTime = ((now - mMovieStart) % dur).toInt()
    }

    /**
     * Draw current GIF frame
     */
    private fun drawMovieFrame(canvas: Canvas) {

        mMovie?.setTime(mCurrentAnimationTime)

        canvas.save()
        canvas.scale(mScaleX, mScaleY)
        mMovie?.draw(canvas, mLeft / mScaleX, mTop / mScaleX)
        canvas.restore()
    }

    //以下三个方法是处理突发状况，屏幕的方向改变，可见状态改变，窗口可见状态改变都需要重绘
    @SuppressLint("NewApi")
    override fun onScreenStateChanged(screenState: Int) {
        super.onScreenStateChanged(screenState)
        mVisible = screenState == View.SCREEN_STATE_ON
        invalidateView()
    }

    //居中显示
    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)

        mLeft = (width - mMeasuredMovieWidth) / 2f
        Log.i(GifView::class.java.simpleName, "$width, height: $height")
        mTop = (height - mMeasuredMovieHeight) / 2f

        mVisible = visibility == View.VISIBLE
    }

    //主要是处理 layout_width和 layout_height 的不同赋值的情况
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        mMovie?.let {
            val movieWidth = it.width()
            val movieHeight = it.height()


            var scaleW = 1f
            val measureModeWidth = View.MeasureSpec.getMode(widthMeasureSpec)

            if (measureModeWidth != View.MeasureSpec.UNSPECIFIED) {
                val maximumWidth = View.MeasureSpec.getSize(widthMeasureSpec)
                scaleW = maximumWidth.toFloat() / movieWidth.toFloat()
            }

            var scaleH = 1f
            val measureModeHeight = View.MeasureSpec.getMode(heightMeasureSpec)

            if (measureModeHeight != View.MeasureSpec.UNSPECIFIED) {
                val maximumHeight = View.MeasureSpec.getSize(heightMeasureSpec)
                scaleH = maximumHeight.toFloat() / movieHeight.toFloat()
            }

            mScaleX = scaleW
            mScaleY = scaleH

            mMeasuredMovieWidth = (movieWidth * mScaleX).toInt()
            mMeasuredMovieHeight = (movieHeight * mScaleY).toInt()

            setMeasuredDimension(mMeasuredMovieWidth, mMeasuredMovieHeight)
            return
        }

        setMeasuredDimension(suggestedMinimumWidth, suggestedMinimumHeight)

    }

    /**
     * Invalidates view only if it is visible. <br></br> [.postInvalidateOnAnimation] is used for Jelly Bean and
     * higher.
     */
    @SuppressLint("NewApi")
    private fun invalidateView() {
        if (mVisible) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                postInvalidateOnAnimation()
            } else {
                invalidate()
            }
        }
    }

    companion object {
        private const val DEFAULT_MOVIE_DURATION = 1000
    }
}
