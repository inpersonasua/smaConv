package smaConv.converters;

import java.util.List;
import java.util.Map;

import smaConv.util.AnkiCard;
import smaConv.util.Deck;
import smaConv.util.Parser;
import smaConv.util.XmlParser;

public class ClozeWithSoundConverter extends ClozeConverter {
  private static String SOUND = "//item/parts/part[@name=\"example\" and @type=\"sfx_id\"]/text()";
  private static String FILES_TO_CONVERT = "//element/element/element/element[@subtype=\"2\"]/@id";
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
    deck.setAnswerTemplate("{{cloze:sentence}}{{sound}}<br>{{translation}}<br>{{synonyms}}");
    return deck;
  }

  @Override
  protected AnkiCard convert(byte[] xmlFile, String xmlFileName) {
    AnkiCard card = super.convert(xmlFile, xmlFileName);

    String soundField = soundField(xmlFileName, xmlParser.getValue(xmlFile, SOUND));

    Map<String, String> answer = card.getAnswer();
    answer.put("sound", soundField);
    AnkiCard cardWithSound = new AnkiCard(card.getQuestion(), answer);
    return cardWithSound;
  }
}
