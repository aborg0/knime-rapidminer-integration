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
package org.knime.core.node.defaultnodesettings;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nullable;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.knime.core.data.DataTableSpec;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.util.MutableInteger;

import com.mind_era.knime_rapidminer.knime.nodes.ProjectHandling;
import com.mind_era.knime_rapidminer.knime.nodes.ProjectHandling.AbstractProjectHandling;
import com.mind_era.knime_rapidminer.knime.nodes.RapidMinerInit;
import com.mind_era.knime_rapidminer.knime.nodes.util.KnimeExampleTable;
import com.mind_era.knime_rapidminer.knime.nodes.util.KnimeRepository;
import com.rapidminer.Process;
import com.rapidminer.example.Attribute;
import com.rapidminer.gui.AbstractUIState;
import com.rapidminer.gui.MainUIState;
import com.rapidminer.gui.RapidMinerGUI;
import com.rapidminer.operator.ports.metadata.AttributeMetaData;
import com.rapidminer.operator.ports.metadata.ExampleSetMetaData;
import com.rapidminer.operator.ports.metadata.MetaData;
import com.rapidminer.repository.RepositoryAccessor;
import com.rapidminer.tools.XMLException;
import com.vlsolutions.swing.docking.DockingContext;
import com.vlsolutions.swing.docking.DockingDesktop;
import com.vlsolutions.swing.toolbars.ToolBarContainer;

/**
 * A {@link DialogComponent} suitable for {@link SettingsModelRapidMinerProject}
 * with editor for the project/{@link Process}.
 *
 * @author Gabor Bakos
 */
