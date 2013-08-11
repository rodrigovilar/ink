package inkstone.utils;

@SuppressWarnings("serial")
public class KioskOverloadOfVisualElementsException extends Exception {
	public KioskOverloadOfVisualElementsException(String message) {
        super(message);
    }

    public KioskOverloadOfVisualElementsException(String message, Throwable throwable) {
        super(message, throwable);
    }
}