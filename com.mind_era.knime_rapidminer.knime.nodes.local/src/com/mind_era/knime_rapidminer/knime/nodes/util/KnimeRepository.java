/*  RapidMiner Integration for KNIME
 *  Copyright (C) 2012 Mind Eratosthenes Kft.
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.mind_era.knime_rapidminer.knime.nodes.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.swing.Action;

import org.knime.core.data.DataTableSpec;
import org.knime.core.node.defaultnodesettings.HasTableSpecAndRowId;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.mind_era.guava.helper.data.Zip;
import com.rapidminer.example.Attribute;
import com.rapidminer.operator.IOObject;
import com.rapidminer.operator.Operator;
import com.rapidminer.operator.ports.metadata.AttributeMetaData;
import com.rapidminer.operator.ports.metadata.ExampleSetMetaData;
import com.rapidminer.operator.ports.metadata.MetaData;
import com.rapidminer.repository.BlobEntry;
import com.rapidminer.repository.DataEntry;
import com.rapidminer.repository.Entry;
import com.rapidminer.repository.Folder;
import com.rapidminer.repository.IOObjectEntry;
import com.rapidminer.repository.MalformedRepositoryLocationException;
import com.rapidminer.repository.ProcessEntry;
import com.rapidminer.repository.Repository;
import com.rapidminer.repository.RepositoryException;
import com.rapidminer.repository.RepositoryListener;
import com.rapidminer.repository.RepositoryLocation;
import com.rapidminer.repository.gui.RepositoryConfigurationPanel;
import com.rapidminer.tools.ProgressListener;

/**
 * The RapidMiner representation of the KNIME data sources.
 * 
 * @author Gabor Bakos
 */
public class KnimeRepository implements Repository {

	/**
	 * The name of the repository (after the '//' part).
	 */
	public static final String KNIME = "KNIME";

	/**
	 * @author Gabor
	 * 
	 */
	public class KnimeIOObjectEntry implements IOObjectEntry {

		/**
		 * The naming scheme prefix for the knime tables.
		 */
		public static final String KNIME_TABLE = "KNIME table ";
		private final DataTableSpec spec;
		private final int index;
		private long lastModDate;

