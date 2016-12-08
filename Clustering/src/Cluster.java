import java.util.HashSet;
import java.util.Set;

public class Cluster {
	public HashSet<Point> cluster;
	public Point centroid;
	public int id;
	
	public Cluster(int id, Point center)
	{
		cluster = new HashSet<Point>();
		this.id = id;
		centroid = center;
	}
	
	public void add(Point x)
	{
		cluster.add(x);
	}
	
	public void remove(Point x)
	{
		cluster.remove(x);
	}
	
	public boolean contains(Point x)
	{
		return cluster.contains(x);
	}
	
	public int size()
	{
		return cluster.size();
	}
	
	public String toStringList()
	{
		String ret = id + ": ";
		
		for (Point p : cluster)
			ret += "(" + p.x + ", " + p.y + "), ";
			
		return ret.substring(0, ret.length() - 2);
	}
	
	public String toString()
	{
		return id + ": " + "(" + centroid.x + ", " + centroid.y + ")";
	}
	
	public Point reevaluate()
	{
		double x = 0;
		double y = 0;
		
		for (Point p : cluster)
		{
			x += p.x;
			y += p.y;
		}
		
		x = x / cluster.size();
		y = y / cluster.size();
		
		return new Point(x, y);
	}
	
	public double distFrom(Point p)
	{		
		double xdif = centroid.x - p.x;
		double ydif = centroid.y - p.y;
		
		
		return Math.sqrt(Math.pow(xdif, 2) + Math.pow(ydif, 2));
	}
}
