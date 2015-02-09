package info.gherna.tazewell.valves;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletException;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.Valve;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ValveBase;

/**
 * Tomcat valve that will add an alias request attribute to an existing request
 * attribute on a Request.
 * 
 * <p>
 * An alias for a request attribute means that a given request attribute will
 * have the same value as another with a different name. If the attribute is on
 * the {@link Request} and an alias is mapped to that request attribute then an
 * alias will be added.
 * </p>
 * 
 * <p>
 * The following must be set for this filter to map aliases:
 * <dl>
 * <dt>{@code aliases}</dt>
 * <dd>Comma-delimited lists of {@code REAL NAME:ALIAS} pairs for attributes.
 * For example, if the request attributes {@code real_name0} and
 * {@code real_name1} were to be aliased by {@code alias0} and {@code alias1},
 * the value for this string would be
 * {@code real_name0:alias0,real_name1:alias1}.</dd>
 * <dl>
 * </p>
 * 
 * @author agherna
 *
 */
public class AliasRequestAttributesValve extends ValveBase {

	private String aliases;

	private Map<String, String> aliasMappings;

	/**
	 * Add aliases for configured {@link Request} attributes where specified.
	 */
	@Override
	public void invoke(Request request, Response response) throws IOException,
			ServletException {

		for (Entry<String, String> e : aliasMappings.entrySet()) {

			if (request.getAttribute(e.getKey()) != null) {
				request.setAttribute(e.getValue(),
						request.getAttribute(e.getKey()));
			}
		}

		// Avoids NPE if getNext() == null.
		Valve v = getNext();
		if (v != null) {
			v.invoke(request, response);
		}
	}

	public void setAliases(String aliases) {
		this.aliases = aliases;
	}

	@Override
	protected void initInternal() throws LifecycleException {
		super.initInternal();

		aliasMappings = new HashMap<>();

		if (aliases != null && !aliases.isEmpty()) {

			String[] splitAliases = aliases.split("\\s*,\\s*");
			for (int i = 0; i < splitAliases.length; i++) {
				String[] alias = splitAliases[i].split("\\s*:\\s*");

				if (alias.length == 2) {
					aliasMappings.put(alias[0], alias[1]);
				}
			}
		}
	}
}
