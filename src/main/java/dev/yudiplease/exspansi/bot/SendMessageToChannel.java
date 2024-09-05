package dev.yudiplease.exspansi.bot;

import dev.yudiplease.exspansi.bot.service.DiscordService;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Attachment;
import discord4j.core.spec.EmbedCreateSpec;
import lombok.Getter;
import org.springframework.context.annotation.Scope;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
@Scope("singleton")
public class SendMessageToChannel {
    private final DiscordService discordService;

    @Getter
    private final List<String> roleIds;

    public SendMessageToChannel(DiscordService discordService) {
        this.discordService = discordService;
        this.roleIds = new ArrayList<>();
        roleIds.add("1271064803703525396");
    }

    public void sendMessageToChannel(Long channelId, String message) {
        discordService.sendMessageToChannel(channelId, message).subscribe();
    }

    public void sendMessageToChannelAsEmbed(Long channelId, EmbedCreateSpec spec) {
        discordService.sendMessageToChannelAsEmbed(channelId, spec).subscribe();
    }

    public Mono<Boolean> checkUserHasRole(String userId) {
        return discordService.checkUserHasAnyRole(userId);
    }
    public void sendMessageWithFileToChannel(Long channelId, String message, Attachment file) {

    }
}
