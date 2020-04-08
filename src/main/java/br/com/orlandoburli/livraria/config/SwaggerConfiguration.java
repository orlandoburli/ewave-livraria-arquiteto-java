package br.com.orlandoburli.livraria.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfiguration {

	@Autowired
	BuildProperties buildProperties;

	@Bean
	public Docket buildApiDocumentation() {
		// @formatter:off
		return new Docket(DocumentationType.SWAGGER_2)
				.select()
				.apis(RequestHandlerSelectors.basePackage("br.com.orlandoburli"))
				.paths(PathSelectors.any())
				.build()
				.apiInfo(buildMetaDataDocumentation());
		// @formatter:on
	}

	private ApiInfo buildMetaDataDocumentation() {
		// @formatter:off
		return new ApiInfoBuilder()
					.title("Livraria")
					.description("API de gestão de livraria")
					.version(buildProperties.getVersion())
					.contact(new Contact("Orlando Burli Júnior", "https://github.com/orlandoburli/ewave-livraria-arquiteto-java", "orlando.burli@gmail.com"))
				.license("Apache License Version 2.0")
				.build();
		// @formatter:on
	}
}
