package gov.miamidade.hgowl.plugin.owl;

import java.io.File;

import gov.miamidade.hgowl.plugin.HGOwlProperties;

import org.hypergraphdb.app.owl.HGDBApplication;
import org.hypergraphdb.app.owl.HGDBOntologyRepository;
import org.hypergraphdb.app.owl.versioning.VHGDBOntologyRepository;
import org.hypergraphdb.app.owl.versioning.distributed.VDHGDBOntologyRepository;
import org.protege.editor.core.OntologyRepository;
import org.protege.editor.core.OntologyRepositoryFactory;

/**
 * HGOwlOntologyRepositoryFactory.
 * A factory for Hypergraph backed Protege OntologyRepositories.
 * 
 * @author Thomas Hilpold (GIC/Miami-Dade County)
 * @created Sep 26, 2011
 */
public class HGOwlOntologyRepositoryFactory extends OntologyRepositoryFactory {

	HGDBOntologyRepository dbRepository;
	
	@Override
	public void initialise() throws Exception {
		String hyperGraphLocation = HGOwlProperties.getInstance().getHgLocationFolderPath();
		try {
			File f = new File(hyperGraphLocation);
			f.mkdirs();
			HGDBOntologyRepository.setHypergraphDBLocation(hyperGraphLocation);
		} catch (RuntimeException e) {
			System.err.println("EXCEPTION setting preferred Hypergraph location:" + e);
			System.err.println("Default will be used:" + HGDBOntologyRepository.getHypergraphDBLocation());
		}
		if (HGDBApplication.DISTRIBUTED) {
			dbRepository = VDHGDBOntologyRepository.getInstance();
		} else if (HGDBApplication.VERSIONING) {
			dbRepository = VHGDBOntologyRepository.getInstance();
		} else {
			dbRepository = HGDBOntologyRepository.getInstance();
		}
	}

	@Override
	public void dispose() throws Exception {
		dbRepository.dispose();
		dbRepository = null;
	}

	@Override
	public OntologyRepository createRepository() {
		OntologyRepository r;
		if (dbRepository == null) throw new IllegalStateException("Cannot create HGOwlOntologyRepository. dbRepository was null.");
		if (dbRepository instanceof VHGDBOntologyRepository) {
			r = new VHGOwlOntologyRepository("Hypergraph with Versioning" , (VHGDBOntologyRepository)dbRepository);
		} else {
			r = new HGOwlOntologyRepository("Hypergraph" , dbRepository);
		}
		return r; 
	}
}