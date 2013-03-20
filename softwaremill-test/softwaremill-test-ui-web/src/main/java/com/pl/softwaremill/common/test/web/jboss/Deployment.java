package pl.softwaremill.common.test.web.jboss;

/**
 * @author Pawel Wrzeszcz (pawel . wrzeszcz [at] gmail . com)
 */
public interface Deployment {
	void deploy(String deployDir) throws Exception;

	void undeploy(String deployDir) throws Exception;

	String getWaitForMessage();

    long getWaitMillis();
}
