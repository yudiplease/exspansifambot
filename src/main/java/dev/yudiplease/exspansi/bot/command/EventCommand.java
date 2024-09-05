package dev.yudiplease.exspansi.bot.command;

import dev.yudiplease.exspansi.bot.SendMessageToChannel;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class EventCommand implements SlashCommand {

    private final SendMessageToChannel sendMessageToChannel;

    @Override
    public String getName() {
        return "event";
    }

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent event) {
        String newEvent = event.getOption("event")
                .flatMap(ApplicationCommandInteractionOption::getValue)
                .map(ApplicationCommandInteractionOptionValue::asString)
                .get();
        String time = event.getOption("time")
                .flatMap(ApplicationCommandInteractionOption::getValue)
                .map(ApplicationCommandInteractionOptionValue::asString)
                .get();
        String location = event.getOption("location")
                .flatMap(ApplicationCommandInteractionOption::getValue)
                .map(ApplicationCommandInteractionOptionValue::asString)
                .get();
        sendMessageToChannel.sendMessageToChannel(1223964747032952893L, String.format("<@&1221116962864631860>\n>>> В **%s** будет проведено мероприятие: ***%s***. Всем быть к этому времени. Место сбора: **%s**", time, newEvent, location));
//                .createMessage(String.format("<@&1221116962864631860>  В %s будет проведено %s. Всем быть к этому времени. Место сбора: %s", newEvent, time, location));
        return event.reply()
                .withEphemeral(true)
                .withContent("Новость о событии опубликована!");
    }
}
