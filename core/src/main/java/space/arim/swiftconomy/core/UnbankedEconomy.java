/* 
 * SwiftConomy-core
 * Copyright © 2020 Anand Beh <https://www.arim.space>
 * 
 * SwiftConomy-core is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * SwiftConomy-core is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with SwiftConomy-core. If not, see <https://www.gnu.org/licenses/>
 * and navigate to version 3 of the GNU General Public License.
 */
package space.arim.swiftconomy.core;

import java.util.List;

import org.bukkit.OfflinePlayer;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

/**
 * Economy without bank support. <br>
 * Explicitly denies plugins which attempt to use banks despite {@link #hasBankSupport()} being false.
 * 
 * @author A248
 *
 */
public interface UnbankedEconomy extends Economy {

	@Override
	default boolean hasBankSupport() {
		return false;
	}
	
	@Override
	default EconomyResponse createBank(String name, OfflinePlayer player) {
		throw new UnsupportedOperationException("Plugin attempted to use banks but there is no support!");
	}

	@Override
	default EconomyResponse deleteBank(String name) {
		throw new UnsupportedOperationException("Plugin attempted to use banks but there is no support!");
	}

	@Override
	default EconomyResponse bankBalance(String name) {
		throw new UnsupportedOperationException("Plugin attempted to use banks but there is no support!");
	}

	@Override
	default EconomyResponse bankHas(String name, double amount) {
		throw new UnsupportedOperationException("Plugin attempted to use banks but there is no support!");
	}

	@Override
	default EconomyResponse bankWithdraw(String name, double amount) {
		throw new UnsupportedOperationException("Plugin attempted to use banks but there is no support!");
	}

	@Override
	default EconomyResponse bankDeposit(String name, double amount) {
		throw new UnsupportedOperationException("Plugin attempted to use banks but there is no support!");
	}

	@Override
	default EconomyResponse isBankOwner(String name, OfflinePlayer player) {
		throw new UnsupportedOperationException("Plugin attempted to use banks but there is no support!");
	}

	@Override
	default EconomyResponse isBankMember(String name, OfflinePlayer player) {
		throw new UnsupportedOperationException("Plugin attempted to use banks but there is no support!");
	}

	@Override
	default List<String> getBanks() {
		throw new UnsupportedOperationException("Plugin attempted to use banks but there is no support!");
	}
	
}