		/**
		 * @param spec
		 * @param idx
		 */
		KnimeIOObjectEntry(final DataTableSpec spec, final int idx) {
			this.spec = spec;
			this.index = idx;
			lastModDate = new Date().getTime();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.rapidminer.repository.Entry#getType()
		 */
		@Override
		public String getType() {
			return "data";
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.rapidminer.repository.DataEntry#getSize()
		 */
		@Override
		public long getSize() {
			// TODO Auto-generated method stub
			return 0;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.rapidminer.repository.DataEntry#getDate()
		 */
		@Override
		public long getDate() {
			return lastModDate;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.rapidminer.repository.Entry#getName()
		 */
		@Override
		public String getName() {
			return KNIME_TABLE + index;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.rapidminer.repository.Entry#getOwner()
		 */
		@Override
		public String getOwner() {
			return KnimeRepository.this.getName();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.rapidminer.repository.Entry#getDescription()
		 */
		@Override
		public String getDescription() {
			return spec.getName();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.rapidminer.repository.Entry#isReadOnly()
		 */
		@Override
		public boolean isReadOnly() {
			return true;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.rapidminer.repository.Entry#rename(java.lang.String)
		 */
		@Override
		public boolean rename(final String newName) throws RepositoryException {
			throw new RepositoryException("Rename not supported.");
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.rapidminer.repository.Entry#move(com.rapidminer.repository.Folder
		 * )
		 */
		@Override
		public boolean move(final Folder newParent) throws RepositoryException {
			throw new RepositoryException("Move not supported.");
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.rapidminer.repository.Entry#getContainingFolder()
		 */
		@Override
		public Folder getContainingFolder() {
			return KnimeRepository.this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.rapidminer.repository.Entry#willBlock()
		 */
		@Override
		public boolean willBlock() {
			return false;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.rapidminer.repository.Entry#getLocation()
		 */
		@Override
		public RepositoryLocation getLocation() {
			try {
				return new RepositoryLocation("//"
						+ KnimeRepository.this.getName() + "/" + getName());
			} catch (final MalformedRepositoryLocationException e) {
				throw new RuntimeException(e);
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.rapidminer.repository.Entry#delete()
		 */
		@Override
		public void delete() throws RepositoryException {
			throw new RepositoryException("Delete not supported.");
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.rapidminer.repository.Entry#getCustomActions()
		 */
		@Override
		public Collection<Action> getCustomActions() {
			return Collections.emptyList();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.rapidminer.repository.DataEntry#getRevision()
		 */
		@Override
		public int getRevision() {
			// TODO Auto-generated method stub
			return 0;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.rapidminer.repository.IOObjectEntry#retrieveData(com.rapidminer
		 * .tools.ProgressListener)
		 */
		@Override
		public IOObject retrieveData(final ProgressListener l)
				throws RepositoryException {
			// TODO Auto-generated method stub
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.rapidminer.repository.IOObjectEntry#retrieveMetaData()
		 */
		@Override
		public MetaData retrieveMetaData() throws RepositoryException {
			return new ExampleSetMetaData(Lists.transform(
					KnimeExampleTable.createAttributes(spec,
							model.isWithRowIds(), model.getRowIdColumnName()),
					new Function<Attribute, AttributeMetaData>() {
						@Override
						public AttributeMetaData apply(final Attribute attribute) {
							return new AttributeMetaData(attribute);
						}
					}));
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.rapidminer.repository.IOObjectEntry#getObjectClass()
		 */
		@Override
		public Class<? extends IOObject> getObjectClass() {
			// TODO Auto-generated method stub
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.rapidminer.repository.IOObjectEntry#storeData(com.rapidminer.
		 * operator.IOObject, com.rapidminer.operator.Operator,
		 * com.rapidminer.tools.ProgressListener)
		 */
		@Override
		public void storeData(final IOObject data,
				final Operator callingOperator, final ProgressListener l)
				throws RepositoryException {
			throw new RepositoryException("Storing data is not supported");
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.rapidminer.repository.Entry#move(com.rapidminer.repository.Folder
		 * , java.lang.String)
		 */
		//@Override
		public boolean move(final Folder arg0, final String arg1)
				throws RepositoryException {
			throw new RepositoryException("Move is not supported");
		}

	}

	private List<RepositoryListener> listeners = new ArrayList<RepositoryListener>();
	private final HasTableSpecAndRowId model;

	/**
	 * @param model
	 */
	public KnimeRepository(final HasTableSpecAndRowId model) {
		this.model = model;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.rapidminer.repository.Folder#getDataEntries()
	 */
	@Override
	public List<DataEntry> getDataEntries() throws RepositoryException {
		return Lists
				.transform(
						Zip.zipWithIndexList(model.getFilteredTableSpecs(), 1),
						new Function<java.util.Map.Entry<DataTableSpec, Integer>, DataEntry>() {
							@Override
							public DataEntry apply(
									final java.util.Map.Entry<DataTableSpec, Integer> inputEntry) {
								return new KnimeIOObjectEntry(inputEntry
										.getKey(), inputEntry.getValue()
										.intValue());
							}
						});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.rapidminer.repository.Folder#getSubfolders()
	 */
	@Override
	public List<Folder> getSubfolders() throws RepositoryException {
		return Collections.emptyList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.rapidminer.repository.Folder#refresh()
	 */
	@Override
	public void refresh() throws RepositoryException {
		// TODO Auto-generated method stub
		// Maybe do nothing?
		// for (RepositoryListener listener : listeners) {
		// for (DataEntry entry : getDataEntries()) {
		// listener.entryAdded(entry, this);
		// }
		// }
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.rapidminer.repository.Folder#containsEntry(java.lang.String)
	 */
	@Override
	public boolean containsEntry(final String name) throws RepositoryException {
		for (final DataEntry entry : getDataEntries()) {
			if (entry.getName().equals(name)) {
				return true;
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.rapidminer.repository.Folder#createFolder(java.lang.String)
	 */
	@Override
	public Folder createFolder(final String name) throws RepositoryException {
		throw new RepositoryException("Cannot create folders here.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.rapidminer.repository.Folder#createIOObjectEntry(java.lang.String,
	 * com.rapidminer.operator.IOObject, com.rapidminer.operator.Operator,
	 * com.rapidminer.tools.ProgressListener)
	 */
	@Override
	public IOObjectEntry createIOObjectEntry(final String name,
			final IOObject ioobject, final Operator callingOperator,
			final ProgressListener progressListener) throws RepositoryException {
		throw new RepositoryException("Cannot create new objects here.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.rapidminer.repository.Folder#createProcessEntry(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public ProcessEntry createProcessEntry(final String name,
			final String processXML) throws RepositoryException {
		throw new RepositoryException("Cannot create new process here.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.rapidminer.repository.Folder#createBlobEntry(java.lang.String)
	 */
	@Override
	public BlobEntry createBlobEntry(final String name)
			throws RepositoryException {
		throw new RepositoryException("Cannot create new blob here.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.rapidminer.repository.Entry#getName()
	 */
	@Override
	public String getName() {
		return KNIME;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.rapidminer.repository.Entry#getType()
	 */
	@Override
	public String getType() {
		return "data";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.rapidminer.repository.Entry#getOwner()
	 */
	@Override
	public String getOwner() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.rapidminer.repository.Entry#getDescription()
	 */
	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.rapidminer.repository.Entry#isReadOnly()
	 */
	@Override
	public boolean isReadOnly() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.rapidminer.repository.Entry#rename(java.lang.String)
	 */
	@Override
	public boolean rename(final String newName) throws RepositoryException {
		throw new RepositoryException("Rename not supported.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.rapidminer.repository.Entry#move(com.rapidminer.repository.Folder)
	 */
	@Override
	public boolean move(final Folder newParent) throws RepositoryException {
		throw new RepositoryException("Move not supported.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.rapidminer.repository.Entry#getContainingFolder()
	 */
	@Override
	public Folder getContainingFolder() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.rapidminer.repository.Entry#willBlock()
	 */
	@Override
	public boolean willBlock() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.rapidminer.repository.Entry#getLocation()
	 */
	@Override
	public RepositoryLocation getLocation() {
		try {
			return new RepositoryLocation("//" + getName());
		} catch (final MalformedRepositoryLocationException e) {
			throw new RuntimeException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.rapidminer.repository.Entry#delete()
	 */
	@Override
	public void delete() throws RepositoryException {
		throw new RepositoryException("Delete not supported.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.rapidminer.repository.Entry#getCustomActions()
	 */
	@Override
	public Collection<Action> getCustomActions() {
		return Collections.emptyList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.rapidminer.repository.Repository#addRepositoryListener(com.rapidminer
	 * .repository.RepositoryListener)
	 */
	@Override
	public void addRepositoryListener(final RepositoryListener l) {
		listeners.add(l);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.rapidminer.repository.Repository#removeRepositoryListener(com.rapidminer
	 * .repository.RepositoryListener)
	 */
	@Override
	public void removeRepositoryListener(final RepositoryListener l) {
		listeners.remove(l);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.rapidminer.repository.Repository#locate(java.lang.String)
	 */
	@Override
	public Entry locate(final String string) throws RepositoryException {
		final List<? extends DataTableSpec> tableSpecs = model
				.getFilteredTableSpecs();
		for (int i = tableSpecs.size(); i-- > 0;) {
			final DataTableSpec dataTableSpec = tableSpecs.get(i);
			if (dataTableSpec.getName().equals(string)
					|| string.equalsIgnoreCase("/"
							+ KnimeIOObjectEntry.KNIME_TABLE + (i + 1))) {
				return new KnimeIOObjectEntry(dataTableSpec, i);
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.rapidminer.repository.Repository#getState()
	 */
	@Override
	public String getState() {
		return model.getFilteredTableSpecs().isEmpty() ? "Not connected" : "OK";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.rapidminer.repository.Repository#getIconName()
	 */
	@Override
	public String getIconName() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.rapidminer.repository.Repository#createXML(org.w3c.dom.Document)
	 */
	@Override
	public Element createXML(final Document doc) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.rapidminer.repository.Repository#shouldSave()
	 */
	@Override
	public boolean shouldSave() {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.rapidminer.repository.Repository#postInstall()
	 */
	@Override
	public void postInstall() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.rapidminer.repository.Repository#preRemove()
	 */
	@Override
	public void preRemove() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.rapidminer.repository.Repository#isConfigurable()
	 */
	@Override
	public boolean isConfigurable() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.rapidminer.repository.Repository#makeConfigurationPanel()
	 */
	@Override
	public RepositoryConfigurationPanel makeConfigurationPanel() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.rapidminer.repository.Folder#canRefreshChild(java.lang.String)
	 */
	//@Override
	public boolean canRefreshChild(final String arg0)
			throws RepositoryException {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.rapidminer.repository.Entry#move(com.rapidminer.repository.Folder,
	 * java.lang.String)
	 */
	//@Override
	public boolean move(final Folder arg0, final String arg1)
			throws RepositoryException {
		throw new RepositoryException("Move is not supported.");
	}

}
