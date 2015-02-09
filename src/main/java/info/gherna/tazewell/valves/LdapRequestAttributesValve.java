package info.gherna.tazewell.valves;

import java.io.IOException;
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import javax.servlet.ServletException;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.Valve;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ValveBase;

/**
 * Tomcat valve that gets attributes from LDAP for an authenticated user.
 * 
 * <p>
 * This valve has to be placed in the pipeline AFTER authentication has occurred
 * in order to retrieve values.
 * </p>
 * 
 * <p>
 * Attributes will be read from the configured LDAP service (see below). All
 * attributes will be added to the {@link Request} object as attributes which
 * will be accessible in Servlet code via
 * {@code HttpServletRequest.getAttribute(java.lang.String)}.
 * </p>
 * 
 * <p>
 * These options must be set or this valve will fail to start:
 * <dl>
 * <dt>{@code ldapUrl}</dt>
 * <dd>URL to the LDAP server.</dd>
 * <dt>{@code bindDn}</dt>
 * <dd>Bind DN for the service user who will make LDAP queries.</dd>
 * <dt>{@code password}</dt>
 * <dd>Service user's password.</dd>
 * <dt>{@code baseDn}</dt>
 * <dd>Base DN to perform all searches from.</dd>
 * <dt>{@code searchAttribute}</dt>
 * <dd>Search attribute to use (e.g. {@code cn}).</dd>
 * </dl>
 * </p>
 * 
 * @author agherna
 *
 */
public class LdapRequestAttributesValve extends ValveBase {

	private String ldapUrl;

	private String bindDn;

	private String password;

	private String baseDn;

	private String searchAttribute;

	private LdapContext ldapCtx;

	/**
	 * Reads attributes from the first LDAP record found based on the value of
	 * {@code Request.getRemoteUser()}. If there is no value for
	 * {@code REMOTE_USER} then this logic will fall through to the next valve
	 * in the chain.
	 */
	@Override
	public void invoke(Request request, Response response) throws IOException,
			ServletException {

		if (request.getRemoteUser() != null
				&& !request.getRemoteUser().isEmpty()) {

			try {

				String filter = String.format("(%s=%s)", searchAttribute,
						request.getRemoteUser());

				SearchControls sc = new SearchControls();
				sc.setSearchScope(SearchControls.SUBTREE_SCOPE);
				sc.setTimeLimit(1500);

				// Create an LdapContext instance for this request only.
				LdapContext ldapCtxReq = ldapCtx.newInstance(null);
				NamingEnumeration<SearchResult> results = ldapCtxReq.search(
						baseDn, filter, sc);

				if (results.hasMoreElements()) {
					SearchResult result = results.next();
					Attributes atts = result.getAttributes();
					NamingEnumeration<? extends Attribute> attrs = atts
							.getAll();
					while (attrs.hasMore()) {
						Attribute attr = attrs.nextElement();
						NamingEnumeration<?> values = attr.getAll();
						StringBuilder attrValue = new StringBuilder();
						while (values.hasMore()) {
							attrValue.append(String.valueOf(values
									.nextElement()));
							if (values.hasMore()) {
								attrValue.append(";");
							}
						}
						request.setAttribute(attr.getID(), attrValue.toString());
					}
				}

			} catch (NamingException e) {
				throw new ServletException(e);
			}
		}

		// Avoids NPE if getNext() == null.
		Valve v = getNext();
		if (v != null) {
			v.invoke(request, response);
		}
	}

	/**
	 * Verify configuration is set and initialize an LdapContext.
	 * 
	 * @throws LifecycleException
	 *             if any parameter is not initialized or there is a problem
	 *             initializing the LdapContext.
	 */
	@Override
	protected void initInternal() throws LifecycleException {
		super.initInternal();

		if (ldapUrl == null || ldapUrl.isEmpty()) {
			throw new LifecycleException("ldapUrl not set!");
		}

		if (bindDn == null || bindDn.isEmpty()) {
			throw new LifecycleException("bindDn not set!");
		}

		if (password == null || password.isEmpty()) {
			throw new LifecycleException("password not set!");
		}

		if (password == null || password.isEmpty()) {
			throw new LifecycleException("password not set!");
		}

		if (baseDn == null || baseDn.isEmpty()) {
			throw new LifecycleException("baseDn not set!");
		}

		if (searchAttribute == null || searchAttribute.isEmpty()) {
			throw new LifecycleException("searchAttribute not set!");
		}

		Hashtable<String, Object> env = new Hashtable<>();
		env.put(Context.INITIAL_CONTEXT_FACTORY,
				"com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, ldapUrl);
		env.put(Context.SECURITY_AUTHENTICATION, "simple");
		env.put(Context.SECURITY_PRINCIPAL, bindDn);
		env.put(Context.SECURITY_CREDENTIALS, password);

		try {
			ldapCtx = new InitialLdapContext(env, null);
		} catch (NamingException e) {
			throw new LifecycleException(e);
		}
	}

	public void setBindDn(String bindDn) {
		this.bindDn = bindDn;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setLdapUrl(String ldapUrl) {
		this.ldapUrl = ldapUrl;
	}

	public void setBaseDn(String baseDn) {
		this.baseDn = baseDn;
	}

	public void setSearchAttribute(String searchAttribute) {
		this.searchAttribute = searchAttribute;
	}
}
