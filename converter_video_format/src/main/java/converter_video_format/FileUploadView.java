package converter_video_format;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.json.JSONArray;
import org.primefaces.json.JSONException;
import org.primefaces.json.JSONObject;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;


@ManagedBean
//@RequestScoped
public class FileUploadView{
	private String destination="/tmp/";
	private String api_key="0cdab19d2adc83a856e9dd7220343ab8";
	private String address_file = "";
	private String url_get = "";
	
	public String getUrl()
	{
		return (url_get);
	}
 
    public void handleFileUpload(FileUploadEvent event) throws IOException, JSONException {
        FacesMessage message = new FacesMessage("Succesful", event.getFile().getFileName() + " is uploaded.");
        FacesContext.getCurrentInstance().addMessage(null, message);
        try {
        	copyFile(event.getFile().getFileName(), event.getFile().getInputstream());
        } catch (IOException e) {
        	e.printStackTrace();
        }
        
        this.address_file = this.destination;
        this.url_get = "http://www.youtube.com/v/KZnUr8lcqjo";
        
        //faz upload do video a ser convertido
        //uploadToAmazon(event.getFile().getFileName());
        
        //String url = getUrlStream(event.getFile().getFileName());
        //String url = getUrlStream("sample.dv");
              
        //salva o video gerado na maquina local
        //String filename = saveUrl(url);
        
        //faz upload do video convertido
        //uploadToAmazon(filename);
        this.url_get = "https://s3-sa-east-1.amazonaws.com/alexcorrea/a63d0051de478ef4cbabf4046d8b1db6.mp4";
    }
    
    public void copyFile(String fileName, InputStream in) {
    	try {
    	// write the inputStream to a FileOutputStream
    	OutputStream out = new FileOutputStream(new File(this.destination + fileName));
    	int read = 0;
    	byte[] bytes = new byte[1024];
    	while ((read = in.read(bytes)) != -1) {
    		out.write(bytes, 0, read);
    	}
    	in.close();
    	out.flush();
    	out.close();
    	System.out.println("New file created!");
    	} catch (IOException e) {
    		System.out.println(e.getMessage());
    	}
    }
    
    public String getUrlStream(String fileName) throws IOException, JSONException
    {
    	//create job
    	CloseableHttpClient httpclient = HttpClients.createDefault();
    	try
    	{
	    	String url = "https://app.zencoder.com/api/v2/jobs";
	    	HttpPost httpPost = new HttpPost(url);
	    	
	    	JSONObject my_obj_internal = new JSONObject();
	    	my_obj_internal.put("label", "mp4 high");
	    	my_obj_internal.put("h264_profile", "high");
	    	JSONArray list_obj = new JSONArray();
	    	list_obj.put(0, my_obj_internal);
	    	
	    	JSONObject my_obj = new JSONObject();
	    	my_obj.put("input", "https://s3-sa-east-1.amazonaws.com/alexcorrea/" + fileName);
	    	my_obj.put("outputs", my_obj_internal);
	    	
	    	String json_string = my_obj.toString();
	    	StringEntity requestEntity = new StringEntity(json_string);
	    	requestEntity.setContentType("application/json");
	    	httpPost.addHeader("Zencoder-Api-Key", this.api_key);
	    	httpPost.setEntity(requestEntity);
	    	CloseableHttpResponse response = httpclient.execute(httpPost);
	    	try
	    	{
	    		if (response.getStatusLine().getStatusCode() != 201) {
	    			throw new RuntimeException("Failed : HTTP error code : " + response.getStatusLine().getStatusCode());
	    		}
	    		BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
 
				JSONObject output = new JSONObject(br.readLine());
				JSONArray outputs = output.getJSONArray("outputs");
				this.url_get = outputs.getJSONObject(0).getString("url");
	    	}
	    	catch (Exception e)
	    	{
	    		System.out.println(e);
	    	}
	    	finally
	    	{
	    		response.close();
	    	}
    	}
    	finally
    	{
	    	httpclient.close();	
    	}
    	return this.url_get;
    }
    
    public void uploadToAmazon(String fileName) throws IOException
    {
    	String existingBucketName = "alexcorrea";
    	String keyName = fileName;
		  
		String filePath = this.address_file + fileName;
		String amazonFileUploadLocationOriginal=existingBucketName;
	
		String accessKey = "AKIAI7KOT344ZYGDVQKA";
        String secretKey = "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";
        try
        {
        	AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        	AmazonS3 s3Client = new AmazonS3Client(credentials);
        	s3Client.setEndpoint("https://s3-sa-east-1.amazonaws.com");
        	
        	FileInputStream stream = new FileInputStream(filePath);
    		ObjectMetadata objectMetadata = new ObjectMetadata();
    		PutObjectRequest putObjectRequest = new PutObjectRequest(amazonFileUploadLocationOriginal, keyName, stream, objectMetadata);
    		putObjectRequest.withCannedAcl(CannedAccessControlList.PublicRead);
    		PutObjectResult result = s3Client.putObject(putObjectRequest);
    		System.out.println("Etag:" + result.getETag() + "-->" + result);
        }
        catch (Exception e)
    	{
    		System.out.println(e);
    	}
    }
    
    public String saveUrl(String urlString)
            throws MalformedURLException, IOException 
    {
    	String filename = "";
    	//para testes
    	this.address_file = "/tmp/";
    	if (this.address_file == "")
    	{
    		filename = "/tmp/test.mp4";
    	}
    	else
    	{
    		String[] parts = urlString.split("\\?");
    		String[] bars = parts[0].split("/");
    		for (int i=0; i < bars.length; i++)
    		{
    			if(bars[i].contains(".mp4"))
    			{
    				filename = bars[i];
    				break;
    			}
    		}
    	}
        BufferedInputStream in = null;
        FileOutputStream fout = null;
        try 
        {
            in = new BufferedInputStream(new URL(urlString).openStream());
            fout = new FileOutputStream(this.address_file + filename);

            final byte data[] = new byte[1024];
            int count;
            while ((count = in.read(data, 0, 1024)) != -1) 
            {
                fout.write(data, 0, count);
            }
        } 
        finally 
        {
            if (in != null) 
            {
                in.close();
            }
            if (fout != null) 
            {
                fout.close();
            }
        }
        return filename;
    }
}