package rbccps.smartcity.IDEAM.APIs;

import javax.servlet.ServletException;

import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.handlers.PathHandler;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;

public class cdxapis {

	static DeploymentInfo servletBuilder;
	static DeploymentManager manager;
	static PathHandler path = null;
	public static void main(String[] args){
		
		servletBuilder = Servlets.deployment().setClassLoader(cdxapis.class.getClassLoader());
		servletBuilder.setDeploymentName("cdx").setContextPath("/cdx");
		
		servletBuilder.addServlets(Servlets.servlet("redirect", new RequestRedirect().getClass()).addMapping("/redirect"));
		servletBuilder.addServlets(Servlets.servlet("follow", new RequestFollow().getClass()).addMapping("/follow"));
		servletBuilder.addServlets(Servlets.servlet("share", new RequestShare().getClass()).addMapping("/share"));
		servletBuilder.addServlets(Servlets.servlet("search", new RequestSearch().getClass()).addMapping("/search"));
		servletBuilder.addServlets(Servlets.servlet("register", new RequestRegister().getClass()).addMapping("/register"));
		servletBuilder.addServlets(Servlets.servlet("publish", new RequestPublish().getClass()).addMapping("/publish/*"));
		
		manager = Servlets.defaultContainer().addDeployment(servletBuilder);
		manager.deploy();
		
		try {
			path = Handlers.path(Handlers.redirect("/cdx")).addPrefixPath("/cdx", manager.start());
		} catch (ServletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Undertow server = Undertow.builder().addHttpListener(8080, "0.0.0.0").setHandler(path).build();
		server.start();
	}
}