package com.lucerotech.aleksandrp.mycirclechartspedometr;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import static com.lucerotech.aleksandrp.mycirclechartspedometr.MainActivity.PICKER_BONE_MASS;
import static com.lucerotech.aleksandrp.mycirclechartspedometr.MainActivity.PICKER_CALORIES;
import static com.lucerotech.aleksandrp.mycirclechartspedometr.MainActivity.PICKER_FAT;
import static com.lucerotech.aleksandrp.mycirclechartspedometr.MainActivity.PICKER_FAT_LEVEL;
import static com.lucerotech.aleksandrp.mycirclechartspedometr.MainActivity.PICKER_MUSCLE_MASS;
import static com.lucerotech.aleksandrp.mycirclechartspedometr.MainActivity.PICKER_WATER;
import static com.lucerotech.aleksandrp.mycirclechartspedometr.MainActivity.PICKER_WEIGHT;

/**
 * Created by AleksandrP on 06.10.2016.
 */

public class CircleBackground extends View   {


    private static final String TAG = CircleBackground.class.getSimpleName();

    private int TIME_UPDATE_INVALIDATE = 250;

    private int resRaindow;

    private String colorBgMain;
    private String colorBacgroundInCircle, colorBacgroundInCircleGradient, colorRainbow = "#4bf7f9";
    private String colorOutCircleFrom, colorOutCircleTo;
    private String colorInnerCircleFrom, colorInnerCircleTo;
    private String colorSegmentBig, colorSegmentSmall, colorLines;


    // TODO: 19.10.2016 эти параметры надо сделать общими, для установки в граффике
    private float startArc;
    private float middleArc;
    private float endArc;

    private float arcForRotationRainbow;

    //    private Handler handler;

    // rainbow
    private RectF rimRainbowRect;
    private Paint rimCirclePaintRainbow;

    private RectF rimRainbowSmallRect;
    private Paint rimCircleSmallPaintRainbow;

    // for lines
    private Paint paintArcBig;
    private Paint paintArcSmall;
    private Paint paintLine;

    // drawing tools
    private RectF rimRect;
    private Paint rimPaint;
    private Paint rimCirclePaint;

    private RectF faceRect;
    //    private Bitmap faceTexture;
//    private Paint facePaint;
    private Paint rimShadowPaint;

    // шкала
    private Path textPath;     // text decimal
    private Paint scaleText;
    private Paint scalePaint;
    private Paint scaleInnerPaintTransparent;// внутренний круг
    private Paint scaleInnerPaint;// внутренний круг
    private RectF scaleRect;        // круг
    private RectF scaleInnerRect;        // внутренний круг

    // text title
    private Paint titlePaint;
    private Path titlePath;

    private Paint logoPaint;
    private Bitmap logo;
    private Matrix logoMatrix;
    private float logoScale;

    private Paint handPaint;
    private Path handPath;
    private Paint handScrewPaint;

    private Paint backgroundPaint;
    // end drawing tools

    private Bitmap background; // holds the cached static part

    // scale configuration
    private int countWicks = 5;        // количество штрихов в разделители
    private static int totalNicks;       // общее количество разделителей
    private static int nicksShowing;  // сколько будет показано
    private static float degreesPerNick;        // градусов на 1 шьрих
    private static final int centerDegree = (0); // the one in the top center (12 o'clock)
    private int minDegrees;
    private int maxDegrees;
    private static final int showTextItem = 10;


    // цвет
    private static int colorWicks;
    private static int colorTitle;
    private static int color;

    // text размер
    private static final float textSize = 0.05f;       // сентер

    public CircleBackground(Context context) {
        super(context);
        init();
    }

