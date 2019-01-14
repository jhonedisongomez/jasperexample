package com.co.coapps.views;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.co.coapps.utilities.ConexionBD;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;

@ManagedBean
public class JasperView implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name1;
	private String name2;
	private ConexionBD conexionBD;
	private StreamedContent file;
	


	@PostConstruct
	public void init() {
		conexionBD = null;

	}

	private String message;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void saveMessage() {
		FacesContext context = FacesContext.getCurrentInstance();

		context.addMessage(null, new FacesMessage("Second Message", "Additional Message Detail"));
	}
	
	public void generateReport() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		conexionBD = new ConexionBD();
		Connection conn = conexionBD.conectar();

		try {

			
			
			Map<String, String> parametros = new HashMap<>();
			ByteArrayOutputStream Teste = new ByteArrayOutputStream();
			parametros.put("diocesis", name1);

			ExternalContext eCtx = FacesContext.getCurrentInstance().getExternalContext();
			URL url = eCtx.getResource("/WEB-INF/resources");
			String ur = url.getPath().replace("%20", " ");
			//File files = new File(ur + "/jasper/report1.jrxml");
			
			JasperReport jasperReport = JasperCompileManager.compileReport(ur + "/jasper/report1.jrxml");
			//InputStream input =(InputStream) JRLoader.loadObjectFromLocation(ur + "/jasper/report1.jasper"); 
			//(JasperReport) JRLoader.loadObjectFromLocation(files.getPath());
			JasperPrint print = JasperFillManager.fillReport(jasperReport, parametros, conn);


			
			HttpServletResponse httpServletResponse = (HttpServletResponse) FacesContext.getCurrentInstance()
					.getExternalContext().getResponse();
			ServletOutputStream servletStream = httpServletResponse.getOutputStream();

			httpServletResponse.addHeader("Content-disposition", "attachment; filename=reporteDatoJava.pdf");
			JasperExportManager.exportReportToPdfStream(print, servletStream);
			FacesContext.getCurrentInstance().responseComplete();
			
			
		} catch (Exception e) {
			e.printStackTrace();
			facesContext.addMessage(null, new FacesMessage("Error", "Unexpected error creating the report"));
			
		}finally {
			try {
				if(conn != null)
					conn.close();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
		}
	}



	public String getName1() {
		return name1;
	}

	public void setName1(String name1) {
		this.name1 = name1;
	}

	public String getName2() {
		return name2;
	}

	public void setName2(String name2) {
		this.name2 = name2;
	}

	public StreamedContent getFile() {
		return file;
	}

	public void setFile(StreamedContent file) {
		this.file = file;
	}

}
