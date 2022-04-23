package com.example.freehandcropdemo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class SomeView extends View implements View.OnTouchListener {
    private Paint paint;
    private List<Point> points;
    boolean flgPathDraw = true;
    Point mfirstpoint = null;
    boolean bfirstpoint;
    Point mlastpoint = null;
    Bitmap bitmap;
    Context mContext;

    public SomeView(Context c, Bitmap bitmap) {
        super(c);
        mContext = c;
        this.bitmap = bitmap;
        setFocusable(true);
        setFocusableInTouchMode(true);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setPathEffect(new DashPathEffect(new float[]{10, 20}, 0));
        paint.setStrokeWidth(5);
        paint.setColor(Color.RED);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        this.setOnTouchListener(this);
        points = new ArrayList<>();
        bfirstpoint = false;
    }

    public void onDraw(Canvas canvas) {
        canvas.drawBitmap(bitmap, 0, 0, null);
        Path path = new Path();
        boolean first = true;

        for (int i = 0; i < points.size(); i += 2) {
            Point point = points.get(i);
            if (first) {
                first = false;
                path.moveTo(point.x, point.y);
            } else if (i < points.size() - 1) {
                Point next = points.get(i + 1);
                path.quadTo(point.x, point.y, next.x, next.y);
            } else {
                mlastpoint = points.get(i);
                path.lineTo(point.x, point.y);
            }
        }
        canvas.drawPath(path, paint);
    }

    public boolean onTouch(View view, MotionEvent event) {
        Point point = new Point();
        point.x = (int) event.getX();
        point.y = (int) event.getY();

        if (flgPathDraw) {
            if (bfirstpoint) {
                if (comparePoint(mfirstpoint, point)) {
                    points.add(mfirstpoint);
                    flgPathDraw = false;
                    showcropdialog();
                } else {
                    points.add(point);
                }
            } else {
                points.add(point);
            }

            if (!(bfirstpoint)) {
                mfirstpoint = point;
                bfirstpoint = true;
            }
        }

        invalidate();

        if (event.getAction() == MotionEvent.ACTION_UP) {
            mlastpoint = point;
            if (flgPathDraw) {
                if (points.size() > 12) {
                    if (!comparePoint(mfirstpoint, mlastpoint)) {
                        flgPathDraw = false;
                        points.add(mfirstpoint);
                        showcropdialog();
                    }
                }
            }
        }
        return true;
    }

    private boolean comparePoint(Point first, Point current) {
        int left_range_x = current.x - 3;
        int left_range_y = current.y - 3;

        int right_range_x = current.x + 3;
        int right_range_y = current.y + 3;

        if ((left_range_x < first.x && first.x < right_range_x)
                && (left_range_y < first.y && first.y < right_range_y)) {
            return points.size() >= 10;
        } else {
            return false;
        }
    }

    public void resetView() {
        points.clear();
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setPathEffect(new DashPathEffect(new float[]{10, 20}, 0));
        paint.setStrokeWidth(5);
        paint.setColor(Color.RED);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        points = new ArrayList<>();
        bfirstpoint = false;

        flgPathDraw = true;
        invalidate();
    }

    private void showcropdialog() {
        DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    ((MainActivity) mContext).cropImage();
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    resetView();
                    break;
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("Do you want to save image?")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show()
                .setCancelable(false);
    }

    public List<Point> getPoints() {
        return points;
    }
}
