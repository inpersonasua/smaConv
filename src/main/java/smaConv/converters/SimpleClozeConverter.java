package smaConv.converters;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;

import smaConv.util.AnkiCard;
import smaConv.util.CardStyles;
import smaConv.util.Deck;
import smaConv.util.Parser;
import smaConv.util.XmlParser;

/**
 * SimpleClozeConverter - gives moderate quality deck: worse than
 * {@link ClozeConverterFromExamples}. <br>
 * Drawbacks below:<br>
 * - didn't include synonyms<br>
 * - cloze question word often isn't in proper form, and sometimes there are more than one question
 * words for example:<br>
 * <i>Sentenece with more than one question [word, phraze, item].</i><br>
 *
 *
 */
class SimpleClozeConverter extends Converter {
  protected static final String FILES_TO_CONVERT = "//element/element/element/element[@subtype=\"3\"]/@id";

  private static final String SENTENCE = "/item/question";
  private static final String TRANSLATION = "/item/answer/text/translation[1]";
  private static final String CLOZE_WORD_PL = "/item/question/text/translation[1]";
  private static final String CLOZE_WORD_EN = "/item/question/droplist/option[@correct=\"true\"]/text()";

  private final XmlParser xmlParser = new XmlParser();

  @Override
  public Deck<AnkiCard> makeDeck(Parser smpakParser) {
    Deck<AnkiCard> deck = new Deck<>();

    List<String> filesToConvert = super.filesToConvert(smpakParser.getFile("course.xml"),
        FILES_TO_CONVERT);

    for (String file : filesToConvert) {
      deck.add(convertFile(smpakParser.getFile(file), file));
    }

    deck.setQuestionTemplate("{{cloze:sentence}}");
    deck.setAnswerTemplate("{{cloze:sentence}}<br>{{translation}}");
    deck.setStyle(CardStyles.DEFAULT_STYLE + CardStyles.DEFAULT_CLOZE_STYLE);
    return deck;
  }

  protected AnkiCard convertFile(byte[] xmlFile, String xmlFileName) {
    String translation = xmlParser.nodeToString(xmlFile, TRANSLATION);
    translation = Jsoup.parse(translation).text();

    String questionNodeString = xmlParser.nodeToString(xmlFile, SENTENCE);
    String wordEn = xmlParser.getValue(xmlFile, CLOZE_WORD_EN);
    String wordPl = xmlParser.nodeToString(xmlFile, CLOZE_WORD_PL);

    String sentence = makeCloze(questionNodeString, wordEn, wordPl);

    Map<String, String> question = new HashMap<>();
    Map<String, String> answer = new HashMap<>();
    question.put("sentence", sentence);
    answer.put("translation", translation);
    return new AnkiCard(question, answer);
  }

  protected String makeCloze(String questionNodeString, String wordEn, String wordPl) {
    String firstPart;
    String clozePart;
    String lastPart;

    firstPart = Arrays.stream(questionNodeString.split("\\r?\\n"))
        .filter(line -> line.contains("<droplist>")).findFirst().orElse("");
    firstPart = Jsoup.parse(firstPart).text();

    clozePart = " {{c1::" + Jsoup.parse(wordEn).text() + "::" + Jsoup.parse(wordPl).text() + "}} ";

    lastPart = Arrays.stream(questionNodeString.split("\\r?\\n"))
        .filter(line -> line.contains("</question>")).findFirst().orElse("");
    lastPart = Jsoup.parse(lastPart).text();

    String clozeSentence = firstPart + clozePart + lastPart;

    return clozeSentence;
  }

}