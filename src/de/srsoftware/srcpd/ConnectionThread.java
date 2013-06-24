package de.srsoftware.srcpd;

import android.os.AsyncTask;
import de.dermoba.srcp.client.SRCPSession;
import de.dermoba.srcp.common.exception.SRCPException;

public class ConnectionThread extends AsyncTask<String, Void, SRCPSession>{

	@Override
	protected SRCPSession doInBackground(String... params) {
		SRCPSession srcpsession=null;
		try {
			srcpsession = new SRCPSession(params[0],Integer.parseInt(params[1]));
			srcpsession.connect();
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SRCPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return srcpsession;
	}

}
