package smaConv.converters;

import smaConv.util.AnkiCard;
import smaConv.util.Deck;
import smaConv.util.Parser;

public class ClozeConverterWithSounds extends ClozeConverter {
  @Override
  public Deck<AnkiCard> makeDeck(Parser smpakParser) {
    Converter converter;
    if (super.hasExamples(smpakParser)) {
      converter = new ClozeConverterFromExamplesWithSound();
    } else {
      converter = new SimpleClozeConverterWithSounds();
    }

    return converter.makeDeck(smpakParser);
  }
}
