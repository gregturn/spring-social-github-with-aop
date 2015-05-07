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

	private final RestTemplateAspect aspect;

	public ExtendedGitHubTemplate(String githubToken, RestTemplateAspect aspect) {
		super(githubToken);
		this.aspect = aspect;
	}

	@Override
	protected RestTemplate postProcess(RestTemplate restTemplate) {
		AspectJProxyFactory factory = new AspectJProxyFactory(super.getRestTemplate());
		factory.addAspect(this.aspect);
		factory.setProxyTargetClass(true);
		return factory.getProxy();
	}

}
