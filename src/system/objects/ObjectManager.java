package system.objects;

import system.Logger.LoggerInterface;
import system.Logger.SystemOutLogger;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ObjectManager
{
    private static ObjectManager instance;

    private final Map<Class<?>, Class<?>> classMap = new HashMap<>();

    private final Map<Class<?>, Object> objectMap = new HashMap<>();

    private final Map<Class<?>, FactoryInterface<?>> objectFactoryMap = new HashMap<>();

    private ObjectManager() {}

    /**
     * Get an instance of an object for the given class name.
     * When the class does not exist, try to create this. This is kind of a lazy loading the class.
     *
     * @param className of the Object to create.
     * @param arguments If created per lazy load, the matching argument constructor is executed
     * @return an instance of the given className. This may be a child class.
     */
    @SuppressWarnings("unchecked")
    public static <T> T get(Class<T> className, Object... arguments)
    {
        ObjectManager om          = ObjectManager.getInstance();
        Class<?>      actualClass = om.getActualClassToCreate(className);

        if (om.objectMap.get(className) == null) {
            om.objectMap.put(className, om.create(className, actualClass, arguments));
        }

        return (T) om.objectMap.get(className);
    }

    /**
     * Get an instance of an object for the given class name.
     * When the class does not exist, try to create this. This is kind of a lazy loading the class.
     *
     * @param qualifiedClassName as String of the Object to create.
     * @param arguments          If created per lazy load, the matching argument constructor is executed
     * @return an instance of the given className. This may be a child class.
     */
    public static Object get(String qualifiedClassName, Object... arguments) throws ClassNotFoundException
    {
        Class<?> className = Class.forName(qualifiedClassName);

        return ObjectManager.get(className, arguments);
    }

    /**
     * Create an instance of an object for the given class name.
     *
     * @param className of the Object to create.
     * @param arguments The matching argument constructor is executed
     * @return an instance of the given className. This may be a child class.
     */
    @SuppressWarnings("unchecked")
    public static <T> T create(Class<T> className, Object... arguments)
    {
        ObjectManager om          = ObjectManager.getInstance();
        Class<?>      actualClass = om.getActualClassToCreate(className);

        return (T) om.create(className, actualClass, arguments);
    }

    /**
     * Add a ClassMapping.
     * This is especially necessary for Interfaces or AbstractClasses. This enables to change the concrete
     * implementation without changing anything inside the business logic.
     * <p>
     * Example:
     * LoggerInterface.class => ContextLogger.class
     * or
     * LoggerInterface.class => NullLogger.class
     *
     * @param className   which identifies as key
     * @param actualClass the class name which should be loaded when className is called.
     */
    public static void set(Class<?> className, Class<?> actualClass)
    {
        ObjectManager om = ObjectManager.getInstance();
        om.classMap.put(className, actualClass);
    }

    /**
     * Add a ClassObjectMapping.
     * This enables the direct mapping of an object without lazy loading. This puts the object directly to the
     * ClassObjectMapping.
     * <p>
     * This is especially necessary for Interfaces or AbstractClasses. This enables to change the concrete
     * implementation without changing anything inside the business logic.
     * <p>
     * Example:
     * LoggerInterface.class => ContextLogger.class
     * or
     * LoggerInterface.class => NullLogger.class
     *
     * @param className which identifies as key
     * @param object    the concrete object which should be loaded when className is called.
     */
    public static void set(Class<?> className, Object object)
    {
        ObjectManager om = ObjectManager.getInstance();
        om.objectMap.put(className, object);
    }

    /**
     * Add a ClassName on FactoryMapping.
     * This enables the lazy loading on objects but with more complex logic. If a class has a more complex
     * logic to get created, we can add a factory which will be called on class loading.
     *
     * @param className which identifies as key
     * @param factory   the factory which should be able to create an instance of the given class name.
     */
    public static void setFactory(Class<?> className, FactoryInterface<?> factory)
    {
        ObjectManager om = ObjectManager.getInstance();
        om.objectFactoryMap.put(className, factory);
    }

    private static ObjectManager getInstance()
    {
        if (instance == null) {
            instance = new ObjectManager();
        }

        return instance;
    }

    private Class<?> getActualClassToCreate(Class<?> className)
    {
        Class<?> actualClass = this.classMap.get(className);

        return actualClass != null ? actualClass : className;
    }

    private Object create(Class<?> className, Class<?> actualClass, Object... arguments)
    {
        FactoryInterface<?> factory = this.getFactoryIfExists(className, actualClass);

        if (factory != null) {
            return factory.create();
        }

        try {
            if (!this.canCreateInstance(actualClass)) {
                throw new InstantiationException("Can't create instance.");
            }

            Object instance;

            if (actualClass.getEnclosingClass() != null) {
                Class<?>[] parameters = new Class[arguments.length];
                parameters[0] = actualClass.getEnclosingClass();

                for (int i = 1; i < arguments.length; i++) {
                    parameters[i] = arguments[i].getClass();
                }

                Constructor<?> constructor = actualClass.getConstructor(parameters);
                constructor.setAccessible(true);
                instance = constructor.newInstance(ObjectManager.get(actualClass.getEnclosingClass(), arguments));
            } else {
                Class<?>[] parameters = new Class[arguments.length];

                for (int i = 0; i < arguments.length; i++) {
                    parameters[i] = arguments[i].getClass();
                }

                Constructor<?> constructor = actualClass.getConstructor(parameters);
                constructor.setAccessible(true);
                instance = constructor.newInstance(arguments);
            }

            this.injectFields(instance);

            return instance;
        } catch (ReflectiveOperationException e) {
            String message = "[ERROR] Could not instantiate class " + className.getName() + ": " + e.getMessage();
            this.getLogger().logException(message, e);
            throw new RuntimeException(message);
        }
    }

    private void injectFields(Object instance) throws IllegalAccessException
    {
        ArrayList<Field> declaredFields = new ArrayList<>(Arrays.asList(instance.getClass().getDeclaredFields()));
        Class<?>         currentClass   = instance.getClass();

        while (currentClass.getSuperclass() != null) {
            currentClass = currentClass.getSuperclass();

            declaredFields.addAll(Arrays.asList(currentClass.getDeclaredFields()));
        }

        for (Field field : declaredFields) {
            if (field.getAnnotation(Inject.class) == null) {
                continue;
            }

            Class<?> concreteClass = field.getType();

            if (field.getAnnotation(Inject.class).concrete() != Object.class) {
                concreteClass = field.getAnnotation(Inject.class).concrete();
            }

            // If in the class annotation defined, create a new instance of the class rather than
            // loading an existing instance
            boolean createInstance = field.getAnnotation(Inject.class).create();
            field.setAccessible(true);
            field.set(instance, createInstance ? create(concreteClass) : get(concreteClass));
        }
    }

    private boolean canCreateInstance(Class<?> toCheck)
    {
        return !Modifier.isAbstract(toCheck.getModifiers());
    }

    private FactoryInterface<?> getFactoryIfExists(Class<?> className, Class<?> actualClass)
    {
        FactoryInterface<?> factory = this.objectFactoryMap.get(actualClass);

        if (factory == null) {
            factory = this.objectFactoryMap.get(className);
        }

        return factory;
    }

    private LoggerInterface getLogger()
    {
        LoggerInterface logger = (LoggerInterface) this.objectMap.get(LoggerInterface.class);

        return logger != null ? logger : new SystemOutLogger();
    }
}
