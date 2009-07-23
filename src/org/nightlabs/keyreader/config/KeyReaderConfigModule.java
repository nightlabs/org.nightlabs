/* *****************************************************************************
 * KeyReader - Framework library for reading keys from arbitrary reader-devices*
 * Copyright (C) 2004-2005 NightLabs - http://NightLabs.org                    *
 *                                                                             *
 * This library is free software; you can redistribute it and/or               *
 * modify it under the terms of the GNU Lesser General Public                  *
 * License as published by the Free Software Foundation; either                *
 * version 2.1 of the License, or (at your option) any later version.          *
 *                                                                             *
 * This library is distributed in the hope that it will be useful,             *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of              *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU           *
 * Lesser General Public License for more details.                             *
 *                                                                             *
 * You should have received a copy of the GNU Lesser General Public            *
 * License along with this library; if not, write to the                       *
 *     Free Software Foundation, Inc.,                                         *
 *     51 Franklin St, Fifth Floor,                                            *
 *     Boston, MA  02110-1301  USA                                             *
 *                                                                             *
 * Or get it online :                                                          *
 *     http://www.gnu.org/copyleft/lesser.html                                 *
 *                                                                             *
 *                                                                             *
 ******************************************************************************/

package org.nightlabs.keyreader.config;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.nightlabs.config.CfModList;
import org.nightlabs.config.ConfigModule;

/**
 * This is the config module storing all settings for all registered key readers.
 * The key readers are identified by their keyReaderID. For each key reader, an
 * instance of KeyReaderCf is created and registered.
 *
 * @author Marco Schulze
 * @version 1.0
 */
public class KeyReaderConfigModule extends ConfigModule
{
	private static final long serialVersionUID = 1L;

	public KeyReaderConfigModule()
  {
  }

  private CfModList<KeyReaderCf> keyReaders;
  public List<KeyReaderCf> getKeyReaders()
  {
    return keyReaders;
  }

  public void setKeyReaders(List<KeyReaderCf> keyReaders)
  {
  	if (keyReaders instanceof CfModList) {
  		this.keyReaders = (CfModList<KeyReaderCf>)keyReaders;
  		this.keyReaders.setOwnerCfMod(this);
  	}
  	else {
  		this.keyReaders = new CfModList<KeyReaderCf>();
  		this.keyReaders.setOwnerCfMod(this);
  		this.keyReaders.addAll(keyReaders);
  	}
  }

  /**
   * key: String keyReaderID<br/>
   * value: KeyReaderCf
   */
  private HashMap<String, KeyReaderCf> keyReadersMap = new HashMap<String, KeyReaderCf>();

  public KeyReaderCf _getKeyReaderCf(String keyReaderID)
  {
    KeyReaderCf keyReaderCf = keyReadersMap.get(keyReaderID);

    if (keyReaderCf == null) {
      keyReaderCf = new KeyReaderCf(keyReaderID);
      keyReaderCf.init();
      keyReadersMap.put(
        keyReaderID,
        keyReaderCf
      );
      keyReaders.add(keyReaderCf);
    } // if (keyReaderCf == null) {

    return keyReaderCf;
  }

  @Override
	public void init()
  {
    if (keyReaders == null)
      keyReaders = new CfModList<KeyReaderCf>();

    keyReaders.setOwnerCfMod(this);

    keyReadersMap.clear();
    for (KeyReaderCf keyReaderCf : keyReaders) {
      keyReaderCf.init();

      keyReadersMap.put(
        keyReaderCf.getKeyReaderID(),
        keyReaderCf
      );
    } // for (Iterator it = keyReaders.iterator(); it.hasNext(); ) {
  }

  public void _addKeyReaderCf(KeyReaderCf keyReaderCf)
  {
  	String keyReaderID = keyReaderCf.getKeyReaderID();
  	if (keyReadersMap.remove(keyReaderID) != null) {
  		for (Iterator<KeyReaderCf> it = keyReaders.iterator(); it.hasNext();) {
				KeyReaderCf krcf = it.next();
				if (keyReaderID.equals(krcf.getKeyReaderID())) {
					it.remove();
					break;
				}
			}
  	}

  	keyReaders.add(keyReaderCf);
  	keyReadersMap.put(keyReaderID, keyReaderCf);
  }

}
