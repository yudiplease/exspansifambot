package dev.yudiplease.exspansi.bot.command.contract;

import dev.yudiplease.exspansi.bot.command.SlashCommand;
import dev.yudiplease.exspansi.bot.entity.Contract;
import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageCreateSpec;
import discord4j.discordjson.json.ApplicationCommandOptionChoiceData;
import discord4j.rest.util.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Component
public class OpenContractCommand implements SlashCommand {
    private final GatewayDiscordClient client;
    private static final Logger logger = LoggerFactory.getLogger(OpenContractCommand.class);
    private final Snowflake channelId = Snowflake.of(1221109587579637771L);

    private final List<Contract> contracts;

    public OpenContractCommand(GatewayDiscordClient client, List<Contract> contracts) {
        this.client = client;
        this.contracts = contracts;
        logger.info(contracts.toString());
        logger.info(String.valueOf(contracts.hashCode()));
    }


    @Override
    public String getName() {
        return "opencontract";
    }

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent event) {
        Contract contract = contracts.stream()
                .filter(contract1 -> contract1.getName().equals(event.getOption("name")
                        .flatMap(ApplicationCommandInteractionOption::getValue)
                        .map(ApplicationCommandInteractionOptionValue::asString)
                        .get()))
                .findFirst().get();
        if (contract.getIsOpened()) {
            return event.reply().withContent("Данный контракт уже активирован!").withEphemeral(true);
        }
        boolean isActivated = Boolean.parseBoolean(event.getOption("activated")
                .flatMap(ApplicationCommandInteractionOption::getValue)
                .map(ApplicationCommandInteractionOptionValue::asString)
                .get());
        contracts.remove(contract);
        contract.setIsOpened(true);
        contracts.add(contract);
        EmbedCreateSpec.Builder spec = EmbedCreateSpec.builder();
        spec.title("ОТКРЫТИЕ КОНТРАКТА");
        spec.color(Color.GREEN);
        spec.addField(String.format("Открыт контракт %s. Начинайте выполнять.", contract.getName()), "", false);
        spec.addField(String.format("Выполненный контракт принесёт в казну семьи $%s.", contract.getCash()), "", false);
        if (!isActivated) {
            spec.addField("Требуется активация за свой счёт.", "", false);
        } else {
            spec.addField("Активация не требуется.", "", false);
        }
        MessageChannel channel = client.getChannelById(channelId).cast(MessageChannel.class).block();
        Message message = Objects.requireNonNull(channel).createMessage(MessageCreateSpec.builder()
                .content("<@&1221116962864631860>")
                .addEmbed(spec.build())
                .build()).block();
        return event.reply()
                .withEphemeral(true)
                .withContent("Задание о выполнении контракта отправлено!");
    }

    private String getChoiceValue(String name) {
        return switch (name) {
            case "\uD83D\uDCB5 Ценная партия II" -> "178000";
            case "\uD83C\uDF3F Гровер III" -> "215000";
            case "\uD83D\uDCE6 Незаконное предприятие" -> "174000";
            case "\uD83D\uDCBB Наводка I" -> "XSS устройство I уровня";
            case "\uD83D\uDE9B Конспирация" -> "100000 - 140000";
            case "\uD83C\uDF4A Апельсины" -> "102500 - 112500";
            case "\uD83C\uDF44 Шампиньоны" -> "125000 - 135000";
            case "\uD83C\uDF32 Сосна" -> "90000 - 100000";
            default -> "";
        };
    }
}
