package dev.yudiplease.exspansi.bot.command;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class TestCommand implements SlashCommand {
    @Override
    public String getName() {
        return "test";
    }

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent event) {
        String name = event.getOption("test")
                .flatMap(ApplicationCommandInteractionOption::getValue)
                .map(ApplicationCommandInteractionOptionValue::asString)
                .get(); //This is warning us that we didn't check if its present, we can ignore this on required options

        //Reply to the slash command, with the name the user supplied
        return  event.reply()
                .withEphemeral(true)
                .withContent("Hello, " + event.getInteraction().getUser().getUsername());
    }
}
