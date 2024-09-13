package dev.yudiplease.exspansi.bot.entity;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Contract {
    private String name;
    private String cash;
    private Boolean isOpened;
}
