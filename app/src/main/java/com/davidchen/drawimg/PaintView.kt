package com.davidchen.drawimg

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.view.MotionEvent
import android.view.View
import java.util.*

class PaintView(context: Context?) : View(context) {

    private var _color: Int = Color.BLACK
    private var _strokeWidth = 0
    private val stack: Stack<MyPath> = Stack()

    var color: Int
        get() = _color
        set(value) {
            _color = value
        }

    var strokeWidth: Int
        get() = _strokeWidth
        set(value) {
            _strokeWidth = value
        }

    fun back(): Boolean {
        postInvalidate()
        return if (stack.isEmpty()) {
            false
        } else {
            stack.pop()
            true
        }
    }

    fun clear() {
        stack.clear()
        postInvalidate()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val px = event.x
        val py = event.y

        when(event.action) {
            MotionEvent.ACTION_DOWN -> {
                stack.push(MyPath(_color, strokeWidth))
                stack.peek().path.moveTo(px, py)
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                stack.peek().path.lineTo(px, py)
            }
            else -> {
                return false
            }
        }
        postInvalidate()
        return false
    }

    override fun onDraw(canvas: Canvas?) {
        if (!stack.empty()) {
            for (s in stack) {
                canvas?.drawPath(s.path, s.brush)
            }
        }
    }

    class MyPath(color: Int, strokeWidth: Int) {
        val path = Path()
        val brush = Paint()
        var strokeWidth: Float = 0.0f

        init {
            this.strokeWidth = strokeWidth.toFloat()
            brush.apply {
                isAntiAlias = true
                this.color = color
                style = Paint.Style.STROKE
                strokeJoin = Paint.Join.ROUND
                this.strokeWidth = strokeWidth.toFloat()
            }
        }
    }
}