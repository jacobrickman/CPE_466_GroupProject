import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Clustering {
	
	
	private static HashMap<Integer, ArrayList<Point>> parseCSV(String name) {
		HashMap<Integer, ArrayList<Point>> ret = new HashMap<>();

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
				if (latitude != 0 && longitude != 0)
				{
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
		
		return ret;
	}
	
	private static class Tuple {
		public final int x;
		public final double y;
		
		public Tuple(int x, double y) {
			this.x = x;
			this.y = y;
		}
	}
	
	private static int getById(int id, ArrayList<Cluster> list) 
	{
		for (int i = 0; i < list.size(); i++)
		{
			if (list.get(i).id == id)
				return i;
		}
		
		return -1;
	}
	
	public static ArrayList<Cluster> runClustering(int iterations, ArrayList<Point> initialCentroids, ArrayList<Point> data)
	{
		ArrayList<Tuple> distTo;
		ArrayList<Cluster> clusters = new ArrayList<>();
		int id = 1;
		int minId;
		double minDist;
		double temp;
		int index;
		
		for (Point p : initialCentroids)
			clusters.add(new Cluster(id++, p));
				
		while (iterations-- != 0)
		{	
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
			
			for (Cluster c : clusters)
				c.reevaluate();
		}
			
		return clusters;
	}
	
	public static void main(String[] args) {
		HashMap<Integer, ArrayList<Point>> locations = parseCSV("ufo_sightings.csv");
		ArrayList<Point> initial = new ArrayList<>();
		initial.add( new Point(44.693947, -69.381927));
		initial.add( new Point(46.921925, -110.454353));
		initial.add( new Point(44.572021, - 122.070938));
		initial.add( new Point(31.054487, - 97.563461));
		
		ArrayList<Cluster> clusters = runClustering(1, initial, locations.get(1960));
		
		for (Cluster c : clusters)
			System.out.println(c.toString());

	}

}
