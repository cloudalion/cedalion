package net.nansore.cedalion.eclipse;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IEditorLauncher;

public class CedalionImagePusher implements IEditorLauncher {

	public class URLValidator implements IInputValidator {

		@Override
		public String isValid(String newText) {
			try {
				new URL(newText);
				return null;				
			} catch(MalformedURLException e) {
				return e.getLocalizedMessage();
			}
		}

	}

	@Override
	public void open(IPath file) {
		InputDialog dlg = new InputDialog(null, "Base URL to push to", "Please enter the URL where you wish this image pushed", "http://localhost:8080/static/" + file.lastSegment(), new URLValidator());
		if(dlg.open() == InputDialog.OK) {
			try {
				URL url = new URL(dlg.getValue());
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setDoOutput(true);
				conn.setDoInput(true);
				conn.setRequestMethod("PUT");
				conn.setRequestProperty("content-type", "application/cedalion");
				InputStream in = new FileInputStream(file.toPortableString());
				int n;
				byte[] buffer = new byte[1024];
				OutputStream outputStream = conn.getOutputStream();
				while ((n = in.read(buffer)) > -1) {
					System.out.println(new String(buffer));
					outputStream.write(buffer, 0, n);
				}
				outputStream.flush();
				outputStream.close();
				in.close();
				InputStream response = conn.getInputStream();
				while(response.read(buffer) > -1)
					;
				
			} catch (Exception e) {
				MessageDialog msgDlg = new MessageDialog(null, "Error", null, e.getMessage(), MessageDialog.ERROR, new String[] {"OK"}, 0);
				msgDlg.open();
			}
		}
		
	}

}
