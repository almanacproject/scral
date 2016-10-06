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
package it.ismb.pert.pwal.managers.wastebin.uei.device.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

/**
 * @author <a href="mailto:dario.bonino@gmail.com">Dario Bonino</a>
 *
 */
public class PersonData
{
	/**
	 * <pre>
	 * {
	 * 	"name":"Pippo",
	 * 	"surname":"Pluto",
	 * 	"title":"Ph.D",
	 * 	"tagId":"ldfwkuhgwdkjhr",
	 * 	"tagDescription":"hwdfjhdjhdfjhgv wdfjhbfw wfejhefw wefhbfrw"
	 * }
	 * </pre>
	 */
	// the "title" of submitting user
	@JsonPropertyDescription("The title of the person submitting this waste")
	@JsonProperty(value = "title")
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private String title;
	
	// the Name of submitting user, if any
	@JsonPropertyDescription("The name of the person submitting this waste")
	@JsonProperty(value = "name")
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private String name;
	
	// the surname of submitting user, if any
	@JsonPropertyDescription("The surname of the person submitting this waste")
	@JsonProperty(value = "surname")
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private String surname;
	
	// the id of the tag of the submitting person
	@JsonPropertyDescription("The tag of the user disposing the waste")
	@JsonProperty(value = "tagId")
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private String tagId;
	
	// the description of the tag of the submitting person
	@JsonPropertyDescription("The human-readable description of the tag of the user disposing the waste")
	@JsonProperty(value = "tagDescription")
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private String tagDescription;
	
	/**
	 * Empty constructor to implement the bean instantiation pattern
	 */
	public PersonData()
	{
		// TODO Auto-generated constructor stub
	}
	
	

	/**
	 * Constructor with all needed fields
	 * @param title The user title (e.g., Ph.D, MSc, etc.)
	 * @param name The user name
	 * @param surname The user surname
	 * @param tagId The tag id
	 * @param tagDescription The tag description
	 */
	public PersonData(String title, String name, String surname, String tagId, String tagDescription)
	{
		this.title = title;
		this.name = name;
		this.surname = surname;
		this.tagId = tagId;
		this.tagDescription = tagDescription;
	}



	/**
	 * Gets the title of the user disposing the waste
	 * @return the title
	 */
	@JsonPropertyDescription("The title of the person submitting this waste")
	@JsonProperty(value = "title")
	public String getTitle()
	{
		return title;
	}

	/**
	 * Sets the title of the user disposing the waste
	 * @param title the title to set
	 */
	@JsonPropertyDescription("The title of the person submitting this waste")
	@JsonProperty(value = "title")
	public void setTitle(String title)
	{
		this.title = title;
	}

	/**
	 * Gets the name of the user disposing the waste
	 * @return the name
	 */
	@JsonPropertyDescription("The name of the person submitting this waste")
	@JsonProperty(value = "name")
	public String getName()
	{
		return name;
	}

	/**
	 * Sets the name of the user disposing the waste
	 * @param name the name to set
	 */
	@JsonPropertyDescription("The name of the person submitting this waste")
	@JsonProperty(value = "name")
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Gets the surname of the user disposing the waste
	 * @return the surname
	 */
	@JsonPropertyDescription("The surname of the person submitting this waste")
	@JsonProperty(value = "surname")
	public String getSurname()
	{
		return surname;
	}

	/**
	 * Sets the surname of the user disposing the waste
	 * @param surname the surname to set
	 */
	@JsonPropertyDescription("The surname of the person submitting this waste")
	@JsonProperty(value = "surname")
	public void setSurname(String surname)
	{
		this.surname = surname;
	}

	/**
	 * Gets the id of the RF-ID tag associated to the user
	 * @return the tagId
	 */
	@JsonPropertyDescription("The tag of the user disposing the waste")
	@JsonProperty(value = "tagId")
	public String getTagId()
	{
		return tagId;
	}

	/**
	 * Sets the id of the RF-ID tag assigned to the user
	 * @param tagId the tagId to set
	 */
	@JsonPropertyDescription("The tag of the user disposing the waste")
	@JsonProperty(value = "tagId")
	public void setTagId(String tagId)
	{
		this.tagId = tagId;
	}

	/**
	 * Gets the human-readable description of the RF-ID tag assigned to the user
	 * @return the tagDescription
	 */
	@JsonPropertyDescription("The human-readable description of the tag of the user disposing the waste")
	@JsonProperty(value = "tagDescription")
	public String getTagDescription()
	{
		return tagDescription;
	}

	/**
	 * Sets the human-readable description of the RF-ID tag assigned to the user
	 * @param tagDescription the tagDescription to set
	 */
	@JsonPropertyDescription("The human-readable description of the tag of the user disposing the waste")
	@JsonProperty(value = "tagDescription")
	public void setTagDescription(String tagDescription)
	{
		this.tagDescription = tagDescription;
	}
	
	
	
}
