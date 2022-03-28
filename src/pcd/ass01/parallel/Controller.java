package pcd.ass01.parallel;

import pcd.ass01.parallel.monitor.Flag;
import pcd.ass01.ui.InputListener;

/**
 * Controller part of the application - passive part.
 *
 * @author aricci
 */
public class Controller implements InputListener {

    private final Flag runningFlag;

    public Controller(Flag runningFlag) {
        this.runningFlag = runningFlag;
    }

    @Override
    public void onResumePressed() {
        runningFlag.set();
    }

    @Override
    public void onPausePressed() {
        runningFlag.reset();
    }
}
