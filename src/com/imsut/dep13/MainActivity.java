package com.imsut.dep13;


import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;





public class MainActivity extends Activity {
	private static final String ns = null;
	public static String defects = ""; 
	final String LOG_TAG = "mmv";
  //TextView tvEnabledGPS;
  //TextView tvStatusGPS;
  //TextView tvLocationGPS;
  //TextView tvEnabledNet;
  //TextView tvStatusNet;
  //TextView tvLocationNet;
  TextView tvMet;
  ImageView imgView;
  ListView list;
   ImageView mImageView;
   ImageView mImageView2;
   CheckBox cbNet;
  
  ArrayList<RPoint> all;
  
  
  TextView textViewMast;
  TextView textViewLat;
  TextView textViewLon; 

  private LocationManager locationManager;
  StringBuilder sbGPS = new StringBuilder();
  StringBuilder sbNet = new StringBuilder();
  
  RPoint p1;
  
  String link;
  
 double kLat=111.1;
 double kLon = 62.6;
  
 double tLat=12;
 double tLon=13;
 
 double nLat=24;
 double nLon=25;
 
 double meters;
 
  WebView webView;
  ProgressDialog progress;
  

  @Override
  protected void onCreate(Bundle savedInstanceState) {
	  
	  
	  progress = new ProgressDialog(this);
      progress.setMessage("Loading...");
  
	  
    super.onCreate(savedInstanceState);
	  Log.d("mmv",  "savedInstanceState" );
    setContentView(R.layout.activity_main);
    //tvEnabledGPS = (TextView) findViewById(R.id.tvEnabledGPS);
    //tvStatusGPS = (TextView) findViewById(R.id.tvStatusGPS);
    //tvLocationGPS = (TextView) findViewById(R.id.tvLocationGPS);
    //tvEnabledNet = (TextView) findViewById(R.id.tvEnabledNet);
    //tvStatusNet = (TextView) findViewById(R.id.tvStatusNet);
    //tvLocationNet = (TextView) findViewById(R.id.tvLocationNet);
    
    
    textViewMast = (TextView) findViewById(R.id.textViewMast);
    textViewLat = (TextView) findViewById(R.id.textViewLat);
    textViewLon = (TextView) findViewById(R.id.textViewLon);
    
    cbNet = (CheckBox) findViewById(R.id.checkBox1);
    
    
	//  Log.d("mmv",  "textViewMast" );
		
  //  webView = (WebView) findViewById(R.id.webView);
    
    
    
    mImageView = (ImageView) findViewById(R.id.imageView1);
    mImageView2 = (ImageView) findViewById(R.id.imageView2);
    
    tvMet = (TextView) findViewById(R.id.textView2);
    
   //  imgView = (ImageView) findViewById(R.id.imageView1);

    locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
    
    all = new ArrayList<RPoint>();
    
    all.add(new RPoint((double)37.528742,(double)55.801684,"Нет разметки","Иванов И.И.","pic1"));
    all.add(new RPoint((double)37.494575,(double)55.777719,"Сбит знак","Иванов И.И.","pic2"));
    all.add(new RPoint((double)37.156882,(double)55.72457,"Яма","Петров П.П.","pic3"));
    all.add(new RPoint((double)37.120575,(double)55.717675,"Отбойник","Пеnров П.П.","pic4"));
    all.add(new RPoint((double)37.120596,(double)55.717571,"Куча мусора","Сидоров С.С.","pic5"));
    all.add(new RPoint((double)37.058783,(double)55.708284,"Авария","Сидоров С.С.","pic6"));
    
    list = (ListView) findViewById(R.id.listView1);
    
    
    String[] names = new String[all.size()];
    for(int i=0;i<all.size();i++)
    {
    	names[i]=((RPoint)(all.toArray()[i])).getName();
    	
    }
    
    list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    // создаем адаптер
    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
        android.R.layout.simple_list_item_single_choice, names);

