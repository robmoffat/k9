

function getChangeUri() {
	const href = document.URL;
	return href.replace("/content", "/change")
}


export { getChangeUri }