/*
 * SmartCityAPI - KML to N3 conversion
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
package it.ismb.pertlab.smartcity.data.kml;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import it.ismb.pertlab.smartcity.api.District;
import it.ismb.pertlab.smartcity.api.GeoPoint;
import it.ismb.pertlab.smartcity.api.Quarter;
import it.ismb.pertlab.smartcity.api.SmartCity;
import it.ismb.pertlab.smartcity.api.WasteBin;
import it.ismb.pertlab.smartcity.data.CityRenderer;
import it.ismb.pertlab.smartcity.data.geonames.GeoNamesHelper;
import it.ismb.pertlab.smartcity.data.jsonld.serialization.JsonLDCityRenderer;
import it.ismb.pertlab.smartcity.data.n3.serialization.N3CityRenderer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import de.micromata.opengis.kml.v_2_2_0.Coordinate;
import de.micromata.opengis.kml.v_2_2_0.Data;
import de.micromata.opengis.kml.v_2_2_0.Geometry;
import de.micromata.opengis.kml.v_2_2_0.MultiGeometry;
import de.micromata.opengis.kml.v_2_2_0.Placemark;
import de.micromata.opengis.kml.v_2_2_0.Point;
import de.micromata.opengis.kml.v_2_2_0.Polygon;
import de.micromata.opengis.kml.v_2_2_0.Snippet;

/**
 * A class which takes care of converting a set of kml files, organized in
 * several folders, into an N3 city representation.
 * 
 * It is almost custom built on available data about Turin, however it should be
 * easily applicable to any other smart city, given that the city can be divided
 * in quarters.
 *
 */
@SuppressWarnings("deprecation")
public class KML2City
{
	// the city KML
	private String cityKML;
	
	// the city quarters as either single or multiple KML files
	private String quartersKML;
	
	// the city districts as either single or multiple KML files
	private String districtsKML;
	
	// the city waste bins as either a single or multiple KML files
	private String wasteBinsKML;
	
	// the bin prefix to id map as a property file
	private Properties binIdToClass;
	
	// the templates folder
	private String templatesFolder;
	
	// the prefix-id separator, if exists
	private char prefixIdSeparator;
	
	// the geonames helper to use for gathering missing city data
	private GeoNamesHelper geoNamesHelper;
	
	// the kml helper to handle common kml operations
	private KMLHelper kmlHelper;
	
	/**
	 * The class constructor, prepares all the data structures needed to
	 * generate the single N3 representation of the city, given the input kml
	 * files.
	 * 
	 * @param cityKML
	 *            , the city definition (boundaries) in KML
	 * @param quartersKML
	 *            , the quarters definition (boundaries) in either single or
	 *            separate KML(s)
	 * @param wasteBinsKML
	 *            , the wasteBin definitions in either single or multiple KML(s)
	 * @param binIdToClass
	 *            , the mapping between any prefix in the Bin files and the
	 *            corresponding classes to generate
	 * @param templatesFolder
	 *            the folder containing the class / individual templates.
	 */
	public KML2City(String cityKML, String districtsKML, String quartersKML, String wasteBinsKML,
			Properties binIdToClass, char prefixIdSeparator, String templatesFolder)
	{
		// store instance data
		this.cityKML = cityKML;
		this.districtsKML = districtsKML;
		this.quartersKML = quartersKML;
		this.wasteBinsKML = wasteBinsKML;
		this.binIdToClass = binIdToClass;
		this.prefixIdSeparator = prefixIdSeparator;
		this.templatesFolder = templatesFolder;
		
		// initialize the geonames helper
		this.geoNamesHelper = new GeoNamesHelper();
		
		// initialize the kml helper
		this.kmlHelper = new KMLHelper();
	}
	
