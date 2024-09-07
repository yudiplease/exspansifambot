package dev.yudiplease.exspansi.bot.command.user;

import dev.yudiplease.exspansi.bot.SendMessageToChannel;
import dev.yudiplease.exspansi.bot.command.SlashCommand;
import dev.yudiplease.exspansi.bot.entity.UserInfo;
import dev.yudiplease.exspansi.bot.service.UserService;
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
public class UserInfoCommand implements SlashCommand {
    private final UserService userService;
    private final SendMessageToChannel sendMessageToChannel;

    @Override
    public String getName() {
        return "userinfo";
    }

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent event) {
        String staticId = event.getOption("staticid")
                .flatMap(ApplicationCommandInteractionOption::getValue)
                .map(ApplicationCommandInteractionOptionValue::asString)
                .get();
        UserInfo userInfo = userService.getById(staticId);
        EmbedCreateSpec spec = EmbedCreateSpec.builder()
                .color(Color.RED)
                .title("ИНФОРМАЦИЯ О ЧЛЕНЕ СЕМЬИ")
                .addField("Имя и фамилия: ", String.format("%s", userInfo.getFullName()), false)
                .addField("Статик: ", String.format("%s", userInfo.getStaticId()), false)
                .addField("Ранг: ", String.format("%s", userInfo.getFamilyRank()), true)
                .addField("Выговоры: ", String.format("%s", userInfo.getWarnings().size()), true)
                .addField("Количество выполненных контрактов: ", String.format("%s", userInfo.getCompletedContracts()), true)
                .build();
        return event.reply()
                .withEphemeral(true)
                .withEmbeds(spec);
    }
}
