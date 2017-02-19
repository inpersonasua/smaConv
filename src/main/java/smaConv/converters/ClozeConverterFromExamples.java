package smaConv.converters;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import smaConv.util.AnkiCard;
import smaConv.util.Deck;
import smaConv.util.Parser;
import smaConv.util.XmlParser;

/**
 * Preferable ClozeConverter - fast and gives the best quality deck.
 *
 */
class ClozeConverterFromExamples extends Converter {
  protected static final String FILES_TO_CONVERT = "//element/element/element/element[@subtype=\"2\"]/@id";

  private static final String EXAMPLE = "//item/parts/part[@name=\"example\" and @type=\"text\"]/text()";
  private static final String TRANSLATION = "//item/parts/part[@name=\"example\" and @type=\"translation_pl\"]/text()";
  private static final String SYNONYMS = "//item/parts/part[@name=\"synonyms\" and @type=\"text\"]/text()";

  private final XmlParser xmlParser = new XmlParser();

  private static final Pattern clozePattern = Pattern.compile(".*\\[(.+?)\\].*");

  @Override
  public Deck<AnkiCard> makeDeck(Parser smpakParser) {
    Deck<AnkiCard> deck = new Deck<>();

    List<String> filesToConvert = super.filesToConvert(smpakParser.getFile("course.xml"),
        FILES_TO_CONVERT);

    for (String file : filesToConvert) {
      AnkiCard card = convertFile(smpakParser.getFile(file), file);
      if (card != null) {
        deck.add(card);
      }
    }

    deck.setQuestionTemplate("{{cloze:sentence}}<br>{{synonyms}}");
    deck.setAnswerTemplate("{{cloze:sentence}}<br>{{translation}}<br>{{synonyms}}");
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
  public AnkiCard convertFile(byte[] xmlFile, String xmlFileName) {
    Map<String, String> question = new HashMap<>();
    Map<String, String> answer = new HashMap<>();

    String exampleText = xmlParser.getValue(xmlFile, EXAMPLE);
    String translationText = xmlParser.getValue(xmlFile, TRANSLATION);
    String synonyms = xmlParser.getValue(xmlFile, SYNONYMS);

    Matcher exampleMatcher = clozePattern.matcher(exampleText);
    Matcher translationMatcher = clozePattern.matcher(translationText);

    if (exampleMatcher.matches() && translationMatcher.matches()) {
      exampleText = exampleText.replaceFirst("\\[.+?\\]",
          "{{c1::" + exampleMatcher.group(1) + "::" + translationMatcher.group(1) + "}}");

      translationText = translationText.replaceFirst("\\[", "").replaceFirst("\\]", "");
      question.put("sentence", exampleText);
      answer.put("translation", translationText);
      answer.put("synonyms", synonyms);
      return new AnkiCard(question, answer);
    } else {
      return null;
    }
  }
}
