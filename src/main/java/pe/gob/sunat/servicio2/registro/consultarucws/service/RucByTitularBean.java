
package pe.gob.sunat.servicio2.registro.consultarucws.service;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para rucByTitularBean complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>{@code
 * <complexType name="rucByTitularBean">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="anhoNac" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="claveSol" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="estado" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="estadoDesc" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="flag22" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="flag22Desc" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="indBueCon" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="indError" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="indMultResult" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="indResDom" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="nombre" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="numReg" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="numRegDesc" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="numRuc" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="sexo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="ubigeo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="ubigeoRuc" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="vigDni" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "rucByTitularBean", propOrder = {
    "anhoNac",
    "claveSol",
    "estado",
    "estadoDesc",
    "flag22",
    "flag22Desc",
    "indBueCon",
    "indError",
    "indMultResult",
    "indResDom",
    "nombre",
    "numReg",
    "numRegDesc",
    "numRuc",
    "sexo",
    "ubigeo",
    "ubigeoRuc",
    "vigDni"
})
public class RucByTitularBean {

    protected String anhoNac;
    protected String claveSol;
    protected String estado;
    protected String estadoDesc;
    protected String flag22;
    protected String flag22Desc;
    protected String indBueCon;
    protected String indError;
    protected String indMultResult;
    protected String indResDom;
    protected String nombre;
    protected String numReg;
    protected String numRegDesc;
    protected String numRuc;
    protected String sexo;
    protected String ubigeo;
    protected String ubigeoRuc;
    protected String vigDni;

    /**
     * Obtiene el valor de la propiedad anhoNac.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAnhoNac() {
        return anhoNac;
    }

    /**
     * Define el valor de la propiedad anhoNac.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAnhoNac(String value) {
        this.anhoNac = value;
    }

    /**
     * Obtiene el valor de la propiedad claveSol.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getClaveSol() {
        return claveSol;
    }

    /**
     * Define el valor de la propiedad claveSol.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setClaveSol(String value) {
        this.claveSol = value;
    }

    /**
     * Obtiene el valor de la propiedad estado.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEstado() {
        return estado;
    }

    /**
     * Define el valor de la propiedad estado.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEstado(String value) {
        this.estado = value;
    }

    /**
     * Obtiene el valor de la propiedad estadoDesc.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEstadoDesc() {
        return estadoDesc;
    }

    /**
     * Define el valor de la propiedad estadoDesc.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEstadoDesc(String value) {
        this.estadoDesc = value;
    }

    /**
     * Obtiene el valor de la propiedad flag22.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFlag22() {
        return flag22;
    }

    /**
     * Define el valor de la propiedad flag22.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFlag22(String value) {
        this.flag22 = value;
    }

    /**
     * Obtiene el valor de la propiedad flag22Desc.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFlag22Desc() {
        return flag22Desc;
    }

    /**
     * Define el valor de la propiedad flag22Desc.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFlag22Desc(String value) {
        this.flag22Desc = value;
    }

    /**
     * Obtiene el valor de la propiedad indBueCon.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIndBueCon() {
        return indBueCon;
    }

    /**
     * Define el valor de la propiedad indBueCon.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIndBueCon(String value) {
        this.indBueCon = value;
    }

    /**
     * Obtiene el valor de la propiedad indError.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIndError() {
        return indError;
    }

    /**
     * Define el valor de la propiedad indError.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIndError(String value) {
        this.indError = value;
    }

    /**
     * Obtiene el valor de la propiedad indMultResult.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIndMultResult() {
        return indMultResult;
    }

    /**
     * Define el valor de la propiedad indMultResult.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIndMultResult(String value) {
        this.indMultResult = value;
    }

    /**
     * Obtiene el valor de la propiedad indResDom.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIndResDom() {
        return indResDom;
    }

    /**
     * Define el valor de la propiedad indResDom.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIndResDom(String value) {
        this.indResDom = value;
    }

    /**
     * Obtiene el valor de la propiedad nombre.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Define el valor de la propiedad nombre.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNombre(String value) {
        this.nombre = value;
    }

    /**
     * Obtiene el valor de la propiedad numReg.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNumReg() {
        return numReg;
    }

    /**
     * Define el valor de la propiedad numReg.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNumReg(String value) {
        this.numReg = value;
    }

    /**
     * Obtiene el valor de la propiedad numRegDesc.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNumRegDesc() {
        return numRegDesc;
    }

    /**
     * Define el valor de la propiedad numRegDesc.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNumRegDesc(String value) {
        this.numRegDesc = value;
    }

    /**
     * Obtiene el valor de la propiedad numRuc.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNumRuc() {
        return numRuc;
    }

    /**
     * Define el valor de la propiedad numRuc.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNumRuc(String value) {
        this.numRuc = value;
    }

    /**
     * Obtiene el valor de la propiedad sexo.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSexo() {
        return sexo;
    }

    /**
     * Define el valor de la propiedad sexo.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSexo(String value) {
        this.sexo = value;
    }

    /**
     * Obtiene el valor de la propiedad ubigeo.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUbigeo() {
        return ubigeo;
    }

    /**
     * Define el valor de la propiedad ubigeo.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUbigeo(String value) {
        this.ubigeo = value;
    }

    /**
     * Obtiene el valor de la propiedad ubigeoRuc.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUbigeoRuc() {
        return ubigeoRuc;
    }

    /**
     * Define el valor de la propiedad ubigeoRuc.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUbigeoRuc(String value) {
        this.ubigeoRuc = value;
    }

    /**
     * Obtiene el valor de la propiedad vigDni.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVigDni() {
        return vigDni;
    }

    /**
     * Define el valor de la propiedad vigDni.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVigDni(String value) {
        this.vigDni = value;
    }

}
