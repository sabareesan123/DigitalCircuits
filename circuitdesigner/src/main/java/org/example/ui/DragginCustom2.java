package org.example.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.TransferHandler.TransferSupport;
import javax.swing.WindowConstants;
public class DragginCustom2 extends JFrame {
	private DataFlavor widgetFlavor = new DataFlavor(CanvasWidget.class,"Canvas Drag and Drop");

	public DragginCustom2() {
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		this.setPreferredSize(new Dimension(600,400));
		
		CanvasModel canvasModel = new CanvasModel();
		Canvas canvas = new Canvas(canvasModel);
		add(canvas, BorderLayout.CENTER);
		
		new CanvasDragController(canvas);
		//drawLine(50,50,100,100);
		CanvasWidget widget = new CanvasWidget();
		widget.setBounds(new Rectangle(10,10,50,50));
		canvasModel.add(widget);
		
		pack();
		setVisible(true);
	}

	// publish subscriber
	class Canvas extends JPanel implements Observer {
		CanvasModel canvasModel;
		List <CanvasWidget> widgetList = null;
		
		public Canvas(CanvasModel canvasModel) {
			this.canvasModel = canvasModel;
			canvasModel.addObserver(this);
		}

		protected void paintChildren(Graphics g) {
			for ( CanvasWidget widget : canvasModel.getWidgetList() )
				widget.paint(g);
		}

		public void update(Observable o, Object arg) {
			repaint();
		}

		public CanvasModel getCanvasModel() {
			return canvasModel;
		}
		
	}
	
	class CanvasModel extends Observable {
		private List<CanvasWidget> widgetList = new ArrayList<CanvasWidget>();
		
		public void add(CanvasWidget widget) {
			widgetList.add(widget);
			notifyModelChanged();
		}

		public void remove(CanvasWidget widget) {
			widgetList.remove(widget);
			notifyModelChanged();
		}
		
		public List<CanvasWidget> getWidgetList() {
			return widgetList;
		}
		
		public CanvasWidget getWidgetAt(Point p) {
			for ( CanvasWidget widget : widgetList ) {
				if ( widget.getBounds().contains(p) )
					return widget;
			}
			return null;
		}
		
		public void notifyModelChanged() {
			setChanged();
			notifyObservers();
		}
	}
	
	class CanvasWidget {
		private Rectangle bounds = new Rectangle(0,0,10,10);
		private boolean moving = false;
		public CanvasWidget() {

		}
		public Rectangle getBounds() {
			return bounds;
		}
		public void setBounds(Rectangle bounds) {
			this.bounds = bounds;
		}
		protected void paint(Graphics g) {
			if ( isMoving() )
				g.setColor(Color.LIGHT_GRAY);
			else
				g.setColor(Color.DARK_GRAY);
			g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
		}
		public boolean isMoving() {
			return moving;
		}
		public void setMoving(boolean moving) {
			this.moving = moving;
		}
	}
	class CanvasWidgetTransferable implements Transferable {
		private DataFlavor[] flavorArray = { widgetFlavor, DataFlavor.stringFlavor };
		private Rectangle bounds = null;	
		public CanvasWidgetTransferable(CanvasWidget canvasWidget) {
			bounds = canvasWidget.getBounds();
		}
		public DataFlavor[] getTransferDataFlavors() {
			return flavorArray;
		}
		public boolean isDataFlavorSupported(DataFlavor flavor) {
			return flavor == widgetFlavor || 
				flavor == DataFlavor.stringFlavor;
		}
		public Object getTransferData(DataFlavor flavor)
				throws UnsupportedFlavorException, IOException {
			if ( flavor == DataFlavor.stringFlavor ) 
				return this.bounds.toString();
			if ( flavor == widgetFlavor )
				return new Rectangle(this.bounds);
			return null;
		}	
	}	
	class CanvasDragController implements DragGestureListener, DragSourceListener, DropTargetListener {	
		private Point dragImageOffset = new Point();	
		public CanvasDragController(Canvas canvas) {
			DragSource source = DragSource.getDefaultDragSource();
			source.createDefaultDragGestureRecognizer(canvas, DnDConstants.ACTION_MOVE, this);
			DropTarget target = new DropTarget(canvas,this);
		}
		public void dragGestureRecognized(DragGestureEvent dge) {
			DragSource source = DragSource.getDefaultDragSource();
			Canvas canvas = (Canvas) dge.getComponent();
			CanvasModel canvasModel = canvas.getCanvasModel();

			CanvasWidget widget = canvasModel.getWidgetAt(dge.getDragOrigin());
			if ( widget != null ) {
				Rectangle widgetBounds = widget.getBounds();
				BufferedImage dragImage = new BufferedImage(widgetBounds.width,widgetBounds.height,BufferedImage.TYPE_INT_ARGB);
				Graphics g = dragImage.getGraphics();
				Point widgetSavedLocation = widget.getBounds().getLocation();
				widgetBounds.setLocation(0, 0);
				widget.paint(g);
				widgetBounds.setLocation(widgetSavedLocation);
				dragImageOffset = new Point(widget.getBounds().x - dge.getDragOrigin().x, widget.getBounds().y - dge.getDragOrigin().y);
				widget.setMoving(true);
				canvasModel.notifyModelChanged();
				CanvasWidgetTransferable transferablePackage = new CanvasWidgetTransferable(widget);
				source.startDrag(dge, DragSource.DefaultMoveDrop, 
						dragImage,
						dragImageOffset,
						transferablePackage, this);
			}

		}
		public void dragEnter(DragSourceDragEvent dsde) {}
		public void dragOver(DragSourceDragEvent dsde) {}
		public void dropActionChanged(DragSourceDragEvent dsde) {}
		public void dragExit(DragSourceEvent dse) {}

