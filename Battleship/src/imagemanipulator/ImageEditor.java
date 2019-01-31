package imagemanipulator;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageEditor {
	public static void main(String[] args) {
		// resizeImages();

		try {
			resizeImages();
			ImageIO.write(crop(ImageIO.read(ImageEditor.class.getResource("/destroyerThin.png")), 0, 0, 60, 50), "png",
					new File("destroyerOne.png"));
			ImageIO.write(crop(ImageIO.read(ImageEditor.class.getResource("/destroyerThin.png")), 60, 0, 60, 50), "png",
					new File("destroyerTwo.png"));
			ImageIO.write(crop(ImageIO.read(ImageEditor.class.getResource("/cruiserThin.png")), 0, 0, 60, 50), "png",
					new File("cruiserOne.png"));
			ImageIO.write(crop(ImageIO.read(ImageEditor.class.getResource("/cruiserThin.png")), 60, 0, 60, 50), "png",
					new File("cruiserTwo.png"));
			ImageIO.write(crop(ImageIO.read(ImageEditor.class.getResource("/cruiserThin.png")), 120, 0, 60, 50), "png",
					new File("cruiserThree.png"));
			ImageIO.write(crop(ImageIO.read(ImageEditor.class.getResource("/submarineThin.png")), 0, 0, 60, 50), "png",
					new File("submarineOne.png"));
			ImageIO.write(crop(ImageIO.read(ImageEditor.class.getResource("/submarineThin.png")), 0, 0, 60, 50), "png",
					new File("submarineOne.png"));
			ImageIO.write(crop(ImageIO.read(ImageEditor.class.getResource("/submarineThin.png")), 60, 0, 60, 50), "png",
					new File("submarineTwo.png"));
			ImageIO.write(crop(ImageIO.read(ImageEditor.class.getResource("/submarineThin.png")), 120, 0, 60, 50),
					"png", new File("submarineThree.png"));
			ImageIO.write(crop(ImageIO.read(ImageEditor.class.getResource("/battleshipThin.png")), 0, 0, 60, 50), "png",
					new File("battleshipOne.png"));
			ImageIO.write(crop(ImageIO.read(ImageEditor.class.getResource("/battleshipThin.png")), 60, 0, 60, 50),
					"png", new File("battleshipTwo.png"));
			ImageIO.write(crop(ImageIO.read(ImageEditor.class.getResource("/battleshipThin.png")), 120, 0, 60, 50),
					"png", new File("battleshipThree.png"));
			ImageIO.write(crop(ImageIO.read(ImageEditor.class.getResource("/battleshipThin.png")), 180, 0, 60, 50),
					"png", new File("battleshipFour.png"));
			ImageIO.write(crop(ImageIO.read(ImageEditor.class.getResource("/aircraftcarrierThin.png")), 0, 0, 60, 50),
					"png", new File("aircraftcarrierOne.png"));
			ImageIO.write(crop(ImageIO.read(ImageEditor.class.getResource("/aircraftcarrierThin.png")), 60, 0, 60, 50),
					"png", new File("aircraftcarrierTwo.png"));
			ImageIO.write(crop(ImageIO.read(ImageEditor.class.getResource("/aircraftcarrierThin.png")), 120, 0, 60, 50),
					"png", new File("aircraftcarrierThree.png"));
			ImageIO.write(crop(ImageIO.read(ImageEditor.class.getResource("/aircraftcarrierThin.png")), 180, 0, 60, 50),
					"png", new File("aircraftcarrierFour.png"));
			ImageIO.write(crop(ImageIO.read(ImageEditor.class.getResource("/aircraftcarrierThin.png")), 240, 0, 60, 50),
					"png", new File("aircraftcarrierFive.png"));

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static BufferedImage crop(BufferedImage src, int x, int y, int width, int height) {
		BufferedImage dest = src.getSubimage(x, y, width, height);
		return dest;
	}

	private static void resizeImages() {
		resize("/destroyer.png");
		resize("/aircraftcarrier.png");
		resize("/cruiser.png");
		resize("/battleship.png");
		resize("/submarine.png");
	}

	private static void resize(String path) {
		try {
			BufferedImage img = ImageIO.read(ImageEditor.class.getResource(path));

			int newWidth = 0;
			switch (img.getWidth()) {
			case 120:
				newWidth = 120;
				break;
			case 240:
				newWidth = 180;
				break;
			case 360:
				newWidth = 240;
				break;
			case 480:
				newWidth = 300;
				break;
			}

			BufferedImage imgnew = new BufferedImage(newWidth, 50, img.getType());

			Graphics2D g2d = imgnew.createGraphics();
			g2d.drawImage(img, 0, 0, newWidth, 50, null);
			g2d.dispose();

			String newPath = path.substring(1);
			newPath = newPath.substring(0, newPath.indexOf('.'));
			newPath += "Thin.png";
			ImageIO.write(imgnew, "png", new File(newPath));
		} catch (IOException exc) {

		}
	}
}
