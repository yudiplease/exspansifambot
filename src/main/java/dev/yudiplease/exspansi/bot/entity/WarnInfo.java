package dev.yudiplease.exspansi.bot.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WarnInfo {
    @JsonProperty("warnid")
    private String warnId;

    @JsonProperty("staticId")
    private String staticId;

    @JsonProperty("issueDate")
    private String issueDate;

    @JsonProperty("reason")
    private String reason;

    public WarnInfo(String staticId, String issueDate, String reason) {
        this.staticId = staticId;
        this.issueDate = issueDate;
        this.reason = reason;
    }
}