    public CircleBackground(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircleBackground(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
//        attachToSensor();
    }

    @Override
    protected void onDetachedFromWindow() {
//        detachFromSensor();
        super.onDetachedFromWindow();
    }

    private void init() {
        setParamsColorString();
        initDrawingTools();
    }

    private void initDrawingTools() {
        float rimRaindowSize = 0.03f;      // ширина обода
        rimRainbowRect = new RectF(0.03f, 0.03f, 0.96f, 0.96f);
        rimRainbowSmallRect = new RectF(rimRainbowRect.left + rimRaindowSize,
                rimRainbowRect.top + rimRaindowSize,
                rimRainbowRect.right - rimRaindowSize,
                rimRainbowRect.bottom - rimRaindowSize);
        rimRect = new RectF(0.12f, 0.12f, 0.88f, 0.88f);


        // the linear gradient is a bit skewed for realism
        rimPaint = new Paint();
        rimPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        rimPaint.setShader(new LinearGradient(0.0f, 0.40f, 0.90f, 0.50f,
                Color.parseColor(colorOutCircleFrom),
                Color.parseColor(colorOutCircleTo),
                Shader.TileMode.CLAMP));

        rimCirclePaintRainbow = new Paint();
        rimCirclePaintRainbow.setStyle(Paint.Style.FILL);
        rimCirclePaintRainbow.setShader(new LinearGradient(
                0.0f, 0.50f, 0.40f, 0.550f,
                Color.parseColor(colorRainbow),
                Color.TRANSPARENT,
                Shader.TileMode.CLAMP));
        rimCirclePaintRainbow.setStrokeWidth(0.015f);


        rimCircleSmallPaintRainbow = new Paint();
        rimCircleSmallPaintRainbow.setAntiAlias(true);
        rimCircleSmallPaintRainbow.setStyle(Paint.Style.FILL);
        rimCircleSmallPaintRainbow.setColor(Color.parseColor(colorBgMain));
//        rimCircleSmallPaintRainbow.setShader(new LinearGradient(
//                0.0f, 0.50f, 0.40f, 0.550f,
//                Color.TRANSPARENT,
//                Color.parseColor(colorBgMain),
//                Shader.TileMode.CLAMP));
        rimCircleSmallPaintRainbow.setStrokeWidth(0.015f);


        paintArcBig = new Paint();
        paintArcBig.setColor(Color.parseColor(colorSegmentBig));

        paintArcSmall = new Paint();
        paintArcSmall.setColor(Color.parseColor(colorSegmentSmall));

        paintLine = new Paint();
        paintLine.setColor(Color.parseColor(colorLines));
//        paintLine.setColor(Color.BLUE);
        paintLine.setStrokeWidth(0.003f);

        rimCirclePaint = new Paint();
        rimCirclePaint.setAntiAlias(true);
        rimCirclePaint.setStyle(Paint.Style.STROKE);
        rimCirclePaint.setColor(Color.argb(0x4f, 0x33, 0x36, 0x33));
        rimCirclePaint.setStrokeWidth(0.005f);

        float rimSize = 0.005f;      // ширина обода
        faceRect = new RectF();
        faceRect.set(rimRect.left + rimSize,
                rimRect.top + rimSize,
                rimRect.right - rimSize,
                rimRect.bottom - rimSize);


        rimShadowPaint = new Paint();
        rimShadowPaint.setColor(Color.parseColor(colorBacgroundInCircle));
        rimShadowPaint.setStyle(Paint.Style.FILL);

        // шкала и текст
        scalePaint = new Paint();
        scalePaint.setStyle(Paint.Style.FILL);
//        scalePaint.setColor(colorWicks);
        scalePaint.setShader(new LinearGradient(0.0f, 0.0f, 0.750f, 0.750f,
                Color.parseColor(colorInnerCircleFrom),
                Color.parseColor(colorInnerCircleTo),
                Shader.TileMode.CLAMP));
        scalePaint.setStrokeWidth(0.005f);
        scalePaint.setAntiAlias(true);

        // шкала и текст
        scaleInnerPaint = new Paint();
        scaleInnerPaint.setStyle(Paint.Style.FILL);
        scaleInnerPaint.setColor(Color.parseColor(colorBacgroundInCircle));
        scaleInnerPaint.setStrokeWidth(0.005f);
        scaleInnerPaint.setAntiAlias(true);

        scaleInnerPaintTransparent = new Paint();
        scaleInnerPaintTransparent.setStyle(Paint.Style.FILL);
//        scaleInnerPaintTransparent.setColor(Color.parseColor(colorBacgroundInCircle));
        scaleInnerPaintTransparent.setShader(new RadialGradient(
                rimRainbowRect.centerX(),
                rimRainbowRect.centerY(),
                rimRainbowSmallRect.width(),
                Color.parseColor(colorBacgroundInCircle),
//                Color.colorBacgroundInCircle,
//                Color.TRANSPARENT,
                Color.parseColor(colorBacgroundInCircleGradient),
                Shader.TileMode.REPEAT));
//        scaleInnerPaintTransparent.setAntiAlias(true);

        // текст
        // текст на шкале
        scaleText = new Paint();
        scaleText.setStyle(Paint.Style.FILL);
        scaleText.setColor(colorWicks);
        scaleText.setStrokeWidth(0.002f);
        scaleText.setAntiAlias(true);
        scaleText.setTextSize(textSize);
        scaleText.setTypeface(Typeface.SANS_SERIF);
        scaleText.setTextScaleX(0.5f);
        scaleText.setTextAlign(Paint.Align.CENTER);

        // текста
        textPath = new Path();
        textPath.addArc(new RectF(0.24f, 0.24f, 0.76f, 0.76f), 0.0f, -180.0f);
//        textPath.addArc(new RectF(0.24f, 0.24f, 0.76f, 0.76f), -120.0f, -180.0f);


        // шкала
        // уменьшение квадрата внутри основного квадрата на размер scalePosition
        float scalePosition = 0.1250f;
        scaleRect = new RectF();
        scaleRect.set(faceRect.left + scalePosition, faceRect.top + scalePosition,
                faceRect.right - scalePosition, faceRect.bottom - scalePosition);
        scaleInnerRect = new RectF();
        scaleInnerRect.set(scaleRect.left + rimSize, scaleRect.top + rimSize,
                scaleRect.right - rimSize, scaleRect.bottom - rimSize);

        titlePaint = new Paint();
        titlePaint.setColor(colorTitle);
        titlePaint.setAntiAlias(true);
        titlePaint.setTypeface(Typeface.DEFAULT_BOLD);
        titlePaint.setTextAlign(Paint.Align.CENTER);
        titlePaint.setTextSize(0.05f);
        titlePaint.setTextScaleX(0.8f);

        titlePath = new Path();
        titlePath.addArc(new RectF(0.24f, 0.24f, 0.76f, 0.76f), -180.0f, -180.0f);

        handPaint = new Paint();
        handPaint.setAntiAlias(true);
        handPaint.setColor(color);
        handPaint.setShadowLayer(0.01f, -0.005f, -0.005f, 0x7f000000);
        handPaint.setStyle(Paint.Style.FILL);

        handPath = new Path();
        handPath.moveTo(0.5f, 0.5f + 0.2f);
        handPath.lineTo(0.5f - 0.010f, 0.5f + 0.2f - 0.007f);
        handPath.lineTo(0.5f - 0.002f, 0.5f - 0.32f);
        handPath.lineTo(0.5f + 0.002f, 0.5f - 0.32f);
        handPath.lineTo(0.5f + 0.010f, 0.5f + 0.2f - 0.007f);
        handPath.lineTo(0.5f, 0.5f + 0.2f);
        handPath.addCircle(0.5f, 0.5f, 0.025f, Path.Direction.CW);

        handScrewPaint = new Paint();
        handScrewPaint.setAntiAlias(true);
        handScrewPaint.setColor(color);
        handScrewPaint.setStyle(Paint.Style.FILL);

        backgroundPaint = new Paint();
        backgroundPaint.setFilterBitmap(true);
    }

    @NonNull
    private void setParamsColorString() {
        String centerStr = "#000000", wicksStr = "#000000", titleStr = "#000000";

        resRaindow = R.drawable.rainbow_light_;

        // background main circle
        colorBgMain = "#FFE8E7E9";

        // background circle
        colorBacgroundInCircle = "#FFE8E7E9";
        colorBacgroundInCircleGradient = "#FFE8E7E9";

        // gradient out circle
        colorOutCircleFrom = "#000000";
        colorOutCircleTo = "#000000";

        // gradient inner circle
        colorInnerCircleFrom = "#000000";
        colorInnerCircleTo = "#000000";

        // color lines
        colorSegmentBig = "#54A1A1A1";
        colorSegmentSmall = "#9A4D4D4D";
        colorLines = "#AA000000";
//        colorLines = "#FF4081";

//        if (SettingsApp.getInstance().isThemeDark()) {
//            resRaindow = R.drawable.rainbow_dark_;
//
//            colorBgMain = "#FF071653";
//
//            colorBacgroundInCircle = "#0b1348";
//            colorBacgroundInCircleGradient = "#5A0B1348";
//
//            colorOutCircleFrom = "#4bf7f9";
//            colorOutCircleTo = "#0b1348";
//
//            colorInnerCircleFrom = "#0b1348";
//            colorInnerCircleTo = "#4bf7f9";
//
//            centerStr = "#4bf7f9";
//            wicksStr = "#ffffff";
//            titleStr = "#adadae";
//
//            colorSegmentBig = "#269BFBFD";
//            colorSegmentSmall = "#889BFBFD";
//            colorLines = "#4bf7f9";
//
//        }

        colorWicks = Color.parseColor(wicksStr);  // вся шкала
        colorTitle = Color.parseColor(titleStr);
        color = Color.parseColor(centerStr);       // сентер

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.d(TAG, "Width spec: " + MeasureSpec.toString(widthMeasureSpec));
        Log.d(TAG, "Height spec: " + MeasureSpec.toString(heightMeasureSpec));

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int chosenWidth = chooseDimension(widthMode, widthSize);
        int chosenHeight = chooseDimension(heightMode, heightSize);

        int chosenDimension = Math.min(chosenWidth, chosenHeight);

        setMeasuredDimension(chosenDimension, chosenDimension);
    }

    private int chooseDimension(int mode, int size) {
        if (mode == MeasureSpec.AT_MOST || mode == MeasureSpec.EXACTLY) {
            return size;
        } else { // (mode == MeasureSpec.UNSPECIFIED)
            return getPreferredSize();
        }
    }

    // in case there is no size specified
    private int getPreferredSize() {
        return 300;
    }


    // рисует радугу
    private void drawRainBow(Canvas canvas) {
        // first, draw the rainbow body
//        canvas.drawOval(rimRainbowRect, rimCirclePaintRainbow);
        canvas.drawOval(rimRainbowSmallRect, rimCircleSmallPaintRainbow);

    }

    // рисует внешний круг
    private void drawRim(Canvas canvas) {
        // first, draw the metallic body
        canvas.drawOval(rimRect, rimPaint);
        // now the outer rim circle
        canvas.drawOval(rimRect, rimCirclePaint);
    }

    // рисует фон
    private void drawFace(Canvas canvas) {
//        canvas.drawOval(faceRect, facePaint); // фон круга до границы
//         draw the inner rim circle
        canvas.drawOval(faceRect, rimCirclePaint); // фон круга до границы
        // draw the rim shadow inside the face
        canvas.drawOval(faceRect, rimShadowPaint);      // рисует внешнюю грань по кругу
    }

    // риссуем шкалу
    private void drawScale(Canvas canvas) {
        canvas.drawOval(scaleRect, scalePaint);
        canvas.drawOval(scaleInnerRect, scaleInnerPaint);

        canvas.save(Canvas.MATRIX_SAVE_FLAG);
        canvas.rotate(180, 0.5f, 0.5f);
        for (int i = 0; i < totalNicks; ++i) {
            float y1 = scaleRect.top - 0.02f;       // установка растояния начала штриха от круга
            float y2 = y1 - 0.02f; // длина штриха

            if (i >= 0 && i <= nicksShowing) {
                canvas.drawLine(0.5f, y1, 0.5f, y2, scalePaint);        // 1 штрих

                // вычисляем значение
                if (i % countWicks == 0) {
                    float value = nickToDegree(i);
                    if (value >= minDegrees && value <= maxDegrees) {
                        if (maxDegrees > 1000 && i % 2 != 0) {
                            continue;
                        }
                        int v = (int) (value / 5);      // округление числа до кратного 5
                        value = v * 5;
                        String valueString = Integer.toString((int) value);
                        drawTitle(canvas, valueString);
//                        canvas.drawTextOnPath(valueString, textPath, 0.0f, 0.1f, scaleText);
                    }
                }
            }

            canvas.rotate(degreesPerNick, 0.5f, 0.5f);
        }
        canvas.restore();
    }

    private void drawTitle(Canvas canvas, String mValueString) {
        //Save original font size
        float originalTextSize = scaleText.getTextSize();
        float unitPosition = 0.07f;

        // set a magnification factor
        final float magnifier = 50f;

        // Scale the canvas
        canvas.save();
        canvas.scale(1f / magnifier, 1f / magnifier);

        // create new rect and paths based on the new scale
        RectF unitRect = new RectF();
        unitRect.set((rimRect.left + unitPosition) * magnifier,
                (rimRect.top + unitPosition) * magnifier,
                (rimRect.right - unitPosition) * magnifier,
                (rimRect.bottom - unitPosition) * magnifier);
        Path unitPath = new Path();
        unitPath.addArc(unitRect, 180.0f, 180.0f);

        // increase the font size
        scaleText.setTextSize(originalTextSize * magnifier);

        // do the drawing of the text
        canvas.drawTextOnPath(mValueString, unitPath, 0.0f, 0.0f, scaleText);

        // bring everything back to normal
        canvas.restore();
        scaleText.setTextSize(originalTextSize);
        canvas.drawPath(unitPath, scaleText);
    }

    private float nickToDegree(int nick) {
        if (nick == 0) {
            return minDegrees;
        }
//        int i = (maxDegrees - minDegrees) / (showTextItem * countWicks);
        float i = (float) (maxDegrees - minDegrees) / (nicksShowing);     // цена деления

        float rawDegree = nick * i;
//        int rawDegree = ((nick < totalNicks / 2) ? nick : (nick - totalNicks)) * 2;
        float shiftedDegree = (rawDegree + minDegrees);
        return shiftedDegree;
    }


    private void drawLinesResalt(Canvas canvas) {

        float startX = rimRect.centerX();
        float startY = rimRect.centerY();

        float add = -180;

//        canvas.drawArc(rimRect, startArc, middleArc, true, paintArcBig);
//        canvas.drawArc(rimRect, startArc + middleArc, endArc, true, paintArcSmall);

        float y11 = rimRect.top - 0.03f;       // установка растояния начала штриха от круга

//        canvas.rotate(startArc + add, startX, startY);
//        canvas.drawLine(startX, startY, startX, y11, paintLine);
        canvas.rotate(middleArc + add, startX, startY);
//        canvas.drawLine(startX, startY, startX, y11, paintLine);
//        canvas.rotate(endArc, startX, startY);
//        canvas.drawLine(startX, startY, startX, y11, paintLine);

        Paint pBun = new Paint();
        pBun.setColor(Color.parseColor(colorLines));
        canvas.drawCircle(startX, rimRect.top - 0.003f, 0.01f, pBun);        // рисуем шарик на стрелочке

        canvas.drawOval(scaleInnerRect, scaleInnerPaintTransparent);  // градиент для стрелочек и внутреннего круга

        Shader shader = new LinearGradient(
                startX, startY, startX, y11,
                Color.TRANSPARENT,
                Color.parseColor(colorLines),
                Shader.TileMode.REPEAT /*or REPEAT*/);
        paintLine.setShader(shader);
        canvas.drawLine(startX, startY - 0.15f, startX, y11, paintLine);
    }

    private void drawBackground(Canvas canvas) {
        if (background == null) {
            Log.w(TAG, "Background not created");
        } else {
            canvas.drawBitmap(background, 0, 0, backgroundPaint);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawBackground(canvas);

        float scale = (float) getWidth();
        canvas.save(Canvas.MATRIX_SAVE_FLAG);
        canvas.scale(scale, scale);

        canvas.restore();
        regenerateBackground();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        Log.d(TAG, "Size changed to " + w + "x" + h);

        regenerateBackground();
    }

    private void regenerateBackground() {
        // free the old bitmap
        if (background != null) {
            background.recycle();
        }

        background = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas backgroundCanvas = new Canvas(background);
        Canvas backgroundCanvasLines = new Canvas(background);
        float scale = (float) getWidth();
        backgroundCanvas.scale(scale, scale);
        backgroundCanvasLines.scale(scale, scale);

        drawBigRainbow();     // радуга
        drawRim(backgroundCanvas);      // внешний круг
        drawFace(backgroundCanvas); // фон до внешнего  круга
        drawScale(backgroundCanvasLines);
//        drawTitle(backgroundCanvas);
        drawLinesResalt(backgroundCanvasLines);  // стрелы

    }

    private void drawBigRainbow() {
        logoPaint = new Paint();
        logoPaint.setFilterBitmap(true);
        logo = BitmapFactory.decodeResource(getContext().getResources(), resRaindow);
        logoMatrix = new Matrix();
        float scale = (float) getWidth();
        logoScale = (scale) / logo.getWidth();
        logoMatrix.setScale(logoScale, logoScale, logoScale, logoScale);
        Canvas canvas = new Canvas(background);

        canvas.save(Canvas.MATRIX_SAVE_FLAG);
        canvas.translate(0, 0);

        Rect rect = canvas.getClipBounds();
        canvas.rotate(arcForRotationRainbow, rect.exactCenterX(), rect.exactCenterY());

        canvas.drawBitmap(logo, logoMatrix, rimCirclePaintRainbow);
        canvas.restore();

    }

    public void setDataInChart(float valueGeneral, float max, float min, int mI) {

        int preMax = 0, preMin = 0;
        float arcRainbow = 0;
        countWicks = 5;
        totalNicks = 140;       // общее количество разделителей

        switch (mI) {
            case PICKER_WATER:
                preMin = 0;
                preMax = 100;
                arcRainbow = 30.0f;
                totalNicks = 140;       // общее количество разделителей
                break;

            case PICKER_CALORIES:
                preMin = 0;
                preMax = 7000;
                arcRainbow = -10.0f;
                totalNicks = 140;       // общее количество разделителей
                break;

            case PICKER_WEIGHT:
//                if (SettingsApp.getInstance().getMetric()) {
//                    preMin = 0;
//                    preMax = 184;
//                    countWicks = 7;        // количество штрихов в разделители
//                    totalNicks = 210;       // общее количество разделителей
//                } else {
                    preMin = 0;
                    preMax = 400;
//                }
                arcRainbow = 5.0f;
                break;

            case PICKER_FAT_LEVEL:
                preMin = 0;
                preMax = 100;
                arcRainbow = -30.0f;
                totalNicks = 180;       // общее количество разделителей
                break;

            case PICKER_MUSCLE_MASS:
//                if (SettingsApp.getInstance().getMetric()) {
//                    preMin = 0;
//                    preMax = 184;
//                    countWicks = 7;        // количество штрихов в разделители
//                    totalNicks = 210;       // общее количество разделителей
//                } else {
                    preMin = 0;
                    preMax = 400;
//                }
                arcRainbow = 0.0f;
                break;

            case PICKER_BONE_MASS:
//                if (SettingsApp.getInstance().getMetric()) {
//                    preMin = 0;
//                    preMax = 184;
//                    countWicks = 7;        // количество штрихов в разделители
//                    totalNicks = 210;       // общее количество разделителей
//                } else {
                    preMin = 0;
                    preMax = 400;
//                }
                arcRainbow = -5.0f;
                break;

            case PICKER_FAT:
                preMin = 0;
                preMax = 100;
                arcRainbow = -30.0f;
                totalNicks = 180;       // общее количество разделителей
                break;
        }


        nicksShowing = (totalNicks / 7) * 4;  // сколько будет показано
        degreesPerNick = 360.0f / totalNicks;        // градусов на 1 шьрих

        this.maxDegrees = preMax;
        this.minDegrees = preMin;


        float i = (float) nicksShowing / (maxDegrees - minDegrees);

        this.middleArc = valueGeneral * i * degreesPerNick;
        this.startArc = 0;
        this.endArc = 0;

        this.arcForRotationRainbow = arcRainbow;
// старая версия для трез стрелок
//        float add = 120;
//        this.startArc = add + min;
//        this.endArc = 0;
//        this.middleArc = 0;
//
//        if (valueGeneral != min && valueGeneral > min) {
//            this.middleArc = valueGeneral - min;
//        } else {
//            this.middleArc = 0;
//        }
//
//        if (max > valueGeneral) {
//            this.endArc = max - min - valueGeneral;
//        } else {
//            this.endArc = 0;
//        }

        invalidate();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                invalidate();
                repertInvalidate();
            }
        }, TIME_UPDATE_INVALIDATE);
    }


    private void repertInvalidate() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                invalidate();
                repertInvalidate();
            }
        }, TIME_UPDATE_INVALIDATE);
    }

}
