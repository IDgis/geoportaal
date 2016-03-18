package nl.idgis.geoportaal.conversie;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Attachment implements Closeable {

	private String fileName;
	private InputStream dataStream;
	private long length;
	private String mimeType;

	public static Attachment openConnection(String url) throws Exception {
		Attachment attachment = new Attachment();

		HttpURLConnection.setFollowRedirects(false);
		HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();

		final int responseCode = connection.getResponseCode();
		if (responseCode != 200)
			throw new Exception("request naar " + url + " stuurde code " + responseCode + " terug");
		
		Long contentLength = connection.getContentLengthLong();
		if(contentLength.intValue() > 214958080) {
			throw new Exception("bijlage van " + url + " is te groot");
		}

		attachment.setLength(contentLength);
		attachment.setMimeType(connection.getContentType());
		attachment.setFileName(url.substring(url.lastIndexOf('/') + 1, url.lastIndexOf('.')));
		attachment.setDataStream(connection.getInputStream());

		return attachment;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public InputStream getDataStream() {
		return dataStream;
	}

	public void setDataStream(InputStream dataStream) {
		this.dataStream = dataStream;
	}

	public long getLength() {
		return length;
	}

	public void setLength(long length) {
		this.length = length;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	@Override
	public void close() throws IOException {
		dataStream.close();
	}

}
