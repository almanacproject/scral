/*
 * PWAL - Lorry Data Manager
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
package it.ismb.pert.pwal.managers.wastebin.lorry.device.data;

/**
 * An enumeration for waste types collected by the lorries interfaced in the
 * ALMANAC Turin pilot. It must be noted that, even if by design enumerations
 * are backed on integers, we selected to make the association explicit as the
 * integer related to a given type is not under our control, and there is no
 * guarantee that it will start from 1 or that it will not have gaps.
 * 
 * @author <a href="mailto:dario.bonino@gmail.com">Dario Bonino</a>
 *
 */
public enum LorryWasteType
{
	ORGANIC(1), PLASTIC(2), GLASS_AND_CANS(3), NOT_RECYCLABLE(4);
	
	private final int type;
	
	// private constructor that associates and arbitrary number to one enum
	// value
	private LorryWasteType(int type)
	{
		this.type = type;
	}
	
	/**
	 * Gets the {@link LorryWasteType} value corresponding to the given integer.
	 * Throws an {@link IllegalArgumentException} if no enumeration value exists
	 * that corresponds to the given integer value
	 * 
	 * @param type
	 *            The integer value of the enum entry
	 * @return The enum entry value
	 * @throws IllegalArgumentException
	 *             if no entry is available for the given integer
	 */
	public static LorryWasteType fromIntType(int type) throws IllegalArgumentException
	{
		// initially no entry matches
		LorryWasteType eType = null;
		
		// search for matches
		for (LorryWasteType enumType : LorryWasteType.values())
		{
			// match type
			if (enumType.type == type)
			{
				// store the matching enum value
				eType = enumType;
				
				// stop search
				break;
			}
		}
		
		// if a value has been found, return it, otherwise throw an exception
		if (eType != null)
		{
			return eType;
		}
		else
		{
			throw new IllegalArgumentException("No Enum specified for this integer value");
		}
	}
}
