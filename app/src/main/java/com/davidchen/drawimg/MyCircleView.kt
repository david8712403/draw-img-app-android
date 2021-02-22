package com.davidchen.drawimg

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

/**
 * MyCircleView
 */
class MyCircleView : View {

    private var _circleColor: Int = Color.BLUE
    private var _ringColor: Int = Color.BLACK
    private var _strokeWidth: Float = 3F
    private var _isCheck: Boolean = false
    private var _circlePaint: Paint? = null
    private var _ringPaint: Paint? = null

    /**
     * The circle color
     */
    var circleColor: Int
        get() = _circleColor
        set(value) {
            _circleColor = value
            invalidate()
        }

    /**
     * The ring color
     */
    var ringColor: Int
        get() = _ringColor
        set(value) {
            _ringColor = value
            invalidate()
        }

    /**
     * stroke width
     */
    var strokeWidth: Float
        get() = _strokeWidth
        set(value) {
            _strokeWidth = value
            invalidate()
        }

    /**
     * In the example view, this dimension is the font size.
     */
    var isCheck: Boolean
        get() = _isCheck
        set(value) {
            _isCheck = value
            invalidate()
        }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init(attrs, defStyle)
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        // Load attributes
        val a = context.obtainStyledAttributes(
            attrs, R.styleable.MyCircleView, defStyle, defStyle
        )

        _circleColor = a.getColor(
            R.styleable.MyCircleView_circleColor,
            circleColor
        )

        _ringColor = a.getColor(
            R.styleable.MyCircleView_ringColor,
            ringColor
        )

        _strokeWidth = a.getDimension(
            R.styleable.MyCircleView_mStrokeWidth,
            strokeWidth
        )

        _isCheck = a.getBoolean(
            R.styleable.MyCircleView_isCheck,
            isCheck
        )

        _circlePaint = Paint()
        _ringPaint = Paint()

        invalidate()
        a.recycle()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // allocations per draw cycle.
        val paddingLeft = paddingLeft
        val paddingTop = paddingTop
        val paddingRight = paddingRight
        val paddingBottom = paddingBottom

        val contentWidth = width - paddingLeft - paddingRight
        val contentHeight = height - paddingTop - paddingBottom

        val radius = contentWidth.div(2)

        _circlePaint?.apply {
            style = Paint.Style.FILL
            color = _circleColor
            isAntiAlias = true
        }
        _circlePaint?.let {
            canvas.drawCircle(
                paddingLeft.toFloat().plus(radius),
                paddingTop.toFloat().plus(radius),
                contentWidth.toFloat().div(2).minus(_strokeWidth.times(2)),
                it
            )
        }

        if (_isCheck) {
            _ringPaint?.apply {
                style = Paint.Style.STROKE
                color = _ringColor
                isAntiAlias = true
                strokeWidth = _strokeWidth
            }
            _ringPaint?.let {
                canvas.drawCircle(
                        paddingLeft.toFloat().plus(radius),
                        paddingTop.toFloat().plus(radius),
                        contentWidth.toFloat().div(2).minus(_strokeWidth.div(2)),
                        it
                )
            }
        }

    }
}