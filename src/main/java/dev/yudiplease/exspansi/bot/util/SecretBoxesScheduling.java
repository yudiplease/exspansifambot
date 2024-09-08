//package dev.yudiplease.exspansi.bot.util;
//
//import discord4j.common.util.Snowflake;
//import discord4j.core.GatewayDiscordClient;
//import discord4j.core.object.entity.Message;
//import discord4j.core.object.entity.channel.MessageChannel;
//import discord4j.core.object.entity.channel.TextChannel;
//import discord4j.core.spec.EmbedCreateSpec;
//import discord4j.rest.util.Color;
//import org.springframework.stereotype.Component;
//import reactor.core.publisher.Mono;
//import reactor.core.scheduler.Schedulers;
//
//import java.time.Duration;
//import java.time.LocalTime;
//import java.time.ZoneId;
//import java.util.Map;
//import java.util.Timer;
//import java.util.TimerTask;
//
//@Component
//public class SecretBoxesScheduling {
//    private final GatewayDiscordClient client;
//    private Snowflake lastMessageId;
//    private final ZoneId zoneId = ZoneId.of("Europe/Moscow");
//    private final Snowflake channelId;
//
//    public SecretBoxesScheduling(GatewayDiscordClient client, Snowflake channelId) {
//        this.client = client;
//        this.channelId = channelId;
//    }
//
//    public void scheduleMessages() {
//        boxesAt10AM();
//        boxesAt2PM();
//        boxesAt6PM();
//        boxesAt10PM();
//    }
//
//    private void boxesAt10PM() {
//        Timer timer = new Timer();
//        TimerTask task = new TimerTask() {
//            @Override
//            public void run() {
//                sendMessage();
//            }
//        };
//        LocalTime targetTime = LocalTime.of(21, 30);
//        long delayMs = Duration.between(LocalTime.now(zoneId), targetTime).toMillis();
//        if (delayMs < 0) {
//            delayMs += Duration.ofDays(1).toMillis();
//        }
//        timer.schedule(task, delayMs);
//        Mono.delay(Duration.ofMinutes(15), Schedulers.parallel())
//                .flatMap(__ -> client.getChannelById(channelId)
//                        .cast(MessageChannel.class)
//                        .flatMap(channel -> channel.getMessageById(lastMessageId))
//                        .flatMap(Message::delete))
//                .then(Mono.defer(this::sendFormDataMessage))
//                .subscribe();
//    }
//
//    private void boxesAt6PM() {
//        Timer timer = new Timer();
//        TimerTask task = new TimerTask() {
//            @Override
//            public void run() {
//                sendMessage();
//            }
//        };
//        LocalTime targetTime = LocalTime.of(17, 30);
//        long delayMs = Duration.between(LocalTime.now(zoneId), targetTime).toMillis();
//        if (delayMs < 0) {
//            delayMs += Duration.ofDays(1).toMillis();
//        }
//        timer.schedule(task, delayMs);
//        Mono.delay(Duration.ofMinutes(15), Schedulers.parallel())
//                .flatMap(__ -> client.getChannelById(channelId)
//                        .cast(MessageChannel.class)
//                        .flatMap(channel -> channel.getMessageById(lastMessageId))
//                        .flatMap(Message::delete))
//                .then(Mono.defer(this::sendFormDataMessage))
//                .subscribe();
//    }
//
//    private void boxesAt2PM() {
//        Timer timer = new Timer();
//        TimerTask task = new TimerTask() {
//            @Override
//            public void run() {
//                sendMessage();
//            }
//        };
//        LocalTime targetTime = LocalTime.of(13, 30);
//        long delayMs = Duration.between(LocalTime.now(zoneId), targetTime).toMillis();
//        if (delayMs < 0) {
//            delayMs += Duration.ofDays(1).toMillis();
//        }
//        timer.schedule(task, delayMs);
//        Mono.delay(Duration.ofMinutes(15), Schedulers.parallel())
//                .flatMap(__ -> client.getChannelById(channelId)
//                        .cast(MessageChannel.class)
//                        .flatMap(channel -> channel.getMessageById(lastMessageId))
//                        .flatMap(Message::delete))
//                .then(Mono.defer(this::sendFormDataMessage))
//                .subscribe();
//    }
//
//    private void boxesAt10AM(TimerTask task) {
//        Timer timer = new Timer();
////        TimerTask task = new TimerTask() {
////            @Override
////            public void run() {
////                sendMessage();
////            }
////        };
//        LocalTime targetTime = LocalTime.of(9, 30);
//        ZoneId zoneId = ZoneId.of("Europe/Moscow");
//        long delayMs = Duration.between(LocalTime.now(zoneId), targetTime).toMillis();
//        if (delayMs < 0) {
//            delayMs += Duration.ofDays(1).toMillis();
//        }
//        timer.schedule(task, delayMs);
//        Mono.delay(Duration.ofMinutes(15), Schedulers.parallel())
//                .flatMap(__ -> client.getChannelById(channelId)
//                        .cast(MessageChannel.class)
//                        .flatMap(channel -> channel.getMessageById(lastMessageId))
//                        .flatMap(Message::delete))
//                .then(Mono.defer(this::sendFormDataMessage))
//                .subscribe();
//    }
//
//
//
//    private Mono<Void> sendFormDataMessage() {
//        int clickCounts = buttonClicks;
//        logger.info(buttonClicks.toString());
//        return client.getChannelById(channelId)
//                .cast(TextChannel.class)
//                .flatMap(textChannel -> {
//                    EmbedCreateSpec.Builder embed = EmbedCreateSpec.builder();
//                    embed.title("Тайники : Проверка участия");
//                    embed.color(Color.RED);
//                    embed.addField("Количество плюсов: ", String.valueOf(clickCounts), false);
//                    embed.addField("Минусы: ", "", false);
//                    for (Map.Entry<String, String> entry : formData.entrySet()) {
//                        embed.addField("staticId: ", entry.getKey(), true);
//                        embed.addField("Причина отсутствия: ", entry.getValue(), true);
//                        embed.addField("", "", false);
//                    }
//                    buttonClickCounts.clear();
//                    buttonClicks = 0;
//                    logger.info(buttonClicks.toString());
//                    return textChannel.createMessage()
//                            .withContent("<@&1221116962864631860>")
//                            .withEmbeds(embed.build());
//                })
//                .then();
//    }
//}
