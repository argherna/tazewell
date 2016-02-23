package com.github.argherna.tazewell.valves;

import java.io.IOException;

import javax.servlet.ServletException;

import org.apache.catalina.Valve;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ValveBase;

public class TomcatAuthorizationValve extends ValveBase {

	@Override
	public void invoke(Request request, Response response) throws IOException, ServletException {
		
		org.apache.coyote.Request req = request.getCoyoteRequest();
		req.setRemoteUserNeedsAuthorization(true);
		request.setCoyoteRequest(req);
		
		// Avoids NPE if getNext() == null.
		Valve v = getNext();
		if (v != null) {
			v.invoke(request, response);
		}

	}

}
