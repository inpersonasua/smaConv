package smaConv.converters;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import smaConv.converters.SimpleWordsConverter;
import smaConv.converters.SimpleWordsWithSoundsConverter;
import smaConv.util.AnkiCard;
import smaConv.util.Deck;
import smaConv.util.Parser;
import smaConv.util.SmpakParser;

public class SimpleWordsWithSoundsConverterTest {
  SimpleWordsConverter converter;
  Parser smpakParser;

  @Before
  public void setUp() {
    converter = new SimpleWordsWithSoundsConverter();
    smpakParser = mock(SmpakParser.class);
    when(smpakParser.getFile("course.xml")).thenReturn(SimpleWordsConverterTest.courseXml.getBytes());
    when(smpakParser.getFile(SimpleWordsConverterTest.xmlFileName))
        .thenReturn(SimpleWordsConverterTest.xmlFile.getBytes());
  }

  @Test
  public void checkTemplates() {
    Deck<AnkiCard> deck = converter.makeDeck(smpakParser);
    assertThat(deck.getQuestionTemplate()).isEqualTo("{{front}}");
    assertThat(deck.getAnswerTemplate()).isEqualTo("{{back}}{{sound}}");
  }

  @Test
  public void checkCardFields() {
    Deck<AnkiCard> deck = converter.makeDeck(smpakParser);
    assertThat(deck.get(0).getQuestion().get("front")).isEqualTo("simple question");
    assertThat(deck.get(0).getAnswer().get("back")).isEqualTo("simple answer");
    assertThat(deck.get(0).getAnswer().get("sound")).isEqualTo("[sound:12345a.mp3]");
  }
}
