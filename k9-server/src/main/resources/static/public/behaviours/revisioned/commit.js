//import { getMainSvg } from '/public/bundles/screen.js';


export function initCommitDocumentCallback(urlCallback) {
	
	
	
	
	return function(documentVersion, textVersion) {
		// persist the update
		const url = urlCallback();
		
		const base64adl = documentVersion.getElementById("adl:markup").textContent
		const base64svg = btoa(textVersion);
		const adlPath = url;
		const svgPath = url.replace('.kite9.xml', '.kite9.svg');
		const filesToContentBase64 = {};
		filesToContentBase64[adlPath] = base64adl;
		filesToContentBase64[svgPath] = base64svg;

		
		const payload = {
			'commitMessage' : 'Change inside Kite9 editor',
			'filesToContentBase64' : filesToContentBase64
		}
		
		fetch(urlCallback(), {
			method: 'POST',
			body: JSON.stringify(payload),
			headers: {
				"Content-Type": "application/json",
				"Accept": "application/hal+json"
			}
		});
		
	}
	
	
	
}