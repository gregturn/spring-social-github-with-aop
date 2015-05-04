package com.greglturnquist.spring.social.github.aop;

import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.social.github.api.impl.GitHubTemplate;
import org.springframework.web.client.RestTemplate;

/**
 * By extending {@link GitHubTemplate}, you can add new operations and also tweak the underlying {@link RestTemplate}.
 *
 * @author Greg Turnquist
 */
public class ExtendedGitHubTemplate extends GitHubTemplate {

	private static RestTemplateAspect aspect;

	public ExtendedGitHubTemplate(String githubToken, RestTemplateAspect aspect) {
		super(makeExtendedGitHubTemplate(githubToken, aspect));
	}

	private static String makeExtendedGitHubTemplate(String githubToken, RestTemplateAspect aspect) {
		ExtendedGitHubTemplate.aspect = aspect;
		return githubToken;
	}

	/**
	 * Override Spring Social Core's passthrough, and replace RestTemplate with an advicsed instance.
	 *
	 * @param restTemplate
	 * @return {@link RestTemplate} with a proxied version
	 */
	@Override
	protected RestTemplate postProcess(RestTemplate restTemplate) {
		AspectJProxyFactory factory = new AspectJProxyFactory(restTemplate);
		factory.addAspect(ExtendedGitHubTemplate.aspect);
		factory.setProxyTargetClass(true);
		return factory.getProxy();
	}

}