public class DialogComponentRapidMinerProject extends
		DialogComponentProject<Process, SettingsModelRapidMinerProject>
		implements RepositoryAccessor, HasTableSpecAndRowId {
	private AbstractUIState state;
	private String rowIdColumnName;

	/**
	 * Constructs the {@link DialogComponentRapidMinerProject}.
	 *
	 * @param model
	 *            The {@link SettingsModelRapidMinerProject} containing the
	 *            settings.
	 */
	public DialogComponentRapidMinerProject(
			final SettingsModelRapidMinerProject model) {
		super(model, true, ".rmp", "");
//		SwingUtilities.invokeLater(() -> getControlsPanel().add(ComicDialog.makeDropDownButton()));
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.knime.core.node.defaultnodesettings.HasTableSpec#getFilteredTableSpecs
	 * ()
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<? extends DataTableSpec> getFilteredTableSpecs() {
		if (getLastTableSpecs() == null) {
			return Collections.emptyList();
		}
		return Arrays.stream(getLastTableSpecs()).flatMap(typeFilterFlatMap(DataTableSpec.class)).collect(Collectors.toList());
	}

	private static<I, T> Function<? super I, Stream<? extends T>> typeFilterFlatMap(final Class<? extends T> cls) {
		return o -> cls.isInstance(o) ? Stream.of(cls.cast(o)) : Stream.<T>empty();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.knime.core.node.defaultnodesettings.DialogComponentProject#createEditor
	 * (org.knime.core.node.defaultnodesettings.SettingsModelProject)
	 */
	@Override
	protected Entry<? extends Component, ProjectHandling<Process>> createEditor(
			final SettingsModelRapidMinerProject model) {
		RapidMinerInit.init(false);
		RapidMinerInit.setPreferences();
		final JPanel panel = new JPanel();
		panel.setPreferredSize(new Dimension(1100, 800));
		panel.setMaximumSize(new Dimension(1100, 800));
		state = new AbstractUIState(/*"design",*/ null, panel) {

			@Override
			public void exit(final boolean relaunch) {
				// Do nothing, we do not exit
			}

			@Override
			public boolean close() {
				metaDataUpdateQueue.shutdown();
				return true;
			}

			@Override
			public void updateRecentFileList() {
				// Do noting
			}

			@Override
			public void setTitle() {
				// Do nothing
			}
		};
		RapidMinerGUI.setMainFrame(state);
		state.getValidateAutomaticallyAction().setSelected(true);

		final DockingContext dockingContext = state.getDockingDesktop()
				.getContext();
		final KnimePerspective perspective = new KnimePerspective(
				dockingContext);
		final DockingDesktop dockingDesktop = state.getDockingDesktop();
		dockingDesktop.setPreferredSize(new Dimension(1000, 700));
		final ToolBarContainer toolBarContainer = ToolBarContainer
				.createDefaultContainer(true, true, true, true);
		toolBarContainer.setPreferredSize(new Dimension(1000, 750));
		final JScrollPane pane = new JScrollPane(toolBarContainer);
		pane.getViewport().setPreferredSize(new Dimension(1000, 850));
		toolBarContainer.add(dockingDesktop, BorderLayout.CENTER);
		perspective.showPerspective(KnimePerspective.DESIGN);
		return new AbstractMap.SimpleImmutableEntry<Component, ProjectHandling<Process>>(
				pane, new AbstractProjectHandling<Process>() {

					@Override
					public void load(final Process project) {
						state.setProcess(project, false);
						setInputPorts(state);
					}

					@Override
					public void saveAs(final File file) throws IOException {
						state.getProcess().save(file);
					}

					@Override
					public byte[] getContent() throws IOException, XMLException {
						return SettingsModelRapidMinerProject
								.saveProcessAsByteArray(state.getProcess());
					}

					@Override
					public void onInputSpecChange(final PortObjectSpec[] specs) {
						setInputPorts(state);
					}
				});
	}

	/**
	 * @return The last tablespecs available to this dialogcomponent, and all
	 *         it's inner objects.
	 * @see #getLastTableSpecs()
	 */
	@Deprecated
	protected PortObjectSpec[] getLastTableSpecs2() {
		return getLastTableSpecs();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.knime.core.node.defaultnodesettings.DialogComponentProject#
	 * checkConfigurabilityBeforeLoad(org.knime.core.node.port.PortObjectSpec[])
	 */
	@Override
	protected void checkConfigurabilityBeforeLoad(final PortObjectSpec[] specs)
			throws NotConfigurableException {
		super.checkConfigurabilityBeforeLoad(specs);
		state.getProcess().setRepositoryAccessor(this);
		final MutableInteger idx = new MutableInteger(0);
		state.getProcess()
				.getContext()
				.setInputRepositoryLocations(
						getFilteredTableSpecs().stream().map(t -> "//" + KnimeRepository.KNIME +  "/"
								+ KnimeRepository.KnimeIOObjectEntry.KNIME_TABLE + idx.inc()).collect(Collectors.toList()));

		state.getProcess().getRootOperator()
				.deliverInputMD(createMetaData(specs));
	}

	/**
	 * Sets the input ports to the KNIME input data.
	 *
	 * @param state
	 *            The RapidMiner {@link MainUIState}.
	 */
	protected void setInputPorts(final MainUIState state) {
		final Process process = state.getProcess();
		process.setRepositoryAccessor(this);
		process.getContext().setInputRepositoryLocations(
				generateLocations(process, this));
		process.getRootOperator().checkAll();
	}

	/**
	 * Generates the input repository locations with the KNIME inputs.
	 *
	 * @param process
	 *            The {@link Process} where the special locations will be used.
	 * @param hasTableSpec
	 *            The content with {@link DataTableSpec}s.
	 * @return The locations for the KNIME input sources.
	 */
	public static List<String> generateLocations(final Process process,
			final HasTableSpecAndRowId hasTableSpec) {
		final MutableInteger idx = new MutableInteger(0);
		final List<String> newLocations =
				hasTableSpec.getFilteredTableSpecs().stream().map(t -> "//"
						+ KnimeRepository.KNIME
						+ "/"
						+ KnimeRepository.KnimeIOObjectEntry.KNIME_TABLE
						+ idx.inc()).collect(Collectors.toList());
		newLocations.addAll(process
				.getContext().getInputRepositoryLocations().stream().skip(newLocations
				.size()).collect(Collectors.toList()));
		return newLocations;
	}

	/**
	 * Creates the {@link MetaData} for the {@code lastTableSpec} array.
	 *
	 * @param lastTableSpecs
	 *            The table specs.
	 * @return A list of the {@link MetaData} created from the
	 *         {@link DataTableSpec}s.
	 */
	protected List<MetaData> createMetaData(
			final PortObjectSpec[] lastTableSpecs) {
		return Arrays.stream(lastTableSpecs).filter(meta -> meta != null).map(meta -> createMetaData(meta, rowIdColumnName != null, rowIdColumnName)).collect(Collectors.toList());
	}

	/**
	 * @param tableSpec
	 *            The {@link DataTableSpec} to transform.
	 * @param withRowIds
	 *            If {@code true}, the row id will be used as an input column.
	 * @param rowIdColumnName
	 *            This will be the name of the row id input column in
	 *            RapidMiner. {@code null} iff not {@code withRowIds}.
	 * @return The RapidMiner {@link MetaData} for the {@code tableSpec}.
	 */
	public static MetaData createMetaData(final PortObjectSpec tableSpec,
			final boolean withRowIds, final String rowIdColumnName) {
		final DataTableSpec spec = (DataTableSpec) tableSpec;
		return new ExampleSetMetaData(KnimeExampleTable
				.createAttributes(spec, withRowIds, rowIdColumnName).stream().map(
				new Function<Attribute, AttributeMetaData>() {
//			private final RoleHandler roleHandler = new RoleHandler(RapidMinerNodePlugin.getDefault().getRoleRegistry());
//			private final Map<String, Collection<? extends Role>> roles = roleHandler.roles(spec);
					@Override
					public AttributeMetaData apply(final Attribute attribute) {
						final AttributeMetaData ret = new AttributeMetaData(attribute);
//						final Collection<? extends Role> collection = roles.get(attribute.getName());
//						if (collection != null && collection.size() > 0) {
//							ret.setRole(RoleRepresentationMapping.getInstance()
//									.rapidMinerRoleNameOf(
//											collection.iterator().next()));
//						}
						return ret;
					}
				}).collect(Collectors.toList()));
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (state == null ? 0 : state.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final DialogComponentRapidMinerProject other = (DialogComponentRapidMinerProject) obj;
		if (state == null) {
			if (other.state != null) {
				return false;
			}
		} else if (state != other.state) {
			return false;
		}
		return true;
	}

	/**
	 * @param rowIdColumnName
	 *            The row id column name. If {@code null}, that means the input
	 *            row id is not available, and new row id will be generated.
	 */
	public void setRowIdColumnName(@Nullable final String rowIdColumnName) {
		final boolean areEquals = this.rowIdColumnName == null
				&& rowIdColumnName == null || this.rowIdColumnName != null
				&& this.rowIdColumnName.equals(rowIdColumnName);
		this.rowIdColumnName = rowIdColumnName;
		if (!areEquals) {
			setInputPorts(state);
		}
	}

	/**
	 * @return the rowIdColumnName
	 */
	@Override
	public @Nullable
	String getRowIdColumnName() {
		return rowIdColumnName;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.knime.core.node.defaultnodesettings.HasTableSpecAndRowId#isWithRowIds
	 * ()
	 */
	@Override
	public boolean isWithRowIds() {
		return rowIdColumnName != null;
	}
}
