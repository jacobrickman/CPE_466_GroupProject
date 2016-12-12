import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

public class Clustering {
	
	public static ArrayList<Cluster> clusters;
	
	/**
	 * Reads in the ufosightings.csv and returns a HashMap containing each year and a list of
	 * all the sightings that year
	 * @author JacobRickman
	 * @param name file name - should be ufosightings.csv
	 * @return HashMap<Integer, ArrayList<Point>>
	 */
	private static HashMap<Integer, ArrayList<Point>> parseCSV(String name) {
		HashMap<Integer, ArrayList<Point>> ret = new HashMap<>();
		int xyz = 0;
		try {
			Scanner file = new Scanner(new File(name));
			String line = "";
			String columns[];
			String date;
			int year;
			Point spot;
			Double latitude;
			Double longitude;
			ArrayList<Point> temp;
			
			
			//Skip the column names in the first line of the file
			file.nextLine();
			
			while (file.hasNextLine()) {
				line = file.nextLine();
				//System.out.println(line);
				
				columns = line.split(",");
				date = columns[0];
				year = Integer.parseInt(date.split("/")[2].split(" ")[0]);
				latitude = Double.parseDouble(columns[columns.length - 2]);
				longitude = Double.parseDouble(columns[columns.length - 1]);
				if (latitude != 0 && longitude != 0 && latitude > 25.2 && latitude < 50 
						&& longitude > -127 && longitude < -64.6)
				{
					xyz++;
					spot = new Point(latitude, longitude);
				
					if (ret.containsKey(year))
						ret.get(year).add(spot);
				
					else
					{
						temp = new ArrayList<>();
						temp.add(spot);
						ret.put(year, temp);
					}
				}
			}
			
		}
		catch (Exception e)
		{
			System.out.println("parse " +e);
		}
		System.out.println(xyz);
		return ret;
	}
	
	/**
	 * Reads the states.csv file and returns a list of all the States
	 * @author JacobRickman
	 * @return List of all the States - my own data type
	 */
	private static ArrayList<State> readStatesCSV()
	{
		Scanner file;
		String line;
		String[] info;
		String name;
		double lat;
		double lon;
		ArrayList<State> states = new ArrayList<>();
		
		try
		{
			file = new Scanner(new File("states.csv"));
			
			while (file.hasNextLine()) {
				line = file.nextLine();
				info = line.split(",");
				name = info[0].replace("\"", "");
				lat = Double.parseDouble(info[6].replace("\"", ""));
				lon = Double.parseDouble(info[7].replace("\"", ""));
				states.add( new State(name, lat, lon));
				
			}
		}
		catch (Exception e)
		{
			System.out.println("States: " + e);
		}
		
		return states;
	}
	
	/**
	 * There wasn't a libraray tuple datatype, I think
	 * @author JacobRickman
	 *
	 */
	private static class Tuple {
		public final int x;
		public final double y;
		
		public Tuple(int x, double y) {
			this.x = x;
			this.y = y;
		}
	}
	
	/**
	 * Returns the cluster with the given Id
	 * @author JacobRickman
	 * @param id of the wanted cluster
	 * @param list of clusters
	 * @return index of the wanted cluster
	 */
	private static int getById(int id, ArrayList<Cluster> list) 
	{
		for (int i = 0; i < list.size(); i++)
		{
			if (list.get(i).id == id)
				return i;
		}
		
		return -1;
	}
	
	/**
	 * Runs the clustering algorightm.
	 * @author JacobRickman
	 * @param iterations number of iterations wanted - should probably be changed
	 * @param initialCentroids, initial centroids of the clusters, list should be of size k
	 * @param data, the data points being clusterd
	 * @return list of the clusters
	 */
	public static ArrayList<Cluster> runClustering(int iterations, ArrayList<Point> initialCentroids, ArrayList<Point> data)
	{
		ArrayList<Tuple> distTo;
		ArrayList<Cluster> clusters = new ArrayList<>();
		int id = 1;
		int minId;
		double minDist;
		double temp;
		int index;
		Point tmpPoint;
		Point tmpPoint2;
		Cluster c1;
		Cluster c2;
		
		for (Point p : initialCentroids)
			clusters.add(new Cluster(id++, p));
				
		while (iterations-- != 0)
		{	
			for (Cluster c : clusters)
				c.centroid = c.reevaluate();
			
			for (Point p : data)
			{
				distTo = new ArrayList<>();
				minId = clusters.get(0).id;
				minDist = clusters.get(0).distFrom(p);
				
				for (Cluster c : clusters)
				{
					temp = c.distFrom(p);
					if (temp < minDist)
					{
						minDist = temp;
						minId = c.id;
					}
					
				}
				
				if ((index = getById(minId, clusters)) != -1)
					clusters.get(index).add(p);
				else
				{
					System.out.println("Id not found\n");
					return null;
				}
			}
		
			
			for (int i = 0; i < clusters.size(); i++)
			{
				c1 = clusters.get(i);
				for (int j = i + 1; j < clusters.size(); j++)
				{
					c2 = clusters.get(j);
					tmpPoint = c1.farthestPoint();
					tmpPoint2 = c2.farthestPoint();
					if (tmpPoint != null && tmpPoint2 != null)
					{
						if ((c1.distFrom(c2.centroid) < c1.distFrom(c1.farthestPoint()) || 
								c1.distFrom(c2.centroid) < c2.distFrom(c2.farthestPoint()) && c1.id != c2.id))
						{
							c1.merge(c2);
							clusters.remove(j);
							
						}
					}
				}
			}
			
		
			
		}
			
		return clusters;
	}
	
