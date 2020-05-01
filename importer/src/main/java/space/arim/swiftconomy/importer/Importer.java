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

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import space.arim.swiftconomy.api.SwiftConomy;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public abstract class Importer {

	private final SwiftConomy economy;
	private final ImportSource<?> source;
	
	public CompletableFuture<?> start() {
		source.link();
		return source.convertBalances(economy.getArithmetic()).thenAcceptAsync(this::processResultsAsync).thenRun(source::unlink);
	}
	
	protected abstract void processResultsAsync(Map<UUID, Long> results);
	
}