	/**
	 * Provides back the smart city api representation of a city, as a
	 * {@link SmartCity} instance.
	 * 
	 * @param contryCode
	 *            The ISO country code (e.g., it, en, etc.).
	 * @return The corresponding {@link SmartCity} instance.
	 */
	public SmartCity getSmartCity(String countryCode)
	{
		// the smart city model class
		SmartCity city = new SmartCity();
		
		// parse the KML file
		Collection<Placemark> allPlacemarks = this.kmlHelper.getAllPlacemarks(this.cityKML);
		
		// assume a single place mark for the city
		Placemark placemark = allPlacemarks.iterator().next();
		
		// get the place mark name, which will be the city name
		String name = placemark.getName();
		
		// format the city name
		name = name.substring(0, 1).toUpperCase() + name.substring(1, name.length()).toLowerCase();
		
		// store the city name
		city.setName(name);
		
		// generate and store the city url
		city.setUrl(name.replaceAll("[\\s-.]", "_"));
		
		// get the geometry object associated to the city
		Geometry placemarkGeometry = placemark.getGeometry();
		
		// if the geometry is a polygon, treat it as the city boundary
		if (placemarkGeometry instanceof Polygon)
		{
			List<Coordinate> coordinates = ((Polygon) placemarkGeometry).getOuterBoundaryIs().getLinearRing()
					.getCoordinates();
			
			city.setBoundary(this.kmlHelper.getKMLPolygonAsGeoBoundary(coordinates));
			
		}
		else if (placemarkGeometry instanceof MultiGeometry)
		{
			// get the point and polygon
			List<Geometry> allGeometries = ((MultiGeometry) placemarkGeometry).getGeometry();
			
			// iterate over geometries
			for (Geometry currentGeometry : allGeometries)
			{
				if (currentGeometry instanceof Polygon)
				{
					List<Coordinate> coordinates = ((Polygon) currentGeometry).getOuterBoundaryIs().getLinearRing()
							.getCoordinates();
					
					city.setBoundary(this.kmlHelper.getKMLPolygonAsGeoBoundary(coordinates));
					
				}
				else if (currentGeometry instanceof Point)
				{
					Coordinate cityCoordinate = ((Point) currentGeometry).getCoordinates().get(0);
					city.setLocation(new GeoPoint(cityCoordinate.getLatitude(), cityCoordinate.getLongitude(),
							cityCoordinate.getAltitude()));
				}
			}
			
		}
		
		// fill with geo names data
		// TODO: parameterize country code...
		this.geoNamesHelper.fillSmartCityData(city, countryCode);
		
		return city;
	}
	
	public Set<District> getDistrict(String kmlFile)
	{
		Set<District> districts = new HashSet<District>();
		
		// parse the KML file
		Collection<Placemark> allPlacemarks = this.kmlHelper.getAllPlacemarks(kmlFile);
		
		// iterate over all quarters
		for (Placemark placemark : allPlacemarks)
		{
			District district = new District();
			
			// get the place mark name, which will be the quarter name
			String name = placemark.getName();
			
			// format the quarter name
			name = name.substring(0, 1).toUpperCase() + name.substring(1, name.length()).toLowerCase();
			
			// replace spaces
			name = name.trim();
			
			// store the quarter name
			district.setName(name);
			
			// generate and store the city url
			district.setUrl(name.replaceAll("[\\s-.]", "_"));
			
			// get the quarter description if available
			String description = placemark.getDescription();
			if (description != null)
				district.setDescription(description);
			else
				district.setDescription("The " + name + " district");
			
			// get the geometry object associated to the city
			Geometry placemarkGeometry = placemark.getGeometry();
			
			// if the geometry is a polygon, treat it as the district boundary
			if (placemarkGeometry instanceof Polygon)
			{
				List<Coordinate> coordinates = ((Polygon) placemarkGeometry).getOuterBoundaryIs().getLinearRing()
						.getCoordinates();
				
				district.setBoundary(this.kmlHelper.getKMLPolygonAsGeoBoundary(coordinates));
				
			}
			
			if ((district.getName() != null) && (!district.getName().isEmpty()) && (district.getBoundary() != null))
				districts.add(district);
		}
		return districts;
	}
	
	public Set<District> getAllDistricts()
	{
		// the set of quarters to return
		Set<District> allDistricts = new HashSet<District>();
		
		// check whether the quarters are ina single file or ina directory
		File districtsKMLFile = new File(this.districtsKML);
		
		if (districtsKMLFile.isDirectory())
		{
			// list kml files in the directory
			String districtFileNames[] = districtsKMLFile.list(new KMLFileExtensionFilter());
			
			// iterate over quarters
			for (int i = 0; i < districtFileNames.length; i++)
			{
				allDistricts.addAll(this.getDistrict(this.districtsKML + File.separator + districtFileNames[i]));
			}
		}
		else
		{
			// directly handle the single KML definition
			allDistricts.addAll(this.getDistrict(this.districtsKML));
		}
		
		return allDistricts;
	}
	
