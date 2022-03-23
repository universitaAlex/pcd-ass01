package pcd.ass01.ui;

import pcd.ass01.model.Body;
import pcd.ass01.model.Boundary;
import pcd.ass01.model.P2d;
import pcd.ass01.model.SimulationDisplay;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Simulation view
 *
 * @author aricci
 *
 */
public class SimulationView implements SimulationDisplay {
        
	private VisualiserFrame frame;
	
    /**
     * Creates a view of the specified size (in pixels)
     * 
     * @param w
     * @param h
     */
    public SimulationView(int w, int h){
    	frame = new VisualiserFrame(w,h);
    }
        
    public void display(Collection<Body> bodies, double vt, long iter, Boundary bounds){
 	   frame.display(bodies, vt, iter, bounds); 
    }

    public static class VisualiserFrame extends JFrame implements KeyListener {

        private VisualiserPanel panel;

        public VisualiserFrame(int w, int h){
            setTitle("Bodies Simulation");
            setSize(w,h);
            setResizable(false);
			setFocusable(true);
			setFocusTraversalKeysEnabled(false);
			requestFocusInWindow();
			panel = new VisualiserPanel(w,h);

			JPanel buttonsPanel = new JPanel();
			buttonsPanel.setLayout(new FlowLayout());
			buttonsPanel.add(new JButton("Play"));
			buttonsPanel.add(new JButton("Pause"));

			GridBagConstraints panelCons = new GridBagConstraints();
			panelCons.fill = GridBagConstraints.BOTH;
			panelCons.weightx = 1;
			panelCons.weighty = 1;
			panelCons.gridx = 0;
			panelCons.gridy = 0;
			GridBagConstraints buttonsConstraints = new GridBagConstraints();
			buttonsConstraints.gridx = 0;
			JPanel cp = new JPanel(new GridBagLayout());
			cp.add(panel, panelCons);
			cp.add(buttonsPanel, buttonsConstraints);
			setContentPane(cp);
			this.addKeyListener(this);

			addWindowListener(new WindowAdapter(){
    			public void windowClosing(WindowEvent ev){
    				System.exit(-1);
    			}
    			public void windowClosed(WindowEvent ev){
    				System.exit(-1);
    			}
    		});
    		this.setVisible(true);
        }

        public void display(Collection<Body> bodies, double vt, long iter, Boundary bounds){
        	try {
	        	SwingUtilities.invokeAndWait(() -> {
	        		panel.display(bodies, vt, iter, bounds);
	            	repaint();
	        	});
        	} catch (Exception ex) {}
        };
        
        public void updateScale(double k) {
        	panel.updateScale(k);
        }

		@Override
		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == 38){  		/* KEY UP */
				panel.scale *= 1.1;
			} else if (e.getKeyCode() == 40){  	/* KEY DOWN */
				panel.scale *= 0.9;
			}
		}

		public void keyReleased(KeyEvent e) {}
		public void keyTyped(KeyEvent e) {}
    }

    public static class VisualiserPanel extends JPanel {
        
    	private Collection<Body> bodies;
    	private Boundary bounds;
    	
    	private long nIter;
    	private double vt;
    	private double scale = 1;
    	
        private long dx;
        private long dy;
        
        public VisualiserPanel(int w, int h){
            setSize(w,h);
            dx = w/2 - 20;
            dy = h/2 - 20;
        }

        public void paint(Graphics g){    		    		
    		if (bodies != null) {
        		Graphics2D g2 = (Graphics2D) g;
        		
        		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
        		          RenderingHints.VALUE_ANTIALIAS_ON);
        		g2.setRenderingHint(RenderingHints.KEY_RENDERING,
        		          RenderingHints.VALUE_RENDER_QUALITY);
        		g2.clearRect(0,0,this.getWidth(),this.getHeight());

        		
        		int x0 = getXcoord(bounds.getX0());
        		int y0 = getYcoord(bounds.getY0());
        		
        		int wd = getXcoord(bounds.getX1()) - x0;
        		int ht = y0 - getYcoord(bounds.getY1());
        		
    			g2.drawRect(x0, y0 - ht, wd, ht);
    			
	    		bodies.forEach( b -> {
	    			P2d p = b.getPos();
			        int radius = (int) (10*scale);
			        if (radius < 1) {
			        	radius = 1;
			        }
			        g2.drawOval(getXcoord(p.getX()),getYcoord(p.getY()), radius, radius); 
			    });		    
	    		String time = String.format("%.2f", vt);
	    		g2.drawString("Bodies: " + bodies.size() + " - vt: " + time + " - nIter: " + nIter + " (UP for zoom in, DOWN for zoom out)", 2, 20);
    		}
        }
        
        private int getXcoord(double x) {
        	return (int)(dx + x*dx*scale);
        }

        private int getYcoord(double y) {
        	return (int)(dy - y*dy*scale);
        }
        
        public void display(Collection<Body> bodies, double vt, long iter, Boundary bounds){
            this.bodies = bodies;
            this.bounds = bounds;
            this.vt = vt;
            this.nIter = iter;
        }
        
        public void updateScale(double k) {
        	scale *= k;
        }
    }
}
