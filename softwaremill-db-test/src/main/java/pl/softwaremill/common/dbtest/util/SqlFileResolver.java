package pl.softwaremill.common.dbtest.util;

/**
 * @author Pawel Wrzeszcz (pawel . wrzeszcz [at] gmail . com)
 */
public class SqlFileResolver {

	private Class clazz;

	public SqlFileResolver(Class clazz) {
		this.clazz = clazz;
	}

	public String getSqlFilePath() {
		return clazz.getPackage().getName().replace(".","/") + "/" + clazz.getSimpleName() + ".sql";
	}
}
