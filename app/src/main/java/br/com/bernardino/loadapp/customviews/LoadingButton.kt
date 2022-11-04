package br.com.bernardino.loadapp.customviews

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.view.View
import androidx.core.content.withStyledAttributes
import br.com.bernardino.loadapp.R
import kotlin.properties.Delegates


class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {


    private var animationDuration: Long=0
    private var textColor: Int = 0
    private var progressBarColor: Int = 0
    private var progressCircleColor: Int = 0
    private lateinit var loadingText: String
    private lateinit var loadCompleteText: String

    private var radius = 0.0f
    private val loadingRect = Rect()
    private var progress = 0
    private val fullRect = RectF()
    private val rectOfArc = RectF()

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 44.0f
        typeface = Typeface.create("", Typeface.BOLD)
    }

    private var valueAnimator = ValueAnimator()

    init {
        isClickable = true

        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            textColor = getColor(R.styleable.LoadingButton_textColor, 0)
            progressBarColor = getColor(R.styleable.LoadingButton_progressbarColor, 0)
            progressCircleColor = getColor(R.styleable.LoadingButton_progressCircleColor, 0)
            animationDuration = getInteger(R.styleable.LoadingButton_animationDuration, 3000).toLong()
            loadingText = getString(R.styleable.LoadingButton_loadingText).toString()
            loadCompleteText = getString(R.styleable.LoadingButton_loadCompleteText).toString()
        }
    }

    var state: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->

        if (new != ButtonState.Completed) {
            updateAnimation(new)

        }
        if (new == ButtonState.Completed) {
            valueAnimator.currentPlayTime
            valueAnimator.cancel()
            requestLayout()
            invalidate()

        }
    }

    private fun updateAnimation(new: ButtonState) {
        valueAnimator = ValueAnimator.ofInt(0, 360).setDuration(animationDuration).apply {
            addUpdateListener {
                progress = it.animatedValue as Int
                if (!progressIsNotCompleted() && state == ButtonState.Loading) {
                    state = ButtonState.Completed
                } else {
                    requestLayout()
                    invalidate()
                }
            }

            if (new == ButtonState.Clicked) {
                repeatCount = ValueAnimator.INFINITE
            }
            repeatMode = ValueAnimator.RESTART

            start()
        }
    }

    private var widthSize = 0
    private var heightSize = 0

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        radius = ((Integer.min(width, height)) / 2.0 * 0.8).toFloat()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        loadingBackground(canvas)

        if (state != ButtonState.Completed) {
            drawLoadingInnerBar(canvas)
            drawLoadingCircle(canvas)
        }
        drawLabel(canvas)
    }

    private fun progressIsNotCompleted(): Boolean = progress < 360

    private fun loadingBackground(canvas: Canvas) {
        if (background is ColorDrawable){
            paint.color = (background as ColorDrawable).color
        }

        fullRect.set(0f, 0f, width.toFloat(), height.toFloat())
        canvas.drawRect(fullRect, paint)
    }

    private fun drawLoadingInnerBar(canvas: Canvas) {
        loadingRect.set(0, 0, width * progress / 360, height)
        paint.color = progressBarColor
        canvas.drawRect(loadingRect, paint)
    }

    private fun drawLoadingCircle(canvas: Canvas) {
        paint.color = progressCircleColor
        rectOfArc.set(
            width - height.toFloat() / 1.25f,
            height.toFloat() / 4f,
            width - height.toFloat() / 4f,
            height.toFloat() / 1.25f
        )
        canvas.drawArc(rectOfArc, 270f, progress.toFloat(), true, paint)
    }

    private fun drawLabel(canvas: Canvas) {
        paint.color = textColor
        val label = if (state != ButtonState.Completed) {
            loadingText
        } else {
            loadCompleteText
        }
        canvas.drawText(label, width.toFloat() / 2, (height.toFloat() / 2f) + 44 / 3, paint)
    }
}