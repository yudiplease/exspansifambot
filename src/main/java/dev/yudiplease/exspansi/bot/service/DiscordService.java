package dev.yudiplease.exspansi.bot.service;

import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.Attachment;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Role;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageCreateSpec;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class DiscordService {
    private static final Logger logger = LoggerFactory.getLogger(DiscordService.class);

    private final GatewayDiscordClient gateway;

    @Getter
    private final List<Snowflake> roleIds;

    public DiscordService(String token) {
        this.gateway = DiscordClient.create(token).login().block();
        this.roleIds = new ArrayList<>();
        roleIds.add(Snowflake.of("1271064803703525396"));
    }

    public Mono<Void> sendMessageToChannel(Long channelId, String message) {
        MessageCreateSpec spec = MessageCreateSpec.builder().content(message).build();
        return gateway.getChannelById(Snowflake.of(channelId))
                .ofType(MessageChannel.class)
                .flatMap(channel -> channel.createMessage(spec))
                .then();
    }

    public Mono<Void> sendMessageToChannelAsEmbed(Long channelId, EmbedCreateSpec embed) {
        MessageCreateSpec spec = MessageCreateSpec.builder().addEmbed(embed).build();
        return gateway.getChannelById(Snowflake.of(channelId))
                .ofType(MessageChannel.class)
                .flatMap(channel -> channel.createMessage(spec))
                .then();
    }
//    public Mono<Void> sendMessageToChannelWithFile(Long channelId, String message, Attachment attachment) {
//        return gateway.getChannelById(Snowflake.of(channelId))
//                .ofType(MessageChannel.class)
//                .flatMap(channel -> channel.createMessage(message -> message.addFile(attachment.getFilename(), )))
//                .then();
//    }

//    public Mono<Void> sendMessageWithFileToChannel(Long channelId, String message, Attachment file) {
//        try {
//            MessageCreateSpec spec = MessageCreateSpec.builder().content(message).addFile(convertAttachmentToFile(file)).build();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        return gateway.getChannelById(Snowflake.of(channelId))
//                .ofType(MessageChannel.class)
//                .flatMap(channel -> channel.createMessage(spec -> {
//                    try {
//                        spec.addFile(convertAttachmentToFile(file)).setContent(message);
//                    } catch (IOException e) {
//                        throw new RuntimeException(e);
//                    }
//                }).then();
//    }

    private static File convertAttachmentToFile(Attachment attachment) throws IOException {
        URL url = new URL(attachment.getUrl());
        Path tempFile = Files.createTempFile("attachment_", "_" + attachment.getFilename());
        Files.copy(url.openStream(), tempFile, StandardCopyOption.REPLACE_EXISTING);
        File file = tempFile.toFile();
        return file;
    }

//    public boolean checkUserHasRole(String userId, List<String> roleIds) {
//        List<Snowflake> snowflakes = gateway.getMemberById(Snowflake.of("487505588700577798"), Snowflake.of(userId))
//                .flatMapMany(Member::getRoles)
//                .map(Role::getId)
//                .collectList().block();
//        List<String> userRoles1 = new ArrayList<>();
//        for (Snowflake snowflake : Objects.requireNonNull(snowflakes)) {
//            String string = snowflake.asString();
//            userRoles1.add(string);
//        }
//        return userRoles1.contains(roleIds);
////        return Objects.requireNonNull(gateway.getMemberById(Snowflake.of("487505588700577798"), Snowflake.of(userId))
////                .flatMapMany(Member::getRoles)
////                .map(Role::getId)
////                .collectList()
////                .map(roles -> roleIds.stream().anyMatch(roles::contains)).block());
//    }

    public Mono<Boolean> checkUserHasAnyRole(String userId) {
        Mono<Boolean> mono = gateway.getMemberById(Snowflake.of("487505588700577798"), Snowflake.of(userId))
                .flatMapMany(Member::getRoles)
                .map(Role::getId)
                .collectList()
                .map(roles -> getRoleIds().stream().anyMatch(roles::contains));
        logger.info(getUserRoleIds(userId).block().toString());
        logger.info(String.valueOf(mono.block().booleanValue()));
        return mono;
    }

    public Mono<List<Role>> getUserRoles(String userId) {
        return gateway.getMemberById(Snowflake.of("487505588700577798"), Snowflake.of(userId))
                .flatMapMany(Member::getRoles)
                .collectList();
    }

    public Mono<List<Long>> getUserRoleIds(String userId) {
        return getUserRoles(userId)
                .map(roles -> roles.stream()
                        .map(Role::getId)
                        .map(Snowflake::asLong)
                        .collect(Collectors.toList()));
    }

//    @PostConstruct
//    public void onStart() {
//        sendStartupMessage();
//    }
//
//    private void sendStartupMessage() {
//        ZoneId moscowZone = ZoneId.of("Europe/Moscow");
//        ZonedDateTime moscowTime = ZonedDateTime.now(moscowZone);
//        String releaseDate = DateTimeFormatter
//                .ofLocalizedDateTime(FormatStyle.SHORT)
//                .format(moscowTime);
//        MessageCreateSpec.Builder spec = MessageCreateSpec.builder();
//        EmbedCreateSpec embed = EmbedCreateSpec.builder()
//                .title("ОБНОВЛЕНИЕ У БОТА")
//                .description(String.format("Версия сборки 0.0.7, %s", releaseDate))
//                .build();
//        spec.content("<%&1222663123882999868>");
//        spec.addEmbed()
//        gateway.getChannelById(Snowflake.of(1281314718258171904L))
//                .ofType(TextChannel.class)
//                .flatMap(channel -> channel.createMessage("Тест"))
//                .subscribe();
//    }
}
