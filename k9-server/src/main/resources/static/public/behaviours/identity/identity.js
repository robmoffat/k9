
var currentUsername;
var currentEmailHash;
var gravatar;

export function identityMetadataCallback(metadata) {
	if (currentUsername != metadata.user) {
		currentUsername = metadata.user;
		currentEmailHash = metadata.email;
		if (currentEmailHash) {
			gravatar = 'https://gravatar.com/avatar/'+currentEmailHash;
		} else {
			gravatar = '/public/behaviours/identity/user.svg';
		}
	}
}



export function identityInstrumentationCallback(nav) {
	
	var avatar = nav.querySelector(".avatar");
	
	if (avatar == undefined) {
		var avatar = document.createElement("img");
		avatar.setAttribute("class", "avatar");
		avatar.setAttribute("title", currentUsername);
	    nav.appendChild(avatar);
	}
	
    avatar.setAttribute("src", gravatar);
}