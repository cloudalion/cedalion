package net.nansore.prolog;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

/**
 * Deprecated.  Not in use
 */
public class PackageLoader {
	Set<String> loadedUrls = new HashSet<String>();
	private File cacheLocation;
	private Proxy proxy;
	private PrologProxy prolog;
	
	public PackageLoader(File cacheLocation, Proxy proxy, PrologProxy prolog) {
		this.cacheLocation = cacheLocation;
		this.proxy = proxy;
		this.prolog = prolog;
	}
	
	public void loadNamespaces() throws PrologException, IOException {
		boolean modified;
		do {
			modified = false;
			Variable varRes = new Variable("Res");
			Variable varNS = new Variable("NS");
			Variable varAlias = new Variable("Alias");
			Iterator<Map<Variable, Object>> i = prolog.getSolutions(new Compound("semImported", varRes, varNS, varAlias));
			while(i.hasNext()) {
				Map<Variable, Object> result = i.next();
				String url = result.get(varNS).toString();
				String alias = result.get(varAlias).toString();
				String res = result.get(varRes).toString();
				System.out.println("res=" + res + " alias=" + alias + " url="+url);
				if(!loadedUrls.contains(url)) {				
					loadedUrls.add(url);
					try {
						loadNamespace(url);
						modified = true;
					} catch (MalformedURLException e) {
						// No problems, this namespace does not need to be loaded.
						System.out.println("No need to load package " + url);
					}
				}
			}
		} while(modified);
	}

	private void loadNamespace(String pkg) throws IOException, PrologException {
		URL url = new URL(pkg);

		File cacheDir = new File(cacheLocation, cacheDirName(url));
		if(!cacheDir.exists()) {
			ZipFile zip = downloadZipFile(url);
			cacheDir.mkdirs();			
			extractZip(zip, cacheDir);
		}
		
		File[] files = cacheDir.listFiles(new FileFilter(){
			public boolean accept(File file) {
				return file.getName().endsWith(".pl");
			}});
		for(int i = 0; i < files.length; i++) {
			loadFile(pkg, files[i]);
		}
	}

	private void loadFile(String pkg, File file) throws PrologException {
		String resourcePath = pkg + "$" + file.getName();
		String filePath = file.getAbsolutePath();
		prolog.hasSolution(new Compound("semAssertTermsFromFile", pkg, resourcePath, filePath));
	}

	private void extractZip(ZipFile zip, File cacheDir) throws FileNotFoundException, IOException {
		for(Enumeration<? extends ZipEntry> en = zip.entries(); en.hasMoreElements(); ) {
			ZipEntry entry = en.nextElement();
			System.out.println("Extracting: " + entry.getName());
			File extractTo = new File(cacheDir, entry.getName());
			if(entry.isDirectory())
				extractTo.mkdirs();
			else
				copyStream(zip.getInputStream(entry), extractTo);
		}
	}


	private ZipFile downloadZipFile(URL url) throws IOException, FileNotFoundException, ZipException {
		URLConnection connection = proxy != null ? url.openConnection(proxy) : url.openConnection();
		connection.connect();
		InputStream input = connection.getInputStream();
		File zipFile = File.createTempFile("zip", ".zip");
		copyStream(input, zipFile);
		ZipFile zip = new ZipFile(zipFile);
		return zip;
	}

	private String cacheDirName(URL url) {
		String[] parts = url.getPath().split("/");
		return parts[parts.length - 1] + "." + Math.abs(url.toString().hashCode());
	}

	private void copyStream(InputStream input, File zipFile) throws FileNotFoundException, IOException {
		FileOutputStream output = new FileOutputStream(zipFile);
		while(true) {
			byte[] buff = new byte[1024];
			int n = input.read(buff);
			if(n == -1)
				break;
			output.write(buff, 0, n);
		}
		output.close();
	}
	
	/*public static void main(String[] args) throws IOException, PrologException {
		PackageLoader loader = new PackageLoader(new File("d:\\"), null, new PrologProxy("pl", new File("service.pl")));
		loader.loadNamespace("http://127.0.0.1/Apache2.2.zip");
	}*/
}
