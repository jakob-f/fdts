
package at.ac.tuwien.media.master.webapp.jaxws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import at.ac.tuwien.media.master.webapp.data.AssetData;

@XmlRootElement(name = "upload", namespace = "http://webapp.master.media.tuwien.ac.at/")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "upload", namespace = "http://webapp.master.media.tuwien.ac.at/", propOrder = {
    "arg0",
    "arg1"
})
public class Upload {

    @XmlElement(name = "arg0", namespace = "")
    private long arg0;
    @XmlElement(name = "arg1", namespace = "")
    private AssetData arg1;

    /**
     * 
     * @return
     *     returns long
     */
    public long getArg0() {
        return this.arg0;
    }

    /**
     * 
     * @param arg0
     *     the value for the arg0 property
     */
    public void setArg0(long arg0) {
        this.arg0 = arg0;
    }

    /**
     * 
     * @return
     *     returns AssetData
     */
    public AssetData getArg1() {
        return this.arg1;
    }

    /**
     * 
     * @param arg1
     *     the value for the arg1 property
     */
    public void setArg1(AssetData arg1) {
        this.arg1 = arg1;
    }

}
