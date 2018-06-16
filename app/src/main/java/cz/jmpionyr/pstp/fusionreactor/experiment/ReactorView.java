package cz.jmpionyr.pstp.fusionreactor.experiment;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.util.AttributeSet;
import android.view.View;


public class ReactorView extends View {

    private ShapeDrawable mDrawable;
    private int width = 0;
    private int height = 0;

    public ReactorView(Context context) {
        super(context);
        init(null, 0);
    }

    public ReactorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public ReactorView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {

        mDrawable = new ShapeDrawable(new OvalShape());
        // If the color isn't set, the shape uses black as the default.
        mDrawable.getPaint().setColor(0xff2ad4ff);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        width = getWidth();
        height = getHeight();

        int max_size = width < height ? width : height;
        int size_unit = max_size / 10;

        // If the bounds aren't set, the shape can't be drawn.
        moveCircle(mDrawable, size_unit * 6, 0, 0);
        mDrawable.draw(canvas);

        for (int i = 0; i < 360; i += 360/8) {
            // mDrawable.getPaint().setColor(0xff2004ff);
            moveCircleOnSphere(mDrawable, size_unit, size_unit * 4,  i);
            mDrawable.draw(canvas);
        }
    }

    private void moveCircle(Drawable drawable, int size, int x, int y) {

        drawable.setBounds(
                (width - size) / 2 + x,
                (height - size) / 2 + y,
                (width + size) / 2 + x,
                (height + size) / 2 + y
        );
    }

    private void moveCircleOnSphere(Drawable drawable, int size, int radius, int angle) {
        double angle_in_radians = Math.toRadians(angle);

        moveCircle(
                drawable,
                size,
                (int) (radius * Math.sin(angle_in_radians)),
                (int) (radius * Math.cos(angle_in_radians))
        );
    }
}
