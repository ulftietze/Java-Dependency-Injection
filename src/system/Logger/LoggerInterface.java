package system.Logger;

public interface LoggerInterface
{
    public void log(String message);

    public void logException(String message, Throwable e);
}
