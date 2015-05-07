package com.greglturnquist.spring.social.github.aop;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.gemstone.gemfire.cache.Cache;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.data.gemfire.CacheFactoryBean;
import org.springframework.data.gemfire.LocalRegionFactoryBean;
import org.springframework.data.gemfire.support.GemfireCacheManager;
import org.springframework.social.github.api.impl.GitHubTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.remove;

@Controller
public class HomeController {

	private final static Logger logger = LoggerFactory.getLogger(HomeController.class);

	@Value("${github.access.token}")
	String githubToken;

	@Value("${org:spring-guides}")
	String org;

	@Value("${site:http://spring.io}")
	String site;

	@Value("${css.selector:a.guide--title}")
	String cssSelector;

	@Value("${drone.site:http://drone-aggregator.guides.spring.io/")
	String droneSite;

	@Autowired
	Description description;

	@Bean
	GitHubTemplate createAGithubTemplate(RestTemplateAspect aspect) {
		return new ExtendedGitHubTemplate(githubToken, aspect);
	}

	@Autowired
	GitHubTemplate gitHubTemplate;

	@Bean
	CacheFactoryBean cacheFactoryBean() {
		return new CacheFactoryBean();
	}

	@Bean
	LocalRegionFactoryBean<String, String> localRegionFactoryBean(Cache cache) {
		LocalRegionFactoryBean<String, String> factoryBean = new LocalRegionFactoryBean<>();
		factoryBean.setCache(cache);
		factoryBean.setName("github");
		return factoryBean;
	}

	@Bean
	GemfireCacheManager gemfireCacheManager(Cache cache) {
		GemfireCacheManager manager = new GemfireCacheManager();
		manager.setCache(cache);
		return manager;
	}

	List<GitHubIssueAndRepoName> issues(String org, List<String> repos) {
		return repos.stream()
				.map(repoName ->
						this.gitHubTemplate.repoOperations().getIssues(org, repoName).stream()
								.filter(gitHubIssue -> gitHubIssue.getState().equals("open"))
								.sorted((o1, o2) -> Integer.compare(o1.getNumber(), o2.getNumber()))
								.map(gitHubIssue -> new GitHubIssueAndRepoName(gitHubIssue, repoName))
								.collect(toList()))
				.filter(gitHubIssueAndRepoNames -> gitHubIssueAndRepoNames.size() > 0)
				.flatMap(Collection::stream)
				.collect(toList());
	}


	@RequestMapping(value = "/")
	public String index(Model model) {
		List<String> repos = new ArrayList<>();

		description.getSections().forEach(section -> {
					Document doc = null;
					try {
						doc = Jsoup.connect(site + section.getSection()).get();
					} catch (IOException e) {
						throw new RuntimeException(e);
					}

					repos.addAll(doc.select(cssSelector).stream()
							.filter(it -> !it.attr("href").contains("tutorials"))
							.map(it -> section.getPrefix() + (remove(remove(it.attr("href"), section.getToStrip()), "/")))
							.collect(toList()));
				}
		);

		model.addAttribute("issues", this.issues(org, repos));
		model.addAttribute("site", site);
		model.addAttribute("org", org);
		model.addAttribute("droneSite", droneSite);

		return "index";
	}

}

