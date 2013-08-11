package inkstone.utils;

@SuppressWarnings("serial")
public class KioskOverloadOfElementsException extends Exception {
	public KioskOverloadOfElementsException(String message) {
        super(message);
    }

    public KioskOverloadOfElementsException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
