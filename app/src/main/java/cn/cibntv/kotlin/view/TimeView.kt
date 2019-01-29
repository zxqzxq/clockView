package cn.cibntv.kotlin.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import java.text.SimpleDateFormat
import java.util.*

/**
 * zxq 自定义时钟
 */
class TimeView : View {
    //外圆画笔
    private var paint: Paint = Paint()
    //文字画笔
    private val paintNum: Paint = Paint()
    //时钟画笔
    private val paintHour: Paint = Paint()
    //分钟画笔
    private val paintMinute: Paint = Paint()
    //秒钟画笔
    private val paintSecond: Paint = Paint()
    //外圆圆心
    private var circle_x: Float = 0f
    private var circle_y: Float = 0f
    //外圆半径
    private var r: Int = 0

    private var num_x: Float = 0f
    private var num_y: Float = 0f

    constructor(context: Context?) : super(context) {
        initPaint()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        initPaint()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initPaint()
    }

    /**
     * 初始化画笔
     */
    private fun initPaint() {
        paint.apply {
            color = Color.BLACK
            isAntiAlias = true
            strokeWidth = 3.toFloat()
            style = Paint.Style.STROKE
        }

        paintNum.apply {
            color = Color.BLACK
            isAntiAlias = true
            strokeWidth = 3.toFloat()
            style = Paint.Style.STROKE
            textAlign = Paint.Align.CENTER
            textSize = 35.toFloat()

        }

        paintSecond.apply {
            color = Color.RED
            isAntiAlias = true
            strokeWidth = 5.toFloat()
            style = Paint.Style.FILL
        }

        paintMinute.apply {
            color = Color.BLACK
            isAntiAlias = true
            strokeWidth = 8.toFloat()
            style = Paint.Style.FILL
        }

        paintHour.apply {
            color = Color.BLACK
            isAntiAlias = true
            strokeWidth = 13.toFloat()
            style = Paint.Style.FILL
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        circle_x = (measuredWidth / 2).toFloat()
        circle_y = (measuredHeight / 2).toFloat()
        r = circle_x.toInt() - 5
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        //绘制外圆
        canvas?.drawCircle(circle_x, circle_y, r.toFloat(), paint)
        //绘制时钟数字
        drawTimeText(canvas)
        //绘制刻度
        drawLines(canvas)

        //绘制圆点
        canvas?.drawCircle(circle_x, circle_y, 15f, paintMinute)

        initCurrentTime(canvas)

        postInvalidateDelayed(1000)
    }

    private fun initCurrentTime(canvas: Canvas?) {
        //获取系统当前时间
        val format = SimpleDateFormat("HH-mm-ss")
        val time = format.format(Date(System.currentTimeMillis()))
        val split = time.split("-")
        val hour = Integer.parseInt(split[0])
        val minute = Integer.parseInt(split[1])
        val second = Integer.parseInt(split[2])

        //时针走过的角度
        val hourAngle = hour * 30 + minute / 2
        //分针走过的角度
        val minuteAngle = minute * 6 + second / 10
        //秒针走过的角度
        val secondAngle = second * 6

        //绘制时钟,以12整点为0°参照点
        canvas?.rotate(hourAngle.toFloat(), circle_x, circle_y)
        canvas?.drawLine(circle_x, circle_y, circle_x, circle_y - r + 150, paintHour)
        canvas?.save()
        canvas?.restore()
        //这里画好了时钟，我们需要再将画布转回来,继续以12整点为0°参照点
        canvas?.rotate(-hourAngle.toFloat(), circle_x, circle_y)

        //绘制分钟
        canvas?.rotate(minuteAngle.toFloat(), circle_x, circle_y)
        canvas?.drawLine(circle_x, circle_y, circle_x, circle_y - r + 60, paintMinute)
        canvas?.save()
        canvas?.restore()
        //这里同上
        canvas?.rotate(-minuteAngle.toFloat(), circle_x, circle_y)

        //绘制秒钟
        canvas?.rotate(secondAngle.toFloat(), circle_x, circle_y)
        canvas?.drawLine(circle_x, circle_y, circle_x, circle_y - r + 20, paintSecond)
    }

    private fun drawLines(canvas: Canvas?) {
        for (i in 0..60) {
            when (i % 5) {
                //绘制整点刻度
                0 -> {
                    paint.strokeWidth = 8.toFloat()
                    canvas?.drawLine(circle_x, circle_y - r, circle_x, circle_y - r + 40, paint)
                }
                else -> {
                    paint.strokeWidth = 3.toFloat()
                    canvas?.drawLine(circle_x, circle_y - r, circle_x, circle_y - r + 30, paint)
                }
            }
            //绕着(x,y)旋转6°
            canvas?.rotate(6f, circle_x, circle_y)
        }
    }

    private fun drawTimeText(canvas: Canvas?) {
        //获取文字高度
        var textSize = paintNum.fontMetrics.bottom - paintNum.fontMetrics.top
        // 数字离圆心的距离,40为刻度的长度,20文字大小
        var distance = r - 40 - 20
        // 每30°写一个数字
        for (i in 0..12) {
            num_x = (distance * Math.sin(i * 30 * Math.PI / 180) + circle_x).toFloat()
            num_y = (circle_y - distance * Math.cos(i * 30 * Math.PI / 180)).toFloat()
            when (i) {
                0 -> canvas?.drawText("12", num_x, num_y + textSize / 3, paintNum)
                else -> canvas?.drawText(i.toString(), num_x, num_y + textSize / 3, paintNum)
            }
        }

    }
}