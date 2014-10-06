package com.imsut.dep13;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.os.AsyncTask;

class LongOperation extends AsyncTask<String, Void, String> {
	 
    @Override
    protected String doInBackground(String... params) {
        String str="error";
        try {
            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet("http://89.169.32.195:8080/ObjectMonitoring/defect");
            HttpResponse response = client.execute(request);
            HttpEntity resEntity = response.getEntity();
            if (resEntity != null) {
            str=EntityUtils.toString(resEntity);
            }
            } catch (UnsupportedEncodingException uee){
            uee.printStackTrace();
            } catch (ClientProtocolException cpe){
            cpe.printStackTrace();
            } catch (IOException ioe){
            ioe.printStackTrace();
            }
          return str;
    }      
 
    @Override
    protected void onPostExecute(String result) {
          //might want to change "executed" for the returned string passed into onPostExecute() but that is upto you
 }
 
    @Override
    protected void onPreExecute() {
    }
 
    @Override
    protected void onProgressUpdate(Void... values) {
    }
}