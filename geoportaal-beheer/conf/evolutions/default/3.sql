# --- !Ups

<<<<<<< Upstream, based on origin/master
ALTER TABLE gb.metadata ALTER COLUMN title TYPE VARCHAR(150);

# --- !Downs

ALTER TABLE gb.metadata ALTER COLUMN title TYPE VARCHAR(50);
=======
UPDATE gb.subject_label SET label = 'Nutsvoorzieningen en communicatie'
	where id=15;
ALTER TABLE gb.metadata ADD COLUMN tsv tsvector;
CREATE INDEX tsv_idx ON gb.metadata USING gin(tsv);
UPDATE gb.metadata SET tsv = tsv_query.document
	from (
		select m.id as mid, to_tsvector('dutch', coalesce((string_agg(m.title, ' ')), '')) ||
			to_tsvector('dutch', coalesce((string_agg(m.description, ' ')), '')) ||
			to_tsvector('dutch', coalesce((string_agg(m.location, ' ')), '')) ||
			to_tsvector('dutch', coalesce((string_agg(m.file_id, ' ')), '')) ||
			to_tsvector('dutch', coalesce((string_agg(m.uuid, ' ')), '')) ||
			to_tsvector('dutch', coalesce((string_agg(ma.attachment_name, ' ')), '')) ||
			to_tsvector('dutch', coalesce((string_agg(til.label, ' ')), '')) ||
			to_tsvector('dutch', coalesce((string_agg(cl.label, ' ')), '')) ||
			to_tsvector('dutch', coalesce((string_agg(m.creator_other, ' ')), '')) ||
			to_tsvector('dutch', coalesce((string_agg(rl.label, ' ')), '')) ||
			to_tsvector('dutch', coalesce((string_agg(ull.label, ' ')), '')) ||
			to_tsvector('dutch', coalesce((string_agg(m.source, ' ')), '')) ||
			to_tsvector('dutch', coalesce((string_agg(sl.label, ' ')), '')) as document

		from gb.metadata m
		left join gb.md_attachment ma on m.id=ma.metadata_id
		join gb.type_information ti on m.type_information=ti.id
		join gb.type_information_label til on ti.id=til.id
		join gb.creator c on m.creator=c.id
		join gb.creator_label cl on c.id=cl.id
		join gb.rights r on m.rights=r.id
		join gb.rights_label rl on r.id=rl.id
		join gb.use_limitation ul on m.use_limitation=ul.id
		join gb.use_limitation_label ull on ul.id=ull.id
		left join gb.md_subject ms on m.id=ms.metadata_id
		join gb.subject s on ms.subject=s.id
		join gb.subject_label sl on s.id=sl.id
		group by m.id
		order by m.id
	) tsv_query
where tsv_query.mid=gb.metadata.id;

# --- !Downs

ALTER TABLE gb.metadata DROP COLUMN tsv;
DROP INDEX tsv_idx;
>>>>>>> 86c66a9 Created sql file for tsvector column
