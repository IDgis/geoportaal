package nl.idgis.portal.harvester.util;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class DateConverter {
	public static Timestamp dateStringToTimestamp(String date) {
		LocalDate localDate = null;
		if(date != null) {
			localDate = LocalDate.parse(date);
		}
		
		if(localDate != null) {
			ZonedDateTime zdt = ZonedDateTime.of(localDate.atStartOfDay(), ZoneId.of("Europe/Amsterdam"));
			return Timestamp.valueOf(zdt.toLocalDateTime());
		}
		
		return null;
	}
}
