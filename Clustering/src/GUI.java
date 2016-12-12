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
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.ScrollPane;
import java.awt.Component;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

/**
 * Draws the sightings and outlines the clusters they are in, on top of a map of the US
 * @author JacobRickman
 *
 */
public class GUI {
	private static JTextField textField;
	private static JTextField textField_1;
	
	 private static void createAndShowGUI() {
	        //Create and set up the window.
	        JFrame frmUfos = new JFrame();
	        frmUfos.setResizable(false);
	        frmUfos.setTitle("UFOs");
	        frmUfos.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	        
	        JSplitPane splitPane = new JSplitPane();
	        frmUfos.getContentPane().add(splitPane, BorderLayout.EAST);
	        
	        JPanel panel = new MyPanel();
	        
	        splitPane.setRightComponent(panel);
	        
	        JSlider slider = new JSlider();
	        slider.addChangeListener(new ChangeListener() {
	        	public void stateChanged(ChangeEvent e) {
	        		if (!slider.getValueIsAdjusting()) {
	        			int year = slider.getValue();
	        			JPanel newPanel = new MyPanel(year);
	        			splitPane.setRightComponent(newPanel);
	        		}
	        	}
	        });
	        slider.setMinorTickSpacing(1);
	        slider.setMajorTickSpacing(10);
	        slider.setMaximum(2010);
	        slider.setMinimum(1910);
	        slider.setValue(1960);
	        slider.setSnapToTicks(true);
	        slider.setPaintTicks(true);
	        slider.setPaintLabels(true);
	        slider.setOrientation(SwingConstants.VERTICAL);
	        splitPane.setLeftComponent(slider);
	        
	        //splitPane.setRightComponent(panel);
	        
	        /*
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
	        */        
	        
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

		public int year;
	
        public MyPanel() {
            setBorder(BorderFactory.createLineBorder(Color.black));
            this.year = 1960;
        }
        
        public MyPanel(int year) {
            setBorder(BorderFactory.createLineBorder(Color.black));
            this.year = year;
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
            
            Clustering.run(year);
            System.out.println("running " + year);

            ovals = Clustering.createCircles(Clustering.clusters);
            
            //Draw Cluster Outlines
            for (Oval o : ovals)
            {
            	g.setColor(Color.BLACK);
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


