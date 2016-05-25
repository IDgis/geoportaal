package controllers;

import static models.QMdAttachment.mdAttachment;
import static models.QMetadata.metadata;

import java.io.ByteArrayInputStream;

import javax.inject.Inject;

import com.querydsl.core.Tuple;

import play.mvc.Controller;
import play.mvc.Result;
import util.QueryDSL;

public class Attachment extends Controller {
	@Inject QueryDSL q;
	
	/**
	 * Open an attachment
	 * 
	 * @param attachmentName the name of the attachment
	 * @param uuid the UUID of the record the attachment belongs to
	 * @return the {@link Result} of a new page where the attachment will be opened
	 */
	public Result openAttachment(String attachmentName, String uuid) {
		return q.withTransaction(tx -> {
			// Fetches the attachment content and mimetype
			Tuple attachment = tx.select(mdAttachment.attachmentContent, mdAttachment.attachmentMimetype)
				.from(mdAttachment)
				.join(metadata).on(mdAttachment.metadataId.eq(metadata.id))
				.where(metadata.uuid.eq(uuid))
				.where(mdAttachment.attachmentName.eq(attachmentName))
				.fetchOne();
			
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
