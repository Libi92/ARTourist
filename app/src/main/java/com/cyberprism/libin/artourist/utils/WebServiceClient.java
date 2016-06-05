package com.cyberprism.libin.artourist.utils;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.util.Log;

public class WebServiceClient {

	public static String connect(String methodName, String[] args,
			String[] values) {

        try {
            SoapObject request = new SoapObject(Globals.NAMESPACE, methodName);
            int i = 0;

            for (String s : args) {
                PropertyInfo propInfo = new PropertyInfo();
                propInfo.name = s;
                propInfo.type = PropertyInfo.STRING_CLASS;
                request.addProperty(propInfo, values[i]);
                i++;
            }

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                    SoapEnvelope.VER11);
            envelope.setOutputSoapObject(request);
            HttpTransportSE androidHttpTransport = new HttpTransportSE(
                    Globals.serviceUrl);
            androidHttpTransport.call(Globals.NAMESPACE + methodName, envelope);
            SoapPrimitive resultsRequestSOAP = (SoapPrimitive) envelope
                    .getResponse(); // Receiving return string

            String result = resultsRequestSOAP.toString();
            if (result != null) {
                return result;
            }
            return "-1";
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.d("Url", Globals.serviceUrl);
            Log.d("Web Service Exception", ex.toString());
            return "-1";
        }
	}

	public static String getlocations() {
		String[] args = new String[0];
		String[] values = new String[0];


		String result = connect("getlocations", args, values);
		return result;
	}

	public static void addRating(String userId, String placeId, String rating) {

		String[] args = new String[3];
		String[] values = new String[3];

		args[0] = "userid";
		args[1] = "placeid";
		args[2] = "rate";

		values[0] = userId;
		values[1] = placeId;
		values[2] = rating;


		connect("ratePlace", args, values);
	}

    public static void addLocation(String name, String details, String latitude, String longitude) {
        String[] args = new String[4];
        String[] values = new String[4];

        args[0] = "name";
        args[1] = "details";
        args[2] = "latitude";
        args[3] = "longitude";

        values[0] = name;
        values[1] = details;
        values[2] = latitude;
        values[3] = longitude;


        connect("addPlace", args, values);
    }
}
