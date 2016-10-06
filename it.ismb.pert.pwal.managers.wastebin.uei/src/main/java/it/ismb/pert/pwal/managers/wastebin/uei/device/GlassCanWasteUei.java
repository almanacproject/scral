/*
 * PWAL - UEI Manager
 * 
 * Copyright (c) 2014 Dario Bonino
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */
package it.ismb.pert.pwal.managers.wastebin.uei.device;

import it.ismb.pert.pwal.managers.wastebin.uei.device.descriptor.UeiDescriptor;
import it.ismb.pertlab.smartcity.api.GlassBin;

/**
 * @author <a href="mailto:dario.bonino@gmail.com">Dario Bonino</a>
 *
 */
public class GlassCanWasteUei extends AbstractUei
{
	
	/**
	 * @param descriptor
	 */
	public GlassCanWasteUei(UeiDescriptor descriptor)
	{
		super(descriptor);
		// TODO Auto-generated constructor stub
	}
	
	/* (non-Javadoc)
	 * @see it.ismb.pertlab.pwal.api.devices.interfaces.Device#getType()
	 */
	public String getType()
	{
		// return the bin type
		return GlassBin.class.getSimpleName();
	}
}
