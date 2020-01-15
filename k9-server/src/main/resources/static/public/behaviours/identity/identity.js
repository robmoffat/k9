import { icon } from '/public/bundles/form.js';


var currentUsername;
var gravatar;
var navigator;
var userPage;

export function identityMetadataCallback(metadata) {
	if (currentUsername != metadata.user) {
		currentUsername = metadata.user;
		gravatar = metadata['user-icon'];
		userPage = metadata['user-page'];
		gravatar = gravatar == undefined ?  '/public/behaviours/identity/user.svg' : gravatar;
		updateAvatar();
	}
}

function updateAvatar() {
	if (navigator) {
		var avatar = navigator.querySelector("#--avatar");
		var newAvatar = icon('--avatar', currentUsername, gravatar, function () {
			if (userPage) {
				window.location.href = userPage;
			}
		});
		if (avatar == undefined) {
			navigator.appendChild(newAvatar);
		} else {
			navigator.replaceChild(newAvatar, avatar)
		}
	}
}

export function identityInstrumentationCallback(nav) {
	navigator = nav;
	updateAvatar();
}