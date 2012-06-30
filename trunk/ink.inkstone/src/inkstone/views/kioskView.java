package inkstone.views;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.part.*;
import inkstone.utils.*;


public class kioskView extends ViewPart {

	public static final String ID_ = "inkstone.views.KioskView";
	private final Display display_ = Display.getCurrent();
	private final Shell shell_ = new Shell(display_);
	private inkstoneGallery inkGallery_;
	private Menu popupMenu_;
	private ExpandBar mainExpBar_;
	private MenuDetectListener popupMenuListener_;
	private Map<String,expBarBundle> eBarsMap_ = new HashMap<String,expBarBundle>();
	
	class expBarBundle {
		ExpandItem expItem_;
		Composite composite_;
		Map<String,CLabel> labels_ = new HashMap<String, CLabel>();
		public expBarBundle(ExpandItem expItem,Composite composite) {
			expItem_ = expItem;
			composite_ = composite;
			labels_ = new HashMap<String, CLabel>();
		}
	}

	public kioskView() {
		inkGallery_ = inkstoneGallery.getInstance();
	}

	@Override
	public void createPartControl(Composite parent) {
		popupMenu_ = new Menu(shell_, SWT.POP_UP);
		initPopupMenu();
		
		mainExpBar_ = new ExpandBar (parent, SWT.V_SCROLL);
		popupMenuListener_ = new MenuDetectListener() {
			@Override
			public void menuDetected(MenuDetectEvent e) {
				popupMenu_.setLocation(e.x, e.y);
				popupMenu_.setVisible(true);
				while (!popupMenu_.isDisposed () && popupMenu_.isVisible ()) {
					if (!display_.readAndDispatch ()) display_.sleep ();
				}
				popupMenu_.setVisible(false);
			}
		};
		mainExpBar_.addMenuDetectListener(popupMenuListener_);
		addGeneralExpandBar(mainExpBar_);
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
		
	}
	
	private void initPopupMenu() {
		
		// TODO Fix accelerators. not working ! see : (http://blog.publicobject.com/2005/03/why-swt-why.html) (http://book.javanb.com/swt-the-standard-widget-toolkit/ch02lev1sec4.html)
		
		MenuItem item = new MenuItem(popupMenu_, SWT.PUSH);
	    item.setText("&Collapse All\tCtrl -");
	    item.setAccelerator(SWT.CTRL + '-');
	    item.addListener (SWT.Selection, new Listener () {
			@Override
			public void handleEvent (Event e) {
				doCollapseAll();
			}
	    });
	    
	    item = new MenuItem(popupMenu_, SWT.PUSH);
	    item.setText("&Expand All\tAlt +");
	    item.setAccelerator(SWT.CTRL + '+');
	    item.addListener (SWT.Selection, new Listener () {
			@Override
			public void handleEvent (Event e) {
				doExpandAll();
			}
	    });
	    
	    item = new MenuItem(popupMenu_, SWT.PUSH);
	    item.setText("&Sync With INK Model\tCtrl+Shift+M");
	    //item.setAccelerator(SWT.CTRL + SWT.SHIFT + 'N');
	    item.addListener (SWT.Selection, new Listener () {
			@Override
			public void handleEvent (Event e) {
				doSyncWithInkModel();
			}
	    });
	    /*
	    item = new MenuItem(popupMenu_, SWT.PUSH);
	    item.setText("I&nclude INK File\tCtrl+N");
	    item.setAccelerator(SWT.CTRL + 'N');
	    item.addListener (SWT.Selection, new Listener () {
			@Override
			public void handleEvent (Event e) {
				doIncludeInkFile();
			}
	    });
	    
	    item = new MenuItem(popupMenu_, SWT.PUSH);
	    item.setText("E&xclude INK File \tCtrl+X");
	    item.setAccelerator(SWT.CTRL + 'X');
	    item.addListener (SWT.Selection, new Listener () {
			@Override
			public void handleEvent (Event e) {
				doExcludeInkFile();
			}
	    });
	    */
	    shell_.setMenu(popupMenu_);
	    
	}
	
	private void doCollapseAll() {
		for(int i=0; i < mainExpBar_.getItemCount(); i++) {
			mainExpBar_.getItem(i).setExpanded(false);
		}
	}
	
	private void doExpandAll() {
		for(int i=0; i < mainExpBar_.getItemCount(); i++) {
			mainExpBar_.getItem(i).setExpanded(true);
		}
	}
	
	/*
	private void doIncludeInkFile() {
		MessageBox messageBox = new MessageBox(shell_, SWT.ICON_WARNING | SWT.OK);
		messageBox.setText("Warning");
        messageBox.setMessage("Include INK File not implemented yet !");
        messageBox.open();
	}
	
	private void doExcludeInkFile() {
		MessageBox messageBox = new MessageBox(shell_, SWT.ICON_WARNING | SWT.OK);
		messageBox.setText("Warning");
        messageBox.setMessage("Exclude INK File not implemented yet !");
        messageBox.open();
	}
	*/
	
