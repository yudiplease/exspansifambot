package dev.yudiplease.exspansi.bot.command.user;

import dev.yudiplease.exspansi.bot.command.SlashCommand;
import dev.yudiplease.exspansi.bot.service.UserService;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class UserDeleteCommand implements SlashCommand {
    private final UserService userService;

    @Override
    public String getName() {
        return "userdelete";
    }

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent event) {
        String staticId = event.getOption("static")
                .flatMap(ApplicationCommandInteractionOption::getValue)
                .map(ApplicationCommandInteractionOptionValue::asString)
                .get();
        userService.deleteById(staticId);
        return event.reply()
                .withEphemeral(true)
                .withContent("Информация о пользователе успешно удалена");
    }
}
