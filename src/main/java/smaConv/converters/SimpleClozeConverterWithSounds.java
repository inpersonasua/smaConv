package smaConv.converters;

import java.util.List;
import java.util.Map;

import smaConv.util.AnkiCard;
import smaConv.util.Deck;
import smaConv.util.Parser;

class SimpleClozeConverterWithSounds extends SimpleClozeConverter {

  @Override
  public Deck<AnkiCard> makeDeck(Parser smpakParser) {
    Deck<AnkiCard> deck = new Deck<>();

    List<String> filesToConvert = super.filesToConvert(smpakParser.getFile("course.xml"),
        FILES_TO_CONVERT);

    for (String file : filesToConvert) {
      deck.add(convertFile(smpakParser.getFile(file), file));
    }

    deck.setQuestionTemplate("{{cloze:sentence}}");
    deck.setAnswerTemplate("{{cloze:sentence}}{{sound}}<br>{{translation}}");
    return deck;
  }

  @Override
  protected AnkiCard convertFile(byte[] xmlFile, String xmlFileName) {
    AnkiCard card = super.convertFile(xmlFile, xmlFileName);

    String soundField = soundField(xmlFileName, "a");

    Map<String, String> answer = card.getAnswer();
    answer.put("sound", soundField);
    AnkiCard cardWithSound = new AnkiCard(card.getQuestion(), answer);

    return cardWithSound;
  }
}
