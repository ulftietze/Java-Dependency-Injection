package model.behavior;

import system.Logger.LoggerInterface;
import system.objects.Inject;

public class SlowDrivingBehavior implements DrivingBehaviorInterface
{
    @Inject
    private LoggerInterface logger;

    @Override
    public void drive()
    {
        this.logger.log("Drive really slow");
    }
}
