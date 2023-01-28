package org.example.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CharacterEntity {
    private BigInteger id;
    private String icon;
    private String name;
    private List<CharacterSchedule> schedule;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CharacterSchedule {
        private String time;
        private String location;
    }
}
