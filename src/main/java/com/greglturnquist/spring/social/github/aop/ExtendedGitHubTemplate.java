package com.greglturnquist.spring.social.github.aop;

import org.springframework.social.github.api.impl.GitHubTemplate;
import org.springframework.web.client.RestTemplate;

/**
 * By extending {@link GitHubTemplate}, you can add new operations and also tweak the underlying {@link RestTemplate}.
 *
 * @author Greg Turnquist
 */
public class ExtendedGitHubTemplate extends GitHubTemplate {

	public ExtendedGitHubTemplate(String githubToken) {
		super(githubToken);
	}

	// TODO Implement extra GitHub operations not found in the project.

	@Override
	protected void configureRestTemplate(RestTemplate restTemplate) {
		// Wrap the RestTemplate with caching somehow!
	}

}
