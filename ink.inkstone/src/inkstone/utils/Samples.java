package inkstone.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.FileDialog;
import org.ink.core.vm.lang.InkObjectState;
import org.ink.core.vm.lang.property.mirror.CollectionPropertyMirror;
import org.ink.core.vm.lang.property.mirror.PropertyMirror;
import org.ink.core.vm.mirror.Mirror;
import org.ink.core.vm.proxy.Proxiable;
import org.ink.core.vm.utils.property.mirror.ListPropertyMirror;
import org.ink.core.vm.utils.property.mirror.MapPropertyMirror;
import org.ink.core.vm.utils.property.mirror.PrimitiveAttributeMirror;
import org.ink.eclipse.InkPlugin;
import org.ink.eclipse.utils.EclipseUtils;
import org.ink.eclipse.utils.InkUtils;

/**
 * Temporary examples class. To be deleted after end of coding.
 * 
 * @author Ofer Calvo
 *
 */
public class Samples {
	
	private static final String INK_META_CLASSES_BAR = "INK Meta-Classes bar";
	private final Map<Object,expBarBundle> eBarsMap_ = new HashMap<Object,expBarBundle>();
	private ExpandBar mainExpBar_;
	private final InkstoneGallery inkGallery_ = InkstoneGallery.getInstance();
	
	/**
	 * Inner class to handle expand bar ExpandItem's.
	 */
	class expBarBundle {
		
		private ExpandItem expItem_;
		private Composite composite_;
		private Map<String,CLabel> labels_ = new HashMap<String, CLabel>();
		
		public expBarBundle(ExpandItem expItem,Composite composite) {
			this.expItem_ = expItem;
			this.composite_ = composite;
			this.labels_ = new HashMap<String, CLabel>();
		}
	}
	
	private Mirror getInkElement(String id){
		InkObjectState s = InkPlugin.getDefault().getInkContext().getState(id, false);
		if(s!=null){
			return s.reflect();
		}
		return null;
	}
	
	private void openInkElement(String id){
		// TODO: Remove 'openInkElement(String id)' after code learning.
		
		Mirror m = getInkElement(id);
		if(m!=null){
			EclipseUtils.openEditor(m);
		}
	}
	
	private void examplePropertiesReflection(String id){
		// TODO: Remove 'examplePropertiesReflection(String id)' after code learning.
		
		Mirror m = getInkElement(id);
		if(m!=null){
			examplePropertiesReflection(m);
			
		}
	}
	
	private void examplePropertiesReflection(Mirror m){
		// TODO: Remove 'examplePropertiesReflection(Mirror m)' after code learning.
		
		PropertyMirror[] pms = m.getPropertiesMirrors();
		for(PropertyMirror pm : pms){
			reflectProperty(m, pm);
		}
		
	}

	private void reflectProperty(Mirror owner, PropertyMirror pm) {
		// TODO: Remove 'reflectProperty(Mirror owner, PropertyMirror pm)' after code learning.
		
		Object value = owner.getPropertyValue(pm.getIndex());
		switch(pm.getTypeMarker()){
		case Primitive:
			PrimitiveAttributeMirror pam = (PrimitiveAttributeMirror)pm;
			switch(pam.getPrimitiveTypeMarker()){
			case Integer:
				break;
			case String:
				break;
			case Boolean:
				break;
			default:
				break;
			}
			break;
		case Collection:
			CollectionPropertyMirror cpm = (CollectionPropertyMirror)pm;
			switch(cpm.getCollectionTypeMarker()){
			case List:
				ListPropertyMirror lpm = (ListPropertyMirror)cpm;
				if(value!=null){
					List l = (List)value;
				}
				PropertyMirror itemMirror = lpm.getItemMirror();
				reflectProperty(owner, itemMirror);
				break;
			case Map:
				MapPropertyMirror mpm = (MapPropertyMirror)cpm;
				if(value!=null){
					Map l = (Map)value;
				}
				PropertyMirror keyMirror = mpm.getKeyMirror();
				PropertyMirror valueMirror = mpm.getValueMirror();
				reflectProperty(owner, keyMirror);
				reflectProperty(owner, valueMirror);
				break;
			}
			break;
		case Enum:
			
			break;
		case Class:
			if(value!=null){
				Proxiable p = (Proxiable)value;
				examplePropertiesReflection(p.reflect());
				
			}
			break;
		}
	}
	
