package smaConv.converters;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import smaConv.util.AnkiCard;
import smaConv.util.Deck;
import smaConv.util.SmpakParser;

public class ClozeConverterWithSoundsTest {
  Converter converter;
  SmpakParser smpakParser;
  Deck<AnkiCard> deck;

  String soundFieldForConverterFromExamples = "[sound:12345a.mp3]";
  String soundFieldForSimpleConverter = "[sound:12346a.mp3]";

  @Before
  public void setUp() {
    converter = new ClozeConverterWithSounds();
    smpakParser = mock(SmpakParser.class);
  }

  @Test
  public void converterFromExamplesShouldBeCalled() {
    when(smpakParser.getFile("course.xml"))
        .thenReturn(ClozeConverterFromExamplesTest.courseXml.getBytes());
    when(smpakParser.getFile(ClozeConverterFromExamplesTest.xmlFileName))
        .thenReturn(ClozeConverterFromExamplesTest.xmlFile.getBytes());

    deck = converter.makeDeck(smpakParser);

    assertThat(deck.get(0).getAnswer().get("sound"))
        .isEqualToIgnoringCase(soundFieldForConverterFromExamples);
  }

  @Test
  public void simpleConverterShouldBeCalled() {
    when(smpakParser.getFile("course.xml"))
        .thenReturn(SimpleClozeConverterTest.courseXml.getBytes());
    when(smpakParser.getFile(ClozeConverterFromExamplesTest.xmlFileName))
        .thenReturn(SimpleClozeConverterTest.xmlFile.getBytes());
    when(smpakParser.getFile(SimpleClozeConverterTest.xmlFileName))
        .thenReturn(SimpleClozeConverterTest.xmlFile.getBytes());

    deck = converter.makeDeck(smpakParser);

    assertThat(deck.get(0).getAnswer().get("sound"))
        .isEqualToIgnoringCase(soundFieldForSimpleConverter);
  }

}
