package com.frejt.piet.reader;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

import javax.imageio.ImageIO;

import com.frejt.piet.entity.Board;
import com.frejt.piet.entity.Codel;
import com.frejt.piet.entity.PPMMetadata;
import com.frejt.piet.exception.ColorNotFoundException;
import com.frejt.piet.exception.ContentTypeNotFoundException;
import com.frejt.piet.exception.FileNotReadException;
import com.frejt.piet.util.Color;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class dedicated to reading a Piet file.
 */
public class FileReader {

	private static final Logger log = LogManager.getLogger(FileReader.class);

	/**
	 * A path to a Piet file.
	 */
	private Path path;

	public FileReader(Path path) {
		this.path = path;
	}

	/**
	 * Converts a file of a known {@link ContentType} into a {@link Board}.
	 * 
	 * @return a Board representing the {@link #path}s file.
	 * @throws FileNotReadException if the file was, for any reason, not able to be read
	 */
	public Board convertFileToBoard() throws FileNotReadException {

		ContentType contentType = getFileType();

		switch (contentType) {
			case PPM:
				return readPpm();
			case PNG:
				return readPng();
			default:
				break;
		}

		return null;
	}

	/**
	 * Gets the {@link ContentType} of the file this reader is reading.
	 * 
	 * @return the appropriate {@link ContentType}
	 * @throws FileNotReadException if the file was, for any reason, not able to be read
	 */
	public ContentType getFileType() throws FileNotReadException {

		ContentType contentType = null;

		try {
			contentType = ContentType.getContentType(Files.probeContentType(path));
		} catch (IOException e) {
			throw new FileNotReadException("Could not read file: " + e.getMessage());
		} catch (ContentTypeNotFoundException e) {
			throw new FileNotReadException("Could not read file: " + e.getMessage());
		}

		return contentType;

	}

	/**
	 * Takes in a {@link ContentType#PPM} file and converts it into a {@link Board}
	 * 
	 * @return a {@link Board} representing the file
	 */
	private Board readPpm() throws FileNotReadException {
		Board board;

		try (Scanner sc = new Scanner(path.toFile())) {
			log.debug(Files.probeContentType(path));

			PPMMetadata meta = new PPMMetadata();
			meta.setMagicNumber(sc.next());
			meta.setColumn(sc.nextInt());
			meta.setRow(sc.nextInt());
			meta.setMaxVaL(sc.nextInt());

			board = new Board(meta.getRow(), meta.getColumn());

			for (int i = 0; i < meta.getRow(); i++) {
				for (int j = 0; j < meta.getColumn(); j++) {
					Integer red, blue, green;

					// TODO: read line and remove everything after "#"

					red = sc.nextInt();
					blue = sc.nextInt();
					green = sc.nextInt();

					try {
						Color color = Color.getColorFromValues(red, blue, green);
						Codel coordinate = new Codel(i, j);
						board.setColor(coordinate, color);
					} catch (ColorNotFoundException e) {
						e.printStackTrace();
					}
				}
			}

			return board;
		} catch (FileNotFoundException e) {
			throw new FileNotReadException(e.getMessage());
		} catch (IOException e) {
			throw new FileNotReadException(e.getMessage());
		}
	}

	/**
	 * Takes in a {@link ContentType#PPM} file and converts it into a {@link Board}
	 * 
	 * @return a {@link Board} representing the file
	 */
	private Board readPng() throws FileNotReadException {
		Board board;

		try {
			BufferedImage image = ImageIO.read(new FileInputStream(path.toFile()));
			board = new Board(image.getHeight(), image.getWidth());
			for (int i = 0; i < image.getHeight(); i++) {
				for (int j = 0; j < image.getWidth(); j++) {
					Integer clr = image.getRGB(j, i);
					Integer red = (clr & 0x00ff0000) >> 16;
					Integer green = (clr & 0x0000ff00) >> 8;
					Integer blue = clr & 0x000000ff;

					try {
						Color color = Color.getColorFromValues(red, green, blue);
						Codel coordinate = new Codel(i, j);
						board.setColor(coordinate, color);
					} catch (ColorNotFoundException e) {
						log.error("Could not get color at index [" + i + ", " + j + "])", e.getMessage());
					}

				}
			}
			return board;
		} catch (IOException e) {
			throw new FileNotReadException(e.getMessage());
		}
	}

}