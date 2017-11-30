package mx.com.rodel.sps.db.common;

public class SqlServiceNotFound extends Exception{
	private static final long serialVersionUID = 3215958629015204526L;

	public SqlServiceNotFound() {
		super("Sponge SqlService not found, this a strange error all builds must contain it, please make sure that you follow the installation steps carefully and check that you are using the last build too!");
	}
}
