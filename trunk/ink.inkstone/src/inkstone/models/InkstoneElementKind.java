package inkstone.models;

import inkstone.preferences.InkstonePreferenceConstants;
import inkstone.utils.KioskOverloadOfVisualElementsException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;

import org.ink.core.vm.mirror.Mirror;


/**
 * The 3rd level object type in the ink descriptive model (used by the InkStone plug-in).
 * Stores list of the same Ink Type/kind of ink elements (Metaclass/Class/Object/Enumeration).
 * Also stores widgets display data.
 * 
 * @author Ofer Calvo
 *
 */
public class InkstoneElementKind {
	private String kindName_;
	private InkstoneLibrary DslLib_;
	private List<InkstoneElement> elements_ = new ArrayList<InkstoneElement>();
	private Image image_;
	private ExpandItem expandItem_;
	private Composite childComposite_;
	private ExpandBar expandBar_;
	private Button lastPageButton_;
	private int displayHeight_ = 0;
	private int elementsCount_ = 0;
	private int pagesCount_ = 1;
	private int currentPage_ = 1;
	private int pageSize;
	
	/**
	 * Comparator class for sorting {@link InkstoneElement} by their full INK id.
	 */
	public class InkstoneElementComparable implements Comparator<InkstoneElement>{
	    @Override
	    public int compare(InkstoneElement e1, InkstoneElement e2) {
	        return (e1.getFullId().compareTo(e2.getFullId()));
	    }
	}
	
	/**
	 * Class constructor.
	 * 
	 * @param kindName The list name.
	 * @param DslLib Reference to the parent library.
	 * @param mirrors A list of reflected ink objects from same kind. 
	 */
	public InkstoneElementKind(String kindName, InkstoneLibrary DslLib, List<Mirror> mirrors, Image image) {
		this.kindName_ = kindName;
		this.DslLib_ = DslLib;
		this.image_ = image;
		elementsCount_ = mirrors.size();
		for( Mirror m : mirrors) {
			elements_.add(new InkstoneElement(m.getShortId(), m.getNamespace(), this));
		}
		Collections.sort(elements_, new InkstoneElementComparable());
	}
	
	public String getKindName() {
		return this.kindName_;
	}
	
	/**
	 * @return The kiosk expand bar item of this kind.
	 */
	public ExpandItem getExpandItem() {
		return this.expandItem_;
	}
	
	/**
	 * @return The kiosk expand bar of this kind.
	 */
	public ExpandBar getExpandBar() {
		return this.expandBar_ ;
	}
	
	public InkstoneLibrary getDslLib() {
		return this.DslLib_;
	}
	
	public int getCurrentPage() {
		return currentPage_;
	}
	
	public int getPageSize() {
		return pageSize;
	}
	
	public void redrawInKiosk(final int newPage, final Button pressedButton, final Display display) throws KioskOverloadOfVisualElementsException {
		if(currentPage_ == newPage) {
			return;
		}
		
		BusyIndicator.showWhile(display, new Runnable() {
			@Override
			public void run() {
				for (int i = (currentPage_-1)*pageSize; (i < elements_.size()) && (i < currentPage_*pageSize); i++) {
					elements_.get(i).dispose();
				}
		
				if( lastPageButton_ != null) {
					lastPageButton_.setForeground(display.getSystemColor(SWT.COLOR_GRAY));
				}
				
				currentPage_ = newPage;
				lastPageButton_ = pressedButton;
				
				for (int i = (currentPage_-1)*pageSize; (i < elements_.size()) && (i < currentPage_*pageSize); i++) {
					if(InkstoneElement.getStopFlag()) break;
					try {
						elements_.get(i).drawInKiosk(childComposite_, image_);
					} catch (KioskOverloadOfVisualElementsException e) {
						// exception never happen here cause visual elements overload check only happen first time the Kiosk draw (not in the redraw). 
						e.printStackTrace();
					}
				}
				
				int oldDisplayHeight = displayHeight_;
				int newDisplayHeight = childComposite_.computeSize(SWT.DEFAULT, SWT.DEFAULT).y;
				
				//childComposite_.pack(true);
				//expandItem_.setHeight(displayHeight_);
				setDisplayHeight(newDisplayHeight - oldDisplayHeight);
				
				pressedButton.setFocus();
				pressedButton.setForeground(display.getSystemColor(SWT.COLOR_BLUE));
			}
		});
	}
	
