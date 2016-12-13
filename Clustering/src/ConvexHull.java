import java.awt.*;
import java.awt.geom.Point2D;
import java.lang.reflect.Array;
import java.util.*;

/**
 * Created by Ryan on 12/8/2016.
 */
public class ConvexHull {
    int flightsThrough = 0;

    /** Hull is ordered starting with leftmost point and going counter-clockwise **/
    public ArrayList<Point> jarvisMarch(Cluster cluster) {
        int numPoints = cluster.size();
        ArrayList<Point> points = new ArrayList<>(cluster.cluster);
        ArrayList<Point> hull = new ArrayList<>();

        // Find leftmost point
        int leftIdx = -1;
        double leftmost = Double.MAX_VALUE;
        for (int i=0; i < points.size(); i++) {
            if (points.get(i).lng < leftmost) {
                leftmost = points.get(i).lng;
                leftIdx = i;
            }
        }

        Point curHullPoint = points.get(leftIdx);

        Point endPoint;
        do {
            hull.add(curHullPoint);
            endPoint = points.get(0);
            for (int i=0; i < points.size(); i++) {
                if (curHullPoint == endPoint || crossProd(curHullPoint, endPoint, points.get(i)) < 0) {
                    endPoint = points.get(i);
                }
            }

            curHullPoint = endPoint;
        } while (endPoint != hull.get(0));

        return hull;
    }
/*
    public LinkedList<Point>[] grahamScan(Cluster cluster) {
        int numPoints = cluster.size();
        ArrayList<Point> points = new ArrayList<>(cluster.cluster);

        // Swap points[1] with lowest latitude
        int lowIdx = -1;
        double lowLat = 999999;
        double lowLng = 999999;
        for (int i=0; i < points.size(); i++) {
            if (points.get(i).lat <= lowLat) {
                if (points.get(i).lat != lowLat || points.get(i).lng < lowLng) {
                    lowLat = points.get(i).lat;
                    lowIdx = i;
                    lowLng = points.get(i).lng;
                }
            }
        }
        swap(points, 1, lowIdx);
        bot = points.get(1);

        // Sort points by polar angle to points[1]
        Collections.sort(points, new Comparator<Point> () {
            @Override
            public int compare(Point p1, Point p2) {
                double det = turn(bot, p1, p2);
                if (det < 0)
                    return -1;
                if (det == 0)
                    return 0;
                return 1;
            }
        });
    }
*/

    public static double crossProd(Point center, Point p1, Point p2) {
        return (p1.lng - center.lng) * (p2.lat - p1.lat) - (p1.lat - center.lat) * (p2.lng - p1.lng);
    }

    private ArrayList<Point> hull;

    public ConvexHull(Cluster cluster) {
        hull = jarvisMarch(cluster);
    }

    @Override
    public String toString() {
        String str = "{";
        for (Point p : hull) {
            str += p.toString() + " ";
        }

        return str + "}";
    }

    public ArrayList<Point> getHull() {
        return hull;
    }
}
