package org.ink.example.customer;

import java.util.Collection;

import org.ink.core.vm.factory.Context;
import org.ink.core.vm.factory.ElementDescriptor;
import org.ink.core.vm.factory.InkVM;
import org.ink.core.vm.factory.VMConfig;
import org.ink.core.vm.factory.resources.DefaultResourceResolver;
import org.ink.core.vm.lang.InkObject;
import org.ink.core.vm.mirror.Mirror;
import org.ink.core.vm.modelinfo.ModelInfoFactory;
import org.ink.core.vm.modelinfo.ModelInfoRepository;
import org.ink.core.vm.modelinfo.relations.IsInstanceOfRelation;

public class Examples {
	
	private static class ModelQueriesResourceResolver extends DefaultResourceResolver{
		@Override
		public boolean enableEagerFetch() {
			return true;
		}
	}
	
	public static void main(String[] args) {
		VMConfig.setInstantiationStrategy(new ModelQueriesResourceResolver());
		Context context = InkVM.instance().getContext();
		InkObject inkObject = context.getObject("example.customer:Customer");
		Mirror m = inkObject.reflect();
		if (inkObject != null) {
			ModelInfoRepository repo = ModelInfoFactory.getInstance();
			Collection<Mirror> temp = repo.findReferrers(m, IsInstanceOfRelation.getInstance(), true);
			if (temp != null) {
				for (Mirror r : temp) {
					System.out.println(r.getId());
				}
			}
		}
		
		ElementDescriptor d = m.getDescriptor();
		System.out.println(d.getResource().getAbsolutePath());
	}

}
