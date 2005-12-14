/*
 * Created on 03.06.2005
 *
 */
package com.nightlabs.editor2d.figures;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * 
 * @author Alexander Bieber <alex[at]nightlabs[DOT]de>
 *
 */
public class SmartUpdateFigure 
	extends Figure
	implements ISmartUpdateFigure
{
	/**
	 * Inner tile class. 
	 */
	private static class Tile implements FigureTile {
		
		private Rectangle bounds;
		private String key;
		private List figures = new LinkedList();
		
		public Tile(Rectangle bounds) {
			this.bounds = bounds;
		}
		
		public String getKey() {
			if (key == null)
				key = getTileKey(bounds.x, bounds.y);
			return key;
		}		

		/**
		 * @see com.nightlabs.editor2d.figures.FigureTile#intersects(org.eclipse.draw2d.geometry.Rectangle)
		 */
		public boolean intersects(Rectangle rect) {
			return bounds.intersects(rect);
		}

		/**
		 * @see com.nightlabs.editor2d.figures.FigureTile#getBounds()
		 */
		public Rectangle getBounds() {
			return bounds;
		}
		
		/**
		 * @see com.nightlabs.editor2d.figures.FigureTile#getTileFigures()
		 */
		public Collection getTileFigures() {
			return figures;
		}

		/**
		 * @see com.nightlabs.editor2d.figures.FigureTile#addFigure(org.eclipse.draw2d.Figure)
		 */
		public void addFigure(IFigure figure) {
			if (!figures.contains(figure))
				figures.add(figure);
		}

		/**
		 * @see com.nightlabs.editor2d.figures.FigureTile#removeFigure(org.eclipse.draw2d.Figure)
		 */
		public void removeFigure(IFigure figure) {
			figures.remove(figure);
		}

		/**
		 * @see com.nightlabs.editor2d.figures.FigureTile#getIntersectionFigures(org.eclipse.draw2d.geometry.Rectangle)
		 */
		public Collection getIntersectingFigures(Rectangle rect) {
			List result = new ArrayList();
			for (Iterator iter = figures.iterator(); iter.hasNext();) {
				Figure figure = (Figure) iter.next();
				if (figure.intersects(rect)) {
					result.add(figure);
				}
			}
			return result;
		}
		
	}
	
	/**
	 * key: String: (x,y) upper left tile corner <br/>
	 * value: FigureTile: The FigureTile for this tile 
	 */
	private Map tiles = new HashMap();
	
	/**
	 * key Figure: childfigure <br/>
	 * value: Set of FigureTiles
	 */
	private Map figuresInTiles = new HashMap();

	/**
	 * The tile dimension calculated in {@link #rebuildTiles()} 
	 */
	private Dimension tileDimension;
	
	/**
	 * EveryTime on rebuild this method is
	 * consulted for the new tile dimensions.
	 * This implementation returns 
	 * x = bounds.x/round(sqrt(childCount)) and
	 * y = bounds.y/round(sqrt(childCount)) 
	 * May be overridden.
	 *  
	 * @return Optimal? tile dimensions for this Figure
	 */
	protected Dimension calculateTileDimension() {
		Dimension dim = new Dimension();
		int fraction = (int)Math.round(Math.sqrt(getChildren().size()));
		if (fraction == 0) {
			dim.width = 0;
			dim.height = 0;
		}
		else {
			dim.width = getBounds().width / fraction;
			dim.height = getBounds().height / fraction;
		}
		return dim;
	}
	
	public static String getTileKey(int x, int y) {
		return "("+x+","+y+")";
	}
	
	/**
	 * Retrieve the FigureTile at the given Position.
	 * Autocreate if not existent.
	 */
	protected FigureTile getTile(int x, int y) {
		String tileKey = getTileKey(x, y);
		FigureTile figureTile = (FigureTile)tiles.get(tileKey);
		if (figureTile == null) {
			figureTile = new Tile(new Rectangle(x, y, tileDimension.width, tileDimension.height));
			tiles.put(tileKey, figureTile);
		}
		return figureTile;
	}
	
	/**
	 * Get all tiles this figure is registered in.
	 * 
	 * @param figure The figure
	 * @return The figures tiles.
	 */
	protected Set getTilesForFigure(IFigure figure) {
		Set figureTiles = (Set)figuresInTiles.get(figure);
		if (figureTiles == null) {
			figureTiles = new HashSet();
			figuresInTiles.put(figure, figureTiles);
		}
		return figureTiles;
	}
	
	/**
	 * Get the top left Position of the
	 * tile this point is in.
	 * 
	 * @param x The points x
	 * @param y The points y
	 */
	protected Point getTileTopLeft(int x, int y) {
		return new Point(
				(x/tileDimension.width) * tileDimension.width,
				(y/tileDimension.height) * tileDimension.height
			);
	}
	
	/**
	 * Integrates the given figure in the current tile build. 
	 * Makes sure that the registrations in the tile maps are
	 * correct after execution.
	 * 
	 * @param figure The figure to integrate
	 */	
	protected void integrateFigure(IFigure figure) {
		Rectangle figureBounds = figure.getBounds();
		Point topLefTilePosition = getTileTopLeft(figureBounds.x, figureBounds.y);
		Point bottomRightTilePosition = getTileTopLeft(figureBounds.x + figureBounds.width, figureBounds.y+figureBounds.height);
		
		// clear registration in current build
		Set figureTiles = getTilesForFigure(figure);
		for (Iterator iter = figureTiles.iterator(); iter.hasNext();) {
			FigureTile figureTile = (FigureTile) iter.next();
			figureTile.removeFigure(figure);
		}
		figureTiles.clear();
		
		// reintegrate in build
		for (int row = topLefTilePosition.y; row <= bottomRightTilePosition.y; row += tileDimension.height) {
			for (int col = topLefTilePosition.x; col <= bottomRightTilePosition.x; col += tileDimension.width) {
				FigureTile figureTile = getTile(col, row);
				figureTile.addFigure(figure);
				figureTiles.add(figureTile);
			}
		}
	}
	
	
	private boolean tilesBuild = false;
	/**
	 * Clears and rebuilds the tiles
	 *
	 */
	protected void rebuildTiles() {
		tiles.clear();
		figuresInTiles.clear();
		tileDimension = calculateTileDimension();
		for (Iterator iter = getChildren().iterator(); iter.hasNext();) {
			Figure figure = (Figure) iter.next();
			integrateFigure(figure);
		}
	}
	
	/**
	 * @see com.nightlabs.editor2d.figures.ISmartUpdateFigure#paintRegion(Graphics, Rectangle)
	 */
	public void paintRegion(Graphics graphics, Rectangle region) {
		internalPaintRegion(graphics, region);
	}

	/**
	 * @see com.nightlabs.editor2d.figures.ISmartUpdateFigure#paintRegion(java.awt.Graphics2D, org.eclipse.draw2d.geometry.Rectangle)
	 */
	public void paintRegion(Graphics2D graphics, Rectangle region) {
		internalPaintRegion(graphics, region);
	}

	private void internalPaintRegion(Object graphics, Rectangle region) {
		if (!tilesBuild)
			rebuildTiles();
		tilesBuild = true;
		internalPaintSelf(graphics, region);		
		if (getChildren().size() == 0)
			return;
		Rectangle regionToPaint = getBounds().getIntersection(region);	
		if (regionToPaint.width == 0 && regionToPaint.height == 0)
			return;
		Point topLefTilePosition = getTileTopLeft(regionToPaint.x, regionToPaint.y);
		Point bottomRightTilePosition = getTileTopLeft(regionToPaint.x + regionToPaint.width, regionToPaint.y+regionToPaint.height);
		
		for (int row = topLefTilePosition.y; row <= bottomRightTilePosition.y; row += tileDimension.height) {
			for (int col = topLefTilePosition.x; col <= bottomRightTilePosition.x; col += tileDimension.width) {
				FigureTile figureTile = getTile(col, row);
				for (Iterator iter = figureTile.getIntersectingFigures(regionToPaint).iterator(); iter.hasNext();) {
					Figure figure = (Figure) iter.next();
					internalPaintChild(figure, graphics, regionToPaint);
				}
			}
		}		
	}
	
	private void internalPaintSelf(Object graphics, Rectangle region) {
		if (graphics instanceof Graphics) {
			Graphics d2dGraphics = (Graphics)graphics;
			this.paint(d2dGraphics);
		}
		else if (graphics instanceof Graphics2D) {
			Graphics2D j2dGraphics = (Graphics2D)graphics;
			if (this instanceof ISmartUpdateFigure)
				((ISmartUpdateFigure)this).paintRegion(j2dGraphics, region);
			else if (this instanceof RendererFigure)
				((RendererFigure)this).paint(j2dGraphics);
		}
	}
	
	private void internalPaintChild(Figure child, Object graphics, Rectangle regionToPaint) {
		if (graphics instanceof Graphics) {
			Graphics d2dGraphics = (Graphics)graphics;
			if (child instanceof ISmartUpdateFigure)
				((ISmartUpdateFigure)child).paintRegion(d2dGraphics, regionToPaint);
			else 
				child.paint(d2dGraphics);
		}
		else if (graphics instanceof Graphics2D) {
			Graphics2D j2dGraphics = (Graphics2D)graphics;
			if (child instanceof ISmartUpdateFigure)
				((ISmartUpdateFigure)child).paintRegion(j2dGraphics, regionToPaint);
			else if (child instanceof RendererFigure)
				((RendererFigure)child).paint(j2dGraphics);
		}
	}


	/**
	 * @see com.nightlabs.editor2d.figures.ISmartUpdateFigure#refresh()
	 */
	public void refresh() {
		tilesBuild = false;
	}

	/**
	 * @see com.nightlabs.editor2d.figures.ISmartUpdateFigure#refresh(org.eclipse.draw2d.Figure)
	 */
	public void refresh(IFigure figure) {
		if (!tilesBuild)
			rebuildTiles();
		tilesBuild = true;
		integrateFigure(figure);
	}
	
}
