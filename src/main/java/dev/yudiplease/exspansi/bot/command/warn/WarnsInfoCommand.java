package dev.yudiplease.exspansi.bot.command.warn;

import dev.yudiplease.exspansi.bot.command.SlashCommand;
import dev.yudiplease.exspansi.bot.entity.UserInfo;
import dev.yudiplease.exspansi.bot.entity.WarnInfo;
import dev.yudiplease.exspansi.bot.service.UserService;
import dev.yudiplease.exspansi.bot.service.WarnService;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Color;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class WarnsInfoCommand implements SlashCommand {
    private final UserService userService;

    @Override
    public String getName() {
        return "warnsinfo";
    }

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent event) {
        String staticId = event.getOption("static")
                .flatMap(ApplicationCommandInteractionOption::getValue)
                .map(ApplicationCommandInteractionOptionValue::asString)
                .get();
        UserInfo userInfo = userService.getById(staticId);
        EmbedCreateSpec.Builder spec = EmbedCreateSpec.builder();
        spec.color(Color.RED);
        spec.title(String.format("Выговоры игрока %s (%s / 3)", userInfo.getFullName(), userInfo.getWarnings().size()));
        for (WarnInfo warn : userInfo.getWarnings()) {
            spec.addField("Выговор:", String.format("%s:", userInfo.getWarnings().indexOf(warn)+1), false);
            spec.addField("Дата выдачи: ", warn.getIssueDate(), true);
            spec.addField("Причина: ", warn.getReason(), true);
        }
        return event.reply()
                .withEphemeral(true)
                .withEmbeds(spec.build());
    }
}
