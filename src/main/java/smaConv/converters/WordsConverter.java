package smaConv.converters;

import java.util.List;

import smaConv.util.AnkiCard;
import smaConv.util.Deck;
import smaConv.util.Parser;
import smaConv.util.XmlParser;

/**
 * Makes Deck of AnkiCard's. Resulting cards have front and back fields: front field is a Polish
 * word and back field is an English word.
 */
public class WordsConverter extends Converter {
  private final XmlParser xmlParser = new XmlParser();

  @Override
  public Deck<AnkiCard> makeDeck(Parser smpakParser) {
    Converter converter;
    if (hasExamples(smpakParser)) {
      converter = new WordsFromExamplesConverter();
    } else {
      converter = new SimpleWordsConverter();
    }

    return converter.makeDeck(smpakParser);
  }

  /**
   * Check if Parser contains units with example sentences.
   *
   * @param smpakParser
   * @return true when Parser xml units have example sentences
   */
  protected boolean hasExamples(Parser smpakParser) {
    String filesExpression = "//element/element/element/element[@subtype=\"5\"]/@id";
    List<String> filesToConvert = super.filesToConvert(smpakParser.getFile("course.xml"),
        filesExpression);

    byte[] file = smpakParser.getFile(filesToConvert.get(0));

    String word = "//item/parts/part[@name=\"phrase\" and @type=\"translation_pl\"]/text()";
    if (xmlParser.getValue(file, word).isEmpty()) {
      return false;
    } else {
      return true;
    }
  }
}