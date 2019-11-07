import { icon } from '/public/bundles/form.js';


var currentUsername;
var gravatar;

export function identityMetadataCallback(metadata) {
	if (currentUsername != metadata.user) {
		currentUsername = metadata.user;
		gravatar = metadata['user-icon'];
		gravatar = gravatar == undefined ?  '/public/behaviours/identity/user.svg' : gravatar;
	}
}



export function identityInstrumentationCallback(nav) {
	
	var avatar = nav.querySelector("#--avatar");
	
	if (avatar == undefined) {
	    nav.appendChild(icon('--avatar', currentUsername, gravatar, undefined));
	}
}