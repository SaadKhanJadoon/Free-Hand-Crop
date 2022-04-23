package com.example.freehandcropdemo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Region;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Bitmap mBitmap;
    private SomeView mSomeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.image);
        mSomeView = new SomeView(this, mBitmap);
        LinearLayout layout = findViewById(R.id.layout);
        LinearLayout.LayoutParams lp =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
        layout.addView(mSomeView, lp);
    }

    public void cropImage() {
        setContentView(R.layout.activity_picture_preview);
        ImageView imageView = findViewById(R.id.image);

        Bitmap fullScreenBitmap =
                Bitmap.createBitmap(mSomeView.getWidth(), mSomeView.getHeight(), mBitmap.getConfig());

        Canvas canvas = new Canvas(fullScreenBitmap);

        Path path = new Path();
        List<Point> points = mSomeView.getPoints();
        for (int i = 0; i < points.size(); i++) {
            path.lineTo(points.get(i).x, points.get(i).y);
        }

        // Cut out the selected portion of the image...
        Paint paint = new Paint();
        canvas.drawPath(path, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(mBitmap, 0, 0, paint);

        // Frame the cut out portion...
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5f);
        canvas.drawPath(path, paint);

        // Create a bitmap with just the cropped area.
        Region region = new Region();
        Region clip = new Region(0, 0, fullScreenBitmap.getWidth(), fullScreenBitmap.getHeight());
        region.setPath(path, clip);
        Rect bounds = region.getBounds();
        Bitmap croppedBitmap =
                Bitmap.createBitmap(fullScreenBitmap, bounds.left, bounds.top,
                        bounds.width(), bounds.height());

        imageView.setImageBitmap(croppedBitmap);
    }
}