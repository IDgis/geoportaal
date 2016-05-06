import com.google.inject.AbstractModule;

import controllers.ZooKeeper;

public class PortalModule extends AbstractModule {
	
	@Override
	protected void configure() {
		bind(ZooKeeper.class).asEagerSingleton();
	}
}