package org.ink.core.vm.modelinfo;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.ink.core.vm.factory.Context;
import org.ink.core.vm.factory.InkVM;
import org.ink.core.vm.factory.internal.CoreNotations;
import org.ink.core.vm.lang.InkObject;
import org.ink.core.vm.modelinfo.relations.ExtendsRelation;
import org.ink.core.vm.modelinfo.relations.IsInstanceOfRelation;
import org.junit.Test;

public class ModelInfoRepositoryTest {

	private final Context context = InkVM.instance().getContext();

	@Test
	public void testModelInfoRepository() {
		InkObject mirror = context.getObject(CoreNotations.Ids.MIRROR);
		InkObject trait = context.getObject(CoreNotations.Ids.TRAIT);
		InkObject traitClass = context.getObject(CoreNotations.Ids.TRAIT_CLASS);
		ModelInfoWriteableRepository repo = ModelInfoFactory.getWriteableInstance();
		repo.register(mirror);
		Collection<InkObject> referrers = repo.findReferrers(trait, ExtendsRelation.getInstance());
		assertTrue(referrers != null);
		assertTrue(referrers.contains(mirror));
		referrers = repo.findReferrers(traitClass, IsInstanceOfRelation.getInstance());
		assertTrue(referrers != null);
		assertTrue(referrers.contains(mirror));

		repo.unregister(mirror);
		referrers = repo.findReferrers(trait, ExtendsRelation.getInstance());
		assertNull(referrers);
		referrers = repo.findReferrers(traitClass, IsInstanceOfRelation.getInstance());
		assertNull(referrers);
	}
}