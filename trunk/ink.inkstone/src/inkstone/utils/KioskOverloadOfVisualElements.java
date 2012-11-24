package inkstone.utils;

@SuppressWarnings("serial")
public class KioskOverloadOfVisualElements extends Exception {
	public KioskOverloadOfVisualElements(String message) {
        super(message);
    }

    public KioskOverloadOfVisualElements(String message, Throwable throwable) {
        super(message, throwable);
    }
}