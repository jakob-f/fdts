package at.frohnwieser.mahut.commons;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import javax.annotation.Nonnull;

public final class MACAddress {

    @Nonnull
    public static byte[] get() {
	byte[] aMACAddress = null;
	try {
	    final Enumeration<NetworkInterface> aNetworkInterfaces = NetworkInterface.getNetworkInterfaces();

	    while (aNetworkInterfaces.hasMoreElements())
		if ((aMACAddress = aNetworkInterfaces.nextElement().getHardwareAddress()) != null)
		    break;
	} catch (final SocketException e) {
	}

	if (aMACAddress == null)
	    throw new NullPointerException("cannot read mac address");

	return aMACAddress;
    }

    @Nonnull
    public static String getAsString() {
	final StringBuilder aSB = new StringBuilder();
	final byte[] aMACAddress = get();

	for (int i = 0; i < aMACAddress.length; i++)
	    aSB.append(String.format("%02X%s", aMACAddress[i], (i < aMACAddress.length - 1) ? "-" : ""));

	return aSB.toString();
    }
}
