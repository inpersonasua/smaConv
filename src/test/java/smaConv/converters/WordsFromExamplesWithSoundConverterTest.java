package smaConv.converters;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import smaConv.util.AnkiCard;
import smaConv.util.Deck;
import smaConv.util.Parser;
import smaConv.util.SmpakParser;

public class WordsFromExamplesWithSoundConverterTest {
  Converter converter;
  Parser smpakParser;

  @Before
  public void setUp() {
    converter = new WordsFromExamplesWithSoundConverter();
    smpakParser = mock(SmpakParser.class);
    when(smpakParser.getFile("course.xml"))
        .thenReturn(WordsFromExamplesConverterTest.courseXml.getBytes());
    when(smpakParser.getFile(WordsFromExamplesConverterTest.xmlFileName))
        .thenReturn(WordsFromExamplesConverterTest.xmlFile.getBytes());
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
    assertThat(deck.get(0).getQuestion().get("front")).isEqualTo("example question");
    assertThat(deck.get(0).getAnswer().get("back")).isEqualTo("example answer");
    assertThat(deck.get(0).getAnswer().get("sound")).isEqualTo("[sound:12345a.mp3]");
  }
}