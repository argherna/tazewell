package com.github.argherna.tazewell.valves;

import java.io.IOException;
import java.security.Principal;

import javax.servlet.ServletException;

import org.apache.catalina.Realm;
import org.apache.catalina.Valve;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ValveBase;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

public class TomcatAuthorizationValve extends ValveBase {

	private static final Log log = LogFactory.getLog(TomcatAuthorizationValve.class);

	@Override
	public void invoke(Request request, Response response) throws IOException, ServletException {

		Realm r = request.getContext().getRealm();
		if (r != null) {
			log.debug(String.format("Found realm class %s, authenticating.", r.getClass().getName()));
			Principal p = r.authenticate(request.getRemoteUser());
			if (p == null) {
				log.warn("Principal object not found!");
			} else {
				log.debug(String.format("Principal %s authenticated", p));
			}
			request.setUserPrincipal(p);
		}

		// Avoids NPE if getNext() == null.
		Valve v = getNext();
		if (v != null) {
			v.invoke(request, response);
		}

	}

}