    // присваиваем адаптер списку
    list.setAdapter(adapter);
    
    
  }
  
  double findMeters()
  {
	  
	  double r1 = (nLon-tLon)*(nLon-tLon);
	  double r2 = (nLat-tLat)*(nLat-tLat);
	  
	  String text = String.format(" nLon %1$.4f, nlat  %2$.4f, tLon  %3$.4f, tLat  %4$.4f, r1  %5$.4f, r2  %6$.4f",nLon,nLat,tLon,tLat,r1,r2);
	  Log.d("mmv",  text );
	
	  
		 
	      
	  
	  return Math.sqrt(Math.pow((nLon-tLon)*kLon*1000.0, 2.0)+Math.pow((nLat-tLat)*kLat*1000.0,2.0));
  }

  @Override
  protected void onResume() {
    super.onResume();
    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
        1000, 10, locationListener);
    locationManager.requestLocationUpdates(
        LocationManager.NETWORK_PROVIDER, 1000 * 10, 10,
        locationListener);
    checkEnabled();
  }

  @Override
  protected void onPause() {
    super.onPause();
    locationManager.removeUpdates(locationListener);
  }

  private LocationListener locationListener = new LocationListener() {

    @Override
    public void onLocationChanged(Location location) {
      showLocation(location);
      
      nLon = location.getLongitude();
      nLat = location.getLatitude();
      
      meters = findMeters();
      String text = String.format("%1$.0f м",meters);
      tvMet.setText(text);
      
      
    }

    @Override
    public void onProviderDisabled(String provider) {
      checkEnabled();
    }

    @Override
    public void onProviderEnabled(String provider) {
      checkEnabled();
      showLocation(locationManager.getLastKnownLocation(provider));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
      if (provider.equals(LocationManager.GPS_PROVIDER)) {
       // tvStatusGPS.setText("onStatusChanged" +"Status: " + String.valueOf(status));
      } else if (provider.equals(LocationManager.NETWORK_PROVIDER)) {
       // tvStatusNet.setText("onStatusChanged Включите GPS");
      }
    }
  };

  private void showLocation(Location location) {
    if (location == null)
      return;
    
    link=String.format("http://static-maps.yandex.ru/1.x/?ll=%1$.4f*%2$.4f&size=273*121&z=17&l=pmap", location.getLongitude(),location.getLatitude());
    link= link.replace(",",".");
    
    link=link.replace("*",",");
    
    
    if (location.getProvider().equals(LocationManager.GPS_PROVIDER)) {
    	
    	
    //  tvLocationGPS.setText("showLocation" + formatLocation(location));
    } else if (location.getProvider().equals(
        LocationManager.NETWORK_PROVIDER)) {
    //  tvLocationNet.setText("showLocationNet "+formatLocation(location));
    }
  }

  private String formatLocation(Location location) {
    if (location == null)
      return "";
    return String.format(
        "Coordinates: lat = %1$.4f, lon = %2$.4f, time = %3$tF %3$tT, speed=%4$.4f",
        location.getLatitude(), location.getLongitude(), new Date(location.getTime()), location.getSpeed());
  }

  private void checkEnabled() {
   // tvEnabledGPS.setText("GPS enabled: " + locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER));
  //  tvEnabledNet.setText("Net enabled: " + locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER));
  }
  
  
 
  
  private Drawable grabImageFromUrl(String url) throws Exception {
		return Drawable.createFromStream(
				(InputStream) new URL(url).getContent(), "src");
	}

 
	
  @Override
  public boolean dispatchKeyEvent(KeyEvent event) {
      int action = event.getAction();
      int keyCode = event.getKeyCode();
          switch (keyCode) {
          case KeyEvent.KEYCODE_VOLUME_UP:
              if (action == KeyEvent.ACTION_DOWN) {
            	  Log.d("mmv", "checked: KeyEvent.KEYCODE_VOLUME_UP" );
            	  }
              return true;
          case KeyEvent.KEYCODE_HEADSETHOOK:
              if (action == KeyEvent.ACTION_DOWN) {
            	  Log.d("mmv", "checked: KeyEvent.KEYCODE_HEADSETHOOK" );  
            	  }
              return true;
          case KeyEvent.KEYCODE_VOLUME_DOWN:
              if (action == KeyEvent.ACTION_DOWN) {
            	  Log.d("mmv", "checked: KeyEvent.KEYCODE_VOLUME_DOWN" );  
            	  }
              return true;
          default:
              return super.dispatchKeyEvent(event);
          }
      }
  
  
