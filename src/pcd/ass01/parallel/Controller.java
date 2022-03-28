package pcd.ass01.parallel;

import pcd.ass01.ui.InputListener;

/**
 * Controller part of the application - passive part.
 *
 * @author aricci
 */
public class Controller implements InputListener {

    private final Simulator simulator;

    public Controller(Simulator simulator) {
        this.simulator = simulator;
    }

    @Override
    public void onResumePressed() {
        new Thread(simulator::playSimulation).start();
    }

    @Override
    public void onPausePressed() {
        new Thread(simulator::pauseSimulation).start();
    }
}
