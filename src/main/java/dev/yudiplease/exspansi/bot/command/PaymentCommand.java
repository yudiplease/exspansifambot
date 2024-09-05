package dev.yudiplease.exspansi.bot.command;

import dev.yudiplease.exspansi.bot.SendMessageToChannel;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.object.entity.Attachment;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Color;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class PaymentCommand implements SlashCommand {

    private final SendMessageToChannel sendMessageToChannel;

    @Override
    public String getName() {
        return "payment";
    }

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent event) {
        String user = event.getInteraction().getUser().getUsername();
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
        sendMessageToChannel.sendMessageToChannelAsEmbed(1221109587579637771l, spec);
        return event.reply()
                .withEphemeral(true)
                .withContent("Запрос о получении выплаты отправлен.");
    }
}
