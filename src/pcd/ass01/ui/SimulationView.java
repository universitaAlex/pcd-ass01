package pcd.ass01.ui;

import pcd.ass01.model.Body;
import pcd.ass01.model.Boundary;
import pcd.ass01.model.P2d;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Simulation view
 *
 * @author aricci
 */
public class SimulationView implements SimulationDisplay {

    private VisualiserFrame frame;

    /**
     * Creates a view of the specified size (in pixels)
     *
     * @param w
     * @param h
     */
    public SimulationView(int w, int h) {
        frame = new VisualiserFrame(w, h);
    }

    @Override
    public void display(Collection<Body> bodies, double vt, long iter, Boundary bounds) {
        frame.display(bodies, vt, iter, bounds);
    }

    public void addListener(InputListener l) {
        frame.addListener(l);
    }

    public void removeListener(InputListener l) {
        frame.removeListener(l);
    }


    public static class VisualiserFrame extends JFrame {
        private static final String MOVE_UP = "move up";
        private static final String MOVE_DOWN = "move down";

        private final VisualiserPanel panel;
        private final ArrayList<InputListener> listeners = new ArrayList<>();

        public VisualiserFrame(int w, int h) {
            setTitle("Bodies Simulation");
            setSize(w, h);
            setResizable(false);
            setFocusTraversalKeysEnabled(false);
            requestFocusInWindow();
            panel = new VisualiserPanel(w, h);

            JPanel buttonsPanel = new JPanel();
            buttonsPanel.setLayout(new FlowLayout());

            JButton playButton = new JButton("Play");
            playButton.addActionListener((action) -> {
                for (InputListener listener : listeners) {
                    listener.onResumePressed();
                }
            });
            buttonsPanel.add(playButton);
            JButton pauseButton = new JButton("Pause");
            pauseButton.addActionListener((action) -> {
                for (InputListener listener : listeners) {
                    listener.onPausePressed();
                }
            });
            buttonsPanel.add(pauseButton);

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

            addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent ev) {
                    System.exit(-1);
                }

                public void windowClosed(WindowEvent ev) {
                    System.exit(-1);
                }
            });
            this.setVisible(true);
            setupKeystrokeActions();
        }

        private void setupKeystrokeActions() {
            InputMap inputMap = panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
            inputMap.put(KeyStroke.getKeyStroke("UP"), MOVE_UP);
            inputMap.put(KeyStroke.getKeyStroke("DOWN"), MOVE_DOWN);
            panel.getActionMap().put(MOVE_UP, new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    updateScale(1.1);
                }
            });
            panel.getActionMap().put(MOVE_DOWN, new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    updateScale(0.9);
                }
            });
        }

        public void display(Collection<Body> bodies, double vt, long iter, Boundary bounds) {
            try {
                SwingUtilities.invokeAndWait(() -> {
                    panel.display(bodies, vt, iter, bounds);
                    repaint();
                });
            } catch (Exception ex) {
            }
        }

        public void updateScale(double k) {
            panel.updateScale(k);
        }

        public void addListener(InputListener l) {
            listeners.add(l);
        }

        public void removeListener(InputListener l) {
            listeners.remove(l);
        }
    }

    public static class VisualiserPanel extends JPanel {

        private Collection<Body> bodies;
        private Boundary bounds;

        private long nIter;
        private double vt;
        private double scale = 1;

        private long dx;
        private long dy;

        public VisualiserPanel(int w, int h) {
            setSize(w, h);
            dx = w / 2 - 20;
            dy = h / 2 - 20;
        }

        public void paint(Graphics g) {
            if (bodies != null) {
                Graphics2D g2 = (Graphics2D) g;

                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_RENDERING,
                        RenderingHints.VALUE_RENDER_QUALITY);
                g2.clearRect(0, 0, this.getWidth(), this.getHeight());


                int x0 = getXcoord(bounds.getX0());
                int y0 = getYcoord(bounds.getY0());

                int wd = getXcoord(bounds.getX1()) - x0;
                int ht = y0 - getYcoord(bounds.getY1());

                g2.drawRect(x0, y0 - ht, wd, ht);

                bodies.forEach(b -> {
                    P2d p = b.getPos();
                    int radius = (int) (10 * scale);
                    if (radius < 1) {
                        radius = 1;
                    }
                    g2.drawOval(getXcoord(p.getX()), getYcoord(p.getY()), radius, radius);
                });
                String time = String.format("%.2f", vt);
                g2.drawString("Bodies: " + bodies.size() + " - vt: " + time + " - nIter: " + nIter + " (UP for zoom in, DOWN for zoom out)", 2, 20);
            }
        }

        private int getXcoord(double x) {
            return (int) (dx + x * dx * scale);
        }

        private int getYcoord(double y) {
            return (int) (dy - y * dy * scale);
        }

        public void display(Collection<Body> bodies, double vt, long iter, Boundary bounds) {
            this.bodies = bodies;
            this.bounds = bounds;
            this.vt = vt;
            this.nIter = iter;
        }

        public void updateScale(double k) {
            scale *= k;
            repaint();
        }
    }
}
