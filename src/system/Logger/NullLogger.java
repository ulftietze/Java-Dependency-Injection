package system.Logger;

public class NullLogger implements LoggerInterface, QueryLoggerInterface
{
    @Override
    public void log(String message)
    {

    }

    @Override
    public void logException(String message, Throwable e)
    {

    }
}
