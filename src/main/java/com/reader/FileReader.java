package com.reader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

import javax.imageio.ImageIO;

import com.entity.Board;
import com.entity.Codel;
import com.entity.Metadata;
import com.exception.ColorNotFoundException;
import com.exception.ContentTypeNotFoundException;
import com.util.Color;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.awt.image.BufferedImage;


public class FileReader {

    private static final Logger log = LoggerFactory.getLogger(FileReader.class);

    private Path path;

    public FileReader(Path path) {
        this.path = path;
    }

    public Board readFile() {
        Board board = null;
        ContentType contentType = null;

        try {
            contentType = ContentType.getContentType(Files.probeContentType(path));
        } catch (IOException e) {

        } catch (ContentTypeNotFoundException e) {

        }
        
        switch(contentType) {
            case PPM:
                return readPpm(this.path);
            case PNG:
                return readPng(this.path);
            default:
                break;
        }

        return board;
    }

    /**
	 * Takes in a .ppm file and converts it into a {@link Board}
	 * @param path - path to the file to be read
	 */
	public static Board readPpm(Path path) {
		Board board;

		try (Scanner sc = new Scanner(path.toFile())) {
			log.debug(Files.probeContentType(path));

			Metadata meta = new Metadata();
			meta.setMagicNumber(sc.next());
			meta.setColumn(sc.nextInt());
			meta.setRow(sc.nextInt());
			meta.setMaxVaL(sc.nextInt());

			board = new Board(meta.getRow(), meta.getColumn());

			for(int i = 0; i < meta.getRow(); i++) {
				for(int j = 0; j < meta.getColumn(); j++) {
					Integer red, blue, green;
					
					red = sc.nextInt();
					blue = sc.nextInt();
					green = sc.nextInt();

					try {
						Color color = Color.getColorFromValues(red, blue, green);
						Codel coordinate = new Codel(i, j);
						board.setColor(coordinate, color);
					} catch(ColorNotFoundException e) {
						e.printStackTrace();
					}						
				}
			}

			return board;
		} catch (FileNotFoundException e) {
			log.info("???");
			log.info(e.toString());
			System.exit(0);
			return null;
		} catch (IOException e) {
			System.exit(0);
			return null;
		}
	}

	public static Board readPng(Path path) {
		Board board;
		
		try {
			BufferedImage image = ImageIO.read(path.toFile());
			image.getWidth();
			image.getHeight();
			board = new Board(image.getHeight(), image.getWidth());
			for(int i = 0; i < image.getHeight(); i++) {
				for(int j = 0; j < image.getWidth(); j++) {
					Integer red, blue, green;
					Integer clr = image.getRGB(j, i);
					red = (clr & 0x00ff0000) >> 16;
					green = (clr & 0x0000ff00) >> 8;
					blue = clr & 0x000000ff;

					try {
						Color color = Color.getColorFromValues(red, green, blue);
						Codel coordinate = new Codel(i, j);
						board.setColor(coordinate, color);
					} catch(ColorNotFoundException e) {
						e.printStackTrace();
					}

				}
			}
			image.getRGB(0, 0);
			return board;
		} catch(IOException e) {
			log.debug(e + "");
		}
		return null;
	}

}