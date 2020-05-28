package controllers;

import static models.QMdAttachment.mdAttachment;
import static models.QMetadata.metadata;
import static models.QUseLimitation.useLimitation;

import java.io.ByteArrayInputStream;

import javax.inject.Inject;

import com.querydsl.core.Tuple;

import play.Configuration;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import util.QueryDSL;

public class Attachment extends Controller {
	@Inject QueryDSL q;
	private final Configuration configuration = play.Play.application().configuration();
	
	/**
	 * Download an attachment
	 * 
	 * @param uuid the uuid of the document the attachment belongs to
	 * @param attachmentName the name of the attachment
	 * @return the {@link Result} of a new page where the attachment will be downloaded
	 */
	public Result download(String uuid, String attachmentName) {
		return q.withTransaction(tx -> {
			// Fetches the attachment content and mimetype
			Tuple attachment = tx.select(mdAttachment.attachmentContent, mdAttachment.attachmentMimetype, useLimitation.name)
				.from(mdAttachment)
				.join(metadata).on(mdAttachment.metadataId.eq(metadata.id))
				.join(useLimitation).on(useLimitation.id.eq(metadata.useLimitation))
				.where(metadata.uuid.eq(uuid))
				.where(mdAttachment.attachmentName.eq(attachmentName))
				.fetchOne();
			
			// Fetch trusted header and check if request is allowed
			String headerTrusted = Http.Context.current().request().getHeader(configuration.getString("trusted.header"));
			if(!"1".equals(headerTrusted) && !"extern".equals(attachment.get(useLimitation.name))) return forbidden("403: forbidden");
			
			// Sets the mimetype of the response
			response().setContentType(attachment.get(mdAttachment.attachmentMimetype));
			
			// Create a byte array and a byte array inputstream
			byte[] content = attachment.get(mdAttachment.attachmentContent);
			ByteArrayInputStream bais = new ByteArrayInputStream(content);
			
			// Return a new page where the attachment will be opened
			return ok(bais);
		});
	}
}
