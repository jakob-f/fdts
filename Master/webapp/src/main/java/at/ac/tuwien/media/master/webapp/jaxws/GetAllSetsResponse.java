
package at.ac.tuwien.media.master.webapp.jaxws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import at.ac.tuwien.media.master.webapp.data.SetData;

@XmlRootElement(name = "getAllSetsResponse", namespace = "http://webapp.master.media.tuwien.ac.at/")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getAllSetsResponse", namespace = "http://webapp.master.media.tuwien.ac.at/")
public class GetAllSetsResponse {

    @XmlElement(name = "return", namespace = "", nillable = true)
    private SetData[] _return;

    /**
     * 
     * @return
     *     returns SetData[]
     */
    public SetData[] getReturn() {
        return this._return;
    }

    /**
     * 
     * @param _return
     *     the value for the _return property
     */
    public void setReturn(SetData[] _return) {
        this._return = _return;
    }

}
