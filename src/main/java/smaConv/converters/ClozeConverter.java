package smaConv.converters;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import smaConv.util.AnkiCard;
import smaConv.util.Deck;
import smaConv.util.Parser;
import smaConv.util.XmlParser;

public class ClozeConverter extends Converter {
  private static String FILES_TO_CONVERT = "//element/element/element/element[@subtype=\"2\"]/@id";

  private static String FRONT = "//item/parts/part[@name=\"example\" and @type=\"text\"]/text()";
  private static String BACK = "//item/parts/part[@name=\"example\" and @type=\"translation_pl\"]/text()";
  private static String SYNONYMS = "//item/parts/part[@name=\"synonyms\" and @type=\"text\"]/text()";

  private final XmlParser xmlParser = new XmlParser();

  @Override
  public Deck<AnkiCard> makeDeck(Parser smpakParser) {
    Deck<AnkiCard> deck = new Deck<>();

    List<String> filesToConvert = super.filesToConvert(smpakParser.getFile("course.xml"),
        FILES_TO_CONVERT);

    for (String file : filesToConvert) {
      deck.add(convert(smpakParser.getFile(file), file));
    }

    deck.setQuestionTemplate("{{cloze:sentence}}<br>{{synonyms}}");
    deck.setAnswerTemplate("{{cloze:sentence}}<br>{{translation}}<br>{{synonyms}}");
    return deck;
  }

  protected AnkiCard convert(byte[] xmlFile, String xmlFileName) {
    Map<String, String> question = new HashMap<>();
    Map<String, String> answer = new HashMap<>();

    String frontText = xmlParser.getValue(xmlFile, FRONT);
    String backText = xmlParser.getValue(xmlFile, BACK);

    String[] clozeString = cloze(frontText, backText);

    String synonyms = xmlParser.getValue(xmlFile, SYNONYMS);

    question.put("sentence", clozeString[0]);
    answer.put("translation", clozeString[1]);
    answer.put("synonyms", synonyms);
    return new AnkiCard(question, answer);
  }

  protected String[] cloze(String sentence, String translation) {
    if (sentence.contains("[") && translation.contains("[")) {
      sentence = sentence.replaceFirst("\\[", "{{c1::").replaceFirst("\\]", "::"
          + translation.substring(translation.indexOf('[') + 1, translation.indexOf(']')) + "}}");

      translation = translation.replace("[", "").replace("]", "");
    }
    return new String[] { sentence, translation };
  }
}