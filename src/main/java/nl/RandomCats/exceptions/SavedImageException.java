package nl.RandomCats.exceptions;

public class SavedImageException extends Exception {
	
	private boolean fatal;
	
	public SavedImageException(String message) {
		super(message);
	}
	
	public SavedImageException(Throwable cause) {
		super(cause);
	}
	
	public SavedImageException(String message, boolean fatal) {
		super(message);
		this.fatal = fatal;
	}
	
	public SavedImageException(String message, Throwable cause) {
		super(message, cause);
	}

	public SavedImageException(String message, Throwable cause, boolean fatal) {
		super(message, cause);
		this.fatal = fatal;
	}

	public boolean isFatal() {
		return fatal;
	}
}
