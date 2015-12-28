package com.teenslane;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Helper 
{
	private static InputStream OpenHttpConnection(String strURL, Boolean cache) throws IOException
	{
		  InputStream inputStream = null;
		  
		  try
		  {
			  URL url = new URL(strURL);
			  URLConnection conn = url.openConnection();
			  conn.setUseCaches(cache);
			  HttpURLConnection httpConn = (HttpURLConnection)conn;
			  httpConn.setRequestMethod("GET");
			  httpConn.connect(); 
	
			  if(httpConn.getResponseCode() == HttpURLConnection.HTTP_OK)
				  inputStream = httpConn.getInputStream();
			  
		  }
		  catch (Exception ex)
		  {
		  }
		  return inputStream;
	}
	  
	public static Bitmap LoadImage(String strURL, BitmapFactory.Options options)
	{       
		  Bitmap bitmap = null;
		  InputStream in = null;
		   
	      try 
	      {
			  URL url = new URL(strURL);
			  URLConnection conn = url.openConnection();
			  conn.setUseCaches(true);
			  HttpURLConnection httpConn = (HttpURLConnection)conn;
			  httpConn.setRequestMethod("GET");
			  httpConn.connect(); 
	
			  if (httpConn.getResponseCode() == HttpURLConnection.HTTP_OK)
				  in = httpConn.getInputStream();
			  
	          if(in != null)
	          { 
	        	  //options.inSampleSize = 2;
	        	  bitmap = BitmapFactory.decodeStream(in, null, options);
	          }
	      }
	      catch(IOException e1) 
	      {
	      }
	      catch(Exception e)
	      {
	      }
	      finally {
			  // always close input stream
			  if(in != null)
			  {
				  try {
					  in.close();
				  } catch (IOException e) {
				  }
			  }
	      }
	      
	      return bitmap;               
	}
}
