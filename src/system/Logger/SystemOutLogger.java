package system.Logger;

import java.util.Arrays;
import java.util.stream.Collectors;

public class SystemOutLogger implements LoggerInterface, QueryLoggerInterface
{
    @Override
    public void log(String message)
    {
        System.out.println(message);
    }

    @Override
    public void logException(String message, Throwable e)
    {
        String trace = Arrays
                .stream(e.getStackTrace())
                .map(StackTraceElement::toString)
                .collect(Collectors.joining("\n"));

        this.log(e.getClass().getName() + ": " + message + "\n" + trace);
    }
}
