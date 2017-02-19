package smaConv.converters;

import java.util.List;

import smaConv.util.AnkiCard;
import smaConv.util.Deck;
import smaConv.util.Parser;
import smaConv.util.XmlParser;

/**
 * Makes Deck of AnkiCard's as a cloze deletion type
 * (<a href="https://apps.ankiweb.net/docs/manual.html#cloze">see Anki SRS online
 * documentation</a>).
 *
 */
public class ClozeConverter extends Converter {
  private final XmlParser xmlParser = new XmlParser();

  @Override
  public Deck<AnkiCard> makeDeck(Parser smpakParser) {
    Converter converter;
    if (hasExamples(smpakParser)) {
      converter = new ClozeConverterFromExamples();
    } else {
      converter = new SimpleClozeConverter();
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
    String filesExpression = "//element/element/element/element[@subtype=\"2\"]/@id";
    List<String> filesToConvert = super.filesToConvert(smpakParser.getFile("course.xml"),
        filesExpression);

    byte[] file = smpakParser.getFile(filesToConvert.get(0));

    String exampleSentence = "//item/parts/part[@name=\"example\" and @type=\"text\"]/text()";
    String exampleTranslation = "//item/parts/part[@name=\"example\" and @type=\"translation_pl\"]/text()";
    if (xmlParser.getValue(file, exampleSentence).isEmpty()
        || xmlParser.getValue(file, exampleTranslation).isEmpty()) {
      return false;
    } else {
      return true;
    }
  }
}
