package smaConv.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Path;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import smaConv.util.AnkiApkg;

public class AnkiApkgTest {
  AnkiApkg ankiApkg;

  @Rule
  public TemporaryFolder folder = new TemporaryFolder();
  Path apkgFile;

  @Before
  public void setUp() throws IOException {
    apkgFile = folder.newFile("file.apkg").toPath();
    ankiApkg = new AnkiApkg(apkgFile);
  }

  @Test
  public void fileShouldBeAdded() {
    assertThat(ankiApkg.addToArchive("someFile", "file content".getBytes())).isTrue();
  }

  @Test
  public void emptyFileShouldNotBeAdded() {
    assertThat(ankiApkg.addToArchive("filename", "".getBytes())).isFalse();
  }
}
