package smaConv.converters;

import java.util.List;
import java.util.Map;

import smaConv.util.AnkiCard;
import smaConv.util.Deck;
import smaConv.util.Parser;
import smaConv.util.XmlParser;

/**
 * Preferable ClozeConverter - fast and gives the best quality deck. Additional adds sound field
 * (did'nt check if the sound file exists).
 */
class ClozeConverterFromExamplesWithSound extends ClozeConverterFromExamples {
  protected static final String SOUND = "//item/parts/part[@name=\"example\" and @type=\"sfx_id\"]/text()";
  protected static final String FILES_TO_CONVERT = "//element/element/element/element[@subtype=\"2\"]/@id";
  private final XmlParser xmlParser = new XmlParser();

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
    deck.setAnswerTemplate("{{cloze:sentence}}{{sound}}<br>{{translation}}<br>{{synonyms}}");
    return deck;
  }

  @Override
  public AnkiCard convertFile(byte[] xmlFile, String xmlFileName) {
    AnkiCard card = super.convertFile(xmlFile, xmlFileName);

    if (card != null) {
      String soundField = soundField(xmlFileName, xmlParser.getValue(xmlFile, SOUND));

      Map<String, String> answer = card.getAnswer();
      answer.put("sound", soundField);
      AnkiCard cardWithSound = new AnkiCard(card.getQuestion(), answer);
      return cardWithSound;
    } else {
      return null;
    }

  }
}