//@Override  
public void onClickLoad(View view) {
	
	Log.d("mmv", "Ща те чаю накачаю" );  
	
    
	//new LongOperation()
   // {
   //     @Override public void onPostExecute(String result)
   //     {
   //     	defects=result;
        	//Log.d("mmv", result ); 
           
  //      }
  //  }.execute("");
        
    String tmp = "";
    //ArrayList<RPoint> web = new ArrayList<RPoint>();
    all.clear();

    XmlPullParser xpp;
    
    try {
     if(cbNet.isChecked())
    	 xpp = prepareXpp("<root>"+defects+"</root>");
     else
    	 xpp = getResources().getXml(R.xml.my);
    	
    	String name="";
    	String lon="";
    	String lat="";
    	String img="";
    	
    	
    	
    	while (xpp.getEventType()!= XmlPullParser.END_DOCUMENT) {
    	    if ((xpp.getEventType() == XmlPullParser.START_TAG)   && xpp.getName().equals("object")) {
    	    	if(xpp.getAttributeName(0).equals("name")){
    	    		name=xpp.getAttributeValue(2)+" "+xpp.getAttributeValue(3);
    	    		img=xpp.getAttributeValue(0);
    	    		Log.d("mmv","name="+name+" img="+img );
    	    	}
    	    	
    	    	if(xpp.getAttributeName(0).equals("objectType"))
    	    	{
    	    		lon=xpp.getAttributeValue(2);
    	    		lat=xpp.getAttributeValue(3);
    	    		Log.d("mmv","longitude="+lon +" latitude="+lat);
    	    		all.add(new RPoint(name,img,lon,lat));
    	    	}
    	    }
    	    xpp.next();
    	}
    	  
      
      Log.d(LOG_TAG, "совсекм END_DOCUMENT");

    } catch (XmlPullParserException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
          
   
    String[] webnames = new String[all.size()];
    for(int i=0;i<all.size();i++)
    {
    	webnames[i]=((RPoint)(all.toArray()[i])).getName();
    }
    
    
    //list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    // создаем адаптер
    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_single_choice, webnames);

    // присваиваем адаптер списку
    list.setAdapter(adapter);      
	      
	     // webView.loadUrl("http://static-maps.yandex.ru/1.x/?ll=37.4951,55.7778&size=150,50&z=17&l=pmap");
	  
  
};






