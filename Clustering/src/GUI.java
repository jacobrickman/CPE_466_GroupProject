import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Draws the sightings and outlines the clusters they are in, on top of a map of the US
 * @author JacobRickman
 *
 */
public class GUI {
	 private static void createAndShowGUI() {
	        //Create and set up the window.
	        JFrame frmUfos = new JFrame();
	        frmUfos.setResizable(false);
	        frmUfos.setTitle("UFOs");
	        frmUfos.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	        
	        JPanel panel = new MyPanel();
	        panel.addMouseListener(new MouseAdapter() {
	        	@Override
	        	public void mouseClicked(MouseEvent e) {
	        		double scaleLong = .03853;
	        		double offsetLong = 64.13079;
	        		double scaleLat = .0290644127;
	        		double offsetLat = -25.22335082;
	        		int offsetX = 1600;
	        		int offsetY = 861;
	        		double pxX = e.getPoint().getX();
	        		double pxY = e.getPoint().getY();
	        		double x = ((pxX - offsetX) * scaleLong) - offsetLong;
	        		double y = ((pxY - offsetY) * scaleLat / -1.0) - offsetLat; 
	        		
	        		System.out.println(x + ", " + y);
	        	}
	        });
	        
	        frmUfos.getContentPane().add(panel);
	        
	        //Display the window.
	        frmUfos.pack();
	        frmUfos.setVisible(true);
	    }
	 
	    public static void main(String[] args) {
	        //Schedule a job for the event-dispatching thread:
	        //creating and showing this application's GUI.
	        javax.swing.SwingUtilities.invokeLater(new Runnable() {
	            public void run() {
	                createAndShowGUI();
	            }
	        });
	    }
}
	    
class MyPanel extends JPanel {

        public MyPanel() {
            setBorder(BorderFactory.createLineBorder(Color.black));
        }

        public Dimension getPreferredSize() {
            return new Dimension(1600,882);
        }

        public void paintComponent(Graphics g) {
        	ArrayList<Oval> ovals = null;
        	int leftShiftedX;
        	int upShiftedY;
        	
            super.paintComponent(g);    
            
            try
            {
            	BufferedImage img = ImageIO.read(new File("Real_Map.jpg"));
            	g.drawImage(img, 0, 0, this);
            }
            catch (Exception e)
            {
            	System.out.println("Paint: " + e);
            }
            
            Clustering.run(1960);

            ovals = Clustering.createCircles(Clustering.clusters);
            
            //Draw Cluster Outlines
            for (Oval o : ovals)
            {
            	g.setColor(Color.WHITE);
            	//g.fillOval(o.midX, o.midY, 10, 10);
            	g.drawOval(o.midX, o.midY, o.width, o.height);
            }
            
            
            ovals = Clustering.createSightings(Clustering.clusters);
            
            
            //Draw sightings
            for (Oval o : ovals)
            {
            	g.setColor(Color.BLACK);
            	g.fillOval(o.midX, o.midY, o.width, o.height);
            }

        }  
}


