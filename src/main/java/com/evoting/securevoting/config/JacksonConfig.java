package com.evoting.securevoting.config;
 
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.hibernate6.Hibernate6Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
 
@Configuration
public class JacksonConfig {
 
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
 
        // ✅ Fix 1: Hibernate lazy proxy serialization (ByteBuddyInterceptor error)
        Hibernate6Module hibernateModule = new Hibernate6Module();
        hibernateModule.disable(Hibernate6Module.Feature.USE_TRANSIENT_ANNOTATION);
        mapper.registerModule(hibernateModule);
 
        // ✅ Fix 2: Java 8 date/time types (LocalDateTime error)
        mapper.registerModule(new JavaTimeModule());
 
        // ✅ Fix 3: Don't serialize dates as timestamps — use ISO string format
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
 
        return mapper;
    }
}