package tools.db;

public class Coordinates {

	private double lon;
	private double lat;

	public Coordinates(double longitude,double latitude) {
		this.lon =longitude;
		this.lat =latitude;}

	public double getLatitude() {return lat;}
	public double getLongitude() {return lon;}
	
}