	public Set<Quarter> getQuarter(String kmlFile)
	{
		Set<Quarter> quarters = new HashSet<Quarter>();
		
		// parse the KML file
		Collection<Placemark> allPlacemarks = this.kmlHelper.getAllPlacemarks(kmlFile);
		
		// iterate over all quarters
		for (Placemark placemark : allPlacemarks)
		{
			Quarter quarter = new Quarter();
			
			// get the place mark name, which will be the quarter name
			String name = placemark.getName();
			
			// format the quarter name
			name = name.substring(0, 1).toUpperCase() + name.substring(1, name.length()).toLowerCase();
			
			// replace spaces
			name = name.trim();
			
			// store the quarter name
			quarter.setName(name);
			
			// generate and store the city url
			quarter.setUrl(name.replaceAll("[\\s-.]", "_"));
			
			// get the quarter description if available
			Snippet snippet = placemark.getSnippet();
			if (snippet != null)
				quarter.setDescription(snippet.getValue());
			else
				quarter.setDescription("The " + name + " quarter");
			
			// get the geometry object associated to the city
			Geometry placemarkGeometry = placemark.getGeometry();
			
			// if the geometry is a polygon, treat it as the city boundary
			if (placemarkGeometry instanceof Polygon)
			{
				List<Coordinate> coordinates = ((Polygon) placemarkGeometry).getOuterBoundaryIs().getLinearRing()
						.getCoordinates();
				
				quarter.setBoundary(this.kmlHelper.getKMLPolygonAsGeoBoundary(coordinates));
				
			}
			
			if ((quarter.getName() != null) && (!quarter.getName().isEmpty()) && (quarter.getBoundary() != null))
				quarters.add(quarter);
		}
		return quarters;
	}
	
	public Set<Quarter> getAllQuarters()
	{
		// the set of quarters to return
		Set<Quarter> allQuarters = new HashSet<Quarter>();
		
		// check whether the quarters are ina single file or ina directory
		File quartersKMLFile = new File(this.quartersKML);
		
		if (quartersKMLFile.isDirectory())
		{
			// list kml files in the directory
			String quarterFileNames[] = quartersKMLFile.list(new KMLFileExtensionFilter());
			
			// iterate over quarters
			for (int i = 0; i < quarterFileNames.length; i++)
			{
				allQuarters.addAll(this.getQuarter(this.quartersKML + File.separator + quarterFileNames[i]));
			}
		}
		else
		{
			// directly handle the single KML definition
			allQuarters.addAll(this.getQuarter(this.quartersKML));
		}
		
		return allQuarters;
	}
	
