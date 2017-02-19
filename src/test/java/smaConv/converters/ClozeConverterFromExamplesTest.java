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

public class ClozeConverterFromExamplesTest {
  ClozeConverterFromExamples converter;
  Parser smpakParser;

  public static String courseXml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n"//
      + "<course xmlns=\"http://www.supermemo.net/2006/smux\">\r\n" //
      + "  <title>Title</title>\r\n"//
      + "  <element>\r\n" //
      + "    <element>\r\n" //
      + "      <element>\r\n"//
      + "        <element id=\"12345\" type=\"exercise\" subtype=\"2\" name=\"\" keywords=\"\" partOfSpeech=\"\" frequency=\"1\" />\r\n"//
      + "        <element id=\"12346\" type=\"exercise\" subtype=\"3\" name=\"\" keywords=\"\" partOfSpeech=\"\" frequency=\"1\" />\r\n"//
      + "      </element>\r\n" //
      + "    </element>\r\n" //
      + "   </element>\r\n" //
      + "</course>";

  public static String xmlFileName = "item12345.xml";
  public static String xmlFile = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n"//
      + "<item xmlns=\"http://www.supermemo.net/2006/smux\">\r\n" //
      + "  <parts>\r\n"//
      + "    <part name=\"example\" type=\"text\">Sentence with [question] in brackets.</part>\r\n"//
      + "    <part name=\"example\" type=\"translation_pl\">Zdanie z [pytaniem] w nawiasach.</part>\r\n"//
      + "    <part name=\"synonyms\" type=\"text\">synonim</part>\r\n"//
      + "    <part name=\"example\" type=\"sfx_id\">a</part>\r\n"//
      + "  </parts>\r\n"//
      + "</item>";//
  public static String wrongXmlFile = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n"//
      + "<item xmlns=\"http://www.supermemo.net/2006/smux\">\r\n" //
      + "  <parts>\r\n"//
      + "    <part name=\"example\" type=\"text\">Sentence with question in brackets.</part>\r\n"//
      + "    <part name=\"example\" type=\"translation_pl\">Zdanie z [pytaniem] w nawiasach.</part>\r\n"//
      + "    <part name=\"synonyms\" type=\"text\">synonim</part>\r\n"//
      + "    <part name=\"example\" type=\"sfx_id\">a</part>\r\n"//
      + "  </parts>\r\n"//
      + "</item>";//

  public static String expectedQuestion = "Sentence with {{c1::question::pytaniem}} in brackets.";
  public static String expectedAnswer = "Zdanie z pytaniem w nawiasach.";

  @Before
  public void setUp() {
    smpakParser = mock(SmpakParser.class);
    when(smpakParser.getFile("course.xml")).thenReturn(courseXml.getBytes());
    when(smpakParser.getFile(xmlFileName)).thenReturn(xmlFile.getBytes());

    converter = new ClozeConverterFromExamples();
    converter.convertFile(xmlFile.getBytes(), xmlFileName);
  }

  @Test
  public void checkTemplates() {
    Deck<AnkiCard> deck = converter.makeDeck(smpakParser);
    assertThat(deck.getQuestionTemplate()).isEqualTo("{{cloze:sentence}}<br>{{synonyms}}");
    assertThat(deck.getAnswerTemplate())
        .isEqualTo("{{cloze:sentence}}<br>{{translation}}<br>{{synonyms}}");
  }

  @Test
  public void checkCardFields() {
    Deck<AnkiCard> deck = converter.makeDeck(smpakParser);
    assertThat(deck.get(0).getQuestion().get("sentence")).isEqualTo(expectedQuestion);
    assertThat(deck.get(0).getAnswer().get("translation")).isEqualTo(expectedAnswer);
  }

  @Test
  public void cardShouldNotBeAddedWhenExampleHaveNoBrackets() {
    when(smpakParser.getFile(xmlFileName)).thenReturn(wrongXmlFile.getBytes());

    Deck<AnkiCard> deck = converter.makeDeck(smpakParser);

    assertThat(deck).isEmpty();
  }
}
