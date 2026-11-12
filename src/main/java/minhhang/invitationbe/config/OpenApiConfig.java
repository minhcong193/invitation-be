package minhhang.invitationbe.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI invitationOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Wedding Invitation API")
                        .description("REST API backend cho hệ thống thiệp cưới trực tuyến, quản lý khách mời, họ hàng và sổ lưu bút.")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Wedding Invitation Support")
                                .email("contact@invitation.vn"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://springdoc.org")));
    }
}

