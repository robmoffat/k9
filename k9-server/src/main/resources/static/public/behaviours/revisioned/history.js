//import { getMainSvg } from '/public/bundles/screen.js';

export function initHistoryMetadataCallback(revisions, callback) {
	
	if (callback==undefined) {
		callback = function(m) {
			return m['revisions'];
		}
	}
	
	// when the url changes, we load up revision information
	return function(metadata) {
		const url = callback(metadata);
		
		fetch(url, {
			headers: {
				"Accept": "application/hal+json"
			}
		})
		.then(response => {
			if (!response.ok) {
				return response.json().then(j => {
					//console.log(JSON.stringify(j));
					throw new Error(j.message);
				});
			}
			return response;
		})
		.then(response => response.json())
		.then(json => revisions.update(json._embedded.revisionList));
		
	}
	
}


export function initHistoryDocumentCallback(revisions, metadata, callback) {
	
	if (callback==undefined) {
		callback = function() {
			return metadata.get('revisions');
		}
	}

	return function(documentVersion, textVersion) {
		// persist the update
		const url = callback();
		
		const base64adl = documentVersion.getElementById("adl:markup").textContent
		const base64svg = btoa(textVersion);
		const payload = {
			'commitMessage' : 'Change inside Kite9 editor',
			'adlBase64' : base64adl,
			'svgBase64' : base64svg
		}
		
		fetch(url, {
			method: 'POST',
			body: JSON.stringify(payload),
			headers: {
				"Content-Type": "application/json",
				"Accept": "application/hal+json"
			}
		})
		.then(response => {
			if (!response.ok) {
				return response.json().then(j => {
					//console.log(JSON.stringify(j));
					throw new Error(j.message);
				});
			}
			return response;
		})
		.then(response => response.json())
		.then(json => revisions.update(json._embedded.revisionList));
		
	}
}