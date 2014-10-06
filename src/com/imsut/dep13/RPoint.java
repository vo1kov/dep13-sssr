package com.imsut.dep13;
public class RPoint
{
	double longitude;
	double lattilude;
	String name;
	String authtor;
	String bitmapname;
	
	public RPoint(double lon, double lat, String n, String a, String b)
	{
		 longitude=lon;
		 lattilude=lat;
		 name=n;
		 authtor=a;
		 bitmapname=b;
	}
	
	public RPoint(String n, String img, String lon,String lat)
	{
		 longitude = Double.parseDouble(lon);
		 lattilude = Double.parseDouble(lat);
		 name= n ;
		 authtor="Сервер";
		 bitmapname=img;
	}
	
	public String getName()
	{
		return name;
	}
	
	public String getMaster()
	{
		return authtor;
	}
	
	
	public double getLat()
	{
		return lattilude;
	}
	
	public double getLon()
	{
		return longitude;
	}
	
	
	public String getPicName()
	{
		
		return bitmapname;
	}
}