XmlPullParser prepareXpp(String str) throws XmlPullParserException {
    // получаем фабрику
    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
    // включаем поддержку namespace (по умолчанию выключена)
    factory.setNamespaceAware(true);
    // создаем парсер
    XmlPullParser xpp = factory.newPullParser();
    // даем парсеру на вход Reader
    xpp.setInput(new StringReader(str));
    return xpp;
  }

  
  public void onClickFind(View view) {
	    //startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
	    RPoint cur = all.get(list.getCheckedItemPosition());
		Log.d("mmv", "checked: " + cur.getName()+" "+cur.getPicName() );
		 
		
		
		
		tLon = cur.getLon();
	     tLat = cur.getLat();
	     //Toast.makeText(this, "Нажата кнопка Cancel", Toast.LENGTH_LONG).show();
	      
	      meters = findMeters();
	      String text = String.format("%1$.0f м",meters);
	      tvMet.setText(text);
	      
	      
	      textViewMast.setText(cur.getMaster());
	       text = String.format("%1$.4f",cur.getLat());
	      textViewLat.setText(text);
	       text = String.format("%1$.4f",cur.getLon());
	      textViewLon.setText(text);
	      
	   /*   switch(list.getCheckedItemPosition()) {
	      case 0: 
	    	  mImageView.setImageResource(R.drawable.pic1);
	  		break;
	      case 1: 
	    	  mImageView.setImageResource(R.drawable.pic2);
	  		break;
	      case 2: 
	    	  mImageView.setImageResource(R.drawable.pic3);
	  		break;
	      case 3: 
	    	  mImageView.setImageResource(R.drawable.pic4);
	  		break;
	      case 4: 
	    	  mImageView.setImageResource(R.drawable.pic5);
	  		break;
	      case 5: 
	    	  mImageView.setImageResource(R.drawable.pic6);
	  		break;
	  
	  	default: 
	  		mImageView.setImageResource(R.drawable.plakat2);
	  	    break;
	  }
	  */
	    String link=String.format("http://static-maps.yandex.ru/1.x/?ll=%1$.4f*%2$.4f&size=300*200&z=17&l=pmap", cur.getLon(),cur.getLat());
	    link= link.replace(",",".");
	    link=link.replace("*",",");
	      
	  
	    new DownloadImageTask().execute(link);
	      //new DownloadImageTask().execute("http://static-maps.yandex.ru/1.x/?ll=37.4951,55.7778&size=150,50&z=17&l=pmap");
	  //link=     "https://b-a.d-cd.net/1f8f54cs-240.jpg";
	  link="http://89.169.32.195:8080/ObjectMonitoring/image?size=200&id="+cur.getPicName()+".jpg";
	  Log.d("mmv", "piclink: " + link);
	       new DownloadImageTask2().execute(link);
	     
    
  };
  
  
  class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
      @Override
      protected Bitmap doInBackground(String... params) {
          publishProgress(new Void[]{}); //or null
          
          String url = "";
          if( params.length > 0 ){
          	url = params[0];		    	
          }
          Log.d("mmv", "url: " + url );
          InputStream input = null;
              try {
                     URL urlConn = new URL(url);
             input = urlConn.openStream(); 
              }
              catch (MalformedURLException e) {
             	e.printStackTrace();
          }
                  catch (IOException e) {
          	e.printStackTrace();
          }
          
              return BitmapFactory.decodeStream(input);
      }
      
      @Override
      protected void onProgressUpdate(Void... values) {
           super.onProgressUpdate(values);
           progress.show();
      }
      
      @Override
      protected void onPostExecute(Bitmap result) {
           super.onPostExecute(result);
           progress.dismiss();
          
           mImageView.setImageBitmap(result);
          

      }
  }
int i=1;
  class DownloadImageTask2 extends AsyncTask<String, Void, Bitmap> {
      @Override
      protected Bitmap doInBackground(String... params) {
          publishProgress(new Void[]{}); //or null
          
          String url = "";
          if( params.length > 0 ){
          	url = params[0];		    	
          }
          Log.d("mmv", "url: " + url );
          InputStream input = null;
              try {
                     URL urlConn = new URL(url);
             input = urlConn.openStream(); 
              }
              catch (MalformedURLException e) {
             	e.printStackTrace();
          }
                  catch (IOException e) {
          	e.printStackTrace();
          }
          
              return BitmapFactory.decodeStream(input);
      }
      
      @Override
      protected void onProgressUpdate(Void... values) {
           super.onProgressUpdate(values);
           progress.show();
      }
      
      @Override
      protected void onPostExecute(Bitmap result) {
           super.onPostExecute(result);
           progress.dismiss();
           mImageView2.setImageBitmap(result);
           
      }
  }
  
  

}



