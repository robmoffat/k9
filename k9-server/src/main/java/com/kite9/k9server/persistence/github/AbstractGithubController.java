package com.kite9.k9server.persistence.github;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.kohsuke.github.GHPerson;
import org.kohsuke.github.GitHub;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ResponseStatusException;

import com.kite9.k9server.command.controllers.AbstractCommandController;

public class AbstractGithubController extends AbstractCommandController {

	@Autowired
	protected GithubContentAPIFactory apiFactory;

	protected GitHub getGithubApi(Authentication authentication) throws Exception {
		return apiFactory.createGithub(authentication);
	}


	public static GHPerson getUserOrOrg(String type, String userorg, GitHub github) throws IOException {
		GHPerson p = null;
		switch (type) {
		case "users":
			p = github.getUser(userorg);
			break;
		case "orgs":
			p = github.getOrganization(userorg);
			break;
		default:
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "type " + type + " not expected");
		}
		return p;
	}


	public static String getDirectoryPath(String reponame, HttpServletRequest req) {
		String path = req.getRequestURI();
		return getDirectoryPath(reponame, path);
	}

	public static String getDirectoryPath(String reponame, String path) {
		int after = path.indexOf(reponame);
		after += reponame.length() + 1;
		if (after > path.length()) {
			return "";
		} else {
			return path.substring(after);
		}
	}

	public static String safeGetName(GHPerson o) {
		String n;
		try {
			n = o.getName();
		} catch (IOException e) {
			throw new UnsupportedOperationException("eh?");
		}
		return n;
	}

	

}