package dev.yudiplease.exspansi.bot.command.warn;

import dev.yudiplease.exspansi.bot.command.SlashCommand;
import dev.yudiplease.exspansi.bot.entity.UserInfo;
import dev.yudiplease.exspansi.bot.entity.WarnInfo;
import dev.yudiplease.exspansi.bot.service.UserService;
import dev.yudiplease.exspansi.bot.service.WarnService;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
public class WarnDeleteCommand implements SlashCommand {
    private final WarnService warnService;
    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(WarnDeleteCommand.class);

    @Override
    public String getName() {
        return "warndelete";
    }

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent event) {
        String staticId = event.getOption("static")
                .flatMap(ApplicationCommandInteractionOption::getValue)
                .map(ApplicationCommandInteractionOptionValue::asString)
                .get();
        String number = event.getOption("number")
                .flatMap(ApplicationCommandInteractionOption::getValue)
                .map(ApplicationCommandInteractionOptionValue::asString)
                .get();
        UserInfo user = userService.getById(staticId);
        List<WarnInfo> warns = user.getWarnings();
        WarnInfo deletingWarn = warnService.getById(staticId, number);
        for (WarnInfo warn : warns) {
            if (warn.getWarnId().equals(number)) {
                deletingWarn = warn;
                break;
            }
        }
        logger.info("{} {} {} {}", deletingWarn.getWarnId(), deletingWarn.getStaticId(), deletingWarn.getIssueDate(), deletingWarn.getReason());
        warns.remove(deletingWarn);
        user.setWarnings(warns);
        warnService.deleteById(staticId, number);
        userService.update(user.getStaticId(), user);
        return event.reply()
                .withEphemeral(true)
                .withContent(String.format("Варн %s игрока со статиком %s успешно удалён.", deletingWarn.getWarnId(), user.getStaticId()));
    }
}
