import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

interface Content {
    String read() throws IOException;

    void write(String content) throws IOException;
}

final class FileContent implements Content {
    private final Path file;

    public FileContent(final Path file) {
        this.file = file;
    }

    @Override
    public String read() throws IOException {
        return Files.readString(this.file);
    }

    @Override
    public void write(final String content) throws IOException {
        Files.writeString(this.file, content);
    }
}

final class AsciiOnlyContent implements Content {
    private final Content content;

    public AsciiOnlyContent(final Content content) {
        this.content = content;
    }

    @Override
    public String read() throws IOException {
        return this.content.read().replaceAll("[^\\x00-\\x7F]", "");
    }

    @Override
    public void write(final String content) throws IOException {
        this.content.write(content.replaceAll("[^\\x00-\\x7F]", ""));
    }
}

final class ContentTest {
    @Test
    void withUnicode() throws IOException {
        final var file = Files.createTempFile("temp", ".txt");
        final var p = new FileContent(file);
        Files.writeString(file, "привет!!!");
        Assertions.assertEquals(p.read(), "привет!!!");
    }

    @Test
    void writesAndReadsContentAndIgnoresUnicode() throws IOException {
        final var p =
            new AsciiOnlyContent(
                new FileContent(Files.createTempFile("temp", ".txt")
                )
            );
        p.write("Привет!!!");
        Assertions.assertEquals(p.read(), "!!!");
    }

    @Test
    void readsContent() throws IOException {
        final var file = Files.createTempFile("temp", ".txt");
        final var p = new FileContent(file);
        Files.writeString(file, "Hi there");
        Assertions.assertEquals(p.read(), "Hi there");
    }
}
