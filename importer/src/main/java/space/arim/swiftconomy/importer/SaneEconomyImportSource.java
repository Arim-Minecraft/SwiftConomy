/* 
 * SwiftConomy-importer
 * Copyright © 2020 Anand Beh <https://www.arim.space>
 * 
 * SwiftConomy-importer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * SwiftConomy-importer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with SwiftConomy-importer. If not, see <https://www.gnu.org/licenses/>
 * and navigate to version 3 of the GNU General Public License.
 */
package space.arim.swiftconomy.importer;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.decimal4j.api.DecimalArithmetic;

public class SaneEconomyImportSource extends ImportSource<BigDecimal> {

	private final String host;
	private final int port;
	private final String database;
	private final String username;
	private final String password;
	
	private Connection connection;
	
	public SaneEconomyImportSource(String host, int port, String database, String username, String password) {
		this.host = host;
		this.port = port;
		this.database = database;
		this.username = username;
		this.password = password;
	}
	
	public SaneEconomyImportSource(String database, String username, String password) {
		this("localhost", 3306, database, username, password);
	}
	
	@Override
	void link() {
		try {
			Class.forName("com.mysql.jdbc");
			connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false&serverTimezone=UTC&useUnicode=true&characterEncoding=utf8", username, password);
		} catch (SQLException | ClassNotFoundException ex) {
			throw new RuntimeException(ex);
		}
	}
	
	@Override
	CompletableFuture<Map<UUID, BigDecimal>> getBalances() {
		return CompletableFuture.supplyAsync(() -> {
			Map<UUID, BigDecimal> result = new HashMap<>();
			try (PreparedStatement prepStmt = connection.prepareStatement("SELECT unique_identifier,balance FROM `saneeconomy_balances`")) {
				ResultSet rs = prepStmt.executeQuery();
				while (rs.next()) {
					result.put(UUID.fromString(rs.getString("unique_identifier").substring(7)), new BigDecimal(rs.getString("balance")));
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
			return result;
		});
	}

	@Override
	long convert(BigDecimal balance, DecimalArithmetic arithmetic) {
		return arithmetic.fromBigDecimal(balance);
	}
	
	@Override
	void unlink() {
		try {
			connection.close();
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
	}

}
