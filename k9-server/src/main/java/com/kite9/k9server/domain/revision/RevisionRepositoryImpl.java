package com.kite9.k9server.domain.revision;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import com.kite9.k9server.domain.document.Document;
import com.kite9.k9server.domain.document.DocumentRepository;

public class RevisionRepositoryImpl implements RevisionRepositoryCustom {

	@Autowired
	RevisionRepository revisionRepository;
	
	@Autowired
	DocumentRepository documentRepository;
		
	
	/** 
	 * Any completely new revision gets set to the document's currentRevision, and 
	 * the previous/next revisions are set.
	 */
	public Revision save(Revision r) {
		Document d = r.getDocument();
		
		if (!d.checkWrite()) {
			throw new HttpClientErrorException(HttpStatus.FORBIDDEN);
		}
		
		Revision currentRevision = d.getCurrentRevision();
		
		if (r.getId() == null) {
			if ((currentRevision != null) ) {
				currentRevision.nextRevision = r;
				r.previousRevision = currentRevision;
			}

			d.setCurrentRevision(r);
		}
		revisionRepository.saveInternal(r);
		
		if (currentRevision != null) {
			revisionRepository.saveInternal(currentRevision);
		}
		
		documentRepository.save(d);
		return r;
	}
	

	@Override
	public Revision saveInternal(Revision entity) {
		return revisionRepository.saveAll(Collections.singleton(entity)).iterator().next();
	}

}
