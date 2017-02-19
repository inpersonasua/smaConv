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

public class WordsFromExamplesConverterTest {
  String FILES_TO_CONVERT = "//element/element/element/element[@subtype=\"5\"]/@id";
  Converter converter;
  Parser smpakParser;
  public static String xmlFileName = "item12345.xml";
  public static String xmlFile = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n"//
      + "<item>\r\n" //
      + "  <parts>\r\n"//
      + "    <part name=\"phrase\" type=\"text\">answer</part>\r\n"//
      + "    <part name=\"phrase\" type=\"translation_pl\">question</part>\r\n"//
      + "    <part name=\"phrase\" type=\"sfx_id\">a</part>" //
      + "  </parts>\r\n"//
      + "</item>";

  public static String courseXml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n"//
      + "<course xmlns=\"http://www.supermemo.net/2006/smux\">\r\n" + "  <title>Title</title>\r\n"//
      + "  <element>\r\n" //
      + "    <element>\r\n" //
      + "      <element>\r\n"//
      + "        <element id=\"12345\" type=\"exercise\" subtype=\"5\" name=\"\" keywords=\"\" partOfSpeech=\"\" frequency=\"1\" />\r\n"//
      + "      </element>\r\n" //
      + "    </element>\r\n" //
      + "   </element>\r\n" //
      + "</course>";

  @Before
  public void setUp() {
    converter = new WordsFromExamplesConverter();
    smpakParser = mock(SmpakParser.class);
    when(smpakParser.getFile("course.xml")).thenReturn(courseXml.getBytes());
    when(smpakParser.getFile(xmlFileName)).thenReturn(xmlFile.getBytes());
  }

  @Test
  public void correctXmlFileNameShouldBeObtainedFromElementId() {
    assertThat(converter.filesToConvert(courseXml.getBytes(), FILES_TO_CONVERT))
        .contains(xmlFileName);
  }

  @Test
  public void deckShouldContainOneCard() {
    assertThat(converter.makeDeck(smpakParser)).hasSize(1);
  }

  @Test
  public void checkTemplates() {
    Deck<AnkiCard> deck = converter.makeDeck(smpakParser);
    assertThat(deck.getQuestionTemplate()).isEqualTo("{{front}}");
    assertThat(deck.getAnswerTemplate()).isEqualTo("{{back}}");
  }

  @Test
  public void checkCardFields() {
    Deck<AnkiCard> deck = converter.makeDeck(smpakParser);
    assertThat(deck.get(0).getQuestion().get("front")).isEqualTo("question");
    assertThat(deck.get(0).getAnswer().get("back")).isEqualTo("answer");
  }
}
