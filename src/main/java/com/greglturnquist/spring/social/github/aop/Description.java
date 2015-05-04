package com.greglturnquist.spring.social.github.aop;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author gturnquist
 */
@Component
@ConfigurationProperties(prefix = "description")
public class Description {

	private final List<Section> sections = new ArrayList<>();

	@Override
	public String toString() {
		final StringBuffer results = new StringBuffer();
		this.sections.forEach(section -> results.append(section.toString()));
		return results.toString();
	}

	public List<Section> getSections() {
		return sections;
	}

}
