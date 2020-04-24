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

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

/**
 * Economy using UUIDs and not player names. <br>
 * Explicitly denies outdated plugins which attempt to use names.
 * 
 * @author A248
 *
 */
@SuppressWarnings("deprecation")
public interface UUIDEconomy extends Economy {

	@Override
	default boolean hasAccount(String playerName) {
		throw new UnsupportedOperationException("Plugin attempted to transact by name instead of UUID");
	}

	@Override
	default boolean hasAccount(String playerName, String worldName) {
		throw new UnsupportedOperationException("Plugin attempted to transact by name instead of UUID");
	}

	@Override
	default double getBalance(String playerName) {
		throw new UnsupportedOperationException("Plugin attempted to transact by name instead of UUID");
	}

	@Override
	default double getBalance(String playerName, String world) {
		throw new UnsupportedOperationException("Plugin attempted to transact by name instead of UUID");
	}

	@Override
	default boolean has(String playerName, double amount) {
		return false;
	}

	@Override
	default boolean has(String playerName, String worldName, double amount) {
		throw new UnsupportedOperationException("Plugin attempted to transact by name instead of UUID");
	}

	@Override
	default EconomyResponse withdrawPlayer(String playerName, double amount) {
		throw new UnsupportedOperationException("Plugin attempted to transact by name instead of UUID");
	}

	@Override
	default EconomyResponse withdrawPlayer(String playerName, String worldName, double amount) {
		throw new UnsupportedOperationException("Plugin attempted to transact by name instead of UUID");
	}

	@Override
	default EconomyResponse depositPlayer(String playerName, double amount) {
		throw new UnsupportedOperationException("Plugin attempted to transact by name instead of UUID");
	}

	@Override
	default EconomyResponse depositPlayer(String playerName, String worldName, double amount) {
		throw new UnsupportedOperationException("Plugin attempted to transact by name instead of UUID");
	}

	@Override
	default EconomyResponse createBank(String name, String player) {
		throw new UnsupportedOperationException("Plugin attempted to transact by name instead of UUID");
	}

	@Override
	default EconomyResponse isBankOwner(String name, String playerName) {
		throw new UnsupportedOperationException("Plugin attempted to transact by name instead of UUID");
	}

	@Override
	default EconomyResponse isBankMember(String name, String playerName) {
		throw new UnsupportedOperationException("Plugin attempted to transact by name instead of UUID");
	}

	@Override
	default boolean createPlayerAccount(String playerName) {
		throw new UnsupportedOperationException("Plugin attempted to transact by name instead of UUID");
	}

	@Override
	default boolean createPlayerAccount(String playerName, String worldName) {
		throw new UnsupportedOperationException("Plugin attempted to transact by name instead of UUID");
	}
	
}
