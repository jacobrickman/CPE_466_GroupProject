import java.util.HashSet;
import java.util.Set;
import java.lang.Math;

/**
 * Data Type for the clusters
 * 	Contains the internal points, centroid, and id,
 * along with functions for finding distance from and reevaluating the centroid
 * and merging
 * 
 * @author JacobRickman
 *
 */
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
		return "Center of " + id + ": " + "(" + centroid.x + ", " + centroid.y + ")";
	}
	
	public HashSet<Point> findOutliers() {
		HashSet<Point> ret = new HashSet<Point>();
		double x = 0;
		double y = 0;
		double sd;
		
		for (Point p : cluster)
		{
			x += Math.pow(p.x - centroid.x, 2);
			y += Math.pow(p.y - centroid.y, 2);
		}
		
		sd = Math.sqrt((x/cluster.size()) + (y/cluster.size()));
		
		for (Point r : cluster)
		{
			x = Math.sqrt(Math.pow(r.x - centroid.x, 2) + Math.pow(r.y - centroid.y, 2));
			if (x > (3 * sd)) {
				ret.add(r);
			}
		}
		return ret;
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
		
		x += centroid.x;
		y += centroid.y;
		
		x = x / (cluster.size() + 1);
		y = y / (cluster.size() + 1);
		
		cluster.clear();
		
		return new Point(x, y);
	}
	
	public double distFrom(Point p)
	{		
		double xdif = centroid.x - p.x;
		double ydif = centroid.y - p.y;
		
		
		return Math.sqrt(Math.pow(xdif, 2) + Math.pow(ydif, 2));
	}
	
	//returns the distance to the furthest point in the cluster
	public double sizeOf()
	{
		double maxDist = 0;
		double temp;
		
		for (Point p : cluster)
		{
			temp = distFrom(p);
			if (temp > maxDist)
				maxDist = temp;
		}
		
		return maxDist;
	}
	
	public Point farthestPoint()
	{
		Point farthest = null;
		double maxDist = 0;
		double tmp;
		
		for (Point p : cluster)
		{
			tmp = distFrom(p);
			if (tmp > maxDist)
			{
				maxDist = tmp;
				farthest = p;
			}
		}
		
		
		return farthest;
	}
	
	public void merge(Cluster c)
	{
		for (Point p : c.cluster)
		{
			this.cluster.add(p);
		}
		
		this.centroid = this.reevaluate();
	}
}
