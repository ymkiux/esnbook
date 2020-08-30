package wooyun.esnb.cursom;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.os.Build;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;

public class CuNo_EditText extends AppCompatEditText {
    public CuNo_EditText(Context context) {
        super(context);
        initPaint();
    }

    public CuNo_EditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaint();
    }

    public CuNo_EditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initPaint();
    }

    private void initPaint() {

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onDraw(Canvas canvas) {
        //设置画笔
        @SuppressLint("DrawAllocation") Paint mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);//描边
        mPaint.setColor(Color.LTGRAY);//画笔颜色
        /*
         * PathEffect有6个子类：
         * CornerPathEffect(float radius)：给path加一些圆角，只有一个构造方法，radius代表圆角半径
         * DashPathEffect(float[] intervals,float phase):实现一个虚实线的效果，intervals就是虚线效果中实线和间隔的长度数组，intervals数组长度至少为2；phase指虚线相位，动态改变它，可是实现虚线移动效果
         * DiscretePathEffect(float segmentLength,float deviation):实现path边界藤蔓丛生的效果；path会被分成长度为segmentLength的碎片，然后在每个碎片上会随机产生以deviation为基数的偏离，这样藤蔓效果就产生了
         * PathDashPathEffect(Path shape,float advance,float phase,Style style):讲path的边界用一下小path首尾相连展现出来；shape就是用来组成path的小图形，advance是shape之间的距离；phase是相位，用来实现path移动的效果
         * ComposePathEffect和SumPathEffect都是讲两个PathEffect组合起来，达到一种组合效果；只不过ComposePahtEffect是两种效果组合起来，而SumPathEffect是将梁红在那个效果叠加起来
         * 值得注意的是，PathEffect虽然名字叫Path的Effect,但它的效果不仅仅局限在path，而是作用paint绘制的所有内容，包括文字
         * 更多详情，自行百度,效果见下图。
         * */
        @SuppressLint("DrawAllocation") PathEffect effects = new DashPathEffect(new float[]{5, 5, 5, 5}, 5);
        mPaint.setPathEffect(effects);

        /* 视图的left ， top ， right ， bottom 的值是针对其父视图的相对位置，所有属性均在下图标出*/
        int left = getLeft();//子View左边距离父view原点的距离
        int right = getRight();//子View右边距离父view原点的距离
        int paddingTop = getPaddingTop();//view的内容到view上面的距离
        int paddingBottom = getPaddingBottom();//view的内容到view下面的距离  30
        int paddingLeft = getPaddingLeft();//view的内容到view左边的距离  60
        int paddingRight = getPaddingRight();//右边留白  60
        int height = getHeight();//view高度 996
        int lineHeight = getLineHeight();//返回一行的高度。注意标记内的文本可能高于或低于这个高度，布局可能包含额外的第一行或最后一行填充的部分。我们只是获取一行文字的高度，不计算间距。 74
        int spcingHeight = (int) getLineSpacingExtra();//获取行距，就是每行文字距离上下横线的距离  15

        int count = (height - paddingTop - paddingBottom) / lineHeight;//横线条数  12

        for (int i = 0; i < count; i++) {
            int baseline = lineHeight * (i + 1) + paddingTop - spcingHeight / 2;//得到第一行线，距离view上面的高度 97  171
            canvas.drawLine(paddingLeft, (int) (baseline * 1.0), right - paddingRight, (int) (baseline * 1.0), mPaint);//划线
        }
        super.onDraw(canvas);

    }
}
