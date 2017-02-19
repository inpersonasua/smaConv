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

public class ClozeConverterFromExamplesWithSoundTest {
  ClozeConverterFromExamplesWithSound converter;
  Parser smpakParser;

  @Before
  public void setUp() {
    converter = new ClozeConverterFromExamplesWithSound();
    smpakParser = mock(SmpakParser.class);
    when(smpakParser.getFile("course.xml"))
        .thenReturn(ClozeConverterFromExamplesTest.courseXml.getBytes());
    when(smpakParser.getFile(ClozeConverterFromExamplesTest.xmlFileName))
        .thenReturn(ClozeConverterFromExamplesTest.xmlFile.getBytes());
  }

  @Test
  public void checkTemplates() {
    Deck<AnkiCard> deck = converter.makeDeck(smpakParser);
    assertThat(deck.getQuestionTemplate()).isEqualTo("{{cloze:sentence}}<br>{{synonyms}}");
    assertThat(deck.getAnswerTemplate())
        .isEqualTo("{{cloze:sentence}}{{sound}}<br>{{translation}}<br>{{synonyms}}");
  }

  @Test
  public void checkCardFields() {
    Deck<AnkiCard> deck = converter.makeDeck(smpakParser);
    assertThat(deck.get(0).getQuestion().get("sentence"))
        .isEqualTo(ClozeConverterFromExamplesTest.expectedQuestion);
    assertThat(deck.get(0).getAnswer().get("translation"))
        .isEqualTo(ClozeConverterFromExamplesTest.expectedAnswer);
    assertThat(deck.get(0).getAnswer().get("sound")).isEqualTo("[sound:12345a.mp3]");
  }

}
