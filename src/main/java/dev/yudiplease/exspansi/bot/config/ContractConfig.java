package dev.yudiplease.exspansi.bot.config;

import dev.yudiplease.exspansi.bot.entity.Contract;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class ContractConfig {
    private final Logger logger = LoggerFactory.getLogger(ContractConfig.class);

    @Bean
    public List<Contract> contracts() {
        List<Contract> contracts = new ArrayList<>();
        contracts.add(new Contract("\uD83D\uDCB5 Ценная партия II", "$178000", false));
        contracts.add(new Contract("\uD83C\uDF3F Гровер III", "$215000", false));
        contracts.add(new Contract("\uD83D\uDCE6 Незаконное предприятие", "$174000", false));
        contracts.add(new Contract("\uD83D\uDCBB Наводка I", "XSS устройство I уровня", false));
        contracts.add(new Contract("\uD83D\uDE9B Конспирация", "$100000 - $140000", false));
        contracts.add(new Contract("\uD83C\uDF4A Апельсины", "$102500 - $112500", false));
        contracts.add(new Contract("\uD83C\uDF44 Шампиньоны", "$125000 - $135000", false));
        contracts.add(new Contract("\uD83C\uDF32 Сосна", "$90000 - $100000", false));
        logger.info(contracts.toString());
        logger.info(String.valueOf(contracts.hashCode()));
        return contracts;
    }
}
