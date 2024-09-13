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
import discord4j.rest.util.Color;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class CloseContractCommand implements SlashCommand {
    private final List<Contract> contracts;
    private final GatewayDiscordClient client;
    private final Snowflake channelId = Snowflake.of(1221109587579637771L);

    @Override
    public String getName() {
        return "closecontract";
    }

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent event) {
        Contract contract = contracts.stream()
                .filter(contract1 -> contract1.getName().equals(event.getOption("name")
                        .flatMap(ApplicationCommandInteractionOption::getValue)
                        .map(ApplicationCommandInteractionOptionValue::asString)
                        .get()))
                .findFirst().get();
        if (!contract.getIsOpened()) {
            return event.reply().withEphemeral(true).withContent("Данный контракт уже закрыт!");
        }
        contracts.remove(contract);
        contract.setIsOpened(false);
        contracts.add(contract);
        EmbedCreateSpec.Builder spec = EmbedCreateSpec.builder();
        spec.title("ЗАКРЫТИЕ КОНТРАКТА");
        spec.color(Color.GREEN);
        spec.addField(String.format("Контракт %s успешно закрыт. Всем спасибо за выполнение.", contract.getName()), "", false);
        spec.addField(String.format("Семья получила %s.", contract.getCash()), "", false);
        MessageChannel channel = client.getChannelById(channelId).cast(MessageChannel.class).block();
        Message message = Objects.requireNonNull(channel).createMessage(MessageCreateSpec.builder()
                .content("<@&1221116962864631860>")
                .addEmbed(spec.build())
                .build()).block();
        return event.reply().withEphemeral(true).withContent("Контракт успешно закрыт!");
    }
}
