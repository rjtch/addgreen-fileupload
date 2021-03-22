package de.addgreen.swaggerConfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import static com.google.common.collect.Lists.newArrayList;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("de.addgreen.storageController"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo())
                .useDefaultResponseMessages(false)
                .globalResponseMessage(RequestMethod.POST, newArrayList(new ResponseMessageBuilder().code(500)
                                .message("500 message")
                                .responseModel(new ModelRef("Error"))
                                .build(),
                        new ResponseMessageBuilder().code(403)
                                .message("Forbidden!!!!!")
                                .build()));
    }

    private ApiInfo apiInfo() {
        return new ApiInfo("Addgreen storage API",
                "Provides API to download prices and stations list in csv format.</p>" +
                        "<p>For download the application you have to fill the path the correct way begining with what you want to download</p>" +
                        " <p>/prices/* or /stations/* following the year the month and the file begining by the day and the type of the file.</p>" +
                        "<p>For e.g. /prices/2020/01/01-prices.csv or /stations/2020/01/01-stations.csv. Note that we only have data from 2020." +
                        " More years will follow.</p>",
                "0.0.1",
                "",
                "",
                "",
                "");
    }
}
