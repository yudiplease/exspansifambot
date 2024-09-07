package dev.yudiplease.exspansi.bot.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@NoArgsConstructor
@Getter
@Setter
public class UserInfo {
    @JsonProperty("FirstLastName")
    private String fullName;

    @JsonProperty("static")
    private String staticId;

    @JsonProperty("rank")
    private Long familyRank;

    @JsonProperty("reprimands")
    private List<WarnInfo> warnings;

    @JsonProperty("completedContracts")
    private Long completedContracts;

    public UserInfo(String fullName, String staticId, Long familyRank) {
        this.fullName = fullName;
        this.staticId = staticId;
        this.familyRank = familyRank;
        this.warnings = new ArrayList<>();
        this.completedContracts = 0L;
    }
}
