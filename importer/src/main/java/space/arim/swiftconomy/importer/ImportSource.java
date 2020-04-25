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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.decimal4j.api.DecimalArithmetic;

public abstract class ImportSource<T> {

	abstract CompletableFuture<Map<UUID, T>> getBalances();
	
	abstract long convert(T balance, DecimalArithmetic arithmetic);
	
	CompletableFuture<Map<UUID, Long>> convertBalances(DecimalArithmetic arithmetic) {
		return getBalances().thenApply((balances) -> {
			Map<UUID, Long> result = new HashMap<>();
			for (Map.Entry<UUID, T> entry : balances.entrySet()) {
				result.put(entry.getKey(), convert(entry.getValue(), arithmetic));
			}
			return result;
		});
	}
	
	abstract void link();
	
	abstract void unlink();
	
}
