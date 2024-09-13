package dev.yudiplease.exspansi.bot;

import discord4j.common.JacksonResources;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.discordjson.json.*;
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
        for (Resource resource : matcher.getResources("commands/*.json")) {
            ApplicationCommandRequest request = d4jMapper.getObjectMapper()
                    .readValue(resource.getInputStream(), ApplicationCommandRequest.class);
            List<ApplicationCommandOptionData> options = new ArrayList<>();
            for (ApplicationCommandOptionData option : request.options().get()) {
                List<ApplicationCommandOptionChoiceData> choices = new ArrayList<>();
                if (option.choices().toOptional().isPresent()) {
                    for (ApplicationCommandOptionChoiceData choice : option.choices().get()) {
                        choices.add(ApplicationCommandOptionChoiceData.builder()
                                .name(choice.name())
                                .value(choice.value())
                                .build());
                    }
                }
                options.add(ApplicationCommandOptionData.builder()
                        .name(option.name())
                        .type(option.type())
                        .description(option.description())
                        .required(option.required())
                        .choices(choices)
                        .build());
            }
            request = ApplicationCommandRequest.builder()
                    .name(request.name())
                    .description(request.description())
                    .options(options)
                    .build();
            commands.add(request);
            LOGGER.info(commands.stream().toString());
        }
        applicationService.bulkOverwriteGlobalApplicationCommand(appId, commands)
                .doOnNext(ignore -> LOGGER.debug("Successfully registered Global commands"))
                .doOnError(e -> LOGGER.error("Failed to register global commands", e))
                .subscribe();
    }
}
