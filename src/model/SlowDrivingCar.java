package model;

import model.behavior.DrivingBehaviorInterface;
import model.behavior.SlowDrivingBehavior;
import system.objects.Inject;

public class SlowDrivingCar implements CarInterface
{
    // Even if the {@class FastDrivingBehavior} is globally configured we can override them per class
    @Inject(concrete = SlowDrivingBehavior.class)
    protected DrivingBehaviorInterface drivingBehavior;

    @Override
    public void drive()
    {
        this.drivingBehavior.drive();
    }
}
