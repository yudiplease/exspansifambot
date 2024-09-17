package dev.yudiplease.exspansi.bot.command;

import dev.yudiplease.exspansi.bot.SendMessageToChannel;
import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageCreateSpec;
import discord4j.rest.util.Color;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Objects;

@Component
public class AfkCommand implements SlashCommand {
    private final Snowflake channelId = Snowflake.of(1222217190871138334L);

    private final GatewayDiscordClient client;

    public AfkCommand(GatewayDiscordClient client) {
        this.client = client;
    }

    @Override
    public String getName() {
        return "afk";
    }

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent event) {
        ZoneId moscowZone = ZoneId.of("Europe/Moscow");
        ZonedDateTime moscowTime = ZonedDateTime.now(moscowZone);
        String issueDate = DateTimeFormatter
                .ofLocalizedDateTime(FormatStyle.SHORT)
                .format(moscowTime);
        String user = event.getInteraction().getUser().getId().asString();
        String time = event.getOption("time")
                .flatMap(ApplicationCommandInteractionOption::getValue)
                .map(ApplicationCommandInteractionOptionValue::asString)
                .get();
        String reason = event.getOption("reason")
                .flatMap(ApplicationCommandInteractionOption::getValue)
                .map(ApplicationCommandInteractionOptionValue::asString)
                .get();
        EmbedCreateSpec spec = EmbedCreateSpec.builder()
                .color(Color.RED)
                .title("АФК отчёт")
                .addField("Отчёт отправил: ", String.format("<@%s>", user), true)
                .addField("Начало ухода: ", issueDate, false)
                .addField("Время ухода в АФК: ", time, false)
                .addField("Причина: ", reason, false)
                .build();
        MessageChannel channel = client.getChannelById(channelId).cast(MessageChannel.class).block();
        Message message = Objects.requireNonNull(channel).createMessage(MessageCreateSpec.builder()
                .content("<@&1222663123882999868>")
                .addEmbed(spec)
                .build()).block();
        return event.reply()
                .withEphemeral(true)
                .withContent("АФК отчёт успешно отправлен!");
    }
}