	private void openInkElemetForEdit(Mirror inkElementMiror) {
		// TODO: remove after code learning.
		EclipseUtils.openEditor(inkElementMiror);
		//use: mirror collection <- InkUtils.getInstances(classId, recursive);
	}
	
	private void doSyncWithInkModel() {
		// TODO: remove after code learning.
		// TODO: Replace this code with INK model investigation algorithm.
		// TODO: Decide how to group INK elements (by type, by src files names, ...)
		// TODO: EclipseUtils.openEditor(o);
		List<IProject> inkProjects = InkPlugin.getDefault().getInkProjects();
		List<String> metaClasses = new ArrayList<String>();
		List<String> classes = new ArrayList<String>();
		List<String> instances = new ArrayList<String>();
		List<String> enums = new ArrayList<String>();
		for(IProject p : inkProjects){
			String[] nss = InkUtils.getProjectNamespaces(p);
			Collection<Mirror> mirrors = InkUtils.getInstances(nss, "ink.core:InkObject", true, true);
			for(Mirror m : mirrors){
				switch(m.getObjectTypeMarker()){
				case Metaclass:
					metaClasses.add(m.getId());
					break;
				case Class:
					classes.add(m.getId());
					break;
				case Enumeration:
					enums.add(m.getId());
				default:
					instances.add(m.getId());
				}
			}
		}
		
		
		addExpandBar(mainExpBar_,INK_META_CLASSES_BAR.intern(),1);
		for(String id : metaClasses){
			addNotation(INK_META_CLASSES_BAR, id, inkGallery_.getImage(InkstoneGallery.METACLASS_ICON));
		}
		addExpandBar(mainExpBar_,"INK Classes bar".intern(),2);
		for(String id : classes){
			addNotation("INK Classes bar", id, inkGallery_.getImage(InkstoneGallery.CLASS_ICON));
		}
		addExpandBar(mainExpBar_,"INK Objects bar".intern(),3);
		for(String id : instances){
			addNotation("INK Objects bar", id, inkGallery_.getImage(InkstoneGallery.OBJECT_ICON));
		}
		addExpandBar(mainExpBar_,"INK Enums bar".intern(),4);
		for(String id : enums){
			addNotation("INK Enums bar", id, inkGallery_.getImage(InkstoneGallery.OBJECT_ICON));
		}
	}
	
	
	private void addExpandBar(ExpandBar bar, String name, int barIndex) {
		expBarBundle bundle = eBarsMap_.get(name);
		if(bundle==null) {
			Composite composite = new Composite (bar, SWT.NONE);
			GridLayout layout = new GridLayout();
			layout.marginLeft = layout.marginTop = layout.marginRight = layout.marginBottom = 2;
			layout.verticalSpacing = 2;
			composite.setLayout(layout);
			//composite.addMenuDetectListener(popupMenuListener_);
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

	private void handleFileBrowse1() {
		String filterNames[]; 
		String filterExtensions[];
		String filterPath;
		String fileSelected;
		FileDialog dialog = new FileDialog (Display.getCurrent().getActiveShell(), SWT.OPEN);
		
		String platform = SWT.getPlatform();
		if (platform.equals("win32") || platform.equals("wpf")) {
			filterNames = new String[] {"INK Files", "All Files (*.*)"};
			filterExtensions = new String[] {"*.ink", "*.*"};
		}
		else {
			filterNames = new String [] {"INK Files", "All Files (*)"};
			filterExtensions = new String [] {"*.ink", "*"};
		}
		filterPath = ResourcesPlugin.getWorkspace().getRoot().getRawLocation().toOSString();
		dialog.setFilterNames(filterNames);
		dialog.setFilterExtensions(filterExtensions);
		dialog.setFilterPath(filterPath);

		fileSelected = dialog.open();
		if(fileSelected != null ) {
			// Do ....
		}
	}
}
