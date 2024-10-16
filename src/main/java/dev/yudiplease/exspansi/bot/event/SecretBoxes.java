package dev.yudiplease.exspansi.bot.event;

import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.event.domain.interaction.ModalSubmitInteractionEvent;
import discord4j.core.object.component.*;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.spec.*;
import discord4j.rest.util.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class SecretBoxes {
    private final GatewayDiscordClient client;
    private final Map<String, String> buttonClickCounts = new HashMap<>();
    private final Map<String, String> formData = new HashMap<>();
    private Integer buttonClicks;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final Snowflake channelId = Snowflake.of(1221140427197911130L);
    private static final String REASON_ID = "reason";
    private static final String STATIC_ID = "staticId";
    private Snowflake lastMessageId;
    private final ZoneId zoneId = ZoneId.of("Europe/Moscow");

    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public SecretBoxes(GatewayDiscordClient client) {
        this.buttonClicks = 0;
        this.client = client;
        client.on(ButtonInteractionEvent.class)
                .flatMap(this::handleButtonClick)
                .onErrorContinue((err, obj) -> {
                    logger.error("Ошибка обработки нажатия на кнопку: " + err.getMessage());
                    err.printStackTrace();
                }).subscribe();
        client.on(ModalSubmitInteractionEvent.class)
                .flatMap(this::handleModalSubmit)
                .onErrorContinue((err, obj) -> {
                    logger.error("Ошибка обработки модального окна: " + err.getMessage());
                    err.printStackTrace();
                }).subscribe();
        scheduleMessage();
    }

    private void scheduleMessage() {
        sendMessageAtTimeEveryDay(LocalTime.of(9, 30), channelId);
        sendMessageAtTimeEveryDay(LocalTime.of(13, 30), channelId);
        sendMessageAtTimeEveryDay(LocalTime.of(17, 30), channelId);
        sendMessageAtTimeEveryDay(LocalTime.of(21, 30), channelId);
    }

    private void sendMessageAtTimeEveryDay(LocalTime targetTime, Snowflake channelId) {
        LocalTime now = LocalTime.now(zoneId);
        Duration duration = Duration.between(now, targetTime);
        if (duration.isNegative()) {
            duration = duration.plusDays(1);
        }
        scheduler.scheduleAtFixedRate(
                () -> sendMessage(channelId),
                duration.toSeconds(),
                Duration.ofDays(1).toSeconds(),
                TimeUnit.SECONDS
        );
    }

    private void sendMessage(Snowflake channelId) {
        ZonedDateTime moscowTime = ZonedDateTime.now(zoneId).plusMinutes(30);
        String startTime = DateTimeFormatter.
                ofLocalizedDateTime(FormatStyle.SHORT)
                .format(moscowTime);
        MessageChannel channel = client.getChannelById(channelId).cast(MessageChannel.class).block();
        Message message = Objects.requireNonNull(channel).createMessage(MessageCreateSpec.builder()
                .content("<@&1221116962864631860>\n" +
                        "**ТАЙНИКИ ЧЕРЕЗ 30 МИНУТ\n" +
                        "Нажмите галочку, чтобы заявить о своём участии, " +
                        "иначе крестик.**\n" +
                        "P.S. **ОБЯЗАТЕЛЬНО ПРИ НАХОЖДЕНИИ В ИГРЕ!**")
                .addEmbed(EmbedCreateSpec.builder()
                        .color(Color.BLUE)
                        .title("ТАЙНИКИ")
                        .addField("Начало мероприятия:", startTime, false).build())
                .addComponent(
                        ActionRow.of(
                                Button.success("yes", "✔"),
                                Button.danger("no", "✖")
                        )
                )
                .build()).block();
        lastMessageId = Objects.requireNonNull(message).getId();
        Mono.delay(Duration.ofMinutes(15), Schedulers.parallel())
                .flatMap(__ -> client.getChannelById(channelId)
                        .cast(MessageChannel.class)
                        .flatMap(messageChannel -> messageChannel.getMessageById(lastMessageId))
                        .flatMap(Message::delete))
                .then(Mono.defer(this::sendFormDataMessage))
                .subscribe();
    }

    @EventListener
    public Mono<Void> handleButtonClick(ButtonInteractionEvent event) {
        String userId = event.getInteraction().getUser().getTag();
        if (buttonClickCounts.containsKey(userId)) {
            return event.reply()
                    .withEphemeral(true)
                    .withContent("Данные о вашем ответе уже учтены.");
        }
        buttonClicks = buttonClickCounts.values().stream()
                .mapToInt(value -> (int) value.chars().filter(c -> c == '+').count())
                .sum();
        if (event.getCustomId().equals("no") && !buttonClickCounts.containsKey(userId)) {
            TextInput descriptionInput = TextInput.paragraph("reason", "Укажите причину отсутствия: ", "Обязательно к заполнению при нахождении в игре");
            List<LayoutComponent> components = new ArrayList<>();
            components.add(ActionRow.of(descriptionInput));
            InteractionPresentModalSpec spec = InteractionPresentModalSpec.builder()
                    .customId("reason")
                    .title("ОТКАЗ ОТ УЧАСТИЯ")
                    .components(components)
                    .build();
            buttonClickCounts.put(userId, "-");
            return event.presentModal(spec);
        }
        if (event.getCustomId().equals("yes") && !buttonClickCounts.containsKey(userId)) {
            buttonClickCounts.put(userId, "+");
            buttonClicks++;
            return event.reply()
                    .withEphemeral(true)
                    .withContent("Ваш плюсик об участии учтён!");
        }
        return Mono.empty();
    }

    @EventListener
    public Mono<Void> handleModalSubmit(ModalSubmitInteractionEvent event) {
        String reason = "";
        String player = event.getInteraction().getUser().getId().asString();
        for (TextInput component : event.getComponents(TextInput.class)) {
            if (REASON_ID.equals(component.getCustomId())) {
                reason = component.getValue().orElse("");
            }
        }
        this.formData.put(player, reason);
        logger.info(this.formData.values().toString());
        return event.reply(InteractionApplicationCommandCallbackSpec.builder()
                .content("Причина отсутствия учтена!").build()
                .withEphemeral(true));
    }

    private Mono<Void> sendFormDataMessage() {
        int clickCounts = buttonClicks;
        return client.getChannelById(channelId)
                .cast(TextChannel.class)
                .flatMap(textChannel -> {
                    EmbedCreateSpec.Builder embed = EmbedCreateSpec.builder();
                    embed.title("Тайники : Проверка участия");
                    embed.color(Color.RED);
                    embed.addField("Количество плюсов: ", String.valueOf(clickCounts), false);
                    if (formData.isEmpty()) {
                        embed.addField("Минусов нет, все кто онлайн, должны принять участие.", "", false);
                    } else {
                        embed.addField("Минусы: ", "", false);
                        for (Map.Entry<String, String> entry : formData.entrySet()) {
                            embed.addField("Тэг игрока: ", String.format("<@%s>", entry.getKey()), true);
                            embed.addField("Причина отсутствия: ", entry.getValue(), true);
                            embed.addField("", "", false);
                        }
                    }
                    buttonClickCounts.clear();
                    buttonClicks = 0;
                    formData.clear();
                    lastMessageId = null;
                    return textChannel.createMessage()
                            .withContent("<@&1221116962864631860> Желаю удачи на тайниках! У вас всё получится!")
                            .withEmbeds(embed.build());
                })
                .then();
    }
}