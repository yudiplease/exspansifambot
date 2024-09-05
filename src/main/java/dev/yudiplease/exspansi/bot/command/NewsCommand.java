package dev.yudiplease.exspansi.bot.command;

import dev.yudiplease.exspansi.bot.SendMessageToChannel;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.User;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class NewsCommand implements SlashCommand {

    private static final Logger logger = LoggerFactory.getLogger(NewsCommand.class);

    private final SendMessageToChannel sendMessageToChannel;

    @Override
    public String getName() {
        return "news";
    }

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent event) {
        String userId = event.getInteraction().getUser().getId().asString();
        logger.info(userId);
        String content = event.getOption("content")
                .flatMap(ApplicationCommandInteractionOption::getValue)
                .map(ApplicationCommandInteractionOptionValue::asString)
                .get();
        sendMessageToChannel.sendMessageToChannel(1221140427197911130L, String.format("<@&1221116962864631860>\n>>> %s", content));
        return event.reply()
                .withEphemeral(true)
                .withContent("Новость опубликована!");
    }
}

