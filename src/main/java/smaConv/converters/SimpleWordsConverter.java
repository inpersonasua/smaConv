package smaConv.converters;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;

import smaConv.util.AnkiCard;
import smaConv.util.Deck;
import smaConv.util.Parser;
import smaConv.util.XmlParser;

/**
 * Converts SupermemoUX vocabulary course to anki.
 *
 */
class SimpleWordsConverter extends Converter {

  protected static final String FILES_TO_CONVERT = "//element/element/element/element[@subtype=\"5\"]/@id";

  private static final String WORD_EN = "/item/answer/text/sentence[1]";
  private static final String WORD_PL = "/item/answer/text/translation[1]";

  protected XmlParser xmlParser = new XmlParser();

  @Override
  public Deck<AnkiCard> makeDeck(Parser smpakParser) {
    Deck<AnkiCard> deck = new Deck<>();

    List<String> filesToConvert = super.filesToConvert(smpakParser.getFile("course.xml"),
        FILES_TO_CONVERT);

    for (String file : filesToConvert) {
      deck.add(convertFile(smpakParser.getFile(file), file));
    }

    deck.setQuestionTemplate("{{front}}");
    deck.setAnswerTemplate("{{back}}");
    return deck;
  }

  /**
   * Converts xml file extracted from smpak to AnkiCard.
   *
   * @param xmlFile
   *          - xml file content as byte array.
   * @param xmlFileName
   *          - full name of xml file (e.g., "item01234.xml").
   * @return AnkiCard of null if xml file don't have necessary parts.
   */
  protected AnkiCard convertFile(byte[] xmlFile, String xmlFileName) {
    Map<String, String> question = new HashMap<>();
    Map<String, String> answer = new HashMap<>();

    String wordPl = xmlParser.nodeToString(xmlFile, WORD_PL);
    String wordEn = xmlParser.nodeToString(xmlFile, WORD_EN);

    question.put("front", Jsoup.parse(wordPl).text());
    answer.put("back", Jsoup.parse(wordEn).text());
    return new AnkiCard(question, answer);
  }
}
