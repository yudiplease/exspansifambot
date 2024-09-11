package dev.yudiplease.exspansi.bot.event;

import dev.yudiplease.exspansi.bot.command.SlashCommand;
import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageCreateSpec;
import discord4j.rest.util.Color;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class Capture implements SlashCommand {
    private final GatewayDiscordClient client;
    private final ZoneId moscowZone = ZoneId.of("Europe/Moscow");
    private final ZonedDateTime moscowTime = ZonedDateTime.now(moscowZone);
    private final Snowflake channelId = Snowflake.of(1221140427197911130L);
    private Snowflake lastMessageId;

    private String time;
    private String versus;
    private String coller;
    private String players;
    private String chargeTime;

    @Override
    public String getName() {
        return "capture";
    }

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent event) {
        this.time = event.getOption("time")
                .flatMap(ApplicationCommandInteractionOption::getValue)
                .map(ApplicationCommandInteractionOptionValue::asString)
                .get();
        this.chargeTime = event.getOption("chargetime")
                .flatMap(ApplicationCommandInteractionOption::getValue)
                .map(ApplicationCommandInteractionOptionValue::asString)
                .get();
        this.versus = event.getOption("vs")
                .flatMap(ApplicationCommandInteractionOption::getValue)
                .map(ApplicationCommandInteractionOptionValue::asString)
                .get();
        this.coller = event.getOption("coller")
                .flatMap(ApplicationCommandInteractionOption::getValue)
                .map(ApplicationCommandInteractionOptionValue::asString)
                .get();
        this.players = event.getOption("players")
                .flatMap(ApplicationCommandInteractionOption::getValue)
                .map(ApplicationCommandInteractionOptionValue::asString)
                .get();
        MessageChannel channel = client.getChannelById(channelId).cast(MessageChannel.class).block();
        Message message = Objects.requireNonNull(channel).createMessage(MessageCreateSpec.builder()
                .content("<@&1221116962864631860>")
                .addEmbed(EmbedCreateSpec.builder()
                        .color(Color.RED)
                        .title("СБОР НА КАПТ")
                        .addField(String.format("Начало капта: %s", time), "", false)
                        .addField(String.format("Сбор в %s", chargeTime), "", false)
                        .addField(String.format("Капт против семьи: %s", versus), "", false)
                        .addField(String.format("Колить будет: %s", coller), "", false)
                        .addField(String.format("Нужно **%s** игроков", players), "", false)
                        .build())
                .build()).block();
        this.lastMessageId = Objects.requireNonNull(message).getId();
        repeatMessage();
        return event.reply()
                .withEphemeral(true)
                .withContent("Объявление о капте успешно отправлено!");
    }

    private void repeatMessage() {
        Mono.delay(Duration.ofMinutes(15), Schedulers.parallel())
                .flatMap(__ -> client.getChannelById(channelId)
                        .cast(MessageChannel.class)
                        .flatMap(messageChannel -> messageChannel.getMessageById(this.lastMessageId))
                        .flatMap(Message::delete))
                .then(Mono.defer(this::sendFormDataMessage))
                .subscribe();
    }

    private Mono<Void> sendFormDataMessage() {
        return client.getChannelById(channelId)
                .cast(TextChannel.class)
                .flatMap(textChannel -> {
                    EmbedCreateSpec.Builder embed = EmbedCreateSpec.builder();
                    embed.title("КАПТ: ПОВТОРНОЕ УВЕДОМЛЕНИЕ");
                    embed.color(Color.RED);
                    embed.addField("Необходимо собраться на капт", "", false);
                    embed.addField(String.format("Начало мероприятия: %s", this.time), "", false);
                    embed.addField(String.format("Против: %s", this.versus), "", false);
                    embed.addField(String.format("Коллить будет %s", this.coller), "", false);
                    embed.addField(String.format("Нужно собрать как минимум %s человек", this.players), "", false);
                    return textChannel.createMessage()
                            .withContent("<@&1221116962864631860>")
                            .withEmbeds(embed.build());
                }).then();
    }
}
