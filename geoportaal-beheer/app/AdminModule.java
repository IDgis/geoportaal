import com.google.inject.AbstractModule;

import controllers.ZooKeeper;

public class AdminModule extends AbstractModule {
	
	@Override
	protected void configure() {
		bind(ZooKeeper.class).asEagerSingleton();
	}
}