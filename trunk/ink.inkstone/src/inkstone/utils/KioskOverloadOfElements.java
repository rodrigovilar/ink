package inkstone.utils;

@SuppressWarnings("serial")
public class KioskOverloadOfElements extends Exception {
	public KioskOverloadOfElements(String message) {
        super(message);
    }

    public KioskOverloadOfElements(String message, Throwable throwable) {
        super(message, throwable);
    }
}
