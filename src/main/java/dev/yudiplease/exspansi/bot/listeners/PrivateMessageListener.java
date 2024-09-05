package dev.yudiplease.exspansi.bot.listeners;

import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.channel.PrivateChannel;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Component
public class PrivateMessageListener {
//
    private static final Logger logger = LoggerFactory.getLogger(PrivateMessageListener.class);

    public PrivateMessageListener(GatewayDiscordClient client) {
        client.on(MessageCreateEvent.class)
                .filter(event -> event.getMessage().getAuthor().map(user -> !user.isBot()).orElse(false))
                .publishOn(Schedulers.boundedElastic())
                .filter(event -> event.getMessage().getChannel().block() instanceof PrivateChannel)
                .flatMap(this::handlePrivateMessage)
                .onErrorResume(error -> {
                    logger.error("Error handling message: " + error.getMessage());
                    return Mono.empty();
                }).subscribe();
    }

    public Mono<Void> handlePrivateMessage(MessageCreateEvent event) {
        EmbedCreateSpec spec = EmbedCreateSpec.builder()
                .color(Color.BLACK)
                .title("EXSPANSI FAMILY")
                .description("Majestic Roleplay Detroit")
                .addField("Привет! Для вступления в семью Exspansi заполни форму ниже: ", "https://docs.google.com/forms/d/e/1FAIpQLSdDEZhQTA4rHLOYHbJyLLTQ2jnNRbPh8QUXyreoIURo4cY35A/viewform", false)
                .image("https://i.imgur.com/Nz25M1t.jpeg")
                .footer("Создано для семьи Exspansi Majestic Roleplay Detroit (#2)", "https://i.imgur.com/Nz25M1t.jpeg")
                .build();
        String message = event.getMessage().getContent();
        return event.getMessage().getChannel()
                .ofType(PrivateChannel.class)
                .flatMap(privateChannel -> {
                    return privateChannel.createMessage(spec);
                }).then();
    }
}