	private void doSyncWithInkModel() {
		// TODO: Replace this code with INK model investigation algorithm.
		// TODO: Decide how to group INK elements (by type, by src files names, ...)
		
		addExpandBar(mainExpBar_,"INK Meta-Classes bar".intern(),1);
		addNotation("INK Meta-Classes bar".intern(), "Shape", inkGallery_.getImage(inkstoneGallery.METACLASS_ICON));
		addExpandBar(mainExpBar_,"INK Classes bar".intern(),2);
		addNotation("INK Classes bar".intern(), "Circle".intern(), inkGallery_.getImage(inkstoneGallery.CLASS_ICON));
		addNotation("INK Classes bar".intern(), "Square".intern(), inkGallery_.getImage(inkstoneGallery.CLASS_ICON));
		addNotation("INK Classes bar".intern(), "triangle".intern(), inkGallery_.getImage(inkstoneGallery.CLASS_ICON));
		addExpandBar(mainExpBar_,"INK Objects bar".intern(),3);
		addNotation("INK Objects bar".intern(), "30Rad Circle".intern(), inkGallery_.getImage(inkstoneGallery.OBJECT_ICON));
		addNotation("INK Objects bar".intern(), "Small Square".intern(), inkGallery_.getImage(inkstoneGallery.OBJECT_ICON));
		addNotation("INK Objects bar".intern(), "Equilateral Triangle".intern(), inkGallery_.getImage(inkstoneGallery.OBJECT_ICON));

	}
	
	private void addGeneralExpandBar(ExpandBar bar) {
		Composite composite = new Composite (bar, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginLeft = layout.marginTop = layout.marginRight = layout.marginBottom = 2;
		layout.verticalSpacing = 2;
		composite.setLayout(layout);
		composite.addMenuDetectListener(popupMenuListener_);
		
		CLabel label = new CLabel(composite, SWT.NONE);
		label.setImage(inkGallery_.getImage(inkstoneGallery.METACLASS_ICON));
		label.setText("INK MetaClass");
		
		label = new CLabel(composite, SWT.NONE);
		label.setImage(inkGallery_.getImage(inkstoneGallery.CLASS_ICON));
		label.setText("INK Class");
		
		label = new CLabel(composite, SWT.NONE);
		label.setImage(inkGallery_.getImage(inkstoneGallery.OBJECT_ICON));
		label.setText("INK Object");
		
		label = new CLabel(composite, SWT.SEPARATOR | SWT.CENTER);
		label.setText("    ----------");
		
		label = new CLabel(composite, SWT.NONE);
		label.setImage(inkGallery_.getImage(inkstoneGallery.EXTENSION_ICON));
		label.setText("INK Extension");
		
		label = new CLabel(composite, SWT.NONE);
		label.setImage(inkGallery_.getImage(inkstoneGallery.INSTANCING_ICON));
		label.setText("INK Instancing");
		
		label = new CLabel(composite, SWT.NONE);
		label.setImage(inkGallery_.getImage(inkstoneGallery.REFERENCING_ICON));
		label.setText("INK Referencing");

		ExpandItem notationsPanel = new ExpandItem(bar, SWT.NONE, 0);
		notationsPanel.setText("INK Notations");
		notationsPanel.setHeight(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
		notationsPanel.setControl(composite);
		notationsPanel.setExpanded(true);
	}
	
	private void addExpandBar(ExpandBar bar, String name, int barIndex) {
		expBarBundle bundle = eBarsMap_.get(name);
		if(bundle==null) {
			Composite composite = new Composite (bar, SWT.NONE);
			GridLayout layout = new GridLayout();
			layout.marginLeft = layout.marginTop = layout.marginRight = layout.marginBottom = 2;
			layout.verticalSpacing = 2;
			composite.setLayout(layout);
			composite.addMenuDetectListener(popupMenuListener_);
			ExpandItem aPanel = new ExpandItem(bar, SWT.NONE, barIndex);
			aPanel.setText(name);
			aPanel.setHeight(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
			aPanel.setControl(composite);
			eBarsMap_.put(name,new expBarBundle(aPanel,composite));
		}
	}
	
	private void addNotation(String barName, String itemName, Image image) {
		expBarBundle bundle= eBarsMap_.get(barName);
		if(bundle!=null) {
			CLabel label = bundle.labels_.get(itemName);
			if(label==null) {
				label = new CLabel(bundle.composite_, SWT.NONE);
				label.setText(itemName);
				if(image!=null) {
					label.setImage(image);
				}				
				bundle.labels_.put(itemName,label);
				bundle.expItem_.setHeight(bundle.composite_.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
			}			
		}
	}
}