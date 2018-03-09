package ua.yaroslav.auth2.integration;

import org.springframework.context.annotation.*;

@Configuration
@ComponentScan(value = {"ua.yaroslav.auth2.auth", "ua.yaroslav.auth2.resource"})
@Import(value = {ua.yaroslav.auth2.AppConfiguration.class, /*ua.yaroslav.auth2.AppRunner.class*/})
public class FakeDatabaseConfiguration {}