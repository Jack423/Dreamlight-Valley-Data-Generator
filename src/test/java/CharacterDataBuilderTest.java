import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.example.CharacterDataBuilder;
import org.example.entities.CharacterEntity;
import org.example.models.CharacterModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@AllArgsConstructor
public class CharacterDataBuilderTest {
    final CharacterDataBuilder characterDataBuilder = new CharacterDataBuilder(new ObjectMapper());

    @Test
    public void buildCharacter_timesGetAddedCorrectly() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm a");
        List<CharacterEntity.CharacterSchedule> scheduleList = List.of(
                CharacterEntity.CharacterSchedule.builder()
                        .time("1:00 AM")
                        .location("Chez Remy eating inside the Restaurant.")
                        .build(),
                CharacterEntity.CharacterSchedule.builder()
                        .time("3:00 AM")
                        .location("Wandering the Valley, his home, and other villager homes.")
                        .build(),
                CharacterEntity.CharacterSchedule.builder()
                        .time("7:00 AM")
                        .location("Unavailable. Sleeping inside his home.")
                        .build()
        );

        List<CharacterModel.CharacterSchedule> expected = List.of(
                CharacterModel.CharacterSchedule.builder()
                        .start(LocalTime.parse("1:00 AM", formatter).getHour())
                        .end(LocalTime.parse("3:00 AM", formatter).getHour())
                        .location("Chez Remy eating inside the Restaurant.")
                        .build(),
                CharacterModel.CharacterSchedule.builder()
                        .start(LocalTime.parse("3:00 AM", formatter).getHour())
                        .end(LocalTime.parse("7:00 AM", formatter).getHour())
                        .location("Wandering the Valley, his home, and other villager homes.")
                        .build(),
                CharacterModel.CharacterSchedule.builder()
                        .start(LocalTime.parse("7:00 AM", formatter).getHour())
                        .end(LocalTime.parse("1:00 AM", formatter).getHour())
                        .location("Unavailable. Sleeping inside his home.")
                        .build()
        );

        List<CharacterModel.CharacterSchedule> actual = characterDataBuilder.buildCharacterSchedule(scheduleList);

        Assertions.assertEquals(expected, actual);
    }
}
