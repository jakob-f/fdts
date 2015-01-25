package at.ac.tuwien.media.master.webapp.ws;

import javax.xml.ws.Endpoint;

import at.frohnwieser.mahut.webapp.ws.WSEndpointImpl;

public class WSEndpointPublisher {
    public static void main(final String[] args) {
	System.out.println("--- START WS ENDPOINT");
	Endpoint.publish("http://localhost:8080/ws", new WSEndpointImpl());
    }
}
