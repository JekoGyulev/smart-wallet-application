package app.exception;

public class RetryFailedNotificationsException extends RuntimeException {
    public RetryFailedNotificationsException(String message) {
        super(message);
    }
}
