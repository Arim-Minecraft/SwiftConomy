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

import org.bukkit.OfflinePlayer;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

/**
 * Economy tracking global balances, not per world.
 * 
 * @author A248
 *
 */
public interface WorldAgnosticEconomy extends Economy {

	@Override
	default boolean hasAccount(OfflinePlayer player, String worldName) {
		return hasAccount(player);
	}
	
	@Override
	default double getBalance(OfflinePlayer player, String world) {
		return getBalance(player);
	}
	
	@Override
	default boolean has(OfflinePlayer player, String worldName, double amount) {
		return has(player, amount);
	}
	
	@Override
	default EconomyResponse withdrawPlayer(OfflinePlayer player, String worldName, double amount) {
		return withdrawPlayer(player, amount);
	}
	
	@Override
	default EconomyResponse depositPlayer(OfflinePlayer player, String worldName, double amount) {
		return depositPlayer(player, amount);
	}
	
	@Override
	default boolean createPlayerAccount(OfflinePlayer player, String worldName) {
		return createPlayerAccount(player);
	}
	
}
