package smaConv.converters;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import smaConv.util.AnkiCard;
import smaConv.util.Deck;
import smaConv.util.Parser;
import smaConv.util.XmlParser;

class WordsFromExamplesConverter extends Converter {

  private static String FILES_TO_CONVERT = "//element/element/element/element[@subtype=\"5\"]/@id";

  private static String BACK = "//item/parts/part[@name=\"phrase\" and @type=\"text\"]/text()";
  private static String FRONT = "//item/parts/part[@name=\"phrase\" and @type=\"translation_pl\"]/text()";

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
    question.put("front", xmlParser.getValue(xmlFile, FRONT));
    answer.put("back", xmlParser.getValue(xmlFile, BACK));
    return new AnkiCard(question, answer);
  }
}