package dev.yudiplease.exspansi.bot.command;

import dev.yudiplease.exspansi.bot.SendMessageToChannel;
import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.object.entity.Attachment;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageCreateSpec;
import discord4j.rest.util.Color;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class PaymentCommand implements SlashCommand {

    private final SendMessageToChannel sendMessageToChannel;
    private final Snowflake channelId = Snowflake.of(1221109587579637771L);
    private final GatewayDiscordClient client;

    @Override
    public String getName() {
        return "payment";
    }

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent event) {
        String contract = event.getOption("contract")
                .flatMap(ApplicationCommandInteractionOption::getValue)
                .map(ApplicationCommandInteractionOptionValue::asString)
                .get();
        String statics = event.getOption("statics")
                .flatMap(ApplicationCommandInteractionOption::getValue)
                .map(ApplicationCommandInteractionOptionValue::asString)
                .get();
        Attachment proof = event.getOption("proof")
                .flatMap(ApplicationCommandInteractionOption::getValue)
                .map(ApplicationCommandInteractionOptionValue::asAttachment)
                .get();
        EmbedCreateSpec spec = EmbedCreateSpec.builder()
                .color(Color.BLUE)
                .title("ЗАПРОС О ВЫПЛАТЕ ДЕНЕГ")
                .addField("Запрос отправил: ", String.format("<@%s>", event.getInteraction().getUser().getId().asString()), true)
                .addField("Название контракта", contract, false)
                .addField("Статики игроков", statics, false)
                .image(proof.getUrl())
                .build();
        MessageChannel channel = client.getChannelById(channelId).cast(MessageChannel.class).block();
        Message message = Objects.requireNonNull(channel).createMessage(MessageCreateSpec.builder()
                .content("<@&1230375980623728670> <@&1280913939139399721>")
                .addEmbed(spec)
                .build()).block();
        return event.reply()
                .withEphemeral(true)
                .withContent("Запрос о получении выплаты отправлен.");
    }
}
