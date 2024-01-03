package app;

import model.behavior.DrivingBehaviorInterface;
import model.behavior.FastDrivingBehavior;
import system.Logger.LoggerInterface;
import system.Logger.SystemOutLogger;
import system.objects.ObjectManager;

public class InstanceRegistrationHandler
{
    public static void run()
    {
        ObjectManager.set(LoggerInterface.class, SystemOutLogger.class);
        ObjectManager.set(DrivingBehaviorInterface.class, FastDrivingBehavior.class);
    }
}
