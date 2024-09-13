//package dev.yudiplease.exspansi.bot.entity;
//
//import discord4j.common.util.Snowflake;
//import discord4j.core.GatewayDiscordClient;
//import discord4j.core.event.domain.lifecycle.ReadyEvent;
//import discord4j.core.object.component.ActionRow;
//import discord4j.core.object.component.Button;
//import discord4j.core.spec.*;
//import discord4j.rest.util.Color;
//import org.springframework.stereotype.Component;
//
//@Component
//public class TestForum {
//    private final GatewayDiscordClient client;
//
//
//    public TestForum(GatewayDiscordClient client) {
//        this.client = client;
//
//        final ForumTagCreateSpec testTag1 = ForumTagCreateSpec.builder()
//                .name("Tag 1")
//                .emojiNameOrNull("✅")
//                .build(),
//                testTag2 = ForumTagCreateSpec.builder()
//                        .name("Tag 2")
//                        .emojiNameOrNull("❤")
//                        .build();
//
//        client.on(ReadyEvent.class, readyEvent -> client.getGuildById(Snowflake.of(487505588700577798L))
//                .flatMap(guild -> guild.createForumChannel(ForumChannelCreateSpec.builder()
//                        .name("test-forum")
//                        .addAvailableTags(testTag1, testTag2)
//                        .defaultSortOrderOrNull(ForumChannel.SortOrder.LATEST_ACTIVITY.getValue())
//                        .defaultForumLayoutOrNull(ForumChannel.LayoutType.GALLERY_VIEW.getValue())
//                        .reason("Создание тестового форум-канала")
//                        .build()))
//                .flatMap(channel -> {
//                    Snowflake tag2ID = channel.getAvailableTags().stream()
//                            .filter(tag -> tag.getName().equals("Tag 2"))
//                            .findFirst()
//                            .map(ForumTag::getId)
//                            .orElseThrow(IllegalArgumentException::new);
//
//                    return channel.startThread(StartThreadInForumChannelSpec.builder()
//                            .name("Test Thread")
//                            .addAppliedTag(tag2ID)
//                            .reason("Создание тестового потока")
//                            .message(ForumThreadMessageCreateSpec.builder()
//                                    .content("Message content")
//                                    .addComponent(ActionRow.of(Button.primary("test",
//                                            "Test button (ничего не делает)")))
//                                    .addEmbed(EmbedCreateSpec.builder()
//                                            .title("тест")
//                                            .color(Color.ORANGE)
//                                            .build())
//                                    .build())
//                            .build());
//                })).subscribe();
//    }
//}
