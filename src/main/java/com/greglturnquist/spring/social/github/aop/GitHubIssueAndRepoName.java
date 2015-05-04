package com.greglturnquist.spring.social.github.aop;

import org.springframework.social.github.api.GitHubIssue;

/**
 * @author gturnquist
 */
public class GitHubIssueAndRepoName {

	private final GitHubIssue issue;
	private final String repo;

	public GitHubIssueAndRepoName(GitHubIssue issue, String repo) {
		this.issue = issue;
		this.repo = repo;
	}

	public GitHubIssue getIssue() {
		return issue;
	}

	public String getRepo() {
		return repo;
	}
}
