package com.example.camera;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH) public class MainActivity extends Activity {

  SurfaceView surfaceView;
  Camera camera;
  MediaRecorder mediaRecorder;

  File photoFile;
  File videoFile;
  File pictures;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    pictures = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
    photoFile = new File(pictures, getCurrentTime()+"m.jpg");

    surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
   // setPreviewSize(false);

    SurfaceHolder holder = surfaceView.getHolder();
    holder.addCallback(new SurfaceHolder.Callback() {
      @Override
      public void surfaceCreated(SurfaceHolder holder) {
        try {
          camera.setPreviewDisplay(holder);
          camera.startPreview();
        } catch (Exception e) {
          e.printStackTrace();
        }
      }

      @Override
      public void surfaceChanged(SurfaceHolder holder, int format,
          int width, int height) { camera.stopPreview();
          setCameraDisplayOrientation(0);
          try {
            camera.setPreviewDisplay(holder);
            camera.startPreview();
          } catch (Exception e) {
            e.printStackTrace();
          }
      }

      @Override
      public void surfaceDestroyed(SurfaceHolder holder) {
      }
    });
    
   // setCameraDisplayOrientation(0);

  }
  
  void setCameraDisplayOrientation(int cameraId) {
	    // определяем насколько повернут экран от нормального положения
	    int rotation = getWindowManager().getDefaultDisplay().getRotation();
	    int degrees = 0;
	    switch (rotation) {
	    case Surface.ROTATION_0:
	      degrees = 0;
	      break;
	    case Surface.ROTATION_90:
	      degrees = 90;
	      break;
	    case Surface.ROTATION_180:
	      degrees = 180;
	      break;
	    case Surface.ROTATION_270:
	      degrees = 270;
	      break;
	    }
	    
	    int result = 0;
	    
	    // получаем инфо по камере cameraId
	    CameraInfo info = new CameraInfo();
	    Camera.getCameraInfo(cameraId, info);

	    // задняя камера
	    if (info.facing == CameraInfo.CAMERA_FACING_BACK) {
	      result = ((360 - degrees) + info.orientation);
	    } else
	    // передняя камера
	    if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
	      result = ((360 - degrees) - info.orientation);
	      result += 360;
	    }
	    result = result % 360;
	    camera.setDisplayOrientation(result);
	  }
  

  void setPreviewSize(boolean fullScreen) {

	    // получаем размеры экрана
	    Display display = getWindowManager().getDefaultDisplay();
	    boolean widthIsMax = display.getWidth() > display.getHeight();
	    int a;

	    // определяем размеры превью камеры
	    Size size = camera.getParameters().getPreviewSize();
	        
	    RectF rectDisplay = new RectF();
	    RectF rectPreview = new RectF();
	    
	    // RectF экрана, соотвествует размерам экрана
	    rectDisplay.set(0, 0, 320, 400);
	    
	    // RectF первью 
	    if (widthIsMax) {
	      // превью в горизонтальной ориентации
	      rectPreview.set(0, 0, size.width, size.height);
	    } else {
	      // превью в вертикальной ориентации
	      rectPreview.set(0, 0, size.height, size.width);
	    }

	    Matrix matrix = new Matrix();
	    // подготовка матрицы преобразования
	    if (!fullScreen) {
	      // если превью будет "втиснут" в экран (второй вариант из урока)
	      matrix.setRectToRect(rectPreview, rectDisplay,
	          Matrix.ScaleToFit.START);
	    } else {
	      // если экран будет "втиснут" в превью (третий вариант из урока)
	      matrix.setRectToRect(rectDisplay, rectPreview,
	          Matrix.ScaleToFit.START);
	      matrix.invert(matrix);
	    }
	    // преобразование
	    matrix.mapRect(rectPreview);

	    // установка размеров surface из получившегося преобразования
	    surfaceView.getLayoutParams().height = 400;
	    surfaceView.getLayoutParams().width = 320;
	  }

  
  @Override
  protected void onResume() {
    super.onResume();
    camera = Camera.open();
  }

  @Override
  protected void onPause() {
    super.onPause();
    
    if (camera != null)
      camera.release();
    camera = null;
  }

  private String getCurrentTime() {
      Calendar calendar = Calendar.getInstance();
      int year = calendar.get(Calendar.YEAR);
      int month = calendar.get(Calendar.MONTH);
      int day = calendar.get(Calendar.DAY_OF_MONTH);
      int hour = calendar.get(Calendar.HOUR_OF_DAY);
      int minute = calendar.get(Calendar.MINUTE);
      int second = calendar.get(Calendar.SECOND);
      return String.format("%02d_%02d_%02d_%02d_%02d", month, day, hour, minute, second); // ЧЧ:ММ:СС - формат времени
  }
  
  int zoom=0;
  
  @Override
  public boolean dispatchKeyEvent(KeyEvent event) {
      int action = event.getAction();
      int keyCode = event.getKeyCode();
          switch (keyCode) {
          case KeyEvent.KEYCODE_VOLUME_UP:
              if (action == KeyEvent.ACTION_DOWN) {
            	  Log.d("mmv", "checked: KeyEvent.KEYCODE_VOLUME_UP" );
            	  Camera.Parameters params = camera.getParameters();
            	  if(zoom<10)
            	  	{
            		  	zoom++;
            		  	if (params.isZoomSupported()){ // check that metering areas are supported
            		  		params.setZoom(zoom);  
            		  		camera.setParameters(params);
            		  	}
            	  	}
          	    
            	  }
              return true;
          case KeyEvent.KEYCODE_HEADSETHOOK:
              if (action == KeyEvent.ACTION_DOWN) {
            	  Log.d("mmv", "checked: KeyEvent.KEYCODE_HEADSETHOOK" );
            	  takePic();
            	  }
              return true;
          case KeyEvent.KEYCODE_VOLUME_DOWN:
              if (action == KeyEvent.ACTION_DOWN) {
            	  Log.d("mmv", "checked: KeyEvent.KEYCODE_VOLUME_DOWN" );  
            	  if(zoom>0)
          	  	{
          		  	zoom--;
          		  Camera.Parameters params = camera.getParameters();
          		  	if (params.isZoomSupported()){ // check that metering areas are supported
          		  		params.setZoom(zoom);  
          		  		camera.setParameters(params);
          		  	}
          	  	}
            	  }
              return true;
          default:
              return super.dispatchKeyEvent(event);
          }
      }
  
  
  void takePic()
  {
	  
	// set Camera parameters
	    Camera.Parameters params = camera.getParameters();

	    if (params.getMaxNumFocusAreas() > 0){ // check that metering areas are supported
	        List<Camera.Area> meteringAreas = new ArrayList<Camera.Area>();

	        Rect areaRect1 = new Rect(-100, -100, 100, 100);    // specify an area in center of image
	        meteringAreas.add(new Camera.Area(areaRect1, 1000)); // set weight to 60%
	        //Rect areaRect2 = new Rect(800, -1000, 1000, -800);  // specify an area in upper right of image
	       // meteringAreas.add(new Camera.Area(areaRect2, 400)); // set weight to 40%
	        params.setFocusAreas(meteringAreas);
	       // params.setF
	        Log.d("mmv", "zoom"+String.format("%1$d", params.getMaxZoom())); 
	    }

	    camera.setParameters(params);
	  
	  
  camera.takePicture(null, null, new PictureCallback() {
    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
      try {
        photoFile = new File(pictures,  getCurrentTime()+"m.jpg");
        FileOutputStream fos = new FileOutputStream(photoFile);
        fos.write(data);
        fos.close();
        Log.d("mmv", "снято"+String.format("%1$d", data.length));
        camera.startPreview();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  });
  
 // camera.release();
 // camera.open();

  }
  
  public void onClickPicture(View view) {
	  
	  takePic();
	    
  }

  
  

}