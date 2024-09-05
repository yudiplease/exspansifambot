package dev.yudiplease.exspansi.bot.listeners;

import discord4j.common.util.Snowflake;
import discord4j.core.object.command.ApplicationCommand;
import discord4j.core.object.entity.Message;
import reactor.core.publisher.Mono;

public abstract class MessageListener {
    private Long author = 0L;

//    public Mono<Void> processMessage(final Message eventMessage) {
//        return Mono.just(eventMessage)
//                .filter(message -> {
//                    final Boolean isNotBot = message.getAuthor()
//                            .map(user -> !user.isBot())
//                            .orElse(false);
//                    if (isNotBot) {
//                        message.getAuthor().ifPresent(user -> author = user.getId().asLong());
//                    }
//                    return isNotBot;
//                })
//                .flatMap(Message::getChannel)
//                .flatMap(channel -> channel.createMessage(String.format(">>> Привет, <@%s>!", author)))
//                .then();
//    }
}
