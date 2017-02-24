package smaConv.converters;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import smaConv.util.AnkiCard;
import smaConv.util.Deck;
import smaConv.util.SmpakParser;

public class WordsConverterTest {
  Converter converter;
  SmpakParser smpakParser;
  Deck<AnkiCard> deck;

  String exampleQuestion = "example question";
  String simpleQuestion = "simple question";

  @Before
  public void setUp() {
    converter = new WordsConverter();
    smpakParser = mock(SmpakParser.class);
  }

  @Test
  public void converterFromExamplesShouldBeCalled() {
    when(smpakParser.getFile("course.xml"))
        .thenReturn(WordsFromExamplesConverterTest.courseXml.getBytes());
    when(smpakParser.getFile(WordsFromExamplesConverterTest.xmlFileName))
        .thenReturn(WordsFromExamplesConverterTest.xmlFile.getBytes());

    deck = converter.makeDeck(smpakParser);

    assertThat(deck.get(0).getQuestion().get("front")).isEqualToIgnoringCase(exampleQuestion);
  }

  @Test
  public void simpleConverterShouldBeCalled() {
    when(smpakParser.getFile("course.xml"))
        .thenReturn(SimpleWordsConverterTest.courseXml.getBytes());
    when(smpakParser.getFile(SimpleWordsConverterTest.xmlFileName))
        .thenReturn(SimpleWordsConverterTest.xmlFile.getBytes());
    when(smpakParser.getFile(SimpleWordsConverterTest.xmlFileName))
        .thenReturn(SimpleWordsConverterTest.xmlFile.getBytes());

    deck = converter.makeDeck(smpakParser);

    assertThat(deck.get(0).getQuestion().get("front")).isEqualToIgnoringCase(simpleQuestion);
  }

}
