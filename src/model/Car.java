package model;

import model.behavior.DrivingBehaviorInterface;
import model.behavior.SlowDrivingBehavior;
import system.Logger.LoggerInterface;
import system.objects.Inject;

public class Car implements CarInterface
{
    @Inject()
    protected DrivingBehaviorInterface drivingBehavior;

    @Inject
    private LoggerInterface logger;

    public Car()
    {

    }

    public Car(String s)
    {
        System.out.println(s);
    }

    public Car(String s, Integer noice)
    {
        System.out.println(s);
        System.out.println(noice);
    }

    @Override
    public void drive()
    {
        this.drivingBehavior.drive();
    }
}
