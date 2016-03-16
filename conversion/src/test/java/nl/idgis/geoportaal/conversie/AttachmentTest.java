package nl.idgis.geoportaal.conversie;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class AttachmentTest {

	@Test
	public void test() throws Exception {
		Attachment attachment = Attachment.openConnection("http://gisopenbaar.overijssel.nl/Geoportal/MIS4GIS/kaartarchief/2015/150062.pdf");

		final String fileName = attachment.getFileName();
		final long length = attachment.getLength();
		final String type = attachment.getMimeType();

		System.out.println("name=" + fileName);
		System.out.println("length=" + length);
		System.out.println("mimetype=" + type);

		assertEquals(fileName, "150062");
		assertEquals(length, 672919);
		assertEquals(type, "pdf");

		attachment.close();
	}

}
