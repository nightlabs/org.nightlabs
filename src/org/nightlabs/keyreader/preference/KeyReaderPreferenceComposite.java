package org.nightlabs.keyreader.preference;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.nightlabs.base.composite.ComboComposite;
import org.nightlabs.base.composite.ListComposite;
import org.nightlabs.base.composite.XComposite;
import org.nightlabs.config.Config;
import org.nightlabs.config.ConfigException;
import org.nightlabs.keyreader.KeyReaderImplementation;
import org.nightlabs.keyreader.KeyReaderImplementationRegistry;
import org.nightlabs.keyreader.KeyReaderUseCase;
import org.nightlabs.keyreader.KeyReaderUseCaseRegistry;
import org.nightlabs.keyreader.config.KeyReaderCf;
import org.nightlabs.keyreader.config.KeyReaderConfigModule;
import org.nightlabs.util.Utils;

public class KeyReaderPreferenceComposite
extends XComposite
{
	private ListComposite<KeyReaderUseCase> keyReaderUseCaseList;

	private XComposite detailComposite;
	private ComboComposite<KeyReaderImplementation> keyReaderImplementationCombo;
	private Label keyReaderImplementationClassName;

	public KeyReaderPreferenceComposite(Composite parent, int style)
	{
		super(parent, style);
		getGridLayout().numColumns = 2;

		keyReaderUseCaseList = new ListComposite<KeyReaderUseCase>(this, SWT.SINGLE, new LabelProvider() {
			@Override
			public String getText(Object element)
			{
				KeyReaderUseCase keyReaderUseCase = (KeyReaderUseCase) element;
				return keyReaderUseCase.getName() + " (" + keyReaderUseCase.getKeyReaderID() + ')';
			}
		});
		keyReaderUseCaseList.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event)
			{
				keyReaderUseCaseSelected();
			}
		});


		detailComposite = new XComposite(this, SWT.BORDER);

		keyReaderImplementationCombo = new ComboComposite<KeyReaderImplementation>(detailComposite, SWT.READ_ONLY | SWT.DROP_DOWN, new LabelProvider() {
			@Override
			public String getText(Object element)
			{
				KeyReaderImplementation keyReaderImplementation = (KeyReaderImplementation) element;
				return keyReaderImplementation.getName(Locale.getDefault());
			}
		}, "Driver");
		keyReaderImplementationCombo.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event)
			{
				KeyReaderImplementation keyReaderImplementation = keyReaderImplementationCombo.getSelectedElement();
				if (keyReaderImplementation == null)
					keyReaderImplementationClassName.setText("");
				else {
					keyReaderImplementationClassName.setText(
							keyReaderImplementation.getKeyReaderClassName());

					keyReaderCf.setKeyReaderClass(keyReaderImplementation.getKeyReaderClassName());
				}
			}
		});

		keyReaderImplementationClassName = new Label(detailComposite, SWT.NONE);
		keyReaderImplementationClassName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		load();
		keyReaderUseCaseSelected();
	}

	private KeyReaderUseCase keyReaderUseCase;
	private KeyReaderConfigModule keyReaderConfigModule;
	private KeyReaderCf keyReaderCf;
	private Map<String, KeyReaderCf> keyReaderID2keyReaderCfClone = new HashMap<String, KeyReaderCf>();

	private void keyReaderUseCaseSelected()
	{
		keyReaderUseCase = keyReaderUseCaseList.getSelectedElement();
		if (keyReaderUseCase != null) {
			detailComposite.setEnabled(true);
			keyReaderCf = keyReaderID2keyReaderCfClone.get(keyReaderUseCase.getKeyReaderID());
			if (keyReaderCf == null) {
				keyReaderCf = keyReaderConfigModule._getKeyReaderCf(keyReaderUseCase.getKeyReaderID()); // guaranteed to return a non-null result
				keyReaderCf = Utils.cloneSerializable(keyReaderCf);
				keyReaderID2keyReaderCfClone.put(keyReaderUseCase.getKeyReaderID(), keyReaderCf);
			}

			loadDataIntoUI();
		}
		else {
			keyReaderCf = null;
			detailComposite.setEnabled(false);
			clearUI();
		}
	}

	private void loadDataIntoUI()
	{
		KeyReaderImplementation keyReaderImplementation = className2KeyReaderImplementation.get(keyReaderCf.getKeyReaderClass());
		keyReaderImplementationCombo.selectElement(keyReaderImplementation);
		keyReaderImplementationClassName.setText(keyReaderCf.getKeyReaderClass());
	}

	private void clearUI()
	{
		keyReaderImplementationCombo.selectElement(null);
//		keyReaderImplementationCombo.setSelection(StructuredSelection.EMPTY);
	}

	private Map<String, KeyReaderImplementation> className2KeyReaderImplementation = new HashMap<String, KeyReaderImplementation>();

	public void load()
	{
		try {
			keyReaderConfigModule = (KeyReaderConfigModule) Config.sharedInstance().createConfigModule(KeyReaderConfigModule.class);
		} catch (ConfigException e) {
			throw new RuntimeException(e);
		}

		for (KeyReaderImplementation keyReaderImplementation : KeyReaderImplementationRegistry.sharedInstance().getKeyReaderImplementations())
			className2KeyReaderImplementation.put(keyReaderImplementation.getKeyReaderClassName(), keyReaderImplementation);

		keyReaderUseCaseList.removeAll();
		keyReaderUseCaseList.addElements(KeyReaderUseCaseRegistry.sharedInstance().getKeyReaderUseCases());

		keyReaderImplementationCombo.removeAll();
		keyReaderImplementationCombo.addElements(KeyReaderImplementationRegistry.sharedInstance().getKeyReaderImplementations());
	}

	public void save()
	{
		for (KeyReaderCf clone : keyReaderID2keyReaderCfClone.values())
			keyReaderConfigModule._addKeyReaderCf(Utils.cloneSerializable(clone));
	}

}
