
package pe.gob.sunat.servicio2.registro.consultarucws.service;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para buscarRucByTitularResponse complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>{@code
 * <complexType name="buscarRucByTitularResponse">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="return" type="{http://service.consultarucws.registro.servicio2.sunat.gob.pe/}rucByTitularBean" minOccurs="0"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "buscarRucByTitularResponse", propOrder = {
    "_return"
})
public class BuscarRucByTitularResponse {

    @XmlElement(name = "return")
    protected RucByTitularBean _return;

    /**
     * Obtiene el valor de la propiedad return.
     * 
     * @return
     *     possible object is
     *     {@link RucByTitularBean }
     *     
     */
    public RucByTitularBean getReturn() {
        return _return;
    }

    /**
     * Define el valor de la propiedad return.
     * 
     * @param value
     *     allowed object is
     *     {@link RucByTitularBean }
     *     
     */
    public void setReturn(RucByTitularBean value) {
        this._return = value;
    }

}
