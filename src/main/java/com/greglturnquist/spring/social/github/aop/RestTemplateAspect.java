package com.greglturnquist.spring.social.github.aop;

import java.lang.reflect.Method;
import java.util.Optional;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

/**
 * @author gturnquist
 */
@Component
@Aspect
public class RestTemplateAspect {

	private static final Logger logger = LoggerFactory.getLogger(RestTemplateAspect.class);

	private final CacheManager cacheManager;

	@Autowired
	public RestTemplateAspect(CacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}

	@Pointcut("execution(* org.springframework.web.client.RestTemplate.getFor*(..))")
	public void pointcut() {}

	@Around("pointcut()")
	public Object cacheGetOperaetions(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
		System.out.println("Hello from AOP!");
		String url = (String) proceedingJoinPoint.getArgs()[0];
		Class<?> clazz = (Class<?>) proceedingJoinPoint.getArgs()[1];
		Object[] args = (Object[]) proceedingJoinPoint.getArgs()[2];

		String resolvedUrl = new UriTemplate(url).expand(args).toString();

		Cache cache = this.cacheManager.getCache("github");
		Optional<Object> response = Optional.ofNullable(cache.get(resolvedUrl, clazz));
		if (response.isPresent()) {
			logger.error("Found my answer in the cache..");
			return response.get();
		} else {
			logger.error("Going to make the real call...");
			// Swap getForObject with getForEntity...
			String oldName = proceedingJoinPoint.getSignature().getName();
			if (oldName.endsWith("Object")) {
				String newName = oldName.substring(0, oldName.indexOf("Object")) + "Entity";
				logger.error("New method name is " + newName);
				Method newMethod = RestTemplate.class.getDeclaredMethod(newName, String.class, Class.class, Object[].class);
				Object results = newMethod.invoke(proceedingJoinPoint.getThis(), url, clazz, args);
				cache.put(resolvedUrl, results);
				return results;
			} else {
				Object results = proceedingJoinPoint.proceed();
				return results;
			}
		}
	}

}
