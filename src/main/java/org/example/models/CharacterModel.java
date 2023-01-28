package org.example.models;

import lombok.Builder;
import lombok.Data;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Data
@Builder
public class CharacterModel {
    private BigInteger id;
    private String icon;
    private String name;
    private List<CharacterSchedule> schedule;

    @Data
    @Builder
    public static class CharacterSchedule {
        private int start;
        private int end;
        private String location;
    }
}
