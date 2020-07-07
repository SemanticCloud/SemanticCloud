package org.semanticcloud.agentGateway;

public class GatewayException extends Exception {

	public GatewayException() {
		super();
	}

	public GatewayException(String message, Throwable cause) {
		super(message, cause);
	}

	public GatewayException(String message) {
		super(message);
	}

	public GatewayException(Throwable cause) {
		super(cause);
	}

}
