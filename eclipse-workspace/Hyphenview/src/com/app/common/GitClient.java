package com.app.common;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
//import org.springframework.web.reactive.function.client.WebClient;
import org.apache.cxf.jaxrs.client.*;


public class GitClient {
    static String url = "https://bitbucket.org/api/1.0/repositories/myrepository/mr/raw/";
    static String branch = "master";
    static String source = "/MavenWeb/src/main/resources/";
    String test = "/src/test/resources/";
    static String user = "username";
    static String password = "password";
 
    public static void usingWebClient() throws FileNotFoundException,IOException {
        downloadFileFromBitBucket("provisioner-components-setup.ini");
        downloadFileFromBitBucket("provisioner-properties-setup.csv");
        downloadFileFromBitBucket("provisioner-resources-setup.zip");
        downloadFileFromBitBucket("provisioner-system-setup.ini");
    }
    	
 	
    private static void downloadFileFromBitBucket(String resource) throws IOException, FileNotFoundException {
       /* WebClient client = WebClient.create(url + branch + source + resource,user, password, null);
        
        Response r = client.accept("text/plain").get();
        System.out.println("reponse" + r.getStatus());
        IOUtils.copyLarge((InputStream) r.getEntity(), new FileOutputStream(new File("C:\\folder" + resource)));*/
    }
 
    public static void main(String args[]) throws FileNotFoundException,IOException {
        usingWebClient();
    }
}