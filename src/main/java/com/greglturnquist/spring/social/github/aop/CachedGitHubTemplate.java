package com.greglturnquist.spring.social.github.aop;

import org.springframework.social.github.api.impl.GitHubTemplate;
import org.springframework.web.client.RestTemplate;

/**
 * @author gturnquist
 */
public class CachedGitHubTemplate extends GitHubTemplate {

	// TODO Implement extra GitHub operations not found in the project.

	@Override
	protected void configureRestTemplate(RestTemplate restTemplate) {
		// Wrap the RestTemplate with caching somehow!
	}

}
