package app;

import model.Car;
import model.SlowDrivingCar;
import system.objects.ObjectManager;

public class Main
{
    public static void main(String[] args)
    {
        InstanceRegistrationHandler.run();

        Car car = ObjectManager.get(Car.class, "Lets gooooo!", 5);
        assert car != null;
        car.drive();

        SlowDrivingCar slowCar = ObjectManager.get(SlowDrivingCar.class);
        assert slowCar != null;
        slowCar.drive();
    }
}