	public Set<WasteBin> getBins(String kmlFile)
	{
		// debug
		System.out.println("Getting bins from: " + kmlFile);
		Set<WasteBin> wasteBins = new HashSet<WasteBin>();
		
		// parse the KML file
		Collection<Placemark> allPlacemarks = this.kmlHelper.getAllPlacemarks(kmlFile);
		
		// iterate over all quarters
		for (Placemark placemark : allPlacemarks)
		{
			// get the place mark name, which will be the quarter name
			String name = placemark.getName();
			
			// replace spaces
			name = name.trim();
			
			// get the bin type
			String typeAndId[] = name.split("[" + this.prefixIdSeparator + "]");
			
			if ((typeAndId.length == 2) && (!typeAndId[0].isEmpty()))
			{
				// try to map the type
				String binClass = (String) this.binIdToClass.get(typeAndId[0]);
				
				// try to create an instance of the class
				if ((binClass != null) && (!binClass.isEmpty()))
				{
					// hey oh! reflection! to instantiate the right bin class
					Class<?> theBinClass;
					
					try
					{
						// get the bin class, if available
						theBinClass = this.getClass().getClassLoader().loadClass(binClass);
						
						// create a new bin instance
						WasteBin wasteBin = (WasteBin) theBinClass.newInstance();
						
						// store the bin name
						wasteBin.setName(name);
						
						// generate and store the bin url
						wasteBin.setUrl(typeAndId[0] + "_" + typeAndId[1]);
						
						// get the quarter description if available
						Snippet snippet = placemark.getSnippet();
						if (snippet != null)
							wasteBin.setDescription(snippet.getValue());
						else
							wasteBin.setDescription("The " + name + " bin");
						
						// get the extended data
						List<Data> extendedData = placemark.getExtendedData().getData();
						
						// extract the address information
						String address = null;
						int nBins = 1;
						
						for (Data currentData : extendedData)
						{
							switch (currentData.getName())
							{
								case "nome_via":
								{
									address = currentData.getValue();
									break;
								}
								case "civico":
								{
									address += " " + currentData.getValue();
									break;
								}
								case "count":
								{
									nBins = Integer.valueOf(currentData.getValue());
									break;
								}
							}
						}
						
						// store the address
						if ((address != null) && (!address.isEmpty()))
							wasteBin.setAddress(address);
						
						// get the geometry object associated to the city
						Geometry placemarkGeometry = placemark.getGeometry();
						
						// if the geometry is a polygon, treat it as the city
						// boundary
						if (placemarkGeometry instanceof Point)
						{
							Coordinate binCoordinate = ((Point) placemarkGeometry).getCoordinates().get(0);
							// build the geopoint representing the bin location
							GeoPoint binLocation = new GeoPoint(binCoordinate.getLatitude(),
									binCoordinate.getLongitude());
							
							// set the bin location
							wasteBin.setLocation(binLocation);
						}
						
						// check if the bin should be cloned, i.e. handle
						// multiple bins in the same location
						
						if ((wasteBin.getName() != null) && (!wasteBin.getName().isEmpty())
								&& (wasteBin.getLocation() != null))
						{
							for (int i = 0; i < nBins; i++)
							{
								if (i == 0)
									wasteBins.add(wasteBin);
								else
								{
									WasteBin clone = (WasteBin) wasteBin.clone();
									clone.setUrl(clone.getUrl() + "_" + i);
									wasteBins.add(clone);
								}
							}
						}
					}
					catch (ClassNotFoundException | InstantiationException | IllegalAccessException
							| CloneNotSupportedException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			
		}
		// debug
		System.out.println("Found " + wasteBins.size() + " bins.");
		return wasteBins;
	}
	
	public Set<WasteBin> getAllBins()
	{
		// the set of quarters to return
		Set<WasteBin> allBins = new HashSet<WasteBin>();
		
		// check whether the quarters are ina single file or ina directory
		File wasteBinsKMLFile = new File(this.wasteBinsKML);
		
		if (wasteBinsKMLFile.isDirectory())
		{
			// list kml files in the directory
			String wasteBinsKMLFileNames[] = wasteBinsKMLFile.list(new KMLFileExtensionFilter());
			
			// iterate over quarters
			for (int i = 0; i < wasteBinsKMLFileNames.length; i++)
			{
				allBins.addAll(this.getBins(this.wasteBinsKML + File.separator + wasteBinsKMLFileNames[i]));
			}
		}
		else
		{
			// directly handle the single KML definition
			allBins.addAll(this.getBins(this.wasteBinsKML));
		}
		
		return allBins;
	}
	
	public static void main(String[] args) throws IOException, ParseException
	{
		// operation flags
		boolean verbose = false;
		String prefix = "tow";
		String ontologyURL = "http://www.ismb.it/ontologies/turin_waste.owl";
		String outputFile = null;
		
		Options options = new org.apache.commons.cli.Options();
		
		options.addOption("p",true, "prefix");
		options.addOption("u",true, "ontology_url");
		options.addOption("v",false, "verbose");
		options.addOption("o",true, "outputfile");
		options.addOption("c",true, "kml folder or file");
		options.addOption("d",true, "district KML folder or file");
		options.addOption("q",true,"quarter KML folder or file");
		options.addOption("b",true,"bins KML folder or file");
		options.addOption("t",true,"templates folder");
		options.addOption("m",true,"mode, either n3 or jsonld");
		
		
		// parse arguments
		CommandLineParser parser = new DefaultParser();
		CommandLine cmd = parser.parse( options, args);
		
		// verbose operation
		if (cmd.hasOption("v"))
		{
			// set verbose
			verbose = true;
		}
		
		// check if prefix and ontology uri have been specified
		if ((cmd.hasOption('p')) && (cmd.hasOption('u')))
		{
			prefix = cmd.getOptionValue('p');
			ontologyURL = cmd.getOptionValue('u');
		}
		
		// checks if a given output file must be generated
		if ((cmd.hasOption('o')) && (!cmd.getOptionValue('o').isEmpty()))
		{
			outputFile = cmd.getOptionValue('o');
		}
		
		// check if mandatory information is available
		if ((cmd.hasOption('c')) && (cmd.hasOption('d')) && (cmd.hasOption('q'))
				&& (cmd.hasOption('m')) && (cmd.hasOption('b'))
				&& (cmd.hasOption('t')) && (!cmd.getOptionValue('c').isEmpty())
				&& (!cmd.getOptionValue('d').isEmpty()) && (!cmd.getOptionValue('q').isEmpty())
				&& (!cmd.getOptionValue('b').isEmpty()) && (!cmd.getOptionValue('m').isEmpty())
				&& (!cmd.getOptionValue('t').isEmpty()))
		{
			
			// debug or verbose information
			if (verbose)
				System.out.println("Starting...");
			
			// loading mappings between kml bin identifiers and wbin classes
			InputStream is = KML2City.class.getClassLoader().getResourceAsStream("bin.properties");
			Properties binToClass = new Properties();
			binToClass.load(is);
			
			// debug or verbose information
			if (verbose)
				System.out.println("Loaded KML bin to wbin identifier mappings...\n");
			
			KML2City generator = new KML2City(cmd.getOptionValue('c'), cmd.getOptionValue('d'), cmd.getOptionValue('q'),
					cmd.getOptionValue('b'), binToClass, '.', cmd.getOptionValue('t'));
			
			// debug or verbose information
			if (verbose)
				System.out.println("Create kml2city converter...\n");
			
			// get the city description
			SmartCity city = generator.getSmartCity("it");
			
			// debug or verbose information
			if (verbose)
				System.out.println("Created city description...\n");
			
			// get the city districts
			Set<District> districts = generator.getAllDistricts();
			
			// debug or verbose information
			if (verbose)
				System.out
						.println("Created " + districts != null ? districts.size() : 0 + "district descriptions...\n");
			
			// get the quarters
			Set<Quarter> quarters = generator.getAllQuarters();
			
			// debug or verbose information
			if (verbose)
				System.out.println("Created " + quarters != null ? quarters.size() : 0 + "quarter descriptions...\n");
			
			// get the bins
			Set<WasteBin> allBins = generator.getAllBins();
			
			// debug or verbose information
			if (verbose)
				System.out.println("Created " + allBins != null ? allBins.size() : 0 + "bin descriptions...\n");
			
			int i = 0;
			long time = System.currentTimeMillis();
			// add the districts to the city
			for (District district : districts)
			{
				city.getDistricts().add(district);
				district.setCity(city);
				i++;
			}
			
			// debug or verbose information
			if (verbose)
				System.out.println("Handled " + i + " districts in " + (System.currentTimeMillis() - time) + "ms\n");
			
			i = 0;
			time = System.currentTimeMillis();
			// add the quarters to the city
			for (Quarter quarter : quarters)
			{
				city.getQuarters().add(quarter);
				quarter.setCity(city);
				
				// check district
				for (District district : districts)
				{
					if (district.getBoundary().contains(quarter.getBoundary().getCentroid()))
					{
						district.addQuarter(quarter);
						quarter.setDistrict(district);
					}
				}
				i++;
			}
			// debug or verbose information
			if (verbose)
				System.out.println("Handled " + i + " quarters in " + (System.currentTimeMillis() - time) + "ms\n");
			
			// assign bins to quarters
			i = 0;
			time = System.currentTimeMillis();
			for (WasteBin bin : allBins)
			{
				for (Quarter quarter : quarters)
				{
					if (quarter.getBoundary().contains(bin.getLocation()))
					{
						bin.setQuarter(quarter);
						quarter.addBin(bin);
					}
				}
				i++;
			}
			
			// debug or verbose information
			if (verbose)
				System.out.println("Handled " + i + " bins in " + (System.currentTimeMillis() - time) + "ms\n");
			
			// prepare the city renderer
			CityRenderer renderer = null;
			
			// handle mode
			if (cmd.getOptionValue('m').equalsIgnoreCase("n3"))
			{
				// render as n3
				System.out.println("Using mode: n3");
				
				// add the prefixes
				renderer = new N3CityRenderer(generator.templatesFolder);
				((N3CityRenderer) renderer).addPrefix("s", "http://schema.org#");
				((N3CityRenderer) renderer).addPrefix("geo", "http://www.opengis.net/ont/geosparql#");
				((N3CityRenderer) renderer).addPrefix("geonames", "http://www.geonames.org/ontology#");
				((N3CityRenderer) renderer).addPrefix("vcard", "http://www.w3.org/2006/vcard/ns#");
				((N3CityRenderer) renderer).addPrefix("owl", "http://www.w3.org/2002/07/owl#");
				((N3CityRenderer) renderer).addPrefix("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
				((N3CityRenderer) renderer).addPrefix("xml", "http://www.w3.org/XML/1998/namespace");
				((N3CityRenderer) renderer).addPrefix("xsd", "http://www.w3.org/2001/XMLSchema#");
				// renderer.addPrefix("geof",
				// "http://www.opengis.net/def/function/geosparql#");
				((N3CityRenderer) renderer).addPrefix("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
				((N3CityRenderer) renderer).addPrefix("wbin", "http://www.ismb.it/ontologies/wastebin#");
				((N3CityRenderer) renderer).addPrefix("places", "http://purl.org/ontology/places#");
				((N3CityRenderer) renderer).addPrefix("gr", "http://purl.org/goodrelations/v1#");
				((N3CityRenderer) renderer).addPrefix("muo", "http://purl.oclc.org/NET/muo/muo#");
				
				// add the imports
				((N3CityRenderer) renderer).addImport("wbin");
				
				// N3CityRenderer renderer = (N3CityRenderer)
				// N3RenderFactory.getN3Renderer(SmartCity.class);
				((N3CityRenderer) renderer).addPrefix(prefix, ontologyURL + "#");
				((N3CityRenderer) renderer).setModelPrefix(prefix);
				((N3CityRenderer) renderer).setModelUri(ontologyURL);
				
			}
			else
			{
				// render as n3
				System.out.println("Using mode: jsonld");
				
				// add the prefixes
				
				//TODO use external param
				renderer = new JsonLDCityRenderer(generator.templatesFolder,generator.templatesFolder+"simpleNamesDefinitions.jsonld");
				((JsonLDCityRenderer) renderer).addPrefix("s", "http://schema.org#");
				((JsonLDCityRenderer) renderer).addPrefix("geo", "http://www.opengis.net/ont/geosparql#");
				((JsonLDCityRenderer) renderer).addPrefix("geonames", "http://www.geonames.org/ontology#");
				((JsonLDCityRenderer) renderer).addPrefix("vcard", "http://www.w3.org/2006/vcard/ns#");
				((JsonLDCityRenderer) renderer).addPrefix("owl", "http://www.w3.org/2002/07/owl#");
				((JsonLDCityRenderer) renderer).addPrefix("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
				//((JsonLDCityRenderer) renderer).addPrefix("xml", "http://www.w3.org/XML/1998/namespace");
				//((JsonLDCityRenderer) renderer).addPrefix("xsd", "http://www.w3.org/2001/XMLSchema#");
				// renderer.addPrefix("geof",
				// "http://www.opengis.net/def/function/geosparql#");
				((JsonLDCityRenderer) renderer).addPrefix("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
				((JsonLDCityRenderer) renderer).addPrefix("wbin", "http://www.ismb.it/ontologies/wastebin#");
				((JsonLDCityRenderer) renderer).addPrefix("places", "http://purl.org/ontology/places#");
				((JsonLDCityRenderer) renderer).addPrefix("gr", "http://purl.org/goodrelations/v1#");
				((JsonLDCityRenderer) renderer).addPrefix("muo", "http://purl.oclc.org/NET/muo/muo#");

				((JsonLDCityRenderer) renderer).addPrefix(prefix, ontologyURL + "#");
				((JsonLDCityRenderer) renderer).setModelPrefix(prefix);
				((JsonLDCityRenderer) renderer).setModelUri(ontologyURL);
			}
			
			if ((verbose) || (outputFile == null))
				System.out.println(renderer.render(city) + "\n");
			
			// write on File if required
			if ((outputFile != null) && (!outputFile.isEmpty()))
			{
				File output = new File(outputFile);
				
				FileWriter fw = new FileWriter(output);
				
				fw.write(renderer.render(city));
				
				fw.flush();
				fw.close();
			}
			
		}
		else
		{
			// automatically generate the help statement
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp( "Kml2City", options );
		}
	}
}
