package nl.idgis.querydsl;

public class factory {

	public static <T extends TsVector> TsVectorPath<T> createTsVector(String var, Class<T> type) {
		return new TsVectorPath<T>(type, var);
	}
}
