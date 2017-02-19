package smaConv.converters;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import smaConv.converters.SimpleClozeConverterWithSounds;
import smaConv.util.AnkiCard;
import smaConv.util.Deck;
import smaConv.util.Parser;
import smaConv.util.SmpakParser;

public class SimpleClozeConverterWithSoundsTest {
  SimpleClozeConverterWithSounds converter;
  Parser smpakParser;

  @Before
  public void setUp() {
    converter = new SimpleClozeConverterWithSounds();
    smpakParser = mock(SmpakParser.class);
    when(smpakParser.getFile("course.xml")).thenReturn(SimpleClozeConverterTest.courseXml.getBytes());
    when(smpakParser.getFile(SimpleClozeConverterTest.xmlFileName))
        .thenReturn(SimpleClozeConverterTest.xmlFile.getBytes());
  }

  @Test
  public void checkTemplates() {
    Deck<AnkiCard> deck = converter.makeDeck(smpakParser);
    assertThat(deck.getQuestionTemplate()).isEqualTo("{{cloze:sentence}}");
    assertThat(deck.getAnswerTemplate())
        .isEqualTo("{{cloze:sentence}}{{sound}}<br>{{translation}}");
  }

  @Test
  public void checkCardFields() {
    Deck<AnkiCard> deck = converter.makeDeck(smpakParser);
    assertThat(deck.get(0).getQuestion().get("sentence")).isEqualTo(SimpleClozeConverterTest.expectedQuestion);
    assertThat(deck.get(0).getAnswer().get("translation")).isEqualTo(SimpleClozeConverterTest.expectedAnswer);
    assertThat(deck.get(0).getAnswer().get("sound")).isEqualTo("[sound:12346a.mp3]");
  }
}