	/**
	 * Draw the kiosk view display widgets.
	 * 
	 * @param parentComposite The ink kind parent composite widget.
	 */
	public void drawInKiosk(ExpandBar parentComposite) throws KioskOverloadOfVisualElementsException {
		
		if(InkstoneElement.getStopFlag()) return;
		if( elements_.size() == 0 ) return;
		
		this.expandBar_ = parentComposite;
		final Display display = parentComposite.getDisplay();
		
		childComposite_ = new Composite(parentComposite, SWT.NONE);
		GridLayout layout = new GridLayout(1, false);
		layout.verticalSpacing = layout.horizontalSpacing = 0;
		layout.marginWidth = layout.marginHeight = 0;
		childComposite_.setLayout(layout);
		childComposite_.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		
		// Re-calc element pages variables
		pageSize = inkstone.Activator.getDefault().getPreferenceStore().getInt(InkstonePreferenceConstants.KIOSK_PAGE_SIZE);
		pagesCount_ = (elementsCount_ / pageSize);
		if( elementsCount_ % pageSize != 0 ) {
			pagesCount_++;
		}
		lastPageButton_ = null;
		
		if(elements_.size() > pageSize) {
			final ScrolledComposite scrollComposite = new ScrolledComposite(childComposite_, SWT.H_SCROLL | SWT.BORDER );
			scrollComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
			final Composite buttonsComposite = new Composite(scrollComposite, SWT.NONE);
			RowLayout pagesLayout = new RowLayout(SWT.HORIZONTAL);
			pagesLayout.wrap = false;
			pagesLayout.spacing = 0;
			pagesLayout.marginWidth = pagesLayout.marginHeight = 0;
			buttonsComposite.setLayout(pagesLayout);
			buttonsComposite.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
			
			for (int i = 1; i <= pagesCount_; i++) {
				Button pageButton = new Button(buttonsComposite, SWT.PUSH);
				int pageStartElement = 1 + (i-1)*pageSize;
				int pageEndElement = (i*pageSize < elements_.size()) ? (i*pageSize) : (elements_.size());
				pageButton.setText("[" + (i) + "]");
				pageButton.setToolTipText("Page " + i + " (elements " + pageStartElement + " to " + pageEndElement + ")");
				pageButton.setData(new Integer(i));
				pageButton.setForeground(display.getSystemColor(SWT.COLOR_GRAY));
				pageButton.addSelectionListener(new SelectionListener() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						try {
							redrawInKiosk((Integer)e.widget.getData(), ((Button)e.getSource()), display);
						} catch (KioskOverloadOfVisualElementsException e1) {
							// This exception should not be reached if the KioskOverloadOfVisualElements was throw properly at the elements drawKiosk method. 
							e1.printStackTrace();
						}
					}
					
					@Override
					public void widgetDefaultSelected(SelectionEvent e) {}
				});
			}
			scrollComposite.setContent(buttonsComposite);
			scrollComposite.setExpandHorizontal(true);
			scrollComposite.setExpandVertical(true);
			scrollComposite.setAlwaysShowScrollBars(true);
			scrollComposite.setShowFocusedControl(true);
			scrollComposite.addControlListener(new ControlAdapter() {
				public void controlResized(ControlEvent e) {
					scrollComposite.setMinSize(buttonsComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
				}
			});
		}
		
		try {
			for (int i = (currentPage_-1)*pageSize; (i < elements_.size()) && (i < currentPage_*pageSize); i++) {
				elements_.get(i).drawInKiosk(childComposite_, image_);
			}
		} catch (KioskOverloadOfVisualElementsException e) {
			throw(e);
		} finally {
			displayHeight_ = childComposite_.computeSize(SWT.DEFAULT, SWT.DEFAULT).y;
			expandItem_ = new ExpandItem(parentComposite, SWT.NONE, 0);
			expandItem_.setHeight(displayHeight_);
			expandItem_.setText("    " + this.kindName_ + " (" + String.valueOf(elements_.size()) + " elements)");
			expandItem_.setControl(childComposite_);
		}		
		
	}

	/**
	 * @return The elements list.
	 */
	public List<InkstoneElement> getElements() {
		return elements_ ;
	}
	
	/**
	 * @return The height of this element.
	 */
	public int getDisplayHeight() {
		return this.displayHeight_;		
	}
	
	/**
	 * Set the widget display height.
	 * @param displayHeight The height.
	 */
	public void setDisplayHeight(int displayHeight) {
		// handle zero height for the filter display look...
		if(displayHeight_== 4) {displayHeight_ = 0;}
		this.displayHeight_ += displayHeight;
		if(displayHeight_==0) {displayHeight_ = 4;}
		
		if(expandItem_.getExpanded()) {
			expandItem_.setHeight(displayHeight_);
			expandBar_.pack();
			DslLib_.setDisplayHeight(displayHeight);
			expandBar_.redraw();
		}
	}
	
	public void refreshData(List<Mirror> mirrors) throws KioskOverloadOfVisualElementsException {
		int oldHeight = displayHeight_;
		int newHeight = 0;
		for (int i = (currentPage_-1)*pageSize; (i < elements_.size()) && (i < currentPage_*pageSize); i++) {
			elements_.get(i).dispose();
		}
		elements_.clear();
		InkstoneElement.resetNumOfVisualElements();
		for( Mirror m : mirrors) {
			InkstoneElement element = new InkstoneElement(m.getShortId(), m.getNamespace(), this);
			elements_.add(element);
		}
		Collections.sort(elements_, new InkstoneElementComparable());
		for (int i = (currentPage_-1)*pageSize; (i < elements_.size()) && (i < currentPage_*pageSize); i++) {
			elements_.get(i).drawInKiosk(childComposite_, image_);
		}
		expandItem_.setText("    " + this.kindName_ + " (" + String.valueOf(elements_.size()) + " elements)");
		newHeight = childComposite_.computeSize(SWT.DEFAULT, SWT.DEFAULT).y;
		setDisplayHeight(newHeight-oldHeight);
	}
	
	private void disposeGraphics() {
		if(elements_ != null) {
			for(InkstoneElement element : elements_) {
				element.dispose();
			}
		}		
	}
	
	public void dispose() {
		disposeGraphics();
		if( expandBar_ != null ) {
			expandBar_.dispose();
		}
		if( childComposite_ != null ) {
			childComposite_.dispose();
		}
	}
}
