package nl.idgis.geoportaal.conversie;

import java.io.File;

public interface OutDestination {

	public void convertFile(File file, MetadataDocument d) throws Exception;

	public void close() throws Exception;
}
