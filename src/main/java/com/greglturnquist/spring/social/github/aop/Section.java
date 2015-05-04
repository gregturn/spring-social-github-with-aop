package com.greglturnquist.spring.social.github.aop;

import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * @author gturnquist
 */
@Data
@RequiredArgsConstructor
public class Section {

	private String prefix;
	private String toStrip;
	private String section;

}
