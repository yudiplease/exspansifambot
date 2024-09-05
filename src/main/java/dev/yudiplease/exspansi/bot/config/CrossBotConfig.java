package dev.yudiplease.exspansi.bot.config;

import dev.yudiplease.exspansi.bot.SendMessageToChannel;
import dev.yudiplease.exspansi.bot.listeners.EventListener;
import dev.yudiplease.exspansi.bot.service.DiscordService;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.Event;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.PrivateChannel;
import discord4j.core.object.presence.ClientActivity;
import discord4j.core.object.presence.ClientPresence;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.rest.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

import java.util.List;

@Configuration
public class CrossBotConfig {
    private static final Logger logger = LoggerFactory.getLogger(CrossBotConfig.class);

    @Value("${discord.token}")
    private String token;

    @Bean
    public DiscordService discordService() {
        return new DiscordService(token);
    }

    @Bean
    public <T extends Event> GatewayDiscordClient gatewayDiscordClient(final List<EventListener<T>> eventListeners) {
        final GatewayDiscordClient client = DiscordClientBuilder.create(token)
                .build()
                .gateway()
                .setInitialPresence(ignore -> ClientPresence.online(ClientActivity.listening("")))
                .login()
                .block();

        long appId = client.getRestClient().getApplicationId().block();

        for (final EventListener<T> listener : eventListeners) {
            client.on(listener.getEventType())
                    .flatMap(listener::execute)
                    .onErrorResume(listener::handleError)
                    .subscribe();
        }

        ApplicationCommandRequest greetCmdRequest = ApplicationCommandRequest.builder()
                .name("greet")
                .description("greeting you")
                .addOption(ApplicationCommandOptionData.builder()
                        .name("name")
                        .description("Your name")
                        .type(ApplicationCommandOption.Type.STRING.getValue())
                        .required(true)
                        .build()
                ).build();

        client.getRestClient().getApplicationService().createGlobalApplicationCommand(appId, greetCmdRequest).subscribe();

        return client;
    }

    @Bean
    public RestClient discordRestClient(GatewayDiscordClient client) {
        return client.getRestClient();
    }

    @Bean
    public SendMessageToChannel sendMessageToChannel(DiscordService discordService) {
        return new SendMessageToChannel(discordService);
    }

    @Bean
    public Mono<Void> botEvents(GatewayDiscordClient gateway) {
        return gateway.on(ReadyEvent.class)
                .flatMap(event -> {
                    logger.info(String.format("Logged in as %s", event.getSelf().getUsername()));
                    return Mono.empty();
                })
                .then(gateway.on(MessageCreateEvent.class)
                        .filter(event -> event.getMessage().getAuthor().map(user -> !user.isBot()).orElse(false))
                        .filter(event -> event.getMessage().getChannel().block() instanceof PrivateChannel)
                        .flatMap(this::handlePrivateMessage)
                        .onErrorResume(error -> {
                            System.err.println("Error handling message: " + error.getMessage());
                            return Mono.empty();
                        })
                        .then());
    }

    private Mono<Void> handlePrivateMessage(MessageCreateEvent event) {
        User user = (User) event.getMessage().getChannel().block();
        String message = event.getMessage().getContent().describeConstable().get();

        return user.getPrivateChannel()
                .flatMap(channel -> channel.createMessage("Hello, " + user.getUsername() + "! You said: " + message))
                .then();
    }
}
