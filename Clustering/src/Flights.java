import java.sql.*;
import java.util.*;
import java.io.*;

/**
 * Created by Ryan on 12/8/2016.
 */
public class Flights {
    public static void calculateClusters(ArrayList<ConvexHull> hulls, String dbName)
            throws SQLException, ClassNotFoundException {
        Connection c;
        Statement stmt;
        String sql;

        Class.forName("org.sqlite.JDBC");
        c = DriverManager.getConnection(dbName);
        sql = "SELECT * FROM ROUTES, US_AIRPORTS a1, US_AIRPORTS a2 "+
                "WHERE AIRPORT_SOURCE_ID = a1.ID AND AIRPORT_DEST_ID = a2.ID;";
        ResultSet rs = c.createStatement().executeQuery(sql);

        while (rs.next()) {
            //Point source = new Point(rs.getInt("a1.LONGITUDE"), rs.getInt("a1.LATITUDE"));
            //Point dest = new Point(rs.getInt("a2.LONGITUDE"), rs.getInt("a2.LATITUDE"));

            Point source = new Point(rs.getInt(5), rs.getInt(6));
            Point dest = new Point(rs.getInt(9), rs.getInt(10));

            for (ConvexHull curConvex : hulls) {
                if (passThrough(source, dest, 1, curConvex.getHull().size(), curConvex)) {
                    curConvex.flightsThrough++;
                }
            }
        }
    }

    public static void readAirports(String filename)
            throws FileNotFoundException, ClassNotFoundException, SQLException {
        Connection c;
        Statement stmt;
        String sql;


        Class.forName("org.sqlite.JDBC");
        c = DriverManager.getConnection("jdbc:sqlite:flights.db");
        stmt = c.createStatement();

        // Create Table
        try {
            sql = "CREATE TABLE US_AIRPORTS " +
                    "(ID INT PRIMARY KEY     NOT NULL," +
                    " COUNTRY        TEXT    NOT NULL, " +
                    " LATITUDE       DECIMAL(5,5)     NOT NULL, " +
                    " LONGITUDE      DECIMAL(5,5)   NOT NULL);";
            stmt.executeUpdate(sql);
        } catch (SQLException e) {

        }
        // Read in file
        Scanner sc = new Scanner(new File(filename));
        while (sc.hasNextLine()) {
            String[] split = sc.nextLine().split(",");
            sql = "INSERT INTO US_AIRPORTS (ID,COUNTRY,LATITUDE,LONGITUDE) " +
                "VALUES ("+split[0]+", "+split[3]+", "+split[6]+", "+split[7]+");";

            if (split[3].equals("\"United States\"")) {
                stmt.executeUpdate(sql);
            }
        }
    }

    public static void readRoutes(String filename)
            throws FileNotFoundException, ClassNotFoundException, SQLException {
        Connection c;
        Statement stmt;
        String sql;
        Class.forName("org.sqlite.JDBC");
        c = DriverManager.getConnection("jdbc:sqlite:flights.db");
        stmt = c.createStatement();

        // Create Table
        try {
            sql = "CREATE TABLE ROUTES " +
                    "(AIRPORT_SOURCE_ID    INT     NOT NULL," +
                    " AIRPORT_DEST_ID      INT    NOT NULL);";
            stmt.executeUpdate(sql);
        } catch (SQLException e) {

        }
        // Read in file
        Scanner sc = new Scanner(new File(filename));
        while (sc.hasNextLine()) {
            String[] split = sc.nextLine().split(",");

            if (Integer.parseInt(split[7]) == 0 &&
                    !split[3].equals("\\N") && !split[5].equals("\\N")) {
                sql = "INSERT INTO ROUTES (AIRPORT_SOURCE_ID,AIRPORT_DEST_ID) " +
                        "VALUES (" + split[3] + ", " + split[5]+ ");";
                stmt.executeUpdate(sql);
            }
        }
    }

    public static boolean passThrough(Point start, Point destination, int startIdx, int endIdx, ConvexHull convex) {
        /*
        System.out.println("START");
        System.out.println(start.toString()+" "+destination.toString());
        System.out.println(convex.getHull().toString());
*/
        ArrayList<Point> points = convex.getHull();
        int midIdx = startIdx + (endIdx - startIdx)/2;
        if (endIdx <= startIdx) {
            return false;
        }

        Point vStart = points.get(0);
        Point vDest = points.get(midIdx);

        if (intersect(start, destination, vStart, vDest)) {
            return true;
        }
        else {
            if (endIdx == startIdx + 1) {
                return false;
            }

            double crossProd = ConvexHull.crossProd(vStart, vDest, start);
            // If start is left of current line
            if (crossProd < 0) {
                return passThrough(start, destination, midIdx, endIdx, convex);
            }
            // If start is right of current line
            else if (crossProd > 0) {
                return passThrough(start, destination, startIdx, midIdx, convex);
            }
            else {
                return true;
            }
        }
    }

    public static boolean intersect(Point start1, Point end1, Point start2, Point end2) {
        return (ConvexHull.crossProd(start1, end1, start2) > 0) != (ConvexHull.crossProd(start1, end1, end2) > 0) &&
                (ConvexHull.crossProd(start2, end2, start1) > 0) !=
                        (ConvexHull.crossProd(start2, end2, end1) > 0);
    }

    public static void main(String[] args)
            throws FileNotFoundException, ClassNotFoundException, SQLException {
        Cluster cluster = new Cluster(0, new Point(1,2));
        cluster.add(new Point(2, -3));
        cluster.add(new Point(2, 2));
        cluster.add(new Point(-2, 2));
        cluster.add(new Point(-1, -2));

        Point x0 = new Point(4, 1);
        Point x1 = new Point(1, -4);

        ConvexHull ch = new ConvexHull(cluster);
        //readAirports("files/airports.csv");
        //readRoutes("files/routes.csv");
        Connection c;
        Statement stmt;
        String sql;


        //Class.forName("org.sqlite.JDBC");
        c = DriverManager.getConnection("jdbc:sqlite:flights.db");
        stmt = c.createStatement();
        sql = "SELECT COUNT(*) FROM ROUTES, US_AIRPORTS a1, US_AIRPORTS a2 "+
                "WHERE AIRPORT_SOURCE_ID = a1.ID AND AIRPORT_DEST_ID = a2.ID;";
        ResultSet rs = stmt.executeQuery(sql);
        System.out.println(rs.getString(1));
    }
}
