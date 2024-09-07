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
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class WarnAddCommand implements SlashCommand {
    private final WarnService warnService;
    private final UserService userService;

    @Override
    public String getName() {
        return "warnadd";
    }

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent event) {
        String staticId = event.getOption("static")
                .flatMap(ApplicationCommandInteractionOption::getValue)
                .map(ApplicationCommandInteractionOptionValue::asString)
                .get();
        if (!staticId.startsWith("#")) {
            staticId = "#" + staticId;
        }
        String reason = event.getOption("reason")
                .flatMap(ApplicationCommandInteractionOption::getValue)
                .map(ApplicationCommandInteractionOptionValue::asString)
                .get();
        ZoneId moscowZone = ZoneId.of("Europe/Moscow");
        ZonedDateTime moscowTime = ZonedDateTime.now(moscowZone);
        String issueDate = DateTimeFormatter
                .ofLocalizedDateTime(FormatStyle.SHORT)
                .format(moscowTime);
        UserInfo user = userService.getById(staticId);
        List<WarnInfo> userWarns = user.getWarnings();
        if (userWarns.size() >= 3) {
            return event.reply()
                    .withEphemeral(true)
                    .withContent("Ошибка: Игрок имеет 3 выговора!");
        }

        int countWarns = userWarns.size();
        WarnInfo warn = new WarnInfo(String.valueOf(countWarns+1), staticId, issueDate, reason);
        warnService.add(String.valueOf(countWarns+1), warn);
        userWarns.add(new WarnInfo(String.valueOf(countWarns+1), staticId, issueDate, reason));
        user.setWarnings(userWarns);
        userService.update(user.getStaticId(), user);
        return event.reply()
                .withEphemeral(true)
                .withContent(String.format("Выговор успешно выдан игроку со статиком: %s. Количество выговоров: %s", user.getStaticId(), user.getWarnings().size()));
    }
}
