package com.co.coapps.views;

import java.io.Serializable;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.primefaces.model.StreamedContent;

import com.co.coapps.utilities.ConexionBD;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

@ManagedBean
public class JasperView implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String diocesis;
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
			parametros.put("diocesis", diocesis);

			ExternalContext eCtx = FacesContext.getCurrentInstance().getExternalContext();
			URL url = eCtx.getResource("/WEB-INF/resources");
			
			//en caso de tener espacios en la ruta que se identifique con %20
			String ur = url.getPath().replace("%20", " ");
			
			JasperReport jasperReport = JasperCompileManager.compileReport(ur + "/jasper/report1.jrxml");
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


	public String getDiocesis() {
		return diocesis;
	}

	public void setDiocesis(String diocesis) {
		this.diocesis = diocesis;
	}

	public StreamedContent getFile() {
		return file;
	}

	public void setFile(StreamedContent file) {
		this.file = file;
	}

}
