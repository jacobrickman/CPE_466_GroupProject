/**
 * Datatype for storing the lat and long of a state and the name of the state
 * State info read in from states.csv
 * 
 * @author JacobRickman
 *
 */
public class State {
	public String name;
	public double latitude;
	public double longitude;
	
	public State(String name, double lat, double lon)
	{
		this.name = name;
		latitude = lat;
		longitude = lon;
	}
	
	public Point getPoint()
	{
		return new Point(latitude, longitude);
	}
	
}
