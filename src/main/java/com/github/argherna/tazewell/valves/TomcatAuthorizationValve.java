package com.github.argherna.tazewell.valves;

import java.io.IOException;
import java.security.Principal;

import javax.servlet.ServletException;

import org.apache.catalina.Realm;
import org.apache.catalina.Valve;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ValveBase;

public class TomcatAuthorizationValve extends ValveBase {

	@Override
	public void invoke(Request request, Response response) throws IOException, ServletException {
		
		Realm r = request.getContext().getRealm();
		if (r != null) {
			Principal p = r.authenticate(request.getRemoteUser());
			request.setUserPrincipal(p);
		}
		
		// Avoids NPE if getNext() == null.
		Valve v = getNext();
		if (v != null) {
			v.invoke(request, response);
		}

	}

}
