package controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import nl.idgis.sys.provisioning.domain.ProxyDomain;
import nl.idgis.sys.provisioning.domain.ProxyDomain.Alias;
import nl.idgis.sys.provisioning.domain.ProxyMapping;
import nl.idgis.sys.provisioning.domain.ProxyMappingType;
import nl.idgis.sys.provisioning.domain.ProxyPath;
import nl.idgis.sys.provisioning.registration.ServiceRegistration;
import nl.idgis.sys.provisioning.registration.ServiceRegistrationException;
import play.Configuration;
import play.Logger;
import play.inject.ApplicationLifecycle;
import play.libs.F.Promise;

@Singleton
public class ZooKeeper {
	
	private ServiceRegistration serviceRegistration = null;
	
	@Inject
	public ZooKeeper (final Configuration configuration, final ApplicationLifecycle applicationLifecycle) {
		// ZooKeeper registration:
		
		final String zooKeeperHosts = configuration.getString ("zooKeeper.hosts", null);
		if (zooKeeperHosts != null) {
			try {
				registerService (zooKeeperHosts, configuration);
				Logger.info("Registering has been successful");
			} catch (ServiceRegistrationException e) {
				Logger.error ("ZooKeeper registration failed", e);
			}
			
			applicationLifecycle.addStopHook (() -> {
				try {
					serviceRegistration.close ();
				} catch (Exception e) {
					Logger.error ("Failed to terminate ZooKeeper registration", e);
				}
				return Promise.pure (null);
			});
		} else {
			Logger.error("Registering has been unsuccessful");
		}
		
	}
	
	/**
	 * Performs the ZooKeeper registration for this Play application. Reads the configuration stored under
	 * the "service" key in application.conf and turns it into registrations for {@link ProxyDomain},
	 * {@link ProxyMapping} and {@link ProxyPath}.
	 * 
	 * @param zooKeeperHosts	The ZooKeeper hosts to connect with.
	 * @param config			The application config
	 * @throws					ServiceRegistrationException 
	 */
	private void registerService (final String zooKeeperHosts, final Configuration config) throws ServiceRegistrationException {
		Logger.info ("Registering with ZooKeeper cluster at " + zooKeeperHosts);

		// Start the service registration daemon:
		serviceRegistration = new ServiceRegistration (zooKeeperHosts, config.getString ("zooKeeper.namespace", null));
		
		// Add a proxy domain:
		final String domain = config.getString ("service.domain.name", "localhost");
		final List<Alias> aliases = new ArrayList<> ();
		final Map<String, List<String>> additionalConfig = new HashMap<> ();
		
		for (final String alias: config.getStringList ("service.domain.aliases", Collections.<String>emptyList ())) {
			aliases.add (new Alias (alias, false));
		}
		
		final Configuration additional = config.getConfig ("service.domain.additionalConfig");
		for (final String configName: additional.keys ()) {
			final List<String> configList = new ArrayList<> ();
			
			for (final String c: additional.getStringList (configName)) {
				if (!c.trim ().isEmpty ()) {
					configList.add (c);
				}
			}
			
			if (!configList.isEmpty ()) {
				additionalConfig.put (configName, configList);
			}
		}
		
		serviceRegistration.registerProxyDomain (new ProxyDomain (
				domain, 
				config.getBoolean ("service.domain.supportHttps", false).booleanValue (),
				aliases, 
				additionalConfig
			));

		// Generate a proxy mapping for this Play! application:
		final String path = config.getString ("application.context", "/");
		final int port = config.getInt ("http.port", 9000);
		final String destinationIP = config.getString ("service.mapping.destinationIP", ServiceRegistration.getPublicIp ());
		serviceRegistration.registerProxyMapping (new ProxyMapping (
				domain, 
				path, 
				"http://" + destinationIP + ":" + port + path, 
				ProxyMappingType.HTTP
			));
		
		// The admin path: force HTTPS and exclude from statistics.
		serviceRegistration.registerProxyPath (new ProxyPath (
				domain, 
				mkPath (path, "/admin"), 
				true, 
				true, 
				null, 
				null, 
				null
			));
		
		// The assets path: exclude from statistics.
		serviceRegistration.registerProxyPath (new ProxyPath (
				domain,
				mkPath (path, "/assets"),
				false,
				true,
				null,
				null,
				null
			));
		
		// Register secured paths:
		final Configuration securedPathsConfiguration = config.getConfig ("service.securedPaths");
		for (final String securedPath: securedPathsConfiguration.keys ()) {
			final String value = securedPathsConfiguration.getString (securedPath, null);
			if (value == null) {
				continue;
			}
			
			final List<String> ips = new ArrayList<> ();
			for (final String s: value.split (",")) {
				if (!s.trim ().isEmpty ()) {
					ips.add (s.trim ());
				}
			}
			
			if (ips.isEmpty ()) {
				continue;
			}
			
			serviceRegistration.registerProxyPath (new ProxyPath (
					domain, 
					securedPath.startsWith ("/") ? securedPath : "/" + securedPath, 
					false, 
					false, 
					null, 
					ips, 
					null
				));
		}
		
	}
	
	/**
	 * Returns a path constructed from a base path and a subpath. Makes sure that the basePath and
	 * subPath are always separated by a single slash, regardless of whether the inputs starts or
	 * ends with a slash.
	 * 
	 * @param basePath	The base path, optionally ending with a slash.
	 * @param subPath	The subpath to add, optionally starting with a slash.
	 * @return			The combined path where a single slash separates basePath and supPath.
	 */
	private static String mkPath (final String basePath, final String subPath) {
		final String base = basePath.endsWith ("/") ? basePath.substring (0, basePath.length () - 1) : basePath;
		final String sub = subPath.startsWith ("/") ? subPath : "/" + subPath;
		
		return base + sub;
	}
}
