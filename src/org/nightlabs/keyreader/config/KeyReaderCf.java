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

import java.io.Serializable;

import org.nightlabs.config.Initializable;
import org.nightlabs.connection.config.ConnectionCf;
import org.nightlabs.connection.rxtx.config.SerialConnectionCf;
import org.nightlabs.keyreader.DummyKeyReader;
import org.nightlabs.keyreader.KeyReaderSharingDevice;

/**
 * This class defines the configuration of one key reader.
 * 
 * @author Marco Schulze
 * @version 1.0
 */
public class KeyReaderCf
implements Initializable, Serializable
{
	private static final long serialVersionUID = 1L;

	public KeyReaderCf()
  {
  }

  public KeyReaderCf(String _keyReaderID)
  {
    this.keyReaderID = _keyReaderID;
  }

  private String keyReaderID;
  public String getKeyReaderID()
  {
    return keyReaderID;
  }
  public void setKeyReaderID(String _keyReaderID)
  {
    this.keyReaderID = _keyReaderID;
  }

  private String shareDeviceWith;

  /**
   * @return The name of the other key reader with which this key reader
   * shares the physical device.
   * 
   * @see org.nightlabs.keyreader.KeyReaderSharingDevice
   * @see #getSlot()
   */
  public String getShareDeviceWith() { return shareDeviceWith; }
  public void setShareDeviceWith(String shareDeviceWith)
  {
    this.shareDeviceWith = shareDeviceWith;
    if (shareDeviceWith != null && !"".equals(shareDeviceWith)) //$NON-NLS-1$
    	keyReaderClass = KeyReaderSharingDevice.class.getName();
  }

  private String keyReaderClass;
  public String getKeyReaderClass() { return keyReaderClass; }
  public void setKeyReaderClass(String _keyReaderClass)
  {
  	this.keyReaderClass = _keyReaderClass;
  	if (!KeyReaderSharingDevice.class.getName().equals(_keyReaderClass))
  		shareDeviceWith = "";	 //$NON-NLS-1$
  }

  private String slot;
  /**
   * The slot is necessary for combi devices. The low-level driver does only
   * dispatch events to the instance of KeyReader that matches the right slot.
   * Note, that most of the drivers ignore this, because they support only one
   * physical reader per connection. But for combi devices, this feature is
   * often essential.
   * <br/><br/>
   * Examble: You have a device
   * which combines two readers and one door. One reader is used for entry
   * and one for exit. If your device is only accessible by one driver (e.g.
   * because it uses a serial port which cannot be shared), you need to
   * configure only one KeyReader with the appropriate driver and for the other,
   * you use a <i>shareDeviceWith</i> setting.
   * <br/><br/>
   * Which slots are existing, is defined by the driver class.
   *
	 * @return Returns the slot.
	 * 
	 * @see org.nightlabs.keyreader.KeyReaderSharingDevice
	 * @see org.nightlabs.keyreader.KeyReader#fireKeyReadEvent(String slot, String key)
	 */
	public String getSlot() {
		return slot;
	}
	/**
	 * @param slot The slot to set.
	 */
	public void setSlot(String slot) {
		this.slot = slot;
	}

  private ConnectionCf connectionCf;
  
  /**
   * Normally, this method returns an inheritent of ConnectionCf and it
   * is necessary to cast it within your driver. You should check the type
   * before and throw a nice exception with a description which class has
   * been expected (means which hardware interfaces are supported by your
   * key reader and driver).
   *
   * @return Returns the port settings defined for this key reader.
   */
  public ConnectionCf getConnectionCf() { return connectionCf; }
  public void setConnectionCf(ConnectionCf connectionCf) { this.connectionCf = connectionCf; }

  public void init()
  {
    if (shareDeviceWith == null)
      shareDeviceWith = ""; //$NON-NLS-1$

    if ("".equals(shareDeviceWith)) { //$NON-NLS-1$
      if (keyReaderClass == null || "".equals(keyReaderClass)) //$NON-NLS-1$
        keyReaderClass = DummyKeyReader.class.getName();

      if (connectionCf == null)
        connectionCf = new SerialConnectionCf();
    }
    else {
      keyReaderClass = KeyReaderSharingDevice.class.getName();
      connectionCf = null;
    }
    
    if (slot == null)
    	slot = "Please see the documentation of your KeyReaderClass for valid slots."; //$NON-NLS-1$

    if (connectionCf != null)
      connectionCf.init();
  }

}
