package org.nightlabs.keyreader.test;

import java.io.File;

import org.nightlabs.config.Config;
import org.nightlabs.keyreader.KeyReader;
import org.nightlabs.keyreader.KeyReaderImplementation;
import org.nightlabs.keyreader.KeyReaderMan;
import org.nightlabs.util.IOUtil;
import org.nightlabs.util.NLLocale;

public class Test
{

	public static void main(String[] args)
	{
		try {
			Config config = Config.createSharedInstance(new File(IOUtil.getTempDir(), "tmpConfig.xml")); //$NON-NLS-1$
			KeyReaderMan keyReaderMan = KeyReaderMan.createSharedInstance(config);

			System.out.println("Registered KeyReader implementations:"); //$NON-NLS-1$
			for(KeyReaderImplementation keyReaderImplementation : keyReaderMan.getKeyReaderImplementations()) {
				System.out.println("  * " + keyReaderImplementation.getKeyReaderClassName() + " - " + keyReaderImplementation.getName(NLLocale.getDefault())); //$NON-NLS-1$ //$NON-NLS-2$
			}

			KeyReader keyReader = keyReaderMan.createKeyReader("reader00"); //$NON-NLS-1$
			config.save();

			keyReader.openPort();
			try {
				Thread.sleep(30000);
			} finally {
				keyReader.close(true);
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

}
