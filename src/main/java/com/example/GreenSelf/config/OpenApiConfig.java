package com.example.GreenSelf.config;



import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "GreenSelf Plant Marketplace API",
                version = "1.0",
                description = "REST API documentation for GreenSelf - Your Plant Marketplace. " +
                        "This API provides endpoints for user management, seller operations, " +
                        "product catalog, payment processing, and admin functionalities.",
                contact = @Contact(
                        name = "GreenSelf Support",
                        email = "support@greenself.com",
                        url = "https://greenself.com/contact"
                ),
                license = @License(
                        name = "Apache 2.0",
                        url = "https://www.apache.org/licenses/LICENSE-2.0.html"
                )
        ),
        servers = {
                @Server(
                        description = "Local Development Server",
                        url = "http://localhost:8080"
                ),
                @Server(
                        description = "Staging Server",
                        url = "https://staging.greenself.com"
                ),
                @Server(
                        description = "Production Server",
                        url = "https://api.greenself.com"
                )
        }
)
@SecurityScheme(
        name = "bearerAuth",
        description = "JWT Bearer token authentication",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
    // This class uses annotations for configuration
    // No additional bean definitions needed
}