	/**
	 * Returns a list of ovals at each sighting point
	 * @author JacobRickman
	 * @param list, list of all the clusters
	 * @return list of all the sightings
	 */
	public static ArrayList<Oval> createSightings(ArrayList<Cluster> list)
	{
		ArrayList<Oval> ovals = new ArrayList<>();
		double scaleLong = .03853;
		double offsetLong = 64.13079;
		double scaleLat = .0290644127;
		double offsetLat = -25.22335082;
		int offsetX = 1600;
		int offsetY = 861;
		int pxX;
		int pxY;
		
		for (Cluster c : list)
		{
			for (Point p : c.cluster)
			{
				pxX = (int) Math.round((p.y + offsetLong) / scaleLong + offsetX);
				pxY = (int) Math.round(-1 * (p.x + offsetLat) / scaleLat + offsetY);
			
			
				ovals.add(new Oval(pxX - 5, pxY - 5, 10, 10));
			}
		}
		
		return ovals;
	}
	
	/**
	 * Returns a list of ovals outlining the clusters
	 * @author JacobRickman
	 * @param list, list of clusters to be drawn
	 * @return list of ovals 
	 */
	public static ArrayList<Oval> createCircles(ArrayList<Cluster> list) 
	{
		ArrayList<Oval> ovals = new ArrayList<>();
		double scaleLong = .03853;
		double offsetLong = 64.13079;
		double scaleLat = .0290644127;
		double offsetLat = -25.22335082;
		int offsetX = 1600;
		int offsetY = 861;
		Point p;
		double A2;
		double B2;
		int radius;
		int pxX;
		int pxY;
		int sizeOfOuterDot = 6;
				
		for (Cluster c : list)
		{
			pxX = (int) Math.round((c.centroid.y + offsetLong) / scaleLong + offsetX);
			pxY = (int) Math.round(-1 * (c.centroid.x + offsetLat) / scaleLat + offsetY);
			
			p = c.farthestPoint();
			
			if (p != null)
			{
				A2 = Math.pow(Math.abs(p.x - c.centroid.x) / scaleLat, 2);
				B2 = Math.pow(Math.abs(p.y - c.centroid.y) / scaleLong, 2);
				radius = (int) Math.round(Math.sqrt(A2 + B2));
				radius += sizeOfOuterDot;
			
				ovals.add(new Oval(pxX - radius, pxY - radius, radius * 2, radius * 2));
			}
		}
		
		return ovals;	
	}
	
	/**
	 * A psuedo main function for the GUI to run
	 * @author JacobRickman
	 * @param year, year of the sightings
	 */
	public static void run(int year)
	{
		HashMap<Integer, ArrayList<Point>> locations = parseCSV("ufo_sightings.csv");
		ArrayList<Point> initial = new ArrayList<>();		
		ArrayList<State> allStates = readStatesCSV();
		
		for (State s : allStates)
		{
			initial.add(s.getPoint());
		}
		
		/*
		initial.add( new Point(44.693947, -69.381927)); //Maine
		initial.add( new Point(46.921925, -110.454353)); //Montana
		initial.add( new Point(44.572021, - 122.070938)); //Oregon
		initial.add( new Point(31.054487, - 97.563461)); //Texas
		*/
		
		clusters = runClustering(2, initial, locations.get(year));
		
		/*
		for (Cluster c : clusters)
		{
			System.out.println(c.toString());
			System.out.println(c.toStringList());
		}
		*/
	}
	
	/**
	 * Main Function, parses the ufo_sightings.csv and run the clustering algorithm
	 * @author JacobRickman
	 * @param args - NONE
	 */
	public static void main(String[] args) {
		HashMap<Integer, ArrayList<Point>> locations = parseCSV("ufo_sightings.csv");
		ArrayList<Point> initial = new ArrayList<>();

		/*
		initial.add( new Point(44.693947, -69.381927)); //Maine
		initial.add( new Point(46.921925, -110.454353)); //Montana
		initial.add( new Point(44.572021, - 122.070938)); //Oregon
		initial.add( new Point(31.054487, - 97.563461)); //Texas
		*/
		
		clusters = runClustering(1, initial, locations.get(1910));
		
		for (Cluster c : clusters)
		{
			System.out.println(c.toString());
			System.out.println(c.toStringList());
		}
		
		//createCircles(clusters);

	}

}
