package smaConv.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;

import smaConv.util.Parser;
import smaConv.util.SmpakParser;

public class SmpakParserTest {
  // File for testing. Total number of files is 51 (most of them are templates
  // and resources).
  private static final String SMPAK_FILE = "src/test/resources/course.smpak";

  private Parser parser;

  @Before
  public void setUp() throws IOException {
    parser = new SmpakParser(FileChannel.open(Paths.get(SMPAK_FILE)));
    parser.parse();
  }

  @Test
  public void shouldBeSupermemoFile() {
    assertThat(parser.isSmpakFile()).isTrue();
  }

  @Test
  public void fileShouldBeFound() throws IOException {
    assertThat(parser.getFile("course.xml")).isNotEmpty();
  }

  @Test
  public void shouldReturnEmptyByteArrayWhenFileNotFound() throws IOException {
    assertThat(parser.getFile("no_such_file")).isEmpty();
  }
}
