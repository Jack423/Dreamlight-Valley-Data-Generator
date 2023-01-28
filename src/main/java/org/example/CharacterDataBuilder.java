package org.example;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.val;
import org.example.entities.CharacterEntity;
import org.example.models.CharacterModel;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@AllArgsConstructor
public class CharacterDataBuilder {
    private static final String CHARACTERS_PATH = "src/main/resources/characters.json";
    private final ObjectMapper mapper;

    public List<CharacterModel> convertCharacters() throws IOException {
        List<CharacterEntity> characterEntities = mapper.readValue(
                new File(CHARACTERS_PATH),
                new TypeReference<List<CharacterEntity>>() {
                }
        );

        return characterEntities.stream().map(characterEntity -> CharacterModel.builder()
                        .id(characterEntity.getId())
                        .icon(characterEntity.getIcon())
                        .name(characterEntity.getName())
                        .schedule(buildCharacterSchedule(characterEntity.getSchedule()))
                        .build())
                .toList();
    }

    public List<CharacterModel.CharacterSchedule> buildCharacterSchedule(List<CharacterEntity.CharacterSchedule> schedule) {
        if (schedule.isEmpty()) {
            return List.of(
                    CharacterModel.CharacterSchedule.builder()
                            .start(0)
                            .end(24)
                            .location("Scrooge McDuck does not sleep or eat at Chez Remy, he can be found inside his store at all times of day and night.")
                            .build()
            );
        }

        final val timeFormatter = DateTimeFormatter.ofPattern("h:mm a");

        return schedule.stream().map(time -> {
            val parsedStartTime = LocalTime.parse(time.getTime(), timeFormatter);

            CharacterEntity.CharacterSchedule nextTime;

            if (schedule.indexOf(time) == (schedule.size() - 1)) {
                nextTime = schedule.get(0);
            } else {
                nextTime = schedule.get(schedule.indexOf(time) + 1);
            }

            val parsedNextTime = LocalTime.parse(nextTime.getTime(), timeFormatter);

            return CharacterModel.CharacterSchedule.builder()
                    .start(parsedStartTime.getHour())
                    .end(parsedNextTime.getHour())
                    .location(time.getLocation())
                    .build();
        }).toList();
    }
}
