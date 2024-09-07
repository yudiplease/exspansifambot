package dev.yudiplease.exspansi.bot;

import discord4j.common.JacksonResources;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.rest.RestClient;
import discord4j.rest.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class GlobalCommandRegister implements ApplicationRunner {
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    private final RestClient client;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        final JacksonResources d4jMapper = JacksonResources.create();

        PathMatchingResourcePatternResolver matcher = new PathMatchingResourcePatternResolver();
        final ApplicationService applicationService = client.getApplicationService();
        final long appId = client.getApplicationId().block();

        List<ApplicationCommandRequest> commands = new ArrayList<>();
        for (Resource resource: matcher.getResources("commands/*.json")) {
            ApplicationCommandRequest request = d4jMapper.getObjectMapper()
                    .readValue(resource.getInputStream(), ApplicationCommandRequest.class);
            commands.add(request);
            LOGGER.info(commands.stream().toString());
        }
        applicationService.bulkOverwriteGlobalApplicationCommand(appId, commands)
                .doOnNext(ignore -> LOGGER.debug("Successfully registered Global commands"))
                .doOnError(e -> LOGGER.error("Failed to register global commands", e))
                .subscribe();
    }
}
