//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.10.08 at 11:10:03 AM CEST 
//


package it.ismb.pertlab.pwal.event.format.linksmart.cnet.jaxb;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the it.ismb.pertlab.pwal.test package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _ProcessContext_QNAME = new QName("http://linksmart.org/Event/1.0", "ProcessContext");
    private final static QName _PhenomenonTime_QNAME = new QName("http://linksmart.org/IoTEntity/1.0", "PhenomenonTime");
    private final static QName _Value_QNAME = new QName("http://linksmart.org/IoTEntity/1.0", "Value");
    private final static QName _Description_QNAME = new QName("http://linksmart.org/IoTEntity/1.0", "Description");
    private final static QName _Timestamp_QNAME = new QName("http://linksmart.org/Event/1.0", "Timestamp");
    private final static QName _Comment_QNAME = new QName("http://linksmart.org/Event/1.0", "Comment");
    private final static QName _Location_QNAME = new QName("http://linksmart.org/Event/1.0", "Location");
    private final static QName _EventID_QNAME = new QName("http://linksmart.org/Event/1.0", "EventID");
    private final static QName _EventType_QNAME = new QName("http://linksmart.org/Event/1.0", "EventType");
    private final static QName _Meta_QNAME = new QName("http://linksmart.org/IoTEntity/1.0", "Meta");
    private final static QName _Content_QNAME = new QName("http://linksmart.org/Event/1.0", "Content");
    private final static QName _UnitOfMeasurement_QNAME = new QName("http://linksmart.org/IoTEntity/1.0", "UnitOfMeasurement");
    private final static QName _Name_QNAME = new QName("http://linksmart.org/IoTEntity/1.0", "Name");
    private final static QName _ResultTime_QNAME = new QName("http://linksmart.org/IoTEntity/1.0", "ResultTime");
    private final static QName _ObjectID_QNAME = new QName("http://linksmart.org/Event/1.0", "ObjectID");
    private final static QName _Topic_QNAME = new QName("http://linksmart.org/Event/1.0", "Topic");
    private final static QName _Project_QNAME = new QName("http://linksmart.org/Event/1.0", "Project");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: it.ismb.pertlab.pwal.test
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Source }
     * 
     */
    public Source createSource() {
        return new Source();
    }

    /**
     * Create an instance of {@link TypedStringType }
     * 
     */
    public TypedStringType createTypedStringType() {
        return new TypedStringType();
    }

    /**
     * Create an instance of {@link Event }
     * 
     */
    public Event createEvent() {
        return new Event();
    }

    /**
     * Create an instance of {@link EventMeta }
     * 
     */
    public EventMeta createEventMeta() {
        return new EventMeta();
    }

    /**
     * Create an instance of {@link ContentType }
     * 
     */
    public ContentType createContentType() {
        return new ContentType();
    }

    /**
     * Create an instance of {@link IoTProperty }
     * 
     */
    public IoTProperty createIoTProperty() {
        return new IoTProperty();
    }

    /**
     * Create an instance of {@link MetaType }
     * 
     */
    public MetaType createMetaType() {
        return new MetaType();
    }

    /**
     * Create an instance of {@link IoTStateObservation }
     * 
     */
    public IoTStateObservation createIoTStateObservation() {
        return new IoTStateObservation();
    }

    /**
     * Create an instance of {@link IoTEntity }
     * 
     */
    public IoTEntity createIoTEntity() {
        return new IoTEntity();
    }

    /**
     * Create an instance of {@link ArrayOfIoTEntity }
     * 
     */
    public ArrayOfIoTEntity createArrayOfIoTEntity() {
        return new ArrayOfIoTEntity();
    }

    /**
     * Create an instance of {@link ArrayOfIoTStateObservation }
     * 
     */
    public ArrayOfIoTStateObservation createArrayOfIoTStateObservation() {
        return new ArrayOfIoTStateObservation();
    }

    /**
     * Create an instance of {@link ArrayOfIoTProperty }
     * 
     */
    public ArrayOfIoTProperty createArrayOfIoTProperty() {
        return new ArrayOfIoTProperty();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TypedStringType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://linksmart.org/Event/1.0", name = "ProcessContext")
    public JAXBElement<TypedStringType> createProcessContext(TypedStringType value) {
        return new JAXBElement<TypedStringType>(_ProcessContext_QNAME, TypedStringType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://linksmart.org/IoTEntity/1.0", name = "PhenomenonTime")
    public JAXBElement<XMLGregorianCalendar> createPhenomenonTime(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_PhenomenonTime_QNAME, XMLGregorianCalendar.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://linksmart.org/IoTEntity/1.0", name = "Value")
    public JAXBElement<String> createValue(String value) {
        return new JAXBElement<String>(_Value_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://linksmart.org/IoTEntity/1.0", name = "Description")
    public JAXBElement<String> createDescription(String value) {
        return new JAXBElement<String>(_Description_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://linksmart.org/Event/1.0", name = "Timestamp")
    public JAXBElement<XMLGregorianCalendar> createTimestamp(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_Timestamp_QNAME, XMLGregorianCalendar.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://linksmart.org/Event/1.0", name = "Comment")
    public JAXBElement<String> createComment(String value) {
        return new JAXBElement<String>(_Comment_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TypedStringType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://linksmart.org/Event/1.0", name = "Location")
    public JAXBElement<TypedStringType> createLocation(TypedStringType value) {
        return new JAXBElement<TypedStringType>(_Location_QNAME, TypedStringType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://linksmart.org/Event/1.0", name = "EventID")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    public JAXBElement<String> createEventID(String value) {
        return new JAXBElement<String>(_EventID_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TypedStringType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://linksmart.org/Event/1.0", name = "EventType")
    public JAXBElement<TypedStringType> createEventType(TypedStringType value) {
        return new JAXBElement<TypedStringType>(_EventType_QNAME, TypedStringType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MetaType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://linksmart.org/IoTEntity/1.0", name = "Meta")
    public JAXBElement<MetaType> createMeta(MetaType value) {
        return new JAXBElement<MetaType>(_Meta_QNAME, MetaType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ContentType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://linksmart.org/Event/1.0", name = "Content")
    public JAXBElement<ContentType> createContent(ContentType value) {
        return new JAXBElement<ContentType>(_Content_QNAME, ContentType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TypedStringType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://linksmart.org/IoTEntity/1.0", name = "UnitOfMeasurement")
    public JAXBElement<TypedStringType> createUnitOfMeasurement(TypedStringType value) {
        return new JAXBElement<TypedStringType>(_UnitOfMeasurement_QNAME, TypedStringType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://linksmart.org/IoTEntity/1.0", name = "Name")
    public JAXBElement<String> createName(String value) {
        return new JAXBElement<String>(_Name_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://linksmart.org/IoTEntity/1.0", name = "ResultTime")
    public JAXBElement<XMLGregorianCalendar> createResultTime(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_ResultTime_QNAME, XMLGregorianCalendar.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TypedStringType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://linksmart.org/Event/1.0", name = "ObjectID")
    public JAXBElement<TypedStringType> createObjectID(TypedStringType value) {
        return new JAXBElement<TypedStringType>(_ObjectID_QNAME, TypedStringType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TypedStringType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://linksmart.org/Event/1.0", name = "Topic")
    public JAXBElement<TypedStringType> createTopic(TypedStringType value) {
        return new JAXBElement<TypedStringType>(_Topic_QNAME, TypedStringType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://linksmart.org/Event/1.0", name = "Project")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    public JAXBElement<String> createProject(String value) {
        return new JAXBElement<String>(_Project_QNAME, String.class, null, value);
    }

}
