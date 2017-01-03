package nl.idgis.geoportaal.attributecheck;

public class MetadataDescription {
	final private String fileIdentifier;
	final private String metadataIdentifier;
	final private String table;
	
	public MetadataDescription(String fileIdentifier, String metadataIdentifier, String table) {
		this.fileIdentifier = fileIdentifier;
		this.metadataIdentifier = metadataIdentifier;
		this.table = table;
	}

	public String getFileIdentifier() {
		return fileIdentifier;
	}

	public String getMetadataIdentifier() {
		return metadataIdentifier;
	}

	public String getTable() {
		return table;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fileIdentifier == null) ? 0 : fileIdentifier.hashCode());
		result = prime * result + ((metadataIdentifier == null) ? 0 : metadataIdentifier.hashCode());
		result = prime * result + ((table == null) ? 0 : table.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MetadataDescription other = (MetadataDescription) obj;
		if (fileIdentifier == null) {
			if (other.fileIdentifier != null)
				return false;
		} else if (!fileIdentifier.equals(other.fileIdentifier))
			return false;
		if (metadataIdentifier == null) {
			if (other.metadataIdentifier != null)
				return false;
		} else if (!metadataIdentifier.equals(other.metadataIdentifier))
			return false;
		if (table == null) {
			if (other.table != null)
				return false;
		} else if (!table.equals(other.table))
			return false;
		return true;
	}
}
