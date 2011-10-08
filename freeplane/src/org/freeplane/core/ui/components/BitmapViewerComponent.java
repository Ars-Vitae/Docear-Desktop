/*
 *  Freeplane - mind map editor
 *  Copyright (C) 2009 Dimitry
 *
 *  This file author is Dimitry
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.freeplane.core.ui.components;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.JComponent;

/**
 * @author Dimitry Polivaev
 * 22.08.2009
 */
public class BitmapViewerComponent extends JComponent {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int hint;
	private BufferedImage cachedImage;
	private final URL url;
	private final Dimension originalSize;

	protected int getHint() {
		return hint;
	}

	public void setHint(final int hint) {
		this.hint = hint;
	}

	public BitmapViewerComponent(final URI uri) throws MalformedURLException, IOException {
		url = uri.toURL();
		cachedImage = ImageIO.read(url);
		originalSize = new Dimension(cachedImage.getWidth(), cachedImage.getHeight());
		hint = Image.SCALE_SMOOTH;
	}

	public Dimension getOriginalSize() {
		return new Dimension(originalSize);
	}

	@Override
	protected void paintComponent(final Graphics g) {
		final int width = getWidth();
		final int height = getHeight();
		if (width == 0 || height == 0) {
			return;
		}
		if(cachedImage == null || cachedImage.getWidth() != width || cachedImage.getHeight() != height){
			BufferedImage image;
	        try {
		        image = ImageIO.read(url);
	        }
	        catch (IOException e) {
				super.paintComponent(g);
				return;
	        }
			final int imageWidth = image.getWidth();
			final int imageHeight = image.getHeight();
			if(imageWidth == 0 || imageHeight == 0){
				super.paintComponent(g);
				return;
			}
			if (imageWidth != width || imageHeight != height) {
				cachedImage = new BufferedImage(width, height, image.getType());
				final double kComponent = (double) height / (double) width;
				final double kImage = (double) imageHeight / (double) imageWidth;
				final Image scaledImage;
				final int x,y;
				if (kComponent >= kImage) {
					final int calcHeight = (int) (width * kImage);
					scaledImage = image.getScaledInstance(width, calcHeight, hint);
					x = 0;
					y = (height - calcHeight) / 2;
				}
				else {
					final int calcWidth = (int) (height / kImage);
					scaledImage = image.getScaledInstance(calcWidth, height, hint);
					x = (width - calcWidth) / 2;
					y = 0;
				}
				cachedImage.createGraphics().drawImage(scaledImage, x, y, null);
			}
			else {
				cachedImage = image;
			}
		}
		g.drawImage(cachedImage, 0, 0, null);
	}
}
