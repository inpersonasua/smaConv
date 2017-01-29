package smaConv.converters;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import smaConv.converters.ClozeWithSoundConverter;
import smaConv.util.AnkiCard;
import smaConv.util.Deck;
import smaConv.util.Parser;
import smaConv.util.SmpakParser;

public class ClozeWithSoundConverterTest {
  ClozeWithSoundConverter converter;
  Parser smpakParser;

  String expectedQuestion = "Sentence with {{c1::question::pytaniem}} in brackets.";
  String expectedAnswer = "Zdanie z pytaniem w nawiasach.";

  @Before
  public void setUp() {
    converter = new ClozeWithSoundConverter();
    smpakParser = mock(SmpakParser.class);
    when(smpakParser.getFile("course.xml")).thenReturn(ClozeConverterTest.courseXml.getBytes());
    when(smpakParser.getFile(ClozeConverterTest.xmlFileName))
        .thenReturn(ClozeConverterTest.xmlFile.getBytes());
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
    assertThat(deck.get(0).getQuestion().get("sentence")).isEqualTo(expectedQuestion);
    assertThat(deck.get(0).getAnswer().get("translation")).isEqualTo(expectedAnswer);
    assertThat(deck.get(0).getAnswer().get("sound")).isEqualTo("[sound:12345a.mp3]");
  }
}
