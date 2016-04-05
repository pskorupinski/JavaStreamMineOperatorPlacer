package org.microcloud.manager.persistence.objectsloader;


public class DataAccessLayerException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = -139486638959918697L;

	public DataAccessLayerException() {
    }

    public DataAccessLayerException(String message) {
        super(message);
    }

    public DataAccessLayerException(Throwable cause) {
        super(cause);
    }

    public DataAccessLayerException(String message, Throwable cause) {
        super(message, cause);
    }
}
