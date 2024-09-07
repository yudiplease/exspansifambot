package dev.yudiplease.exspansi.bot.command.user;

import dev.yudiplease.exspansi.bot.command.SlashCommand;
import dev.yudiplease.exspansi.bot.entity.UserInfo;
import dev.yudiplease.exspansi.bot.service.UserService;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class UserAddCommand implements SlashCommand {
    private final UserService userService;
    @Override
    public String getName() {
        return "useradd";
    }
    @Override
    public Mono<Void> handle(ChatInputInteractionEvent event) {
        String fullName = event.getOption("fullname")
                .flatMap(ApplicationCommandInteractionOption::getValue)
                .map(ApplicationCommandInteractionOptionValue::asString)
                .get();
        String staticId = event.getOption("static")
                .flatMap(ApplicationCommandInteractionOption::getValue)
                .map(ApplicationCommandInteractionOptionValue::asString)
                .get();
        Long rank = event.getOption("rank")
                .flatMap(ApplicationCommandInteractionOption::getValue)
                .map(ApplicationCommandInteractionOptionValue::asLong)
                .get();
        userService.add(new UserInfo(fullName, staticId, rank));
        return event.reply()
                .withEphemeral(true)
                .withContent("Информация об игроке успешно сохранена.");
    }
}
