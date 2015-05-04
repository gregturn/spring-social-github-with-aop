package com.greglturnquist.spring.social.github.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author gturnquist
 */
@Component
@Aspect
public class RestTemplateAspect {

	private static final Logger logger = LoggerFactory.getLogger(RestTemplateAspect.class);

	@Pointcut("execution(* org.springframework.social.github.api.impl.GitHubTemplate.*(..))")
	public void pointcut() {}

	@Around("pointcut()")
	public Object logRestOprerations(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
		logger.error(proceedingJoinPoint.getSignature().toString());
		return proceedingJoinPoint.proceed();
	}

}
