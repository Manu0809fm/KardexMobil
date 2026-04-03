
package pe.gob.sunat.servicio2.registro.consultarucws.service;

import javax.xml.namespace.QName;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlElementDecl;
import jakarta.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the pe.gob.sunat.servicio2.registro.consultarucws.service package. 
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

    private static final QName _BuscarRuc_QNAME = new QName("http://service.consultarucws.registro.servicio2.sunat.gob.pe/", "buscarRuc");
    private static final QName _BuscarRucByRepreLegal_QNAME = new QName("http://service.consultarucws.registro.servicio2.sunat.gob.pe/", "buscarRucByRepreLegal");
    private static final QName _BuscarRucByRepreLegalResponse_QNAME = new QName("http://service.consultarucws.registro.servicio2.sunat.gob.pe/", "buscarRucByRepreLegalResponse");
    private static final QName _BuscarRucByTitular_QNAME = new QName("http://service.consultarucws.registro.servicio2.sunat.gob.pe/", "buscarRucByTitular");
    private static final QName _BuscarRucByTitularResponse_QNAME = new QName("http://service.consultarucws.registro.servicio2.sunat.gob.pe/", "buscarRucByTitularResponse");
    private static final QName _BuscarRucResponse_QNAME = new QName("http://service.consultarucws.registro.servicio2.sunat.gob.pe/", "buscarRucResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: pe.gob.sunat.servicio2.registro.consultarucws.service
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link BuscarRuc }
     * 
     * @return
     *     the new instance of {@link BuscarRuc }
     */
    public BuscarRuc createBuscarRuc() {
        return new BuscarRuc();
    }

    /**
     * Create an instance of {@link BuscarRucByRepreLegal }
     * 
     * @return
     *     the new instance of {@link BuscarRucByRepreLegal }
     */
    public BuscarRucByRepreLegal createBuscarRucByRepreLegal() {
        return new BuscarRucByRepreLegal();
    }

    /**
     * Create an instance of {@link BuscarRucByRepreLegalResponse }
     * 
     * @return
     *     the new instance of {@link BuscarRucByRepreLegalResponse }
     */
    public BuscarRucByRepreLegalResponse createBuscarRucByRepreLegalResponse() {
        return new BuscarRucByRepreLegalResponse();
    }

    /**
     * Create an instance of {@link BuscarRucByTitular }
     * 
     * @return
     *     the new instance of {@link BuscarRucByTitular }
     */
    public BuscarRucByTitular createBuscarRucByTitular() {
        return new BuscarRucByTitular();
    }

    /**
     * Create an instance of {@link BuscarRucByTitularResponse }
     * 
     * @return
     *     the new instance of {@link BuscarRucByTitularResponse }
     */
    public BuscarRucByTitularResponse createBuscarRucByTitularResponse() {
        return new BuscarRucByTitularResponse();
    }

    /**
     * Create an instance of {@link BuscarRucResponse }
     * 
     * @return
     *     the new instance of {@link BuscarRucResponse }
     */
    public BuscarRucResponse createBuscarRucResponse() {
        return new BuscarRucResponse();
    }

    /**
     * Create an instance of {@link RucByTitularBean }
     * 
     * @return
     *     the new instance of {@link RucByTitularBean }
     */
    public RucByTitularBean createRucByTitularBean() {
        return new RucByTitularBean();
    }

    /**
     * Create an instance of {@link RucByRucBean }
     * 
     * @return
     *     the new instance of {@link RucByRucBean }
     */
    public RucByRucBean createRucByRucBean() {
        return new RucByRucBean();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BuscarRuc }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link BuscarRuc }{@code >}
     */
    @XmlElementDecl(namespace = "http://service.consultarucws.registro.servicio2.sunat.gob.pe/", name = "buscarRuc")
    public JAXBElement<BuscarRuc> createBuscarRuc(BuscarRuc value) {
        return new JAXBElement<>(_BuscarRuc_QNAME, BuscarRuc.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BuscarRucByRepreLegal }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link BuscarRucByRepreLegal }{@code >}
     */
    @XmlElementDecl(namespace = "http://service.consultarucws.registro.servicio2.sunat.gob.pe/", name = "buscarRucByRepreLegal")
    public JAXBElement<BuscarRucByRepreLegal> createBuscarRucByRepreLegal(BuscarRucByRepreLegal value) {
        return new JAXBElement<>(_BuscarRucByRepreLegal_QNAME, BuscarRucByRepreLegal.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BuscarRucByRepreLegalResponse }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link BuscarRucByRepreLegalResponse }{@code >}
     */
    @XmlElementDecl(namespace = "http://service.consultarucws.registro.servicio2.sunat.gob.pe/", name = "buscarRucByRepreLegalResponse")
    public JAXBElement<BuscarRucByRepreLegalResponse> createBuscarRucByRepreLegalResponse(BuscarRucByRepreLegalResponse value) {
        return new JAXBElement<>(_BuscarRucByRepreLegalResponse_QNAME, BuscarRucByRepreLegalResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BuscarRucByTitular }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link BuscarRucByTitular }{@code >}
     */
    @XmlElementDecl(namespace = "http://service.consultarucws.registro.servicio2.sunat.gob.pe/", name = "buscarRucByTitular")
    public JAXBElement<BuscarRucByTitular> createBuscarRucByTitular(BuscarRucByTitular value) {
        return new JAXBElement<>(_BuscarRucByTitular_QNAME, BuscarRucByTitular.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BuscarRucByTitularResponse }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link BuscarRucByTitularResponse }{@code >}
     */
    @XmlElementDecl(namespace = "http://service.consultarucws.registro.servicio2.sunat.gob.pe/", name = "buscarRucByTitularResponse")
    public JAXBElement<BuscarRucByTitularResponse> createBuscarRucByTitularResponse(BuscarRucByTitularResponse value) {
        return new JAXBElement<>(_BuscarRucByTitularResponse_QNAME, BuscarRucByTitularResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BuscarRucResponse }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link BuscarRucResponse }{@code >}
     */
    @XmlElementDecl(namespace = "http://service.consultarucws.registro.servicio2.sunat.gob.pe/", name = "buscarRucResponse")
    public JAXBElement<BuscarRucResponse> createBuscarRucResponse(BuscarRucResponse value) {
        return new JAXBElement<>(_BuscarRucResponse_QNAME, BuscarRucResponse.class, null, value);
    }

}
