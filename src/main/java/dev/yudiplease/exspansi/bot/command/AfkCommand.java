package dev.yudiplease.exspansi.bot.command;

import dev.yudiplease.exspansi.bot.SendMessageToChannel;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Color;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class AfkCommand implements SlashCommand {
    private final SendMessageToChannel sendMessageToChannel;

    @Override
    public String getName() {
        return "afk";
    }

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent event) {
        ZoneId moscowZone = ZoneId.of("Europe/Moscow");
        ZonedDateTime moscowTime = ZonedDateTime.now(moscowZone);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        String formattedTime = moscowTime.format(formatter);
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
                .addField("Начало ухода: ", formattedTime, false)
                .addField("Время ухода в АФК: ", time, false)
                .addField("Причина: ", reason, false)
                .build();
        sendMessageToChannel.sendMessageToChannelAsEmbed(1222217190871138334l, spec);
        return event.reply()
                .withEphemeral(true)
                .withContent("АФК отчёт успешно отправлен!");
    }
}
