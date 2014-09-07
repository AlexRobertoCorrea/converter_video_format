package converter_video_format;

import java.io.Serializable;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

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


@ManagedBean(name = "FileUploadView", eager = true)
@RequestScoped
public class FileUploadView implements Serializable{
	private static final long serialVersionUID = 1L; 
	private String destination="/tmp/";
	private String api_key="0cdab19d2adc83a856e9dd7220343ab8";
	private String adress_file = "";
	private String url_get = "";
	
	public String getUrl()
	{
		return url_get;
	}
 
    public void handleFileUpload(FileUploadEvent event) throws IOException, JSONException {
        FacesMessage message = new FacesMessage("Succesful", event.getFile().getFileName() + " is uploaded.");
        FacesContext.getCurrentInstance().addMessage(null, message);
        try {
        	copyFile(event.getFile().getFileName(), event.getFile().getInputstream());
        } catch (IOException e) {
        	e.printStackTrace();
        }
        this.adress_file = this.destination + "sample.dv";
        String url = getUrlStream();
        System.out.println(url);
    }
    
    private void copyFile(String fileName, InputStream in) {
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
    
    public String getUrlStream() throws IOException, JSONException
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
	    	my_obj.put("input", this.adress_file);
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
	    	finally
	    	{
	    		response.close();
	    	}
    	}
    	finally
    	{
	    	httpclient.close();	
    	}
    	this.url_get = "https://zencoder-temp-storage-us-east-1.s3.amazonaws.com/o/20140906/13d3913a408b2559b831c339296297c6/4039f7dbdc71d00e816e521549a7349e.mp4?AWSAccessKeyId=AKIAI456JQ76GBU7FECA&Signature=wgtYo%2FCY515siVZyfMZksKLCV%2F8%3D&Expires=1410123555";
    	return this.url_get;
    }    
}