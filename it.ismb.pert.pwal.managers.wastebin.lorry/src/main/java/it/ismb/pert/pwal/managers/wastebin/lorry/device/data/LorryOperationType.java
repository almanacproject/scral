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
 * An enumeration describing allowed lorry operations, i.e., charge e discharge.
 * @author <a href="mailto:dario.bonino@gmail.com">Dario Bonino</a>
 *
 */
public enum LorryOperationType
{
	CHARGE("C"), DISCHARGE("D");
	
	private String operationType;
	
	/**
	 * private constructor to prevent unwanted initializations
	 * 
	 * @param operationType
	 */
	private LorryOperationType(String operationType)
	{
		this.operationType = operationType;
	}
	
	/**
	 * Parse operation type from String, throws an exception if the operation is
	 * not supported, i.e., listed in the enumeration values
	 * 
	 * @param operationType
	 * @return
	 */
	public LorryOperationType fromString(String operationType)
	{
		// the resulting enum value
		LorryOperationType oType = null;
		
		// search for matches
		for (LorryOperationType enumType : LorryOperationType.values())
		{
			// match type
			if (enumType.operationType.equalsIgnoreCase(operationType))
			{
				// store the matching enum value
				oType = enumType;
				
				// stop search
				break;
			}
		}
		
		// if a value has been found, return it, otherwise throw an exception
		if (oType != null)
		{
			return oType;
		}
		else
		{
			throw new IllegalArgumentException("No Enum specified for this value");
		}
	}
	
}
