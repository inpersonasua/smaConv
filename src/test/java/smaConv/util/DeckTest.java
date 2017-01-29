package smaConv.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import smaConv.util.AnkiCard;
import smaConv.util.Deck;

public class DeckTest {

  Deck<AnkiCard> deck = new Deck<>();
  Map<String, String> question = new HashMap<>();
  Map<String, String> answer = new HashMap<>();

  @Test
  public void soundEntryFromQuestionShouldBeAdded() {
    question.put("field1", "value1");
    question.put("field2", "[sound:123.mp3]");
    AnkiCard card = new AnkiCard(question, answer);
    deck.add(card);

    assertThat(deck.getSounds()).containsExactly("123.mp3");
  }

  @Test
  public void soundEntryFromAnswerShouldBeAdded() {
    answer.put("sound", "abc [sound:123.mp3] [xyz]");
    answer.put("field", "value");
    AnkiCard card = new AnkiCard(question, answer);
    deck.add(card);

    assertThat(deck.getSounds()).containsExactly("123.mp3");
  }

  @Test
  public void soundsShouldNotContainDuplicates() {
    question.put("sound", "[sound:123.mp3]");
    question.put("field", "[sound:123.mp3]");
    AnkiCard card = new AnkiCard(question, answer);
    deck.add(card);
    deck.add(card);

    assertThat(deck.size()).isEqualTo(2);
    assertThat(deck.getSounds()).containsExactly("123.mp3");
  }
}
