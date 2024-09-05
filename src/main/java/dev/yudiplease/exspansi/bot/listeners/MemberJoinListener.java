package dev.yudiplease.exspansi.bot.listeners;

import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.guild.MemberJoinEvent;
import discord4j.core.object.entity.channel.MessageChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class MemberJoinListener {
    private static final Logger logger = LoggerFactory.getLogger(MemberJoinListener.class);

    public MemberJoinListener(GatewayDiscordClient client) {
        client.on(MemberJoinEvent.class)
                .filter(event -> event.getGuildId().equals(Snowflake.of("487505588700577798")))
                .flatMap(this::handleMemberJoin)
                .subscribe();
    }

    private Mono<Void> handleMemberJoin(MemberJoinEvent event) {
        logger.info(String.format("%s joined to server!", event.getMember().getUsername()));
        return event.getGuild()
                .flatMap(guild -> guild.getChannelById(Snowflake.of("487538053275975682")))
                .cast(MessageChannel.class)
                .flatMap(channel -> channel.createMessage("Привет, " + event.getMember().getUsername()
                        + "! Добро пожаловать на сервер семьи Exspansi проекта Majestic RP Detroit! \n"
                        + "Пожалуйста, оформи свой никнейм здесь по форме: \n"
                        + "Ранг | Имя | Статик \n"
                        + "При несоблюдении данного правила ты можешь быть исключён с сервера без объяснения причин!\n"
                        + "После того, как ты это сделал, отпиши пожалуйста плюсик в этот чат, после чего тебе выдадут роль!\n\n"
                        + "***Приятного времяпрепровождения <3***"))

                .then();
    }
}
