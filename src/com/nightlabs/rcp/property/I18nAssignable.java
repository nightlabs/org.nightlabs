/**
 * <p> Project: com.nightlabs.gui </p>
 * <p> Copyright: Copyright (c) 2005 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 29.07.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package com.nightlabs.rcp.property;

import com.nightlabs.i18n.I18nText;
import com.nightlabs.util.IAssignable;

public interface I18nAssignable 
extends IAssignable
{
	I18nText getI18nText();
}
