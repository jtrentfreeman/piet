package com.frejt.piet.reader;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import java.nio.file.Paths;

import com.frejt.piet.entity.Board;
import com.frejt.piet.entity.Codel;
import com.frejt.piet.exception.FileNotReadException;
import com.frejt.piet.util.Color;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class FileReaderTest {

    private static Board expected;

    @BeforeAll
    public static void beforeAll() {

        expected = new Board(4, 6);

        expected.setColor(new Codel(0, 0), Color.LIGHT_RED);
        expected.setColor(new Codel(0, 1), Color.LIGHT_YELLOW);
        expected.setColor(new Codel(0, 2), Color.LIGHT_GREEN);
        expected.setColor(new Codel(0, 3), Color.LIGHT_CYAN);
        expected.setColor(new Codel(0, 4), Color.LIGHT_BLUE);
        expected.setColor(new Codel(0, 5), Color.LIGHT_MAGENTA);

        expected.setColor(new Codel(1, 0), Color.RED);
        expected.setColor(new Codel(1, 1), Color.YELLOW);
        expected.setColor(new Codel(1, 2), Color.GREEN);
        expected.setColor(new Codel(1, 3), Color.CYAN);
        expected.setColor(new Codel(1, 4), Color.BLUE);
        expected.setColor(new Codel(1, 5), Color.MAGENTA);

        expected.setColor(new Codel(2, 0), Color.DARK_RED);
        expected.setColor(new Codel(2, 1), Color.DARK_YELLOW);
        expected.setColor(new Codel(2, 2), Color.DARK_GREEN);
        expected.setColor(new Codel(2, 3), Color.DARK_CYAN);
        expected.setColor(new Codel(2, 4), Color.DARK_BLUE);
        expected.setColor(new Codel(2, 5), Color.DARK_MAGENTA);

        expected.setColor(new Codel(3, 0), Color.WHITE);
        expected.setColor(new Codel(3, 1), Color.WHITE);
        expected.setColor(new Codel(3, 2), Color.WHITE);

        expected.setColor(new Codel(3, 3), Color.BLACK);
        expected.setColor(new Codel(3, 4), Color.BLACK);
        expected.setColor(new Codel(3, 5), Color.BLACK);

    }

    /**
     * Tests that, when the path to a PPM file is provided to
     * {@link FileReader#getFileType()},
     * the {@link ContentType#PPM} enum is returned.
     */
    @Test
    public void getFileType_PPMFile_ReturnsPPM() throws FileNotReadException {

        Path path = Paths
                .get("C:\\Users\\frejt\\code\\newPiet\\app\\src\\test\\resources\\com\\frejt\\piet\\reader\\Test.ppm");
        FileReader reader = new FileReader(path);

        ContentType expected = ContentType.PPM;

        ContentType actual = reader.getFileType();

        assertEquals(expected, actual);

    }

    /**
     * Tests that, when the path to a PNG file is provided to
     * {@link FileReader#getFileType()},
     * the {@link ContentType#PNG} enum is returned.
     */
    @Test
    public void getFileType_PNGFile_ReturnsPNG() throws FileNotReadException {

        Path path = Paths
                .get("C:\\Users\\frejt\\code\\newPiet\\app\\src\\test\\resources\\com\\frejt\\piet\\reader\\Test.png");
        FileReader reader = new FileReader(path);

        ContentType expected = ContentType.PNG;

        ContentType actual = reader.getFileType();

        assertEquals(expected, actual);

    }

    /**
     * Tests that, when the path to a non-supported file type is provided to
     * {@link FileReader#getFileType()}, an exception is thrown.
     */
    @Test
    public void getFileType_Unsupported_ThrowsException() {

        Path path = Paths
                .get("C:\\Users\\frejt\\code\\newPiet\\app\\src\\test\\resources\\com\\frejt\\piet\\reader\\Test.jpg");
        FileReader reader = new FileReader(path);

        Assertions.assertThrows(FileNotReadException.class, () -> {
            reader.getFileType();
        });

    }

    /**
     * Tests that, when a PPM file is being read, all of the colours can be
     * accurately converted and read to a Board.
     */
    @Test
    public void convertFileToBoard_PPMFile_ReturnsAccurateBoard() throws FileNotReadException {

        Path path = Paths
                .get("C:\\Users\\frejt\\code\\newPiet\\app\\src\\test\\resources\\com\\frejt\\piet\\reader\\Test.ppm");

        FileReader reader = new FileReader(path);

        Board actual = reader.convertFileToBoard();

        assertEquals(expected.toString(), actual.toString());

    }

    /**
     * Tests that, when PNG file is being read, all of the colours can be accurately
     * converted and read to a Board.
     */
    @Test
    public void convertFileToBoard_PNGFile_ReturnsAccurateBoard() throws FileNotReadException {

        Path path = Paths
                .get("C:\\Users\\frejt\\code\\newPiet\\app\\src\\test\\resources\\com\\frejt\\piet\\reader\\Test.png");

        FileReader reader = new FileReader(path);

        Board actual = reader.convertFileToBoard();

        assertEquals(expected.toString(), actual.toString());

    }

}