		public void dragDropEnd(DragSourceDropEvent dsde) {
			Canvas canvas = (Canvas) dsde.getDragSourceContext().getComponent();
			CanvasModel canvasModel = canvas.getCanvasModel();
			
			Rectangle bounds = null;
			try {
				bounds = (Rectangle) dsde.getDragSourceContext().getTransferable().getTransferData(widgetFlavor);
			} catch (UnsupportedFlavorException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			if ( dsde.getDropSuccess() ) {
				if ( dsde.getDropAction() == DnDConstants.ACTION_MOVE) {
					Point p = new Point(bounds.x,bounds.y);
					CanvasWidget widget = null;
					for ( CanvasWidget searchWidget : canvasModel.widgetList ) {
						if ( searchWidget.getBounds().getLocation().equals(p) )
							widget = searchWidget;
					}
					if ( widget != null)
						canvasModel.remove(widget);
				}
			} else {
				Point p = new Point(bounds.x,bounds.y);
				CanvasWidget widget = canvasModel.getWidgetAt(p);
				widget.setMoving(false);
				canvasModel.notifyModelChanged();
			}
		}
		public void dragEnter(DropTargetDragEvent dtde) {}
		public void dragOver(DropTargetDragEvent dtde) {}
		public void dropActionChanged(DropTargetDragEvent dtde) {}
		public void dragExit(DropTargetEvent dte) {}

		public void drop(DropTargetDropEvent dtde) {
			if (!dtde.isDataFlavorSupported(widgetFlavor)) {
				dtde.rejectDrop();
			}
			
			dtde.acceptDrop(DnDConstants.ACTION_MOVE);
			
			Canvas canvas = (Canvas) dtde.getDropTargetContext().getComponent();
			CanvasModel canvasModel = canvas.getCanvasModel();
			
			Rectangle bounds = new Rectangle(0,0,10,10);
			try {
				bounds = (Rectangle) dtde.getTransferable().getTransferData(widgetFlavor);
			} catch (UnsupportedFlavorException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			CanvasWidget widget = new CanvasWidget();
			
			Point mouseLocation = dtde.getLocation();
			Point realDropLocation = new Point(mouseLocation.x + dragImageOffset.x,mouseLocation.y + dragImageOffset.y);
			bounds.setLocation(realDropLocation);
			widget.setBounds(bounds);
			canvasModel.add(widget);
			dtde.dropComplete(true);
		}
	}
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new DragginCustom2();
			}
		});
	}
}