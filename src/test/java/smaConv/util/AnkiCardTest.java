package smaConv.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

import smaConv.util.AnkiCard;

public class AnkiCardTest {
  private final HashMap<String, String> question = new HashMap<>();
  private final HashMap<String, String> answer = new HashMap<>();
  private final String frontValue = "value for front";
  private final String backValue = "value for back";
  private AnkiCard ankiCard;

  @Before
  public void setUp() {
    question.put("front", frontValue);
    answer.put("back", backValue);
    ankiCard = new AnkiCard(question, answer);
  }

  @Test
  public void checkValueForQuestion() {
    assertThat(ankiCard.getQuestion()).isEqualTo(question);
  }

  @Test
  public void checkValueForAnswer() {
    assertThat(ankiCard.getAnswer()).isEqualTo(answer);
  }

}
