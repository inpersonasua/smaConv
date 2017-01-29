package smaConv.converters;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import smaConv.converters.WordsConverter;
import smaConv.converters.WordsWithSoundsConverter;
import smaConv.util.AnkiCard;
import smaConv.util.Deck;
import smaConv.util.Parser;
import smaConv.util.SmpakParser;

public class WordsWithSoundsConverterTest {
  WordsConverter converter;
  Parser smpakParser;

  @Before
  public void setUp() {
    converter = new WordsWithSoundsConverter();
    smpakParser = mock(SmpakParser.class);
    when(smpakParser.getFile("course.xml")).thenReturn(WordsConverterTest.courseXml.getBytes());
    when(smpakParser.getFile(WordsConverterTest.xmlFileName))
        .thenReturn(WordsConverterTest.xmlFile.getBytes());
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
    assertThat(deck.get(0).getQuestion().get("front")).isEqualTo("question");
    assertThat(deck.get(0).getAnswer().get("back")).isEqualTo("answer");
    assertThat(deck.get(0).getAnswer().get("sound")).isEqualTo("[sound:12345a.mp3]");
  }
}
