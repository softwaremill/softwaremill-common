package pl.softwaremill.common.util.io;

import org.testng.annotations.Test;

import java.io.File;

import static org.fest.assertions.Assertions.*;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class RichFileTest {
    @Test
    public void getNameWithoutExtensionTwoDots() {
        assertThat(new RichFile(new File("/dir/a.b.txt")).getNameWithoutExtension()).isEqualTo("a.b");
    }

    @Test
    public void getNameWithoutExtensionOneDot() {
        assertThat(new RichFile(new File("/dir/nesteddir/filename.JPEG")).getNameWithoutExtension()).isEqualTo("filename");
    }

    @Test
    public void getNameWithoutExtensionNoDots() {
        assertThat(new RichFile(new File("/dir/nesteddir/filename")).getNameWithoutExtension()).isEqualTo("filename");
    }

    @Test
    public void createFileSameNameDifferentExtension() {
        // Given
        File testFile = new File("/some/path/file.txt");

        // When
        String newExtension = "jpg";
        File result = new RichFile(testFile).createFileSameNameDifferentExtension(newExtension);

        // Then
        assertThat(result.getAbsolutePath()).matches("[A-Z]:\\\\some\\\\path\\\\file\\.jpg|/some/path/file\\.jpg");
    }

    @Test
    public void getExtensionTwoDots() {
        assertThat(new RichFile(new File("/dir/nesteddir/filename.JPEG")).getExtension()).isEqualTo("JPEG");
    }

    @Test
    public void getExtensionOneDot() {
        assertThat(new RichFile(new File("/dir/a.b.txt")).getExtension()).isEqualTo("txt");
    }

    @Test
    public void getExtensionNoDots() {
        assertThat(new RichFile(new File("/dir/nesteddir/filename")).getExtension()).isEqualTo("");
    }
}
