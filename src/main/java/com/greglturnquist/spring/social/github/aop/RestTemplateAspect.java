package com.greglturnquist.spring.social.github.aop;

import java.util.Optional;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriTemplate;

/**
 * @author gturnquist
 */
@Component
@Aspect
public class RestTemplateAspect {

	private static final Logger logger = LoggerFactory.getLogger(RestTemplateAspect.class);

	private CacheManager cacheManager;

	public CacheManager getCacheManager() {
		return cacheManager;
	}

	public void setCacheManager(CacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}

	@Pointcut("execution(* org.springframework.web.client.RestTemplate.getFor*(..))")
	public void pointcut() {}

	@Around("pointcut()")
	public Object logRestOprerations(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
		logger.error(proceedingJoinPoint.getSignature().toString());
		return proceedingJoinPoint.proceed();
	}

	@Around("pointcut()")
	public Object cacheGetOperaetions(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
		String url = (String) proceedingJoinPoint.getArgs()[0];
		Class<?> clazz = (Class<?>) proceedingJoinPoint.getArgs()[1];
		Object[] args = (Object[]) proceedingJoinPoint.getArgs()[2];

		String resolvedUrl = new UriTemplate(url).expand(args).toString();

		Cache cache = this.cacheManager.getCache("github");
		Optional<Object> response = Optional.ofNullable(cache.get(resolvedUrl, clazz));
		if (response.isPresent()) {
			return response.get();
		} else {
			Object results = proceedingJoinPoint.proceed();
			cache.put(resolvedUrl, results);
			return results;
		}
	}

}
