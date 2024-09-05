package dev.yudiplease.exspansi.bot.command;

import dev.yudiplease.exspansi.bot.SendMessageToChannel;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Color;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class BotNewsCommand implements SlashCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(BotNewsCommand.class);
    private final SendMessageToChannel sendMessageToChannel;
    @Override
    public String getName() {
        return "botnews";
    }

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent event) {
        String user = event.getInteraction().getUser().getId().asString();
        if (!user.equals("315915770330611712")) {
            return event.reply()
                    .withEphemeral(true)
                    .withContent("Увы, но эта команда только для Майка, пупс. Извини.");
        }
        String version = event.getOption("version")
                .flatMap(ApplicationCommandInteractionOption::getValue)
                .map(ApplicationCommandInteractionOptionValue::asString)
                .get();
        String news = event.getOption("new")
                .flatMap(ApplicationCommandInteractionOption::getValue)
                .map(ApplicationCommandInteractionOptionValue::asString)
                .get();
        EmbedCreateSpec spec = EmbedCreateSpec.builder()
                .color(Color.WHITE)
                .title("НОВАЯ ФИЧА!")
                .addField("Версия сборки: ", version, false)
                .addField("Что нового: ", news, false)
                .build();
        sendMessageToChannel.sendMessageToChannelAsEmbed(1281314718258171904l, spec);
        return event.reply()
                .withEphemeral(true)
                .withContent("Новость об изменениях в боте опубликована!");
    }
}
